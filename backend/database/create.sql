-- ============================================
-- Event Management App — Andmebaasi skeem
-- Versioon 2: PDF spetsifikatsiooni järgi
-- ============================================

-- ============================================
-- Tabelid
-- ============================================

-- Linnad (hallatakse Admin'is)
CREATE TABLE cities (
    id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name  VARCHAR(100) NOT NULL UNIQUE
);

-- Kasutajad
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name     VARCHAR(100)         NOT NULL,
    email         VARCHAR(255)         NOT NULL UNIQUE,
    password_hash VARCHAR(255)         NOT NULL,
    phone         VARCHAR(20),
    description   TEXT,
    role          VARCHAR(20)          NOT NULL DEFAULT 'USER'
                      CHECK (role IN ('USER', 'ADMIN')),
    is_organizer  BOOLEAN              NOT NULL DEFAULT FALSE,
    status        VARCHAR(30)          NOT NULL DEFAULT 'ACTIVE'
                      CHECK (status IN ('ACTIVE', 'PENDING_ACTIVATION', 'DELETED')),
    created_at    TIMESTAMP            NOT NULL DEFAULT NOW()
);

-- Oskuse-sildid
CREATE TABLE skill_tags (
    id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name  VARCHAR(50) NOT NULL UNIQUE
);

-- Sündmused
CREATE TABLE events (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizer_id      UUID         NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    city_id           UUID         NOT NULL REFERENCES cities(id) ON DELETE RESTRICT,
    title             VARCHAR(200) NOT NULL,
    description       TEXT,
    address           VARCHAR(255),
    event_date        DATE         NOT NULL,
    start_time        TIME         NOT NULL,
    end_time          TIME         NOT NULL,
    max_participants  INT          CHECK (max_participants > 0),
    banner_image_url  VARCHAR(500),
    is_cancelled      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Sündmuse oskuse-sildid (M:N)
CREATE TABLE event_skill_tags (
    event_id      UUID NOT NULL REFERENCES events(id)     ON DELETE CASCADE,
    skill_tag_id  UUID NOT NULL REFERENCES skill_tags(id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, skill_tag_id)
);

-- Registreerimised
CREATE TABLE registrations (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID        NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    event_id       UUID        NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    status         VARCHAR(20) NOT NULL DEFAULT 'LAHEB'
                       CHECK (status IN ('LAHEB', 'VOIB_OLLA', 'EI_LAHE')),
    registered_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, event_id)
);

-- Kommentaarid (toetab vastuseid läbi parent_comment_id)
CREATE TABLE comments (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id           UUID      NOT NULL REFERENCES events(id)   ON DELETE CASCADE,
    user_id            UUID      NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    parent_comment_id  UUID               REFERENCES comments(id) ON DELETE CASCADE,
    content            TEXT      NOT NULL,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================
-- E-commerce
-- ============================================

-- Tooted
CREATE TABLE products (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200)   NOT NULL,
    description     TEXT,
    price           NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    image_url       VARCHAR(500),
    stock_quantity  INT            NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    created_at      TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Ostukorvid (üks kasutaja kohta)
CREATE TABLE carts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID      NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Ostukorvi tooted
CREATE TABLE cart_items (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id     UUID NOT NULL REFERENCES carts(id)    ON DELETE CASCADE,
    product_id  UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity    INT  NOT NULL CHECK (quantity > 0),
    UNIQUE (cart_id, product_id)
);

-- Tellimused
CREATE TABLE orders (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id              UUID           REFERENCES users(id) ON DELETE SET NULL,
    status               VARCHAR(30)    NOT NULL DEFAULT 'PENDING'
                             CHECK (status IN ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    subtotal             NUMERIC(10, 2) NOT NULL,
    shipping             NUMERIC(10, 2) NOT NULL DEFAULT 0,
    tax                  NUMERIC(10, 2) NOT NULL DEFAULT 0,
    total                NUMERIC(10, 2) NOT NULL,
    -- Arveldus & transport (PDF lk 12)
    billing_first_name   VARCHAR(50)    NOT NULL,
    billing_last_name    VARCHAR(50)    NOT NULL,
    billing_company      VARCHAR(100),
    billing_country      VARCHAR(50)    NOT NULL,
    billing_street       VARCHAR(200)   NOT NULL,
    billing_postal_code  VARCHAR(20)    NOT NULL,
    billing_city         VARCHAR(100)   NOT NULL,
    billing_phone        VARCHAR(20)    NOT NULL,
    billing_email        VARCHAR(255)   NOT NULL,
    created_at           TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Tellimuse read (snapshot ostuhetkest)
CREATE TABLE order_items (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id           UUID           NOT NULL REFERENCES orders(id)   ON DELETE CASCADE,
    product_id         UUID                    REFERENCES products(id) ON DELETE SET NULL,
    product_name       VARCHAR(200)   NOT NULL,
    quantity           INT            NOT NULL CHECK (quantity > 0),
    price_at_purchase  NUMERIC(10, 2) NOT NULL
);

-- ============================================
-- Indeksid
-- ============================================

CREATE INDEX idx_events_organizer        ON events(organizer_id);
CREATE INDEX idx_events_date             ON events(event_date);
CREATE INDEX idx_events_city             ON events(city_id);
CREATE INDEX idx_registrations_user      ON registrations(user_id);
CREATE INDEX idx_registrations_event     ON registrations(event_id);
CREATE INDEX idx_event_skill_tags_event  ON event_skill_tags(event_id);
CREATE INDEX idx_event_skill_tags_tag    ON event_skill_tags(skill_tag_id);
CREATE INDEX idx_comments_event          ON comments(event_id);
CREATE INDEX idx_comments_parent         ON comments(parent_comment_id);
CREATE INDEX idx_cart_items_cart         ON cart_items(cart_id);
CREATE INDEX idx_order_items_order       ON order_items(order_id);
CREATE INDEX idx_orders_user             ON orders(user_id);
