-- =============================================
-- INTERACTIVE KIOSK MANAGEMENT SYSTEM
-- Project II - Computer Engineering Degree
-- IPVC 2024/2025
-- PostgreSQL 15+
-- =============================================

DROP TABLE IF EXISTS daily_metrics       CASCADE;
DROP TABLE IF EXISTS interaction_events  CASCADE;
DROP TABLE IF EXISTS sessions            CASCADE;
DROP TABLE IF EXISTS order_lines         CASCADE;
DROP TABLE IF EXISTS orders              CASCADE;
DROP TABLE IF EXISTS product_store       CASCADE;
DROP TABLE IF EXISTS products            CASCADE;
DROP TABLE IF EXISTS categories          CASCADE;
DROP TABLE IF EXISTS kiosks              CASCADE;
DROP TABLE IF EXISTS stores              CASCADE;
DROP TABLE IF EXISTS users               CASCADE;
DROP TABLE IF EXISTS roles               CASCADE;


-- -----------------------------------------------
-- MODULE 1 — AUTHENTICATION AND ACCESS CONTROL
-- -----------------------------------------------

CREATE TABLE roles (
    id_role     BIGSERIAL    PRIMARY KEY,
    role_name   VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(500)
);

INSERT INTO roles (role_name, description) VALUES
    ('ADMIN',    'Acesso total ao sistema'),
    ('MANAGER',  'Gestão de lojas, produtos e encomendas'),
    ('OPERATOR', 'Gestão operacional de encomendas e quiosques');


-- -----------------------------------------------
-- MODULE 2 — STORES AND KIOSK DEVICES
-- -----------------------------------------------

CREATE TABLE stores (
    id_store      BIGSERIAL    PRIMARY KEY,
    store_name    VARCHAR(200) NOT NULL,
    store_type    VARCHAR(50)  NOT NULL,
    address       VARCHAR(300),
    city          VARCHAR(100),
    postal_code   VARCHAR(10),
    phone         VARCHAR(20),
    description   VARCHAR(1000),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_store_type CHECK (store_type IN ('EYEWEAR', 'MAKEUP', 'JEWELLERY'))
);

INSERT INTO stores (store_name, store_type, address, city, postal_code, phone) VALUES
    ('Óptica Viana Centro',      'EYEWEAR',   'Rua do Poço, 14',               'Viana do Castelo', '4900-512', '258 123 456'),
    ('Glam Beauty Braga',        'MAKEUP',    'Avenida Central, 82',           'Braga',            '4710-229', '253 987 654'),
    ('Ourivesaria Prata & Ouro', 'JEWELLERY', 'Rua de Santa Catarina, 201',    'Porto',            '4000-447', '222 345 678');

CREATE TABLE users (
    id_user       BIGSERIAL    PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(200) NOT NULL UNIQUE,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    id_role       BIGINT       NOT NULL REFERENCES roles(id_role),
    id_store      BIGINT       REFERENCES stores(id_store)
);

-- passwords: admin/admin123 | joao.silva/manager123 | ana.costa,rui.mendes/operator123
INSERT INTO users (username, password_hash, email, id_role, id_store) VALUES
    ('admin',      '$2a$10$C6aEz7XnILve/jzKgh7dJOp02qMmkIgueR3liuN4JMI28QVAvTGTG', 'admin@kiosks.pt',      1, NULL),
    ('joao.silva', '$2a$10$ASElDgZufxVJs/mUz5ztquNdf/0e/lUxMNB8ytgHrjRcSZ5PbPdbe', 'joao.silva@kiosks.pt', 2, 1),
    ('ana.costa',  '$2a$10$6k2MrJ8VwCqcWEvb9z011.xl.xeL4rJl//ZgnIe01u68xxsV.FAW.', 'ana.costa@kiosks.pt',  3, 2),
    ('rui.mendes', '$2a$10$6k2MrJ8VwCqcWEvb9z011.xl.xeL4rJl//ZgnIe01u68xxsV.FAW.', 'rui.mendes@kiosks.pt', 3, 2);


CREATE TABLE kiosks (
    id_kiosk          BIGSERIAL    PRIMARY KEY,
    kiosk_name        VARCHAR(100) NOT NULL,
    serial_number     VARCHAR(100) UNIQUE,
    model             VARCHAR(100),
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    installation_date DATE,
    last_connection   TIMESTAMP,
    id_store          BIGINT       NOT NULL REFERENCES stores(id_store),

    CONSTRAINT chk_kiosk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE'))
);

