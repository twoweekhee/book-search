-- =========================
-- Book 데이터 500개 삽입
-- =========================
DO $$
    DECLARE
        i INTEGER;
        book_titles TEXT[] := ARRAY[
            'Programming', 'Development', 'Guide', 'Handbook', 'Mastery', 'Complete', 'Advanced', 'Essential',
            'Professional', 'Practical', 'Modern', 'Effective', 'Learning', 'Building', 'Creating', 'Designing',
            'Understanding', 'Exploring', 'Implementing', 'Optimizing'
            ];
        book_subjects TEXT[] := ARRAY[
            'Java', 'Python', 'JavaScript', 'Go', 'React', 'Spring', 'Node.js', 'Angular', 'Vue.js', 'Docker',
            'Kubernetes', 'AWS', 'MongoDB', 'PostgreSQL', 'Redis', 'Machine Learning', 'AI', 'Blockchain',
            'Cybersecurity', 'DevOps', 'Microservices', 'API', 'Database', 'Web', 'Mobile', 'Cloud', 'Linux',
            'Git', 'Testing', 'Architecture'
            ];
        authors TEXT[] := ARRAY[
            'John Smith', 'Jane Doe', 'Michael Johnson', 'Sarah Wilson', 'David Brown', 'Emily Davis',
            'James Miller', 'Lisa Anderson', 'Robert Taylor', 'Jennifer White', 'William Harris', 'Mary Clark',
            'Thomas Lewis', 'Patricia Young', 'Charles King', 'Linda Wright', 'Christopher Green', 'Barbara Hall',
            'Daniel Adams', 'Susan Baker'
            ];
        publishers TEXT[] := ARRAY[
            'Tech Publications', 'O''Reilly Media', 'Manning Publications', 'Packt Publishing', 'Wiley',
            'Addison-Wesley', 'Prentice Hall', 'McGraw-Hill', 'Apress', 'No Starch Press'
            ];
    BEGIN
        FOR i IN 1..500 LOOP
                INSERT INTO book (title, subtitle, author, isbn, publisher, published) VALUES (
                                                                                                  book_subjects[((i-1) % array_length(book_subjects, 1)) + 1] || ' ' ||
                                                                                                  book_titles[((i-1) % array_length(book_titles, 1)) + 1],

                                                                                                  CASE WHEN i % 3 = 0 THEN 'Version ' || (i % 5 + 1) || '.0' ELSE NULL END,

                                                                                                  authors[((i-1) % array_length(authors, 1)) + 1],

                                                                                                  '978' || LPAD((1000000000 + i)::text, 10, '0'),

                                                                                                  publishers[((i-1) % array_length(publishers, 1)) + 1],

                                                                                                  ('2015-01-01'::date + (i || ' days')::interval)::date
                                                                                              );
            END LOOP;
    END $$;

-- =========================
-- Keyword 데이터 100개 삽입
-- =========================
DO $$
    DECLARE
        i INTEGER;
        j INTEGER;
        keywords TEXT[] := ARRAY[
            'java', 'python', 'javascript', 'spring', 'react', 'node', 'angular', 'vue', 'docker', 'kubernetes',
            'aws', 'mongodb', 'postgresql', 'redis', 'mysql', 'api', 'rest', 'microservices', 'devops', 'git',
            'linux', 'ubuntu', 'security', 'blockchain', 'ai', 'machine learning', 'deep learning', 'data science',
            'web development', 'mobile', 'android', 'ios', 'flutter', 'react native', 'testing', 'junit',
            'selenium', 'cypress', 'jest', 'architecture', 'design patterns', 'clean code', 'algorithms',
            'data structures', 'database', 'sql', 'nosql', 'cloud', 'azure', 'gcp', 'serverless'
            ];
        search_count INTEGER;
    BEGIN
        FOR i IN 1..100 LOOP
                search_count := (random() * 49 + 1)::integer;
                FOR j IN 1..search_count LOOP
                        INSERT INTO keyword_log (keyword, searched_at) VALUES (
                                                                                  keywords[((i-1) % array_length(keywords, 1)) + 1],
                                                                                  NOW() - (random() * interval '30 days')
                                                                              );
                    END LOOP;
            END LOOP;
    END $$;
