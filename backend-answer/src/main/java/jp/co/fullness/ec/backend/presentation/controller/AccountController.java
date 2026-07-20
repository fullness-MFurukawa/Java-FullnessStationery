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

import jp.co.fullness.ec.backend.application.account.AccountRegisterService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.presentation.form.AccountRegisterForm;
import lombok.RequiredArgsConstructor;

/**
 * 担当者アカウント登録(UC009 / BP003→BP004→BP005)のコントローラ。
 * @SessionAttributes で入力→確認→完了の途中データを保持する。
 */
@Controller
@RequestMapping("/admin/account")
@SessionAttributes("accountRegisterForm")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRegisterService accountRegisterService;

    /** セッションに無い場合の初期フォーム。 */
    @ModelAttribute("accountRegisterForm")
    public AccountRegisterForm setupForm() {
        return new AccountRegisterForm();
    }

    /** 入力画面(BP003)。 */
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("employees", accountRegisterService.findRegisterableEmployees());
        return "admin/account/form";
    }

    /** 確認画面(BP004)。 */
    @PostMapping("/confirm")
    public String confirm(@Validated @ModelAttribute("accountRegisterForm") AccountRegisterForm form,
                          BindingResult bindingResult,
                          Model model) {
        // 1) 入力バリデーション
        if (bindingResult.hasErrors()) {
            model.addAttribute("employees", accountRegisterService.findRegisterableEmployees());
            return "admin/account/form";
        }
        // 2) アカウント名の重複チェック
        if (accountRegisterService.isAccountNameDuplicated(form.getAccountName())) {
            bindingResult.rejectValue("accountName", "duplicate", "このアカウント名は既に使用されています");
            model.addAttribute("employees", accountRegisterService.findRegisterableEmployees());
            return "admin/account/form";
        }
        // 3) 確認画面に社員名を表示するため解決
        model.addAttribute("employeeName", resolveEmployeeName(form.getEmployeeId()));
        return "admin/account/confirm";
    }

    /** 登録実行 → 完了(BP005)へリダイレクト。 */
    @PostMapping("/complete")
    public String complete(@ModelAttribute("accountRegisterForm") AccountRegisterForm form,
                           SessionStatus sessionStatus,
                           RedirectAttributes redirectAttributes) {
        // 登録後は未作成一覧から消えるため、社員名を先に解決しておく
        String employeeName = resolveEmployeeName(form.getEmployeeId());
        try {
            accountRegisterService.register(form.getEmployeeId(), form.getAccountName(), form.getPassword());
        } catch (DomainException e) {
            // 業務エラー(重複など) → 入力へ戻す(入力値はセッション保持)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/account/form";
        }
        // 完了画面へ引き継ぎ、セッションの入力データはクリア
        redirectAttributes.addFlashAttribute("completedEmployeeName", employeeName);
        redirectAttributes.addFlashAttribute("completedAccountName", form.getAccountName());
        sessionStatus.setComplete();
        return "redirect:/admin/account/complete";
    }

    /** 完了画面(BP005)。直接アクセスはメニューへ。 */
    @GetMapping("/complete")
    public String completeView(Model model) {
        if (!model.containsAttribute("completedAccountName")) {
            return "redirect:/admin";
        }
        return "admin/account/complete";
    }

    /** キャンセル → メニュー(入力データ破棄)。 */
    @GetMapping("/cancel")
    public String cancel(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/admin";
    }

    /** 社員IDから社員名を解決する(アカウント未作成一覧から)。 */
    private String resolveEmployeeName(Integer employeeId) {
        return accountRegisterService.findRegisterableEmployees().stream()
                .filter(e -> e.getId().equals(employeeId))
                .map(Employee::getName)
                .findFirst()
                .orElse("");
    }
}
