package jp.co.fullness.ec.frontend.presentation.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.fullness.ec.frontend.application.customer.CustomerRegisterService;
import jp.co.fullness.ec.frontend.presentation.form.CustomerRegisterForm;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class CustomerRegisterController {

    /** 入力データをセッション保持するキー。 */
    private static final String FORM_KEY = "customerRegisterForm";
    /** 登録直後フラグ(完了画面の直接アクセス防止)。 */
    private static final String COMPLETED_KEY = "customerRegisterCompleted";

    private final CustomerRegisterService customerRegisterService;

    /** FP003 入力画面表示。戻る/初回とも同じ画面。 */
    @GetMapping("/form")
    public String form(Model model, HttpSession session) {
        if (!model.containsAttribute(FORM_KEY)) {
            Object saved = session.getAttribute(FORM_KEY);
            model.addAttribute(FORM_KEY,
                    saved != null ? saved : new CustomerRegisterForm());
        }
        return "account/form";
    }

    /** FP003→FP004 確認へ(確認ボタン)。バリデーション＋重複チェック。 */
    @PostMapping("/confirm")
    public String confirm(@Valid @ModelAttribute(FORM_KEY) CustomerRegisterForm form,
                          BindingResult bindingResult,
                          HttpSession session) {

        // 形式が正しいときだけDB重複チェック
        if (!bindingResult.hasFieldErrors("mailAddress")
                && customerRegisterService.isMailAddressTaken(form.getMailAddress())) {
            bindingResult.rejectValue("mailAddress", "duplicate",
                    "このメールアドレスは既に登録されています");
        }
        if (!bindingResult.hasFieldErrors("username")
                && customerRegisterService.isUsernameTaken(form.getUsername())) {
            bindingResult.rejectValue("username", "duplicate",
                    "このアカウント名は既に使用されています");
        }

        if (bindingResult.hasErrors()) {
            return "account/form";
        }

        session.setAttribute(FORM_KEY, form);
        return "account/confirm";
    }

    /** FP004 戻る→入力へ(入力値を保持)。 */
    @PostMapping("/back")
    public String back(HttpSession session, RedirectAttributes ra) {
        Object saved = session.getAttribute(FORM_KEY);
        if (saved != null) {
            ra.addFlashAttribute(FORM_KEY, saved);
        }
        return "redirect:/account/form";
    }

    /** FP004→登録(登録ボタン)→FP005。 */
    @PostMapping("/complete")
    public String complete(HttpSession session, RedirectAttributes ra) {
        CustomerRegisterForm form = (CustomerRegisterForm) session.getAttribute(FORM_KEY);
        if (form == null) {
            ra.addFlashAttribute("errorMessage",
                    "入力情報が見つかりません。再度入力してください。");
            return "redirect:/account/form";
        }

        customerRegisterService.register(form);

        session.removeAttribute(FORM_KEY);
        session.setAttribute(COMPLETED_KEY, Boolean.TRUE);
        return "redirect:/account/complete";
    }

    /** FP005 完了画面。直接アクセスはトップへ。 */
    @GetMapping("/complete")
    public String completeView(HttpSession session) {
        if (session.getAttribute(COMPLETED_KEY) == null) {
            return "redirect:/";   // 不正アクセス
        }
        session.removeAttribute(COMPLETED_KEY);
        return "account/complete";
    }
}