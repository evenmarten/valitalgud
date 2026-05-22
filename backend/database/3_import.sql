-- ============================================
-- Näidisandmed
-- ============================================

-- Maakonnad (linnad viitavad maakondadele, seega lisame maakonnad enne linnu)
INSERT INTO counties (name) VALUES
    ('Harju maakond'),
    ('Hiiu maakond'),
    ('Ida-Viru maakond'),
    ('Järva maakond'),
    ('Jõgeva maakond'),
    ('Lääne maakond'),
    ('Lääne-Viru maakond'),
    ('Põlva maakond'),
    ('Pärnu maakond'),
    ('Rapla maakond'),
    ('Saare maakond'),
    ('Tartu maakond'),
    ('Valga maakond'),
    ('Viljandi maakond'),
    ('Võru maakond');

-- Linnad (iga linn kuulub ühte maakonda)
INSERT INTO cities (name, county_id) VALUES
    ('Tallinn',    (SELECT id FROM counties WHERE name = 'Harju maakond')),
    ('Kärdla',     (SELECT id FROM counties WHERE name = 'Hiiu maakond')),
    ('Jõhvi',      (SELECT id FROM counties WHERE name = 'Ida-Viru maakond')),
    ('Paide',      (SELECT id FROM counties WHERE name = 'Järva maakond')),
    ('Jõgeva',     (SELECT id FROM counties WHERE name = 'Jõgeva maakond')),
    ('Haapsalu',   (SELECT id FROM counties WHERE name = 'Lääne maakond')),
    ('Rakvere',    (SELECT id FROM counties WHERE name = 'Lääne-Viru maakond')),
    ('Põlva',      (SELECT id FROM counties WHERE name = 'Põlva maakond')),
    ('Pärnu',      (SELECT id FROM counties WHERE name = 'Pärnu maakond')),
    ('Rapla',      (SELECT id FROM counties WHERE name = 'Rapla maakond')),
    ('Kuressaare', (SELECT id FROM counties WHERE name = 'Saare maakond')),
    ('Tartu',      (SELECT id FROM counties WHERE name = 'Tartu maakond')),
    ('Valga',      (SELECT id FROM counties WHERE name = 'Valga maakond')),
    ('Viljandi',   (SELECT id FROM counties WHERE name = 'Viljandi maakond')),
    ('Võru',       (SELECT id FROM counties WHERE name = 'Võru maakond'));

-- Rollid
INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');

-- Kasutajad
-- NB! Õppeprojekt — paroolid on tahtlikult plain text. Tootmises kasuta alati hash'imist (nt BCrypt).
INSERT INTO users (password, role_id, status) VALUES
    ('password', (SELECT id FROM roles WHERE name = 'USER'),  'ACTIVE'),
    ('password', (SELECT id FROM roles WHERE name = 'ADMIN'), 'ACTIVE'),
    ('password', (SELECT id FROM roles WHERE name = 'USER'),  'ACTIVE'),
    ('password', (SELECT id FROM roles WHERE name = 'USER'),  'ACTIVE'),
    ('password', (SELECT id FROM roles WHERE name = 'USER'),  'ACTIVE');

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

-- Sündmused (maakond tuleneb linnast — events viitab ainult linnale)
INSERT INTO events (organizer_id, city_id, title, description, address, event_date, start_time, end_time, max_participants, banner_image_url) VALUES
    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'organizer@example.com'),
     (SELECT id FROM cities WHERE name = 'Tallinn'),
     'Suur Tehnoloogiakonverents',
     'Aastane konverents, mis toob kokku tehnoloogiamaailma tipud.',
     'Kultuurikatel, Põhja pst 27a',
     '2023-10-26', '09:00', '18:00', 100, 'https://picsum.photos/seed/tehnokonverents/1200/400'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'organizer@example.com'),
     (SELECT id FROM cities WHERE name = 'Tartu'),
     'Maraton Jooksuüritus',
     'Traditsiooniline maraton nii proffidele kui harrastajatele.',
     'Raekoja plats 1',
     '2023-11-15', '08:00', '14:00', 500, 'https://picsum.photos/seed/maraton/1200/400'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'organizer@example.com'),
     (SELECT id FROM cities WHERE name = 'Pärnu'),
     'Jazz Festival',
     'Kolmepäevane jazzmuusika festival erinevate artistidega.',
     'Rannapark',
     '2023-12-01', '17:00', '23:00', 200, 'https://picsum.photos/seed/jazzfestival/1200/400'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'organizer@example.com'),
     (SELECT id FROM cities WHERE name = 'Tallinn'),
     'Veebiarenduse Töötuba',
     'Praktiline töötuba modernsest veebiarendusest Vue ja Spring Bootiga.',
     'Ülemiste City, Valukoja 8',
     '2026-06-15', '10:00', '16:00', 30, 'https://picsum.photos/seed/veebiarendus/1200/400'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'organizer@example.com'),
     (SELECT id FROM cities WHERE name = 'Pärnu'),
     'Suvine Discgolfi Turniir',
     'Lõbus discgolfi turniir kõigile tasemetele, auhinnad parimatele.',
     'Rannapark, discgolfi rada',
     '2026-07-04', '11:00', '17:00', 64, 'https://picsum.photos/seed/discgolf/1200/400'),

    ((SELECT u.id FROM users u JOIN contacts c ON c.user_id = u.id WHERE c.email = 'organizer@example.com'),
     (SELECT id FROM cities WHERE name = 'Tartu'),
     'Disainikonverents',
     'Kohtumispaik UX- ja graafilistele disaineritele, töötoad ja ettekanded.',
     'Aparaaditakas, Riia 15b',
     '2026-09-20', '09:30', '17:30', 150, 'https://picsum.photos/seed/disainikonverents/1200/400');

