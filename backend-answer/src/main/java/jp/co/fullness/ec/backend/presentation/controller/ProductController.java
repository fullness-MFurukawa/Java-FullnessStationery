package jp.co.fullness.ec.backend.presentation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.fullness.ec.backend.application.product.ProductSearchService;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.presentation.form.ProductSearchForm;
import lombok.RequiredArgsConstructor;

/**
 * 商品検索(UC011 / BP006)のコントローラ。商品管理のハブ画面。
 */
@Controller
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductController {

    private static final int PAGE_SIZE = 10;

    private final ProductSearchService productSearchService;

    /** 商品検索画面(BP006)。 */
    @GetMapping
    public String search(@ModelAttribute("productSearchForm") ProductSearchForm form, Model model) {
        Integer categoryId = form.getCategoryId();

        long total = productSearchService.count(categoryId);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));

        // ページ番号の補正(1〜totalPages)
        int page = form.getPage() < 1 ? 1 : form.getPage();
        if (page > totalPages) {
            page = totalPages;
        }

        List<Product> products = productSearchService.search(categoryId, page, PAGE_SIZE);

        // 各商品の image_url(S3キー)を署名付きURLに変換
        Map<Integer, String> imageUrls = new HashMap<>();
        for (Product product : products) {
            if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
                imageUrls.put(product.getId(), productSearchService.generateImageUrl(product.getImageUrl()));
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("imageUrls", imageUrls);
        model.addAttribute("categories", productSearchService.findAllCategories());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", total);
        return "admin/product/search";
    }
}