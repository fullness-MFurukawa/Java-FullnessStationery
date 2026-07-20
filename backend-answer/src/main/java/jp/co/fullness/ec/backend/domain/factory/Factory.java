package jp.co.fullness.ec.backend.domain.factory;

/**
 * 集約(ドメインオブジェクト)を構築するファクトリの汎用インターフェイス。
 *
 * @param <T> 構築する集約の型
 * @param <S> 構築の入力となるソース(構成要素をまとめたパラメータ)の型
 */
public interface Factory<T, S> {

    /**
     * ソースから集約を構築する。
     *
     * @param source 構築に必要な入力
     * @return 構築された集約
     */
    T create(S source);
}
