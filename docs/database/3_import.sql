-- ============================================
-- Näidisandmed
-- ============================================

INSERT INTO users (full_name, email, password_hash, phone, role) VALUES
    ('John Doe',    'john.doe@example.com', 'hash1', '+372 555 1234', 'USER'),
    ('Jane Admin',  'admin@example.com',    'hash2', '+372 555 5678', 'ADMIN');

INSERT INTO tags (name) VALUES
    ('IT'),
    ('Sport'),
    ('Muusika');

INSERT INTO events (organizer_id, title, description, event_date, start_time, end_time, location, city) VALUES
    ((SELECT id FROM users WHERE email = 'admin@example.com'),
     'Suur Tehnoloogiakonverents',
     'Aastane konverents, mis toob kokku tehnoloogiamaailma tipud.',
     '2023-10-26', '09:00', '18:00', 'Kultuurikatel', 'Tallinn'),

    ((SELECT id FROM users WHERE email = 'admin@example.com'),
     'Maraton Jooksuüritus',
     'Traditsiooniline maraton nii proffidele kui harrastajatele.',
     '2023-11-15', '08:00', '14:00', 'Raekoja plats', 'Tartu'),

    ((SELECT id FROM users WHERE email = 'admin@example.com'),
     'Jazz Festival',
     'Kolmepäevane jazzmuusika festival erinevate artistidega.',
     '2023-12-01', '17:00', '23:00', 'Rannapark', 'Pärnu');

INSERT INTO event_tags (event_id, tag_id) VALUES
    ((SELECT id FROM events WHERE title = 'Suur Tehnoloogiakonverents'),
     (SELECT id FROM tags WHERE name = 'IT')),
    ((SELECT id FROM events WHERE title = 'Maraton Jooksuüritus'),
     (SELECT id FROM tags WHERE name = 'Sport')),
    ((SELECT id FROM events WHERE title = 'Jazz Festival'),
     (SELECT id FROM tags WHERE name = 'Muusika'));
