package jp.co.fullness.ec.backend.presentation.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.co.fullness.ec.backend.application.security.LoginAttemptService;
import jp.co.fullness.ec.backend.presentation.form.LoginForm;
import lombok.RequiredArgsConstructor;

/**
 * 担当者ログイン画面(BP002)のコントローラ。
 * GET で画面表示、POST でフォーム検証・ロック確認・認証を行う。
 */
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    /** 認証成功後の SecurityContext をセッションへ保存するリポジトリ。 */
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    /** ログイン画面を表示する。 */
    @GetMapping("/admin/login")
    public String showLogin(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "admin/login";
    }

    /** ログイン(認証)を実行する。 */
    @PostMapping("/admin/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        // 1) 入力値のバリデーション
        if (bindingResult.hasErrors()) {
            return "admin/login";
        }

        String username = loginForm.getUsername();

        // 2) アカウントロックの確認
        if (loginAttemptService.isLocked(username)) {
            model.addAttribute("errorMessage",
                    "アカウントがロックされています。しばらく経ってから再度お試しください");
            return "admin/login";
        }

        // 3) 認証処理
        try {
            UsernamePasswordAuthenticationToken authRequest =
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            username, loginForm.getPassword());
            Authentication authentication = authenticationManager.authenticate(authRequest);

            // 認証成功 → 失敗記録をクリア
            loginAttemptService.reset(username);

            // セッション固定攻撃対策: セッションIDを再生成
            request.getSession();
            request.changeSessionId();

            // SecurityContext を確立してセッションへ保存
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            return "redirect:/admin";

        } catch (AuthenticationException e) {
            // 認証失敗 → 失敗回数を記録
            loginAttemptService.recordFailure(username);

            if (loginAttemptService.isLocked(username)) {
                model.addAttribute("errorMessage",
                        "アカウントがロックされています。しばらく経ってから再度お試しください");
            } else {
                model.addAttribute("errorMessage", "アカウント名またはパスワードが正しくありません");
            }
            return "admin/login";
        }
    }
}