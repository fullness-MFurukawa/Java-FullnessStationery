package jp.co.fullness.ec.backend.application.security;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * ログイン失敗回数を管理し、連続5回失敗で10分間ロックする(BP002 セキュリティ仕様)。
 * ※ 本演習ではインメモリ管理(再起動でリセット・複数インスタンス非対応)。
 */
@Service
public class LoginAttemptService {

    /** ロックしきい値(連続失敗回数) */
    private static final int MAX_ATTEMPTS = 5;
    /** ロック時間(分) */
    private static final long LOCK_MINUTES = 10;

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    /**
     * 指定アカウントが現在ロック中かを判定する。
     * ロック時間を過ぎている場合は記録をクリアして未ロックとして扱う。
     *
     * @param username アカウント名
     * @return ロック中の場合 true
     */
    public boolean isLocked(String username) {
        Attempt attempt = attempts.get(username);
        if (attempt == null || attempt.getLockedUntil() == null) {
            return false;
        }
        if (LocalDateTime.now().isBefore(attempt.getLockedUntil())) {
            return true;
        }
        // ロック時間が経過 → リセット
        attempts.remove(username);
        return false;
    }

    /**
     * ログイン失敗を記録する。連続失敗が5回に達したら10分間ロックする。
     *
     * @param username アカウント名
     */
    public void recordFailure(String username) {
        attempts.compute(username, (key, current) -> {
            int count = (current == null) ? 1 : current.getCount() + 1;
            LocalDateTime lockedUntil = (count >= MAX_ATTEMPTS)
                    ? LocalDateTime.now().plusMinutes(LOCK_MINUTES)
                    : null;
            return new Attempt(count, lockedUntil);
        });
    }

    /**
     * ログイン成功時に失敗記録をクリアする。
     *
     * @param username アカウント名
     */
    public void reset(String username) {
        attempts.remove(username);
    }

    /** 失敗回数とロック期限を保持する内部クラス。 */
    private static class Attempt {
        private final int count;
        private final LocalDateTime lockedUntil;

        Attempt(int count, LocalDateTime lockedUntil) {
            this.count = count;
            this.lockedUntil = lockedUntil;
        }

        int getCount() {
            return count;
        }

        LocalDateTime getLockedUntil() {
            return lockedUntil;
        }
    }
}
