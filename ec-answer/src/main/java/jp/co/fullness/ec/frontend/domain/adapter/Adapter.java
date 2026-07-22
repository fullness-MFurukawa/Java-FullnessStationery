package jp.co.fullness.ec.frontend.domain.adapter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ドメインオブジェクトと他のモデル(DTO・DBの行など)を相互変換するアダプタの汎用インターフェイス。
 *
 * @param <D> ドメインオブジェクトの型
 * @param <M> 変換元/変換先となる他のモデルの型
 */
public interface Adapter<D, M> {

    /**
     * 他のモデルからドメインオブジェクトへ復元する。
     *
     * @param model 変換元のモデル
     * @return 復元されたドメインオブジェクト
     */
    D toDomain(M model);

    /**
     * ドメインオブジェクトから他のモデルへ変換する。
     *
     * @param domain ドメインオブジェクト
     * @return 変換されたモデル
     */
    M fromDomain(D domain);

    /**
     * モデルのコレクションをドメインオブジェクトのリストへ一括復元する(利便メソッド)。
     *
     * @param models 変換元のモデル群
     * @return 復元されたドメインオブジェクトのリスト
     */
    default List<D> toDomainList(Collection<? extends M> models) {
        return models.stream().map(this::toDomain).collect(Collectors.toList());
    }

    /**
     * ドメインオブジェクトのコレクションをモデルのリストへ一括変換する(利便メソッド)。
     *
     * @param domains 変換元のドメインオブジェクト群
     * @return 変換されたモデルのリスト
     */
    default List<M> fromDomainList(Collection<? extends D> domains) {
        return domains.stream().map(this::fromDomain).collect(Collectors.toList());
    }
}