INSERT INTO kiosks (kiosk_name, serial_number, model, installation_date, last_connection, id_store) VALUES
    ('Quiosque Entrada Viana',   'SN-VIA-001', 'TouchPro X3', '2024-03-10', NOW() - INTERVAL '2 hours',     1),
    ('Quiosque Interior Viana',  'SN-VIA-002', 'TouchPro X3', '2024-03-10', NOW() - INTERVAL '5 hours',     1),
    ('Quiosque Braga Principal', 'SN-BRG-001', 'TouchPro X3', '2024-04-15', NOW() - INTERVAL '1 hour',      2),
    ('Quiosque Porto Centro',    'SN-PRT-001', 'TouchPro X3', '2024-05-20', NOW() - INTERVAL '30 minutes',  3);


-- -----------------------------------------------
-- MODULE 3 — PRODUCT CATALOGUE
-- -----------------------------------------------

CREATE TABLE categories (
    id_category   BIGSERIAL    PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description   VARCHAR(500),
    display_order INT          NOT NULL DEFAULT 0,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    id_store      BIGINT       NOT NULL REFERENCES stores(id_store)
);

INSERT INTO categories (category_name, display_order, id_store) VALUES
    -- Óptica Viana
    ('Óculos de Sol',      1, 1),
    ('Óculos de Grau',     2, 1),
    ('Lentes de Contacto', 3, 1),
    -- Glam Beauty
    ('Lábios',             1, 2),
    ('Olhos',              2, 2),
    ('Rosto',              3, 2),
    ('Cuidado de Pele',    4, 2),
    -- Ourivesaria
    ('Colares',            1, 3),
    ('Pulseiras',          2, 3),
    ('Brincos',            3, 3),
    ('Anéis',              4, 3);


-- products: sem id_store e sem stock_quantity (stock fica em product_store)
CREATE TABLE products (
    id_product   BIGSERIAL      PRIMARY KEY,
    product_name VARCHAR(200)   NOT NULL,
    description  VARCHAR(1000),
    price        NUMERIC(10,2)  NOT NULL CHECK (price >= 0),
    image_url    VARCHAR(500),
    sku          VARCHAR(50)    UNIQUE,
    active       BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP      NOT NULL DEFAULT NOW(),
    id_category  BIGINT         NOT NULL REFERENCES categories(id_category)
);

