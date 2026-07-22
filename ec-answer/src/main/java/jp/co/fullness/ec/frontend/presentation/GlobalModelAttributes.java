package jp.co.fullness.ec.frontend.presentation;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.co.fullness.ec.frontend.application.security.CustomerUserDetails;
import jp.co.fullness.ec.frontend.domain.model.Cart;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final Cart cart;

    @ModelAttribute("cart")
    public Cart cart() {
        return cart;
    }

    @ModelAttribute("displayName")
    public String displayName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomerUserDetails details) {
            return details.getDisplayName();
        }
        return null;
    }
}