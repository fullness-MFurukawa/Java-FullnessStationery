package jp.co.fullness.ec.frontend.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** トップ(/)は暫定で商品検索へ。FP001 トップ画面は後で実装。 */
    @GetMapping("/")
    public String top() {
        return "redirect:/products/search";
    }
}