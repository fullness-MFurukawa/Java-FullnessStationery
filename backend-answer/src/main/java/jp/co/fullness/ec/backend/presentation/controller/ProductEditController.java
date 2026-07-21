package jp.co.fullness.ec.backend.presentation.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.fullness.ec.backend.application.product.ProductUpdateCommand;
import jp.co.fullness.ec.backend.application.product.ProductUpdateService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.presentation.form.ProductEditForm;
import lombok.RequiredArgsConstructor;

/**
 * 商品修正(UC012 / BP009→BP010→BP011)のコントローラ。
 */
@Controller
@RequestMapping("/admin/product/edit")
@SessionAttributes("productEditForm")
@RequiredArgsConstructor
public class ProductEditController {

    private static final int MAX_IMAGE_PX = 1000;

    private final ProductUpdateService productUpdateService;

    @ModelAttribute("productEditForm")
    public ProductEditForm setupForm() {
        return new ProductEditForm();
    }

    /** 入力画面(BP009)。対象商品をDBから読み込んでプリセットする。 */
    @GetMapping("/{productId}")
    public String edit(@PathVariable Integer productId, Model model) {
        Optional<Product> opt = productUpdateService.findById(productId);
        if (opt.isEmpty()) {
            return "redirect:/admin/product";
        }
        Product product = opt.get();

        ProductEditForm form = new ProductEditForm();
        form.setProductId(product.getId());
        form.setName(product.getName());
        form.setPrice(product.getPrice());
        form.setStockQuantity(product.getStock() != null ? product.getStock().getQuantity() : null);
        form.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        form.setImageKey(product.getImageUrl());

        model.addAttribute("productEditForm", form);
        model.addAttribute("categories", productUpdateService.findAllCategories());
        model.addAttribute("imageUrl", productUpdateService.generateImageUrl(product.getImageUrl()));
        return "admin/product/edit";
    }

    /** 確認画面(BP010)。 */
    @PostMapping("/confirm")
    public String confirm(@Validated @ModelAttribute("productEditForm") ProductEditForm form,
                          BindingResult bindingResult,
                          Model model) {
        // 画像は変更時のみ検証
        validateImageIfPresent(form, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productUpdateService.findAllCategories());
            if (form.getImageKey() != null) {
                model.addAttribute("imageUrl", productUpdateService.generateImageUrl(form.getImageKey()));
            }
            return "admin/product/edit";
        }

        // 新規ファイルがあれば S3 にアップロードしてキーを差し替え
        uploadImageIfPresent(form);

        model.addAttribute("categoryName", resolveCategoryName(form.getCategoryId()));
        model.addAttribute("imageUrl", productUpdateService.generateImageUrl(form.getImageKey()));
        return "admin/product/edit_confirm";
    }

    /** 更新実行 → 完了(BP011)へ。 */
    @PostMapping("/complete")
    public String complete(@ModelAttribute("productEditForm") ProductEditForm form,
                           SessionStatus sessionStatus,
                           RedirectAttributes redirectAttributes) {
        try {
            productUpdateService.update(new ProductUpdateCommand(
                    form.getProductId(), form.getName(), form.getPrice(),
                    form.getStockQuantity(), form.getCategoryId(), form.getImageKey()));
        } catch (DomainException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/product/edit/" + form.getProductId();
        }
        redirectAttributes.addFlashAttribute("completedProductName", form.getName());
        sessionStatus.setComplete();
        return "redirect:/admin/product/edit/complete";
    }

    /** 完了画面(BP011)。直接アクセスは商品検索へ。 */
    @GetMapping("/complete")
    public String completeView(Model model) {
        if (!model.containsAttribute("completedProductName")) {
            return "redirect:/admin/product";
        }
        return "admin/product/edit_complete";
    }

    /** キャンセル → 商品検索(入力データ破棄)。 */
    @GetMapping("/cancel")
    public String cancel(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/admin/product";
    }

    // ---------------- helpers ----------------

    /** 画像は「変更時のみ」検証(必須チェックはしない)。 */
    private void validateImageIfPresent(ProductEditForm form, BindingResult bindingResult) {
        MultipartFile imageFile = form.getImageFile();
        if (imageFile == null || imageFile.isEmpty()) {
            return; // 変更なし → 既存画像を維持
        }
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

    /** 新規ファイルがあれば S3 にアップロードし、キー・ファイル名を差し替える。 */
    private void uploadImageIfPresent(ProductEditForm form) {
        MultipartFile imageFile = form.getImageFile();
        if (imageFile == null || imageFile.isEmpty()) {
            return; // 既存キーを維持
        }
        try {
            String key = productUpdateService.uploadImage(
                    imageFile.getBytes(), imageFile.getContentType(), imageFile.getOriginalFilename());
            form.setImageKey(key);
            form.setImageFileName(imageFile.getOriginalFilename());
            form.setImageFile(null);
        } catch (IOException e) {
            throw new IllegalStateException("画像のアップロードに失敗しました", e);
        }
    }

    @SuppressWarnings("null")
    private String resolveCategoryName(Integer categoryId) {
        return productUpdateService.findAllCategories().stream()
                .filter(c -> c.getId().equals(categoryId))
                .map(ProductCategory::getName)
                .findFirst()
                .orElse("");
    }
}