-- Sündmuse oskuse-sildid
INSERT INTO event_skill_tags (event_id, skill_tag_id) VALUES
    ((SELECT id FROM events     WHERE title = 'Suur Tehnoloogiakonverents'),
     (SELECT id FROM skill_tags WHERE name  = 'IT')),
    ((SELECT id FROM events     WHERE title = 'Suur Tehnoloogiakonverents'),
     (SELECT id FROM skill_tags WHERE name  = 'JavaScript')),
    ((SELECT id FROM events     WHERE title = 'Maraton Jooksuüritus'),
     (SELECT id FROM skill_tags WHERE name  = 'Sport')),
    ((SELECT id FROM events     WHERE title = 'Jazz Festival'),
     (SELECT id FROM skill_tags WHERE name  = 'Muusika')),
    ((SELECT id FROM events     WHERE title = 'Veebiarenduse Töötuba'),
     (SELECT id FROM skill_tags WHERE name  = 'IT')),
    ((SELECT id FROM events     WHERE title = 'Veebiarenduse Töötuba'),
     (SELECT id FROM skill_tags WHERE name  = 'React')),
    ((SELECT id FROM events     WHERE title = 'Suvine Discgolfi Turniir'),
     (SELECT id FROM skill_tags WHERE name  = 'Sport')),
    ((SELECT id FROM events     WHERE title = 'Disainikonverents'),
     (SELECT id FROM skill_tags WHERE name  = 'Design'));

-- Näidis-tooted (e-commerce). Järjekord = kuvamise järjekord poes (sorteeritud id järgi).
INSERT INTO products (name, description, price, image_url, stock_quantity) VALUES
    ('Nokamüts',    'Reguleeritava rihmaga nokamüts Valitalgud logoga — kaitseb päikese eest talgupäeval.', 18.00, '/images/products/cap.png',    40),
    ('Kapuutspusa', 'Pehme ja soe kapuutspusa Valitalgud logoga — ideaalne jahedaks talgupäevaks.',         39.90, '/images/products/hoodie.png', 35),
    ('T-särk',      'Puuvillane T-särk Valitalgud logoga — mugav ja hingav igaks talguks.',                  20.00, '/images/products/shirt.png',  100),
    ('Seljakott',   'Vastupidav ja avar seljakott talgutarvikute ja varustuse kandmiseks.',                 45.00, '/images/products/bag.png',    25),
    ('Termopudel',  'Roostevabast terasest termopudel (500 ml) — hoiab joogi kuuma või külmana terve päeva.', 22.50, '/images/products/bottle.png', 50),
    ('Võtmehoidja', 'Vastupidav Valitalgud võtmehoidja — väike meene, mis hoiab võtmed alati käeulatuses.',  6.90, '/images/products/hanger.png', 80),
    ('Kruus',       'Keraamiline kruus Valitalgud logoga — sobib hommikukohvile ja talgupäeva soojenduseks.', 12.00, '/images/products/cup.png',    60),
    ('Lauamatt',    'Suur lauamatt Valitalgud logoga — sile pind hiirele ja klaviatuurile, korrastab töölaua.', 24.00, '/images/products/mat.png',    45),
    ('Sülearvuti ümbris', 'Pehme vooderdusega sülearvuti ümbris Valitalgud logoga — kaitseb kriimustuste eest.', 29.90, '/images/products/sleeve.png', 30);

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