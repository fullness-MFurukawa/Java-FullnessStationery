-- =====================================================================
-- フルネス文具 EC システム : テーブル定義(DDL)
-- ---------------------------------------------------------------------
-- 対象   : PostgreSQL 17 / データベース "fullness-ecx"
-- 実行例 : psql -U fullness -h localhost -d "fullness-ecx" -f V1__create_tables.sql
-- 備考   : 設計書「テーブル定義書」を正としています。
--          ・id はアプリで採番するため SERIAL(自動採番)を採用
--          ・注文明細テーブルはテーブル一覧の名称 orders_detail を採用
--          ・customer.created_at / employee.kana はテーブル定義書に準拠
--          ・一意制約(アカウント名・メール・在庫の商品ID等)は
--            画面設計の重複チェック要件に合わせて付与
-- ---------------------------------------------------------------------
-- Flyway を使う場合は本ファイルを
--   src/main/resources/db/migration/V1__create_tables.sql
-- に配置すればアプリ起動時に自動適用されます(手動実行は不要)。
-- =====================================================================

-- 既存テーブルがある場合に作り直せるよう、依存関係の逆順で削除
DROP TABLE IF EXISTS orders_detail   CASCADE;
DROP TABLE IF EXISTS orders          CASCADE;
DROP TABLE IF EXISTS customer        CASCADE;
DROP TABLE IF EXISTS payment_method  CASCADE;
DROP TABLE IF EXISTS order_status    CASCADE;
DROP TABLE IF EXISTS product_stock   CASCADE;
DROP TABLE IF EXISTS product         CASCADE;
DROP TABLE IF EXISTS product_category CASCADE;
DROP TABLE IF EXISTS employee_account CASCADE;
DROP TABLE IF EXISTS employee        CASCADE;
DROP TABLE IF EXISTS department      CASCADE;

-- ---------------------------------------------------------------------
-- 部署テーブル
-- ---------------------------------------------------------------------
CREATE TABLE department (
    id   SERIAL       PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
COMMENT ON TABLE department IS '部署';

-- ---------------------------------------------------------------------
-- 社員テーブル
-- ---------------------------------------------------------------------
CREATE TABLE employee (
    id            SERIAL       PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    kana          VARCHAR(100) NOT NULL,
    department_id INTEGER      NOT NULL REFERENCES department(id)
);
COMMENT ON TABLE employee IS '社員';

-- ---------------------------------------------------------------------
-- 社員アカウントテーブル
--   ・name(アカウント名)は一意
--   ・1社員につきアカウントは1つ(employee_id 一意)
--   ・password はハッシュ値を格納(BCrypt)
-- ---------------------------------------------------------------------
CREATE TABLE employee_account (
    id          SERIAL       PRIMARY KEY,
    name        VARCHAR(20)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    employee_id INTEGER      NOT NULL UNIQUE REFERENCES employee(id)
);
COMMENT ON TABLE employee_account IS '社員アカウント';

-- ---------------------------------------------------------------------
-- 商品カテゴリテーブル
-- ---------------------------------------------------------------------
CREATE TABLE product_category (
    id   SERIAL      PRIMARY KEY,
    name VARCHAR(30) NOT NULL
);
COMMENT ON TABLE product_category IS '商品カテゴリ';

-- ---------------------------------------------------------------------
-- 商品テーブル
--   ・delete_flg = 1 で論理削除(物理削除は行わない: UC013 備考)
-- ---------------------------------------------------------------------
CREATE TABLE product (
    id                  SERIAL       PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    price               INTEGER      NOT NULL,
    image_url           VARCHAR(200),
    product_category_id INTEGER      NOT NULL REFERENCES product_category(id),
    delete_flg          INTEGER      NOT NULL DEFAULT 0
);
COMMENT ON TABLE product IS '商品';
COMMENT ON COLUMN product.delete_flg IS '削除フラグ(0:有効, 1:削除)';

-- ---------------------------------------------------------------------
-- 商品在庫テーブル
--   ・1商品につき在庫レコードは1つ(product_id 一意)
-- ---------------------------------------------------------------------
CREATE TABLE product_stock (
    id         SERIAL  PRIMARY KEY,
    quantity   INTEGER NOT NULL,
    product_id INTEGER NOT NULL UNIQUE REFERENCES product(id)
);
COMMENT ON TABLE product_stock IS '商品在庫';

-- ---------------------------------------------------------------------
-- 注文ステータステーブル
-- ---------------------------------------------------------------------
CREATE TABLE order_status (
    id   SERIAL       PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
COMMENT ON TABLE order_status IS '注文ステータス';

-- ---------------------------------------------------------------------
-- 支払い方法テーブル
-- ---------------------------------------------------------------------
CREATE TABLE payment_method (
    id   SERIAL       PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
COMMENT ON TABLE payment_method IS '支払い方法';

-- ---------------------------------------------------------------------
-- 顧客(アカウント)テーブル
--   ・mail_address / username は一意
--   ・password はハッシュ値を格納(BCrypt)
-- ---------------------------------------------------------------------
CREATE TABLE customer (
    id           SERIAL       PRIMARY KEY,
    name         VARCHAR(20)  NOT NULL,
    address1     VARCHAR(100) NOT NULL,
    address2     VARCHAR(100),
    phone_number VARCHAR(20)  NOT NULL,
    mail_address VARCHAR(200) NOT NULL UNIQUE,
    username     VARCHAR(30)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE customer IS '顧客(アカウント)';

-- ---------------------------------------------------------------------
-- 注文テーブル
--   ・「order」は予約語のため、テーブル名は複数形 orders を使用
-- ---------------------------------------------------------------------
CREATE TABLE orders (
    id                SERIAL    PRIMARY KEY,
    order_date        TIMESTAMP NOT NULL,
    amount_total      INTEGER   NOT NULL,
    customer_id       INTEGER   NOT NULL REFERENCES customer(id),
    order_status_id   INTEGER   NOT NULL REFERENCES order_status(id),
    payment_method_id INTEGER   NOT NULL REFERENCES payment_method(id)
);
COMMENT ON TABLE orders IS '注文';

-- ---------------------------------------------------------------------
-- 注文明細テーブル
-- ---------------------------------------------------------------------
CREATE TABLE orders_detail (
    id         SERIAL  PRIMARY KEY,
    order_id   INTEGER NOT NULL REFERENCES orders(id),
    product_id INTEGER NOT NULL REFERENCES product(id),
    count      INTEGER NOT NULL
);
COMMENT ON TABLE orders_detail IS '注文明細';

-- 参照用インデックス(検索性能向上)
CREATE INDEX idx_product_category      ON product(product_category_id);
CREATE INDEX idx_orders_customer       ON orders(customer_id);
CREATE INDEX idx_orders_status         ON orders(order_status_id);
CREATE INDEX idx_orders_detail_order   ON orders_detail(order_id);
CREATE INDEX idx_orders_detail_product ON orders_detail(product_id);
