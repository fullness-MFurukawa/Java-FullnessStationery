package jp.co.fullness.ec.backend.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.fullness.ec.backend.application.product.ProductDeleteService;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.presentation.form.ProductDeleteForm;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductDeleteController {

    private final ProductDeleteService productDeleteService;

    /** BP007 削除確認: 検索画面の[削除]リンクから id を受けて表示 */
    @GetMapping("/delete")
    public String confirm(@ModelAttribute ProductDeleteForm form, Model model) {
        if (form.getId() == null) {
            return "redirect:/admin/product";          
        }
        Product product = productDeleteService.findById(form.getId());
        model.addAttribute("product", product);
        model.addAttribute("imageUrl", productDeleteService.generateImageUrl(product.getImageUrl()));
        model.addAttribute("productDeleteForm", form);
        return "admin/product/delete_confirm";
    }

    /** 削除実行 → BP008 完了へリダイレクト */
    @PostMapping("/delete")
    public String delete(@ModelAttribute ProductDeleteForm form,
                         RedirectAttributes redirectAttributes) {
        Product product = productDeleteService.findById(form.getId()); // 完了表示用に名称取得
        productDeleteService.delete(form.getId());
        redirectAttributes.addFlashAttribute("deletedProductName", product.getName());
        return "redirect:/admin/product/delete/complete";
    }

    /** BP008 削除完了(直接アクセスは検索へ) */
    @GetMapping("/delete/complete")
    public String complete(Model model) {
        if (!model.containsAttribute("deletedProductName")) {
            return "redirect:/admin/product";          
        }
        return "admin/product/delete_complete";
    }
}