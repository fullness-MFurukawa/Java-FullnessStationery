package jp.co.fullness.ec.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import jp.co.fullness.ec.frontend.domain.model.Cart;

@Configuration
public class CartConfig {

    /** セッションごとのカート(スコープ付きプロキシで注入される)。 */
    @Bean
    @SessionScope
    public Cart cart() {
        return new Cart();
    }
}