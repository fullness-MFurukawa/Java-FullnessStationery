package jp.co.fullness.ec.backend.presentation.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.fullness.ec.backend.application.category.CategoryRegisterService;
import jp.co.fullness.ec.backend.presentation.form.CategoryRegisterForm;
import lombok.RequiredArgsConstructor;

/**
 * 商品カテゴリ登録(UC014 / BP019→BP020→BP021)のコントローラ。
 */
@Controller
@RequestMapping("/admin/category")
@SessionAttributes("categoryRegisterForm")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRegisterService categoryRegisterService;

    /** セッションに無い場合の初期フォーム。 */
    @ModelAttribute("categoryRegisterForm")
    public CategoryRegisterForm setupForm() {
        return new CategoryRegisterForm();
    }

    /** 入力画面(BP019)。 */
    @GetMapping("/form")
    public String form() {
        return "admin/category/form";
    }

    /** 確認画面(BP020)。 */
    @PostMapping("/confirm")
    public String confirm(@Validated @ModelAttribute("categoryRegisterForm") CategoryRegisterForm form,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/category/form";
        }
        return "admin/category/confirm";
    }

    /** 登録実行 → 完了(BP021)へリダイレクト。 */
    @PostMapping("/complete")
    public String complete(@ModelAttribute("categoryRegisterForm") CategoryRegisterForm form,
                           SessionStatus sessionStatus,
                           RedirectAttributes redirectAttributes) {
        categoryRegisterService.register(form.getName());
        redirectAttributes.addFlashAttribute("completedCategoryName", form.getName());
        sessionStatus.setComplete();
        return "redirect:/admin/category/complete";
    }

    /** 完了画面(BP021)。直接アクセスはメニューへ。 */
    @GetMapping("/complete")
    public String completeView(Model model) {
        if (!model.containsAttribute("completedCategoryName")) {
            return "redirect:/admin";
        }
        return "admin/category/complete";
    }

    /** キャンセル → メニュー(入力データ破棄)。 */
    @GetMapping("/cancel")
    public String cancel(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/admin";
    }
}
