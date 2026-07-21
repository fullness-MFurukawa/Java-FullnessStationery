package jp.co.fullness.ec.backend.presentation.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.fullness.ec.backend.application.product.ProductCreateService;
import jp.co.fullness.ec.backend.application.product.ProductRegisterCommand;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.presentation.form.ProductRegisterForm;
import lombok.RequiredArgsConstructor;

/**
 * 新商品登録(UC010 / BP012→BP013→BP014)のコントローラ。
 * @SessionAttributes で入力→確認→完了を保持する。
 */
@Controller
@RequestMapping("/admin/product")
@SessionAttributes("productRegisterForm")
@RequiredArgsConstructor
public class ProductRegisterController {

    /** 画像の許容最大ピクセル(幅・高さ) */
    private static final int MAX_IMAGE_PX = 1000;

    private final ProductCreateService productCreateService;

    @ModelAttribute("productRegisterForm")
    public ProductRegisterForm setupForm() {
        return new ProductRegisterForm();
    }

    /** 入力画面(BP012)。 */
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("categories", productCreateService.findAllCategories());
        return "admin/product/register";
    }

    /** 確認画面(BP013)。 */
    @PostMapping("/confirm")
    public String confirm(@Validated @ModelAttribute("productRegisterForm") ProductRegisterForm form,
                          BindingResult bindingResult,
                          Model model) {
        // 画像の検証(必須・形式・サイズ)
        validateImage(form, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productCreateService.findAllCategories());
            return "admin/product/register";
        }

        // 全項目OKなら、新規ファイルがあれば S3 へアップロードしてキーを保持
        uploadImageIfPresent(form);

        // 確認表示用にカテゴリ名と署名付きURLを解決
        model.addAttribute("categoryName", resolveCategoryName(form.getCategoryId()));
        model.addAttribute("imageUrl", productCreateService.generateImageUrl(form.getImageKey()));
        return "admin/product/register_confirm";
    }

    /** 登録実行 → 完了(BP014)へ。 */
    @PostMapping("/complete")
    public String complete(@ModelAttribute("productRegisterForm") ProductRegisterForm form,
                           SessionStatus sessionStatus,
                           RedirectAttributes redirectAttributes) {
        try {
            productCreateService.register(new ProductRegisterCommand(
                    form.getName(), form.getPrice(), form.getStockQuantity(),
                    form.getCategoryId(), form.getImageKey()));
        } catch (DomainException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/product/add";
        }
        redirectAttributes.addFlashAttribute("completedProductName", form.getName());
        sessionStatus.setComplete();
        return "redirect:/admin/product/complete";
    }

    /** 完了画面(BP014)。直接アクセスは商品検索へ。 */
    @GetMapping("/complete")
    public String completeView(Model model) {
        if (!model.containsAttribute("completedProductName")) {
            return "redirect:/admin/product";
        }
        return "admin/product/register_complete";
    }

    /** キャンセル → 商品検索(入力データ破棄)。 */
    @GetMapping("/cancel")
    public String cancel(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/admin/product";
    }

    // ---------------- helpers ----------------

    /** 画像の検証(必須・形式・サイズ)。エラーは bindingResult に積む。 */
    private void validateImage(ProductRegisterForm form, BindingResult bindingResult) {
        MultipartFile imageFile = form.getImageFile();
        boolean hasNewFile = imageFile != null && !imageFile.isEmpty();

        if (!hasNewFile) {
            // 新規ファイル無し。確認から戻った場合など既存キーがあれば検証済みとみなす
            if (form.getImageKey() == null) {
                bindingResult.rejectValue("imageFile", "required", "画像をアップロードしてください");
            }
            return;
        }

        @SuppressWarnings("null")
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            bindingResult.rejectValue("imageFile", "format", "正しい画像形式でアップロードしてください");
            return;
        }
        try {
            BufferedImage img = ImageIO.read(imageFile.getInputStream());
            if (img == null) {
                bindingResult.rejectValue("imageFile", "format", "正しい画像形式でアップロードしてください");
                return;
            }
            if (img.getWidth() > MAX_IMAGE_PX || img.getHeight() > MAX_IMAGE_PX) {
                bindingResult.rejectValue("imageFile", "size", "画像サイズは1000px以下でアップロードしてください");
            }
        } catch (IOException e) {
            bindingResult.rejectValue("imageFile", "error", "画像の読み込みに失敗しました");
        }
    }

    /** 新規ファイルがあれば S3 にアップロードし、キー・ファイル名を保持する。 */
    private void uploadImageIfPresent(ProductRegisterForm form) {
        MultipartFile imageFile = form.getImageFile();
        if (imageFile == null || imageFile.isEmpty()) {
            return; // 既存キーを使用
        }
        try {
            String key = productCreateService.uploadImage(
                    imageFile.getBytes(), imageFile.getContentType(), imageFile.getOriginalFilename());
            form.setImageKey(key);
            form.setImageFileName(imageFile.getOriginalFilename());
            form.setImageFile(null); // セッションに MultipartFile を残さない
        } catch (IOException e) {
            throw new IllegalStateException("画像のアップロードに失敗しました", e);
        }
    }

    @SuppressWarnings("null")
    private String resolveCategoryName(Integer categoryId) {
        return productCreateService.findAllCategories().stream()
                .filter(c -> c.getId().equals(categoryId))
                .map(ProductCategory::getName)
                .findFirst()
                .orElse("");
    }
}