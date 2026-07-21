package jp.co.fullness.ec.backend.application.category;

/**
 * 商品カテゴリ登録(UC014)の application サービス。
 */
public interface CategoryRegisterService {

    /**
     * 商品カテゴリを登録する。
     *
     * @param name カテゴリ名
     */
    void register(String name);
}