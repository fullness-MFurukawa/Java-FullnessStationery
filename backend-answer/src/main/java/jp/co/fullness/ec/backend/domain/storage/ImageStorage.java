package jp.co.fullness.ec.backend.domain.storage;

/**
 * 画像ストレージのポート(出力ポート)。
 * <p>永続化技術に依存しないドメイン側のインターフェイスとして定義し、
 * 実装は infrastructure 層(S3)で行う。</p>
 */
public interface ImageStorage {

    /**
     * 画像を保存する。
     *
     * @param key         保存先のオブジェクトキー(例: products/xxxx.png)
     * @param content     画像のバイト列
     * @param contentType MIMEタイプ(例: image/png)
     */
    void upload(String key, byte[] content, String contentType);

    /**
     * 表示用の署名付きURL(期限付き)を生成する。
     *
     * @param key オブジェクトキー
     * @return 署名付きURL
     */
    String generatePresignedUrl(String key);
}