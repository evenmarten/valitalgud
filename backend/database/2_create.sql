-- ============================================
-- Event Management App — Andmebaasi skeem
-- Versioon 2: PDF spetsifikatsiooni järgi
-- ============================================

-- ============================================
-- Tabelid
-- ============================================

-- Linnad (hallatakse Admin'is)
CREATE TABLE cities (
                        id    SERIAL PRIMARY KEY,
                        name  VARCHAR(100) NOT NULL UNIQUE
);

-- Rollid
CREATE TABLE roles (
                       id    SERIAL PRIMARY KEY,
                       name  VARCHAR(20) NOT NULL UNIQUE
);

-- Kasutajad
CREATE TABLE users (
                       id            SERIAL PRIMARY KEY,
                       password_hash VARCHAR(255)         NOT NULL,
                       role_id       INTEGER              NOT NULL DEFAULT 1 REFERENCES roles(id),
                       is_organizer  BOOLEAN              NOT NULL DEFAULT FALSE,
                       status        VARCHAR(30)          NOT NULL DEFAULT 'ACTIVE'
                           CHECK (status IN ('ACTIVE', 'PENDING_ACTIVATION', 'DELETED')),
                       created_at    TIMESTAMP            NOT NULL DEFAULT NOW()
);

-- Kontaktandmed
CREATE TABLE contacts (
                          id         SERIAL PRIMARY KEY,
                          user_id    INTEGER      NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                          full_name  VARCHAR(100) NOT NULL,
                          email      VARCHAR(255) NOT NULL UNIQUE,
                          phone      VARCHAR(20)
);

-- Oskuse-sildid
CREATE TABLE skill_tags (
                            id    SERIAL PRIMARY KEY,
                            name  VARCHAR(50) NOT NULL UNIQUE
);

-- Sündmused
CREATE TABLE events (
                        id                SERIAL PRIMARY KEY,
                        organizer_id      INTEGER      NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
                        city_id           INTEGER      NOT NULL REFERENCES cities(id) ON DELETE RESTRICT,
                        title             VARCHAR(200) NOT NULL,
                        description       VARCHAR(2000),
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
                                  event_id      INTEGER NOT NULL REFERENCES events(id)     ON DELETE CASCADE,
                                  skill_tag_id  INTEGER NOT NULL REFERENCES skill_tags(id) ON DELETE CASCADE,
                                  PRIMARY KEY (event_id, skill_tag_id)
);

-- Registreerimised
CREATE TABLE registrations (
                               id             SERIAL PRIMARY KEY,
                               user_id        INTEGER     NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
                               event_id       INTEGER     NOT NULL REFERENCES events(id) ON DELETE CASCADE,
                               status         VARCHAR(20) NOT NULL DEFAULT 'LAHEB'
                                   CHECK (status IN ('LAHEB', 'VOIB_OLLA', 'EI_LAHE')),
                               registered_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
                               UNIQUE (user_id, event_id)
);

-- Kommentaarid (toetab vastuseid läbi parent_comment_id)
CREATE TABLE comments (
                          id                 SERIAL PRIMARY KEY,
                          event_id           INTEGER   NOT NULL REFERENCES events(id)   ON DELETE CASCADE,
                          user_id            INTEGER   NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
                          parent_comment_id  INTEGER            REFERENCES comments(id) ON DELETE CASCADE,
                          content            VARCHAR(2000) NOT NULL,
                          created_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================
-- E-commerce
-- ============================================

-- Tooted
CREATE TABLE products (
                          id              SERIAL PRIMARY KEY,
                          name            VARCHAR(200)   NOT NULL,
                          description     VARCHAR(1000),
                          price           NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
                          image_url       VARCHAR(500),
                          stock_quantity  INT            NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
                          created_at      TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Ostukorvid (üks kasutaja kohta)
CREATE TABLE carts (
                       id          SERIAL PRIMARY KEY,
                       user_id     INTEGER   NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                       created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Ostukorvi tooted
CREATE TABLE cart_items (
                            id          SERIAL PRIMARY KEY,
                            cart_id     INTEGER NOT NULL REFERENCES carts(id)    ON DELETE CASCADE,
                            product_id  INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                            quantity    INT     NOT NULL CHECK (quantity > 0),
                            UNIQUE (cart_id, product_id)
);

-- Arveldusandmed (snapshot ostuhetkest)
CREATE TABLE billings (
                          id           SERIAL PRIMARY KEY,
                          first_name   VARCHAR(50)  NOT NULL,
                          last_name    VARCHAR(50)  NOT NULL,
                          company      VARCHAR(100),
                          country      VARCHAR(50)  NOT NULL,
                          street       VARCHAR(200) NOT NULL,
                          postal_code  VARCHAR(20)  NOT NULL,
                          city         VARCHAR(100) NOT NULL,
                          phone        VARCHAR(20)  NOT NULL,
                          email        VARCHAR(255) NOT NULL
);

-- Tellimused
CREATE TABLE orders (
                        id          SERIAL PRIMARY KEY,
                        user_id     INTEGER        REFERENCES users(id)    ON DELETE SET NULL,
                        billing_id  INTEGER        NOT NULL REFERENCES billings(id) ON DELETE RESTRICT,
                        status      VARCHAR(30)    NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
                        subtotal    NUMERIC(10, 2) NOT NULL,
                        shipping    NUMERIC(10, 2) NOT NULL DEFAULT 0,
                        tax         NUMERIC(10, 2) NOT NULL DEFAULT 0,
                        total       NUMERIC(10, 2) NOT NULL,
                        created_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Tellimuse read (snapshot ostuhetkest)
CREATE TABLE order_items (
                             id                 SERIAL PRIMARY KEY,
                             order_id           INTEGER        NOT NULL REFERENCES orders(id)   ON DELETE CASCADE,
                             product_id         INTEGER                 REFERENCES products(id) ON DELETE SET NULL,
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
