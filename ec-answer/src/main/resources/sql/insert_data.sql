-- =====================================================================
-- フルネス文具 EC システム : 初期データ投入(マスタ＋動作確認用サンプル)
-- ---------------------------------------------------------------------
-- 対象   : PostgreSQL 17 / データベース "fullness-ecx"
-- 実行例 : psql -U fullness -h localhost -d "fullness-ecx" -f V2__insert_master_data.sql
-- 内容   :
--   1) マスタ    : 部署 / 社員 / 社員アカウント / 注文ステータス /
--                  支払い方法 / 商品カテゴリ
--   2) サンプル  : 商品 / 在庫 / 顧客 / 注文 / 注文明細
--                  (UC015 購入履歴検索・UC016 注文ステータス更新の
--                   動作確認用。設計書の画面モックに合わせた値)
-- 認証情報 :
--   ・担当者ログイン  … アカウント名: fullness / パスワード: fullness123
--   ・顧客ログイン    … メール: testuser@example.com / パスワード: fullness123
--   ※ password 列は BCrypt ハッシュ($2b$10$...)を格納しています。
-- ---------------------------------------------------------------------
-- Flyway を使う場合は本ファイルを
--   src/main/resources/db/migration/V2__insert_master_data.sql
-- に配置すればアプリ起動時に自動適用されます。
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. 部署
-- ---------------------------------------------------------------------
INSERT INTO department (id, name) VALUES
    (1, '販売管理部');

-- ---------------------------------------------------------------------
-- 2. 社員
--    ※ 社員2・3 はアカウント未作成。担当者アカウント登録(UC009/BP003)の
--       「アカウント未作成の社員のみ選択肢に表示」動作確認用。
-- ---------------------------------------------------------------------
INSERT INTO employee (id, name, kana, department_id) VALUES
    (1, 'フルネス太郎', 'フルネスタロウ', 1),
    (2, 'フルネス花子', 'フルネスハナコ', 1),
    (3, 'フルネス次郎', 'フルネスジロウ', 1);

-- ---------------------------------------------------------------------
-- 3. 社員アカウント
--    password = BCrypt('fullness123')
-- ---------------------------------------------------------------------
INSERT INTO employee_account (id, name, password, employee_id) VALUES
    (1, 'fullness', '$2b$10$3yyBaOF6W34X4L.rXhYK1.vwzcUTomCfwIeRnhz1F11wS8h.b0U42', 1);

-- ---------------------------------------------------------------------
-- 4. 注文ステータス (UC016: 注文済 → 入金済 → 配送中 → 完了)
-- ---------------------------------------------------------------------
INSERT INTO order_status (id, name) VALUES
    (1, '注文済'),
    (2, '入金済'),
    (3, '配送中'),
    (4, '完了');

-- ---------------------------------------------------------------------
-- 5. 支払い方法 (現時点では「現金」のみ)
-- ---------------------------------------------------------------------
INSERT INTO payment_method (id, name) VALUES
    (1, '現金');

-- ---------------------------------------------------------------------
-- 6. 商品カテゴリ
-- ---------------------------------------------------------------------
INSERT INTO product_category (id, name) VALUES
    (1, '文房具'),
    (2, 'ノート'),
    (3, 'ファイル');

-- ---------------------------------------------------------------------
-- 7. 商品 (設計書モックの文房具を採用)
-- ---------------------------------------------------------------------
INSERT INTO product (id, name, price, image_url, product_category_id, delete_flg) VALUES
    (1, '水性ボールペン(黒)', 120, '/images/pen_black.png',  1, 0),
    (2, '水性ボールペン(赤)', 120, '/images/pen_red.png',    1, 0),
    (3, '水性ボールペン(青)', 120, '/images/pen_blue.png',   1, 0),
    (4, '油性ボールペン(黒)', 100, '/images/oilpen_black.png',1, 0),
    (5, '鉛筆(黒)',          100, '/images/pencil_black.png',1, 0);

-- ---------------------------------------------------------------------
-- 8. 商品在庫
-- ---------------------------------------------------------------------
INSERT INTO product_stock (id, quantity, product_id) VALUES
    (1, 10, 1),
    (2, 10, 2),
    (3, 10, 3),
    (4, 10, 4),
    (5, 10, 5);

-- ---------------------------------------------------------------------
-- 9. 顧客
--    password = BCrypt('fullness123')
-- ---------------------------------------------------------------------
INSERT INTO customer (id, name, address1, address2, phone_number, mail_address, username, password, created_at) VALUES
    (1, 'テスト顧客', '東京都新宿区', 'テストビル101', '090-1234-5678',
        'testuser@example.com', 'testuser',
        '$2b$10$3yyBaOF6W34X4L.rXhYK1.vwzcUTomCfwIeRnhz1F11wS8h.b0U42',
        '2024-05-01 09:00:00');

-- ---------------------------------------------------------------------
-- 10. 注文 (BP015 購入履歴検索の画面モックに合わせたサンプル)
--     order_status_id : 3=配送中, 4=完了 / payment_method_id : 1=現金
-- ---------------------------------------------------------------------
INSERT INTO orders (id, order_date, amount_total, customer_id, order_status_id, payment_method_id) VALUES
    (1, '2024-05-10 10:00:00', 340, 1, 4, 1),
    (2, '2024-05-12 15:30:00', 100, 1, 3, 1),
    (3, '2024-05-13 09:00:00', 120, 1, 4, 1);

-- ---------------------------------------------------------------------
-- 11. 注文明細
--     注文1: 水性ボールペン(黒)x2 + 油性ボールペン(黒)x1 = 340円
--     注文2: 鉛筆(黒)x1 = 100円
--     注文3: 水性ボールペン(赤)x1 = 120円
-- ---------------------------------------------------------------------
INSERT INTO orders_detail (id, order_id, product_id, count) VALUES
    (1, 1, 1, 2),
    (2, 1, 4, 1),
    (3, 2, 5, 1),
    (4, 3, 2, 1);

-- ---------------------------------------------------------------------
-- 12. SERIAL(自動採番)シーケンスの現在値を、投入済みの最大IDに合わせる
--     ※ 明示的に id を指定して INSERT したため、次回の自動採番が
--        重複しないようシーケンスを更新します。
-- ---------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('department',       'id'), (SELECT MAX(id) FROM department));
SELECT setval(pg_get_serial_sequence('employee',         'id'), (SELECT MAX(id) FROM employee));
SELECT setval(pg_get_serial_sequence('employee_account', 'id'), (SELECT MAX(id) FROM employee_account));
SELECT setval(pg_get_serial_sequence('order_status',     'id'), (SELECT MAX(id) FROM order_status));
SELECT setval(pg_get_serial_sequence('payment_method',   'id'), (SELECT MAX(id) FROM payment_method));
SELECT setval(pg_get_serial_sequence('product_category', 'id'), (SELECT MAX(id) FROM product_category));
SELECT setval(pg_get_serial_sequence('product',          'id'), (SELECT MAX(id) FROM product));
SELECT setval(pg_get_serial_sequence('product_stock',    'id'), (SELECT MAX(id) FROM product_stock));
SELECT setval(pg_get_serial_sequence('customer',         'id'), (SELECT MAX(id) FROM customer));
SELECT setval(pg_get_serial_sequence('orders',           'id'), (SELECT MAX(id) FROM orders));
SELECT setval(pg_get_serial_sequence('orders_detail',    'id'), (SELECT MAX(id) FROM orders_detail));