INSERT INTO products (product_name, description, price, sku, id_category) VALUES
    -- Óculos de Sol (cat 1)
    ('Ray-Ban Aviator Classic RB3025',  'Armação dourada, lentes verdes G-15. Clássico atemporal.',          149.90, 'OPT-SOL-001', 1),
    ('Oakley Holbrook OO9102',          'Armação acetato preta, lentes Prizm polarizadas.',                   119.00, 'OPT-SOL-002', 1),
    ('Polaroid PLD 2134/S',             'Armação tartaruga, lentes polarizadas castanho degradê.',             69.90, 'OPT-SOL-003', 1),
    ('Silhouette Titan Minimal',        'Sem aro, ultra-leves 1.8g, estilo minimalista austríaco.',           289.00, 'OPT-SOL-004', 1),
    -- Óculos de Grau (cat 2)
    ('Ray-Ban RX5228 Wayfarer',         'Acetato preto, montura quadrada. Inclui estojo e pano.',              99.90, 'OPT-GRU-001', 2),
    ('Zeiss Single Vision DuraVision',  'Lentes monofocais com revestimento anti-reflexo e anti-risco.',      189.00, 'OPT-GRU-002', 2),
    ('Lindberg Strip Titanium',         'Armação titânio sem parafusos, design dinamarquês premiado.',        349.00, 'OPT-GRU-003', 2),
    -- Lentes de Contacto (cat 3)
    ('Acuvue Oasys 1-Day (30 un.)',     'Lentes diárias com HydraLuxe, alta transmissão de oxigênio.',         32.50, 'OPT-LEN-001', 3),
    ('Dailies Total 1 (90 un.)',        'Lentes diárias com gradiente de água, máximo conforto.',              79.90, 'OPT-LEN-002', 3),

    -- Lábios (cat 4)
    ('MAC Ruby Woo Lipstick',           'Batom vermelho mate icónico, fórmula retro matte duradoura.',          22.00, 'GLM-LAB-001', 4),
    ('Charlotte Tilbury Pillow Talk',   'Batom nude-rosado, acabamento semimate, fórmula hidratante.',         36.00, 'GLM-LAB-002', 4),
    ('NYX Lip Liner Nude',              'Lápis labial tom nude, longa duração, fácil aplicação.',               9.90, 'GLM-LAB-003', 4),
    -- Olhos (cat 5)
    ('Maybelline Sky High Mascara',     'Rímel preto intenso, fórmula flexível, cílios alongados.',            14.90, 'GLM-OLH-001', 5),
    ('Urban Decay Naked Palette',       'Paleta 12 sombras nude-smoky, pigmentação intensa.',                   54.00, 'GLM-OLH-002', 5),
    ('Benefit Precisely My Brow',       'Lápis de sobrancelha micro-preciso, 5 tons disponíveis.',             26.00, 'GLM-OLH-003', 5),
    -- Rosto (cat 6)
    ('Charlotte Tilbury Flawless Filter','Base iluminadora multifuncional, acabamento glowy natural.',         48.00, 'GLM-ROS-001', 6),
    ('NARS Radiant Creamy Concealer',   'Corretor de alta cobertura, 30 tons, acabamento luminoso.',           33.00, 'GLM-ROS-002', 6),
    -- Cuidado de Pele (cat 7)
    ('La Roche-Posay Anthelios SPF50+', 'Protetor solar facial, textura ultra-fluida, resistente à água.',    22.90, 'GLM-PEL-001', 7),
    ('Bioderma Sensibio H2O (500ml)',   'Água micelar para pele sensível, desmaquilhante suave.',              18.50, 'GLM-PEL-002', 7),

    -- Colares (cat 8)
    ('Colar Prata 925 Coração',         'Prata 925, pendente coração com zircónia, cadeia 45cm.',              65.00, 'JOI-COL-001', 8),
    ('Colar Ouro 19k Veneziana',        'Ouro 19 quilates, malha veneziana, comprimento 50cm.',               189.00, 'JOI-COL-002', 8),
    ('Colar Prata com Pérola Natural',  'Prata 925 com pendente de pérola de água doce natural.',              89.00, 'JOI-COL-003', 8),
    -- Pulseiras (cat 9)
    ('Pulseira Ouro Branco 19k',        'Ouro branco 19k, malha rolo, 19cm. Fecho borboleta.',                229.00, 'JOI-PUL-001', 9),
    ('Pulseira Prata Rígida Lisa',      'Prata 925 polida, bangle rígido, tamanho ajustável.',                 55.00, 'JOI-PUL-002', 9),
    -- Brincos (cat 10) — exemplo de produto em DUAS lojas (Glam Beauty e Ourivesaria)
    ('Brincos Prata Argola 30mm',       'Prata 925 polida, argola clássica 30mm, fecho pressão.',              35.00, 'JOI-BRI-001', 10),
    ('Brincos Ouro Pérola Akoya',       'Ouro 19k com pérola Akoya 7mm, fecho roseta.',                      145.00, 'JOI-BRI-002', 10),
    -- Anéis (cat 11)
    ('Anel Solitário Prata Zircónia',   'Prata 925, zircónia 1ct, acabamento ródio. Tamanhos 14-22.',         79.00, 'JOI-ANE-001', 11),
    ('Anel Aliança Ouro 19k',           'Ouro 19k, largura 4mm, polido, ideal para aliança de casamento.',    310.00, 'JOI-ANE-002', 11);


-- tabela de associação produto ↔ loja com stock e estado independentes
CREATE TABLE product_store (
    id_product      BIGINT   NOT NULL REFERENCES products(id_product) ON DELETE CASCADE,
    id_store        BIGINT   NOT NULL REFERENCES stores(id_store)    ON DELETE CASCADE,
    stock_quantity  INT      NOT NULL DEFAULT 0,
    active          BOOLEAN  NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_product, id_store)
);

-- Óptica Viana Centro (loja 1)
INSERT INTO product_store (id_product, id_store, stock_quantity) VALUES
    (1,  1, 12), (2,  1, 8),  (3,  1, 15), (4,  1, 3),
    (5,  1, 10), (6,  1, 6),  (7,  1, 2),
    (8,  1, 50), (9,  1, 30);

