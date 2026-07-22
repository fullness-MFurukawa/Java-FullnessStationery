package jp.co.fullness.ec.frontend.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import jp.co.fullness.ec.frontend.infrastructure.entity.CustomerEntity;

@Mapper
public interface CustomerMapper {

    /** メールアドレスで顧客を取得(0/1件)。UC002 ログイン認証。 */
    CustomerEntity selectByMailAddress(String mailAddress);

    /** IDで顧客を取得(0/1件)。 */
    CustomerEntity selectById(Integer id);

    /** 新規登録(採番IDを keyProperty=id で反映)。UC001。 */
    void insert(CustomerEntity entity);

    /** メールアドレスの重複件数。UC001 重複チェック。 */
    int countByMailAddress(String mailAddress);

    /** アカウント名の重複件数。UC001 重複チェック。 */
    int countByUsername(String username);
}
