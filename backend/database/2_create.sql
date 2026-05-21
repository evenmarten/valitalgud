-- Created by Redgate Data Modeler (https://datamodeler.redgate-platform.com)
-- Last modification date: 2026-05-15 12:35:56.658

-- tables
-- Table: billings
CREATE TABLE billings (
                          id serial  NOT NULL,
                          first_name varchar(50)  NOT NULL,
                          last_name varchar(50)  NOT NULL,
                          company varchar(100)  NULL,
                          country varchar(50)  NOT NULL,
                          street varchar(200)  NOT NULL,
                          postal_code varchar(20)  NOT NULL,
                          city varchar(100)  NOT NULL,
                          phone varchar(20)  NOT NULL,
                          email varchar(255)  NOT NULL,
                          CONSTRAINT billings_pk PRIMARY KEY (id)
);

-- Table: cart_items
CREATE TABLE cart_items (
                            id serial  NOT NULL,
                            cart_id integer  NOT NULL,
                            product_id integer  NOT NULL,
                            quantity int  NOT NULL,
                            CONSTRAINT AK_7 UNIQUE (cart_id, product_id) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                            CONSTRAINT CHECK_5 CHECK (( quantity > 0 )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                            CONSTRAINT cart_items_pk PRIMARY KEY (id)
);

CREATE INDEX idx_cart_items_cart on cart_items (cart_id ASC);

-- Table: carts
CREATE TABLE carts (
                       id serial  NOT NULL,
                       user_id integer  NOT NULL,
                       created_at timestamp  NOT NULL DEFAULT now(),
                       updated_at timestamp  NOT NULL DEFAULT now(),
                       CONSTRAINT AK_6 UNIQUE (user_id) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                       CONSTRAINT carts_pk PRIMARY KEY (id)
);

-- Table: cities
CREATE TABLE cities (
                        id serial  NOT NULL,
                        name varchar(100)  NOT NULL,
                        county_id integer  NOT NULL,
                        CONSTRAINT AK_0 UNIQUE (name) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                        CONSTRAINT cities_pk PRIMARY KEY (id)
);

-- Table: counties
CREATE TABLE counties (
                          id serial  NOT NULL,
                          name varchar(100)  NOT NULL,
                          CONSTRAINT counties_name_unique UNIQUE (name) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                          CONSTRAINT counties_pk PRIMARY KEY (id)
);

-- Table: comments
CREATE TABLE contacts (
                          id serial  NOT NULL,
                          user_id integer  NOT NULL,
                          full_name varchar(100)  NOT NULL,
                          email varchar(255)  NOT NULL,
                          phone varchar(20)  NULL,
                          description varchar(500)  NULL,
                          CONSTRAINT AK_2 UNIQUE (user_id) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                          CONSTRAINT AK_3 UNIQUE (email) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                          CONSTRAINT contacts_pk PRIMARY KEY (id)
);

-- Table: contacts
CREATE TABLE event_skill_tags (
                                  event_id integer  NOT NULL,
                                  skill_tag_id integer  NOT NULL,
                                  CONSTRAINT event_skill_tags_pk PRIMARY KEY (event_id,skill_tag_id)
);

-- Table: event_skill_tags
CREATE INDEX idx_event_skill_tags_event on event_skill_tags (event_id ASC);

CREATE INDEX idx_event_skill_tags_tag on event_skill_tags (skill_tag_id ASC);

CREATE TABLE events (
                        id serial  NOT NULL,
                        organizer_id integer  NOT NULL,
                        city_id integer  NOT NULL,
                        title varchar(200)  NOT NULL,
                        description varchar(2000)  NULL,
                        address varchar(255)  NULL,
                        event_date date  NOT NULL,
                        start_time time  NOT NULL,
                        end_time time  NOT NULL,
                        max_participants int  NULL,
                        banner_image_url varchar(500)  NULL,
                        is_cancelled boolean  NOT NULL DEFAULT false,
                        created_at timestamp  NOT NULL DEFAULT now(),
                        CONSTRAINT CHECK_1 CHECK (( max_participants > 0 )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                        CONSTRAINT events_pk PRIMARY KEY (id)
);

-- Table: events
CREATE INDEX idx_events_organizer on events (organizer_id ASC);

CREATE INDEX idx_events_date on events (event_date ASC);

CREATE INDEX idx_events_city on events (city_id ASC);

CREATE TABLE order_items (
                             id serial  NOT NULL,
                             order_id integer  NOT NULL,
                             product_id integer  NULL,
                             product_name varchar(200)  NOT NULL,
                             quantity int  NOT NULL,
                             price_at_purchase numeric(10,2)  NOT NULL,
                             CONSTRAINT CHECK_7 CHECK (( quantity > 0 )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                             CONSTRAINT order_items_pk PRIMARY KEY (id)
);

-- Table: order_items
CREATE INDEX idx_order_items_order on order_items (order_id ASC);

CREATE TABLE orders (
                        id serial  NOT NULL,
                        user_id integer  NULL,
                        billing_id integer  NOT NULL,
                        status varchar(30)  NOT NULL DEFAULT 'pending',
                        subtotal numeric(10,2)  NOT NULL,
                        shipping numeric(10,2)  NOT NULL DEFAULT 0,
                        tax numeric(10,2)  NOT NULL DEFAULT 0,
                        total numeric(10,2)  NOT NULL,
                        created_at timestamp  NOT NULL DEFAULT now(),
                        CONSTRAINT CHECK_6 CHECK (( status IN ( 'PENDING' , 'PAID' , 'SHIPPED' , 'DELIVERED' , 'CANCELLED' ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                        CONSTRAINT orders_pk PRIMARY KEY (id)
);

-- Table: orders
CREATE INDEX idx_orders_user on orders (user_id ASC);

CREATE TABLE products (
                          id serial  NOT NULL,
                          name varchar(200)  NOT NULL,
                          description varchar(1000)  NULL,
                          price numeric(10,2)  NOT NULL,
                          image_url varchar(500)  NULL,
                          stock_quantity int  NOT NULL DEFAULT 0,
                          created_at timestamp  NOT NULL DEFAULT now(),
                          CONSTRAINT CHECK_3 CHECK (( price >= 0 )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                          CONSTRAINT CHECK_4 CHECK (( stock_quantity >= 0 )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                          CONSTRAINT products_pk PRIMARY KEY (id)
);

-- Table: products
CREATE TABLE registrations (
                               id serial  NOT NULL,
                               user_id integer  NOT NULL,
                               event_id integer  NOT NULL,
                               status varchar(20)  NOT NULL DEFAULT 'laheb',
                               registered_at timestamp  NOT NULL DEFAULT now(),
                               CONSTRAINT AK_5 UNIQUE (user_id, event_id) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                               CONSTRAINT CHECK_2 CHECK (( status IN ( 'LAHEB' , 'VOIB_OLLA' , 'EI_LAHE' ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                               CONSTRAINT registrations_pk PRIMARY KEY (id)
);

-- Table: registrations
CREATE TABLE comments (
                          id serial  NOT NULL,
                          event_id integer  NOT NULL,
                          user_id integer  NOT NULL,
                          content varchar(1000)  NOT NULL,
                          created_at timestamp  NOT NULL DEFAULT now(),
                          CONSTRAINT comments_pk PRIMARY KEY (id)
);

CREATE INDEX idx_registrations_user on registrations (user_id ASC);

CREATE INDEX idx_registrations_event on registrations (event_id ASC);

-- Table: roles
CREATE TABLE roles (
                       id serial  NOT NULL,
                       name varchar(20)  NOT NULL,
                       CONSTRAINT AK_1 UNIQUE (name) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                       CONSTRAINT roles_pk PRIMARY KEY (id)
);

-- Table: skill_tags
CREATE TABLE skill_tags (
                            id serial  NOT NULL,
                            name varchar(50)  NOT NULL,
                            CONSTRAINT AK_4 UNIQUE (name) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                            CONSTRAINT skill_tags_pk PRIMARY KEY (id)
);

-- Table: users
CREATE TABLE users (
                       id serial  NOT NULL,
                       password varchar(255)  NOT NULL,
                       role_id integer  NOT NULL DEFAULT 1,
                       status varchar(30)  NOT NULL DEFAULT 'active',
                       created_at timestamp  NOT NULL DEFAULT now(),
                       CONSTRAINT CHECK_0 CHECK (( status IN ( 'ACTIVE' , 'PENDING_ACTIVATION' , 'DELETED' ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                       CONSTRAINT users_pk PRIMARY KEY (id)
);

-- foreign keys
-- Reference: FK_0 (table: users)
ALTER TABLE users ADD CONSTRAINT FK_0
    FOREIGN KEY (role_id)
        REFERENCES roles (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_1 (table: contacts)
ALTER TABLE contacts ADD CONSTRAINT FK_1
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_11 (table: carts)
ALTER TABLE carts ADD CONSTRAINT FK_11
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_12 (table: cart_items)
ALTER TABLE cart_items ADD CONSTRAINT FK_12
    FOREIGN KEY (cart_id)
        REFERENCES carts (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_13 (table: cart_items)
ALTER TABLE cart_items ADD CONSTRAINT FK_13
    FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_14 (table: orders)
ALTER TABLE orders ADD CONSTRAINT FK_14
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE  SET NULL
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_15 (table: orders)
ALTER TABLE orders ADD CONSTRAINT FK_15
    FOREIGN KEY (billing_id)
        REFERENCES billings (id)
        ON DELETE  RESTRICT
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_16 (table: order_items)
ALTER TABLE order_items ADD CONSTRAINT FK_16
    FOREIGN KEY (order_id)
        REFERENCES orders (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_17 (table: order_items)
ALTER TABLE order_items ADD CONSTRAINT FK_17
    FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE  SET NULL
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_2 (table: events)
ALTER TABLE events ADD CONSTRAINT FK_2
    FOREIGN KEY (organizer_id)
        REFERENCES users (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_3 (table: events)
ALTER TABLE events ADD CONSTRAINT FK_3
    FOREIGN KEY (city_id)
        REFERENCES cities (id)
        ON DELETE  RESTRICT
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: cities_county_fk (table: cities)
ALTER TABLE cities ADD CONSTRAINT cities_county_fk
    FOREIGN KEY (county_id)
        REFERENCES counties (id)
        ON DELETE  RESTRICT
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_4 (table: event_skill_tags)
ALTER TABLE event_skill_tags ADD CONSTRAINT FK_4
    FOREIGN KEY (event_id)
        REFERENCES events (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_5 (table: event_skill_tags)
ALTER TABLE event_skill_tags ADD CONSTRAINT FK_5
    FOREIGN KEY (skill_tag_id)
        REFERENCES skill_tags (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_6 (table: registrations)
ALTER TABLE registrations ADD CONSTRAINT FK_6
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_7 (table: registrations)
ALTER TABLE registrations ADD CONSTRAINT FK_7
    FOREIGN KEY (event_id)
        REFERENCES events (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_8 (table: comments)
ALTER TABLE comments ADD CONSTRAINT FK_8
    FOREIGN KEY (event_id)
        REFERENCES events (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: FK_9 (table: comments)
ALTER TABLE comments ADD CONSTRAINT FK_9
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE  CASCADE
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- End of file.

