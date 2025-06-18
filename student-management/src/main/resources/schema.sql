-- Удаление таблицы, каждый раз когда запускаете бэк оно удаляется создается по новому (главное наличие базы)
DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS students;

-- students
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    group_name VARCHAR(255) NOT NULL
);

-- grades
CREATE TABLE grades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    score INT NOT NULL,
    grade_date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

 INSERT INTO students (first_name, last_name, group_name) VALUES ('The', 'Rock', '22-02');
 INSERT INTO students (first_name, last_name, group_name) VALUES ('Dinmukhamed', 'Aqtay', '22-02');
 INSERT INTO students (first_name, last_name, group_name) VALUES ('John', 'IttynBalasy', '22-01');
 INSERT INTO students (first_name, last_name, group_name) VALUES ('Қақащи', 'Минато', '22-02');
 INSERT INTO students (first_name, last_name, group_name) VALUES ('Мадра', 'Щесуи', '22-01');
 INSERT INTO students (first_name, last_name, group_name) VALUES ('Tauasar', 'Akniet', '22-02');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (1, 'Математика', 95, '2025-06-15');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (1, 'Физика', 88, '2025-06-10');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (2, 'Математика', 95, '2025-06-15');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (2, 'Физика', 88, '2025-06-10');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (3, 'Математика', 95, '2025-06-15');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (3, 'Физика', 88, '2025-06-10');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (4, 'Математика', 95, '2025-06-15');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (4, 'Физика', 88, '2025-06-10');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (5, 'Математика', 95, '2025-06-15');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (5, 'Физика', 88, '2025-06-10');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (6, 'Математика', 95, '2025-06-15');
 INSERT INTO grades (student_id, subject, score, grade_date) VALUES (6, 'Физика', 88, '2025-06-10');