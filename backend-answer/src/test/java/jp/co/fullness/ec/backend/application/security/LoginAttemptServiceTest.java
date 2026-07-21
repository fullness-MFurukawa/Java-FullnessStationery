package jp.co.fullness.ec.backend.application.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LoginAttemptServiceTest {

    private final LoginAttemptService service = new LoginAttemptService();

    @Test
    void 初期状態はロックされていない() {
        assertThat(service.isLocked("user")).isFalse();
    }

    @Test
    void 失敗4回まではロックされない() {
        for (int i = 0; i < 4; i++) {
            service.recordFailure("user");
        }
        assertThat(service.isLocked("user")).isFalse();
    }

    @Test
    void 失敗5回でロックされる() {
        for (int i = 0; i < 5; i++) {
            service.recordFailure("user");
        }
        assertThat(service.isLocked("user")).isTrue();
    }

    @Test
    void reset_失敗記録をクリアしロックを解除する() {
        for (int i = 0; i < 5; i++) {
            service.recordFailure("user");
        }
        assertThat(service.isLocked("user")).isTrue();

        service.reset("user");

        assertThat(service.isLocked("user")).isFalse();
    }

    @Test
    void アカウントごとに独立して管理される() {
        for (int i = 0; i < 5; i++) {
            service.recordFailure("userA");
        }

        assertThat(service.isLocked("userA")).isTrue();
        assertThat(service.isLocked("userB")).isFalse();   // 別アカウントは影響を受けない
    }
}