package jp.co.fullness.ec.frontend.presentation.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jp.co.fullness.ec.frontend.presentation.form.LoginForm;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    /** FP002 ログイン画面表示 */
    @GetMapping("/login")
    public String showLoginForm(@ModelAttribute LoginForm loginForm) {
        return "login";
    }

    /** ログイン処理（手動認証） */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm,
                        BindingResult bindingResult,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {

        // 入力チェック（未入力・形式不正など）
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            // メールアドレス＋パスワードで認証
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginForm.getMailAddress(),
                            loginForm.getPassword()));

            // 認証済みコンテキストをセッションへ保存（セッション固定攻撃対策込み）
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            request.changeSessionId();
            securityContextRepository.saveContext(context, request, response);

            return "redirect:/";

        } catch (AuthenticationException e) {
            // 認証失敗（該当なし・パスワード不一致）
            model.addAttribute("loginError",
                    "メールアドレスまたはパスワードが正しくありません");
            return "login";
        }
    }
}
