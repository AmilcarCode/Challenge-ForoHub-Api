-- Add default topic entries with existing users and courses
INSERT INTO topics (titulo, mensaje, fecha_creacion, status, autor, curso) VALUES
(
    'Introduction to Spring Boot',
    'What are the basic concepts of Spring Boot?',
    NOW(),
    'UNANSWERED',
    1, -- Assuming user ID 1 from V7__insert-table-users.sql
    1  -- Assuming course ID 1 from V8__insert-table-courses.sql
),
(
    'Java Best Practices',
    'What are some recommended best practices for Java development?',
    NOW(),
    'UNANSWERED',
    1, -- Assuming user ID 1 
    1  -- Assuming course ID 1
),
(
    'Spring Security Overview',
    'How does authentication work in Spring Security?',
    NOW(),
    'UNANSWERED',
    1, -- Assuming user ID 1
    1  -- Assuming course ID 1
);