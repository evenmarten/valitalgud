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
INSERT INTO users (password_hash, role_id, status) VALUES
    ('$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', (SELECT id FROM roles WHERE name = 'USER'),  'ACTIVE'),
    ('$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', (SELECT id FROM roles WHERE name = 'ADMIN'), 'ACTIVE'),
    ('$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', (SELECT id FROM roles WHERE name = 'USER'),  'ACTIVE'),
    ('$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', (SELECT id FROM roles WHERE name = 'USER'),  'ACTIVE'),
    ('$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', (SELECT id FROM roles WHERE name = 'USER'),  'ACTIVE');

-- Kontaktid (full_name, email, phone on contacts tabelis, mitte users tabelis)
INSERT INTO contacts (user_id, full_name, email, phone) VALUES
    ((SELECT id FROM users ORDER BY id ASC LIMIT 1 OFFSET 0), 'John Doe',       'john.doe@example.com',  '+372 555 1234'),
    ((SELECT id FROM users ORDER BY id ASC LIMIT 1 OFFSET 1), 'Jane Admin',     'admin@example.com',     '+372 555 5678'),
    ((SELECT id FROM users ORDER BY id ASC LIMIT 1 OFFSET 2), 'Tech Events OÜ', 'organizer@example.com', '+372 555 9999'),
    ((SELECT id FROM users ORDER BY id ASC LIMIT 1 OFFSET 3), 'Alice Smith',    'alice@example.com',     '+372 555 1111'),
    ((SELECT id FROM users ORDER BY id ASC LIMIT 1 OFFSET 4), 'Bob Johnson',    'bob@example.com',       '+372 555 2222');

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
    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'organizer@example.com'),
     (SELECT id FROM cities WHERE name = 'Tallinn'),
     'Suur Tehnoloogiakonverents',
     'Aastane konverents, mis toob kokku tehnoloogiamaailma tipud.',
     'Kultuurikatel, Põhja pst 27a',
     '2023-10-26', '09:00', '18:00', 100, 'http://example.com/banner1.jpg'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'organizer@example.com'),
     (SELECT id FROM cities WHERE name = 'Tartu'),
     'Maraton Jooksuüritus',
     'Traditsiooniline maraton nii proffidele kui harrastajatele.',
     'Raekoja plats 1',
     '2023-11-15', '08:00', '14:00', 500, NULL),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'organizer@example.com'),
     (SELECT id FROM cities WHERE name = 'Pärnu'),
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
    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'john.doe@example.com'),
     (SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
     'LAHEB'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'alice@example.com'),
     (SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
     'LAHEB'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'bob@example.com'),
     (SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
     'VOIB_OLLA'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'john.doe@example.com'),
     (SELECT id FROM events WHERE title = 'Maraton Jooksuüritus'),
     'EI_LAHE'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'john.doe@example.com'),
     (SELECT id FROM events WHERE title = 'Jazz Festival'),
     'VOIB_OLLA');

-- Näidis-kommentaarid
INSERT INTO comments (event_id, user_id, content) VALUES
    ((SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
     (SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'alice@example.com'),
     'I''m excited about the speakers this year! Will there be a live stream option?'),

    ((SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
     (SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'bob@example.com'),
     'Great question, Alice! I''m also hoping for a live stream.');