-- Glam Beauty Braga (loja 2)
INSERT INTO product_store (id_product, id_store, stock_quantity) VALUES
    (10, 2, 20), (11, 2, 18), (12, 2, 35),
    (13, 2, 25), (14, 2, 7),  (15, 2, 12),
    (16, 2, 9),  (17, 2, 14),
    (18, 2, 22), (19, 2, 16),
    -- Brincos Argola também na Glam (stock próprio)
    (25, 2, 5);

-- Ourivesaria Prata & Ouro (loja 3)
INSERT INTO product_store (id_product, id_store, stock_quantity) VALUES
    (20, 3, 8),  (21, 3, 4),  (22, 3, 6),
    (23, 3, 3),  (24, 3, 11),
    (25, 3, 8),  (26, 3, 5),
    (27, 3, 14), (28, 3, 2);


-- -----------------------------------------------
-- MODULE 4 — ORDERS
-- -----------------------------------------------

CREATE TABLE orders (
    id_order    BIGSERIAL      PRIMARY KEY,
    reference   VARCHAR(20)    NOT NULL UNIQUE,
    status      VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    order_total NUMERIC(10,2)  NOT NULL CHECK (order_total >= 0),
    notes       VARCHAR(500),
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP,
    id_kiosk    BIGINT         NOT NULL REFERENCES kiosks(id_kiosk),

    CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'READY', 'COLLECTED', 'CANCELLED'))
);

CREATE TABLE order_lines (
    id_line           BIGSERIAL     PRIMARY KEY,
    quantity          INT           NOT NULL DEFAULT 1 CHECK (quantity >= 1),
    unit_price        NUMERIC(10,2) NOT NULL CHECK (unit_price >= 0),
    line_total        NUMERIC(10,2) NOT NULL CHECK (line_total >= 0),
    product_name_snap VARCHAR(200)  NOT NULL,
    id_order          BIGINT        NOT NULL REFERENCES orders(id_order),
    id_product        BIGINT        REFERENCES products(id_product) ON DELETE SET NULL
);

INSERT INTO orders (reference, status, order_total, created_at, id_kiosk) VALUES
    ('ORD-2026-A1B2C3D4', 'PENDING',   149.90, NOW() - INTERVAL '10 minutes',  1),
    ('ORD-2026-E5F6G7H8', 'PENDING',   219.80, NOW() - INTERVAL '25 minutes',  1),
    ('ORD-2026-I9J0K1L2', 'READY',     189.00, NOW() - INTERVAL '1 hour',      2),
    ('ORD-2026-M3N4O5P6', 'COLLECTED', 469.00, NOW() - INTERVAL '3 hours',     2),
    ('ORD-2026-Q7R8S9T0', 'PENDING',    36.90, NOW() - INTERVAL '5 minutes',   3),
    ('ORD-2026-U1V2W3X4', 'READY',     108.00, NOW() - INTERVAL '45 minutes',  3),
    ('ORD-2026-Y5Z6A7B8', 'CANCELLED',  22.00, NOW() - INTERVAL '2 hours',     3),
    ('ORD-2026-C9D0E1F2', 'COLLECTED', 320.00, NOW() - INTERVAL '6 hours',     4),
    ('ORD-2026-G3H4I5J6', 'PENDING',   254.00, NOW() - INTERVAL '15 minutes',  4),
    ('ORD-2026-K7L8M9N0', 'PENDING',    79.00, NOW() - INTERVAL '8 minutes',   4);

