package jp.co.fullness.ec.backend.domain.exception;

/**
 * ドメイン層で発生する業務ルール違反・不整合を表す基底例外。
 *
 * <p>非チェック例外({@link RuntimeException})として定義し、
 * ドメイン規則に反する状態を検知した際にスローする。
 * より具体的な例外は本クラスを継承して定義する
 * (例: 対象データ不存在、重複、業務ルール違反 など)。</p>
 */
public class DomainException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * メッセージを指定して例外を生成する。
     *
     * @param message エラーメッセージ
     */
    public DomainException(String message) {
        super(message);
    }

    /**
     * メッセージと原因例外を指定して例外を生成する。
     *
     * @param message エラーメッセージ
     * @param cause   原因となった例外
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}