package jp.co.fullness.ec.backend.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.fullness.ec.backend.application.security.LoginUserDetails;

/**
 * メニュー画面(BP001)の表示を担うコントローラ。
 */
@Controller
public class MenuController {

    @GetMapping("/admin")
    public String menu(@AuthenticationPrincipal LoginUserDetails userDetails, Model model) {
        model.addAttribute("displayName", userDetails.getDisplayName());
        return "admin/menu";
    }
}