INSERT INTO order_lines (quantity, unit_price, line_total, product_name_snap, id_order, id_product) VALUES
    (1, 149.90, 149.90, 'Ray-Ban Aviator Classic RB3025',    1, 1),
    (1, 119.00, 119.00, 'Oakley Holbrook OO9102',            2, 2),
    (1, 100.80, 100.80, 'Acuvue Oasys 1-Day (30 un.)',       2, 8),
    (1, 189.00, 189.00, 'Zeiss Single Vision DuraVision',    3, 6),
    (1, 349.00, 349.00, 'Lindberg Strip Titanium',           4, 7),
    (1, 120.00, 120.00, 'Colar Prata 925 Coração',           4, 20),
    (1,  36.00,  36.00, 'Charlotte Tilbury Pillow Talk',     5, 11),
    (1,   0.90,   0.90, 'NYX Lip Liner Nude',                5, 12),
    (2,  54.00, 108.00, 'Urban Decay Naked Palette',         6, 14),
    (1,  22.00,  22.00, 'MAC Ruby Woo Lipstick',             7, 10),
    (1, 310.00, 310.00, 'Anel Aliança Ouro 19k',             8, 28),
    (1, 229.00, 229.00, 'Pulseira Ouro Branco 19k',          9, 23),
    (1,  25.00,  25.00, 'Colar Prata com Pérola Natural',    9, 22),
    (1,  79.00,  79.00, 'Anel Solitário Prata Zircónia',    10, 27);


-- -----------------------------------------------
-- MODULE 5 — SESSIONS AND ANALYTICS
-- -----------------------------------------------

CREATE TABLE sessions (
    id_session   BIGSERIAL  PRIMARY KEY,
    started_at   TIMESTAMP  NOT NULL DEFAULT NOW(),
    ended_at     TIMESTAMP,
    duration_sec INT        CHECK (duration_sec >= 0),
    id_kiosk     BIGINT     NOT NULL REFERENCES kiosks(id_kiosk)
);

INSERT INTO sessions (started_at, ended_at, duration_sec, id_kiosk) VALUES
    (NOW() - INTERVAL '2 hours',    NOW() - INTERVAL '1 hour 50 min',  600,  1),
    (NOW() - INTERVAL '1 hour',     NOW() - INTERVAL '50 minutes',     580,  1),
    (NOW() - INTERVAL '3 hours',    NOW() - INTERVAL '2 hours 45 min', 900,  2),
    (NOW() - INTERVAL '30 minutes', NULL,                               NULL, 3),
    (NOW() - INTERVAL '4 hours',    NOW() - INTERVAL '3 hours 30 min', 1800, 4);


CREATE TABLE interaction_events (
    id_event     BIGSERIAL    PRIMARY KEY,
    event_type   VARCHAR(50)  NOT NULL,
    occurred_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    duration_sec INT          CHECK (duration_sec >= 0),
    extra_data   VARCHAR(1000),
    id_session   BIGINT       NOT NULL REFERENCES sessions(id_session),
    id_product   BIGINT       REFERENCES products(id_product) ON DELETE SET NULL,

    CONSTRAINT chk_event_type CHECK (event_type IN ('VIEW', 'ADD_TO_CART', 'ORDER_CREATED', 'EXIT'))
);

INSERT INTO interaction_events (event_type, occurred_at, duration_sec, id_session, id_product) VALUES
    ('VIEW',          NOW() - INTERVAL '2 hours',         12, 1, 1),
    ('ADD_TO_CART',   NOW() - INTERVAL '1 hour 59 min',    3, 1, 1),
    ('ORDER_CREATED', NOW() - INTERVAL '1 hour 58 min',    5, 1, NULL),
    ('VIEW',          NOW() - INTERVAL '1 hour',           18, 2, 2),
    ('VIEW',          NOW() - INTERVAL '59 minutes',       22, 2, 3),
    ('ADD_TO_CART',   NOW() - INTERVAL '58 minutes',        4, 2, 2),
    ('ORDER_CREATED', NOW() - INTERVAL '57 minutes',        6, 2, NULL),
    ('EXIT',          NOW() - INTERVAL '50 minutes',        2, 2, NULL);


-- -----------------------------------------------
-- MODULE 6 — PRE-CALCULATED METRICS
-- -----------------------------------------------

CREATE TABLE daily_metrics (
    id_metric        BIGSERIAL     PRIMARY KEY,
    reference_date   DATE          NOT NULL,
    total_sessions   INT           NOT NULL DEFAULT 0,
    total_orders     INT           NOT NULL DEFAULT 0,
    total_events     INT           NOT NULL DEFAULT 0,
    avg_duration_sec NUMERIC(10,2),
    total_revenue    NUMERIC(10,2),
    id_kiosk         BIGINT        NOT NULL REFERENCES kiosks(id_kiosk),
    id_top_product   BIGINT        REFERENCES products(id_product) ON DELETE SET NULL,

    CONSTRAINT uq_metric_kiosk_date UNIQUE (id_kiosk, reference_date)
);
