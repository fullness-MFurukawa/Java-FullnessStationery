package jp.co.fullness.ec.frontend.presentation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.fullness.ec.frontend.application.product.ProductSearchService;
import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.presentation.form.ProductSearchForm;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductSearchService productSearchService;

    /** FP006 カテゴリ商品検索。 */
    @GetMapping("/search")
    public String search(@ModelAttribute ProductSearchForm form, Model model) {
        List<Product> products = productSearchService.searchByCategory(form.getCategoryId());

        // 各商品の画像キー → 署名付きURL
        Map<Integer, String> imageUrls = new HashMap<>();
        for (Product product : products) {
            if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
                imageUrls.put(product.getId(), productSearchService.generateImageUrl(product.getImageUrl()));
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("imageUrls", imageUrls);
        model.addAttribute("categories", productSearchService.findAllCategories());
        if (products.isEmpty()) {
            model.addAttribute("message", "該当する商品が見つかりませんでした");
        }
        return "products/search";
    }

    /** FP007 商品詳細。 */
    @GetMapping("/detail/{productId}")
    public String detail(@PathVariable Integer productId, Model model) {
        Product product;
        try {
            product = productSearchService.findById(productId);
        } catch (DomainException e) {
            model.addAttribute("errorMessage", "指定された商品は存在しません");
            return "products/detail";
        }

        int stock = (product.getStock() != null) ? product.getStock().getQuantity() : 0;

        model.addAttribute("product", product);
        model.addAttribute("imageUrl", productSearchService.generateImageUrl(product.getImageUrl()));
        model.addAttribute("stock", stock);
        // 数量プルダウン 1〜在庫数
        model.addAttribute("quantityOptions", IntStream.rangeClosed(1, stock).boxed().toList());
        return "products/detail";
    }
}
