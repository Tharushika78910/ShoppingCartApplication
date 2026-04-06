CREATE DATABASE IF NOT EXISTS shopping_cart_localization
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;

CREATE TABLE IF NOT EXISTS cart_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    total_items INT NOT NULL,
    total_cost DOUBLE NOT NULL,
    language VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_record_id INT,
    item_number INT NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    subtotal DOUBLE NOT NULL,
    FOREIGN KEY (cart_record_id) REFERENCES cart_records(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS localization_strings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    language VARCHAR(10) NOT NULL
);


USE shopping_cart_localization;

INSERT INTO localization_strings (`key`, value, language) VALUES
('app.title', 'Shopping Cart', 'en_US'),
('language.label', 'Select Language', 'en_US'),
('confirm.language', 'Confirm Language', 'en_US'),
('number.of.items', 'Number of Items', 'en_US'),
('enter.items', 'Enter Items', 'en_US'),
('item.label', 'Item', 'en_US'),
('enter.price', 'Enter price', 'en_US'),
('enter.quantity', 'Enter quantity', 'en_US'),
('item.total', 'Item Total:', 'en_US'),
('cart.total', 'Cart Total:', 'en_US'),
('error.invalid.integer', 'Please enter a valid integer.', 'en_US'),
('error.number.gt.zero', 'Please enter a number greater than 0.', 'en_US'),
('error.invalid.price.quantity', 'Please enter valid numbers for price and quantity.', 'en_US'),

('app.title', 'Varukorg', 'sv_SE'),
('language.label', 'Välj språk', 'sv_SE'),
('confirm.language', 'Bekräfta språk', 'sv_SE'),
('number.of.items', 'Antal artiklar', 'sv_SE'),
('enter.items', 'Mata in artiklar', 'sv_SE'),
('item.label', 'Artikel', 'sv_SE'),
('enter.price', 'Ange pris', 'sv_SE'),
('enter.quantity', 'Ange antal', 'sv_SE'),
('item.total', 'Artikelsumma:', 'sv_SE'),
('cart.total', 'Varukorgssumma:', 'sv_SE'),
('error.invalid.integer', 'Ange ett giltigt heltal.', 'sv_SE'),
('error.number.gt.zero', 'Ange ett nummer större än 0.', 'sv_SE'),
('error.invalid.price.quantity', 'Ange giltiga värden för pris och antal.', 'sv_SE'),

('app.title', 'Ostoskori', 'fi_FI'),
('language.label', 'Valitse kieli', 'fi_FI'),
('confirm.language', 'Vahvista kieli', 'fi_FI'),
('number.of.items', 'Tuotteiden määrä', 'fi_FI'),
('enter.items', 'Syötä tuotteet', 'fi_FI'),
('item.label', 'Tuote', 'fi_FI'),
('enter.price', 'Anna hinta', 'fi_FI'),
('enter.quantity', 'Anna määrä', 'fi_FI'),
('item.total', 'Tuotteen summa:', 'fi_FI'),
('cart.total', 'Ostoskorin summa:', 'fi_FI'),
('error.invalid.integer', 'Anna kelvollinen kokonaisluku.', 'fi_FI'),
('error.number.gt.zero', 'Anna numero, joka on suurempi kuin 0.', 'fi_FI'),
('error.invalid.price.quantity', 'Anna kelvolliset hinnan ja määrän arvot.', 'fi_FI'),

('app.title', 'ショッピングカート', 'ja_JP'),
('language.label', '言語を選択', 'ja_JP'),
('confirm.language', '言語を確認', 'ja_JP'),
('number.of.items', '商品の数', 'ja_JP'),
('enter.items', '商品を入力', 'ja_JP'),
('item.label', '商品', 'ja_JP'),
('enter.price', '価格を入力', 'ja_JP'),
('enter.quantity', '数量を入力', 'ja_JP'),
('item.total', '商品合計:', 'ja_JP'),
('cart.total', 'カート合計:', 'ja_JP'),
('error.invalid.integer', '有効な整数を入力してください。', 'ja_JP'),
('error.number.gt.zero', '0より大きい数を入力してください。', 'ja_JP'),
('error.invalid.price.quantity', '価格と数量に有効な数値を入力してください。', 'ja_JP'),

('app.title', 'عربة التسوق', 'ar_AR'),
('language.label', 'اختر اللغة', 'ar_AR'),
('confirm.language', 'تأكيد اللغة', 'ar_AR'),
('number.of.items', 'عدد العناصر', 'ar_AR'),
('enter.items', 'إدخال العناصر', 'ar_AR'),
('item.label', 'العنصر', 'ar_AR'),
('enter.price', 'أدخل السعر', 'ar_AR'),
('enter.quantity', 'أدخل الكمية', 'ar_AR'),
('item.total', 'إجمالي العنصر:', 'ar_AR'),
('cart.total', 'إجمالي السلة:', 'ar_AR'),
('error.invalid.integer', 'يرجى إدخال عدد صحيح صالح.', 'ar_AR'),
('error.number.gt.zero', 'يرجى إدخال رقم أكبر من 0.', 'ar_AR'),
('error.invalid.price.quantity', 'يرجى إدخال قيم صحيحة للسعر والكمية.', 'ar_AR');

USE shopping_cart_localization;

INSERT INTO localization_strings (`key`, value, language) VALUES
('calculate.total', 'Calculate Total', 'en_US'),
('calculate.total', 'Laske yhteensä', 'fi_FI'),
('calculate.total', 'Beräkna total', 'sv_SE'),
('calculate.total', '合計を計算', 'ja_JP'),
('calculate.total', 'احسب الإجمالي', 'ar_AR');
