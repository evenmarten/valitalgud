-- ============================================
-- Näidisandmed
-- ============================================

-- Linnad
INSERT INTO cities (name) VALUES
                              ('Tallinn'),
                              ('Tartu'),
                              ('Pärnu');

-- Rollid
INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');

-- Kasutajad
-- NB! password_hash on BCrypt hash sõnale "password" — kasutamiseks testkasutajatega
INSERT INTO users (full_name, email, password_hash, phone, role_id, is_organizer) VALUES
                                                                                   ('John Doe',       'john.doe@example.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+372 555 1234', (SELECT id FROM roles WHERE name = 'USER'),  FALSE),
                                                                                   ('Jane Admin',     'admin@example.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+372 555 5678', (SELECT id FROM roles WHERE name = 'ADMIN'), TRUE),
                                                                                   ('Tech Events OÜ', 'organizer@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+372 555 9999', (SELECT id FROM roles WHERE name = 'USER'),  TRUE),
                                                                                   ('Alice Smith',    'alice@example.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+372 555 1111', (SELECT id FROM roles WHERE name = 'USER'),  FALSE),
                                                                                   ('Bob Johnson',    'bob@example.com',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+372 555 2222', (SELECT id FROM roles WHERE name = 'USER'),  FALSE);

-- Oskuse-sildid
INSERT INTO skill_tags (name) VALUES
                                  ('IT'),
                                  ('Sport'),
                                  ('Muusika'),
                                  ('JavaScript'),
                                  ('Python'),
                                  ('React'),
                                  ('Design');

-- Sündmused
INSERT INTO events (organizer_id, city_id, title, description, address, event_date, start_time, end_time, max_participants, banner_image_url) VALUES
                                                                                                                                                  ((SELECT id FROM users  WHERE email = 'organizer@example.com'),
                                                                                                                                                   (SELECT id FROM cities WHERE name  = 'Tallinn'),
                                                                                                                                                   'Suur Tehnoloogiakonverents',
                                                                                                                                                   'Aastane konverents, mis toob kokku tehnoloogiamaailma tipud.',
                                                                                                                                                   'Kultuurikatel, Põhja pst 27a',
                                                                                                                                                   '2023-10-26', '09:00', '18:00', 100, 'http://example.com/banner1.jpg'),

                                                                                                                                                  ((SELECT id FROM users  WHERE email = 'organizer@example.com'),
                                                                                                                                                   (SELECT id FROM cities WHERE name  = 'Tartu'),
                                                                                                                                                   'Maraton Jooksuüritus',
                                                                                                                                                   'Traditsiooniline maraton nii proffidele kui harrastajatele.',
                                                                                                                                                   'Raekoja plats 1',
                                                                                                                                                   '2023-11-15', '08:00', '14:00', 500, NULL),

                                                                                                                                                  ((SELECT id FROM users  WHERE email = 'organizer@example.com'),
                                                                                                                                                   (SELECT id FROM cities WHERE name  = 'Pärnu'),
                                                                                                                                                   'Jazz Festival',
                                                                                                                                                   'Kolmepäevane jazzmuusika festival erinevate artistidega.',
                                                                                                                                                   'Rannapark',
                                                                                                                                                   '2023-12-01', '17:00', '23:00', 200, NULL);

-- Sündmuse oskuse-sildid
INSERT INTO event_skill_tags (event_id, skill_tag_id) VALUES
                                                          ((SELECT id FROM events     WHERE title = 'Suur Tehnoloogiakonverents'),
                                                           (SELECT id FROM skill_tags WHERE name  = 'IT')),
                                                          ((SELECT id FROM events     WHERE title = 'Suur Tehnoloogiakonverents'),
                                                           (SELECT id FROM skill_tags WHERE name  = 'JavaScript')),
                                                          ((SELECT id FROM events     WHERE title = 'Maraton Jooksuüritus'),
                                                           (SELECT id FROM skill_tags WHERE name  = 'Sport')),
                                                          ((SELECT id FROM events     WHERE title = 'Jazz Festival'),
                                                           (SELECT id FROM skill_tags WHERE name  = 'Muusika'));

-- Näidis-tooted (e-commerce)
INSERT INTO products (name, description, price, image_url, stock_quantity) VALUES
                                                                               ('Water Bottle', 'Roostevabast terasest joogipudel, 500ml.',       15.99, 'http://example.com/bottle.jpg',    50),
                                                                               ('Scarf',        'Pehme villasall talviseks ilmaks.',              25.00, 'http://example.com/scarf.jpg',     30),
                                                                               ('Gauntlets',    'Naha-kindad ekstreemspordi harrastajatele.',     35.50, 'http://example.com/gauntlets.jpg', 20),
                                                                               ('T-shirt',      'Puuvillane T-särk Event Management App logoga.', 20.00, 'http://example.com/tshirt.jpg',   100),
                                                                               ('Cap',          'Reguleeritav nokamüts.',                         18.75, 'http://example.com/cap.jpg',       40);

-- Registreerimised (eri statustega — testimaks "Minu sündmused" ja "View Participants" vaateid)
INSERT INTO registrations (user_id, event_id, status) VALUES
                                                          ((SELECT id FROM users  WHERE email = 'john.doe@example.com'),
                                                           (SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
                                                           'LAHEB'),

                                                          ((SELECT id FROM users  WHERE email = 'alice@example.com'),
                                                           (SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
                                                           'LAHEB'),

                                                          ((SELECT id FROM users  WHERE email = 'bob@example.com'),
                                                           (SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
                                                           'VOIB_OLLA'),

                                                          ((SELECT id FROM users  WHERE email = 'john.doe@example.com'),
                                                           (SELECT id FROM events WHERE title = 'Maraton Jooksuüritus'),
                                                           'EI_LAHE'),

                                                          ((SELECT id FROM users  WHERE email = 'john.doe@example.com'),
                                                           (SELECT id FROM events WHERE title = 'Jazz Festival'),
                                                           'VOIB_OLLA');

-- Näidis-kommentaarid: tippkommentaar + vastus (näitab threading-võimekust)
INSERT INTO comments (event_id, user_id, content) VALUES
    ((SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
     (SELECT id FROM users  WHERE email = 'alice@example.com'),
     'I''m excited about the speakers this year! Will there be a live stream option?');

INSERT INTO comments (event_id, user_id, parent_comment_id, content) VALUES
    ((SELECT id FROM events   WHERE title = 'Suur Tehnoloogiakonverents'),
     (SELECT id FROM users    WHERE email = 'bob@example.com'),
     (SELECT id FROM comments WHERE content LIKE 'I''m excited about the speakers%' LIMIT 1),
     'Great question, Alice! I''m also hoping for a live stream.');
