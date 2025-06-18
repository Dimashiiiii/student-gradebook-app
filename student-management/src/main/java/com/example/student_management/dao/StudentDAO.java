package com.example.student_management.dao;

import com.example.student_management.model.Students;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentDAO {
    private final DataSource dataSource;

    /**
     * Конструктор для StudentDAO.
     * Spring автоматически передаст сюда объект DataSource, который помогает подключиться к базе данных.
     *
     * @param dataSource Объект, предоставляющий подключение к базе данных.
     */
    @Autowired
    public StudentDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Сохраняет нового студента в базе данных.
     * После успешной записи, студент получает свой уникальный ID от базы данных.
     *
     * @param students Объект Students, который мы хотим добавить в базу данных.
     * @return Тот же объект Students, но уже с заполненным ID, полученным из базы данных.
     * @throws RuntimeException Если что-то пошло не так при сохранении (например, ошибка SQL,
     * или студент не был добавлен, или его ID не удалось получить).
     */
    public Students save(Students students) {
        String sql = "INSERT INTO students (first_name, last_name, group_name) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, students.getFirstName());
            ps.setString(2, students.getLastName());
            ps.setString(3, students.getGroupName());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating student failed, no rows affected.\n" +
                        "Создание student завершилось неудачей, строки не были затронуты.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    students.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating student failed, no ID obtained.\n" +
                            "Не удалось создать студента, идентификатор не получен.");
                }
            }

            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to saved student: " + e.getMessage());
        }
    }

    /**
     * Получает список всех студентов из базы данных.
     * Вы можете использовать этот метод, чтобы фильтровать студентов по имени или фамилии,
     * а также сортировать их по определенному полю (имени, фамилии или ID) в нужном порядке.
     *
     * @param firstNameFilter (Опционально) Часть имени для поиска (например, "Иван" найдет "Иван" и "Иванна").
     * @param lastNameFilter (Опционально) Часть фамилии для поиска.
     * @param sortBy Поле, по которому нужно отсортировать студентов ('firstName', 'lastName' или 'id').
     * Если не указано, сортировка будет по ID.
     * @param sortOrder Порядок сортировки ('asc' для возрастания, 'desc' для убывания).
     * Если не указано, по умолчанию будет "по возрастанию".
     * @return Список студентов, которые соответствуют вашим критериям фильтрации и сортировки.
     * @throws RuntimeException Если произошла ошибка при получении данных из базы данных.
     */
    public List<Students> findAll(String firstNameFilter, String lastNameFilter, String sortBy, String sortOrder) {
        List<Students> students = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder("SELECT id, first_name, last_name, group_name FROM students WHERE 1=1");

        List<Object> params = new ArrayList<>();

        // фильтр для имени
        if (firstNameFilter != null && !firstNameFilter.isEmpty()) {
            sqlBuilder.append(" AND first_name LIKE ?");
            params.add("%" + firstNameFilter + "%"); // '%фильтр%' = "содержит"
        }

        // фильтр для фамилий
        if (lastNameFilter != null && !lastNameFilter.isEmpty()) {
            sqlBuilder.append(" AND last_name LIKE ?");
            params.add("%" + lastNameFilter + "%");
        }

        // Если указан параметр для сортировки, добавляем его
        if (sortBy != null && !sortBy.isEmpty()) {
            sqlBuilder.append(" ORDER BY ");
            // Проверяем, чтобы поле для сортировки было безопасным (защита от SQL-инъекций)
            if ("firstName".equalsIgnoreCase(sortBy)) {
                sqlBuilder.append("first_name");
            } else if ("lastName".equalsIgnoreCase(sortBy)) {
                sqlBuilder.append("last_name");
            } else {
                sqlBuilder.append("id");
            }

            // порядок сортировки
            if (sortOrder != null && ("asc".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder))) {
                sqlBuilder.append(" ").append(sortOrder.toUpperCase());
            } else {
                sqlBuilder.append(" ASC"); // по умолчанию asc
            }
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRowToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve students: " + e.getMessage(), e);
        }
        return students;
    }

    /**
     * Находит одного студента по его уникальному идентификатору (ID).
     *
     * @param id Уникальный номер студента, которого мы ищем.
     * @return Объект Optional, который будет содержать студента, если он найден,
     * или будет пустым, если студента с таким ID не существует.
     * @throws RuntimeException Если произошла ошибка при поиске студента в базе данных.
     */
    public Optional<Students> findById(Integer id) {
        String sql = "SELECT id, first_name, last_name, group_name FROM students WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve student by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Обновляет информацию о существующем студенте в базе данных.
     * Вы передаете объект студента с новыми данными, и метод найдет студента по его ID
     * и обновит его имя, фамилию и название группы.
     *
     * @param student Объект Students, содержащий обновленные данные. ID студента в этом объекте
     * используется для того, чтобы знать, какого студента обновлять.
     * @return Количество строк, которые были изменены в базе данных (обычно 1, если обновление прошло успешно).
     * @throws RuntimeException Если произошла ошибка при обновлении студента.
     */
    public int update(Students student) {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, group_name = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setString(3, student.getGroupName());
            ps.setInt(4, student.getId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update student: " + e.getMessage(), e);
        }
    }

    /**
     * Удаляет студента из базы данных по его уникальному идентификатору (ID).
     *
     * @param id Уникальный номер студента, которого мы хотим удалить.
     * @return Количество строк, которые были удалены из базы данных (обычно 1, если студент был найден и удален).
     * @throws RuntimeException Если произошла ошибка при удалении студента.
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete student: " + e.getMessage(), e);
        }
    }

    /**
     * Вспомогательный метод для маппинга строки ResultSet на объект Student.
     * Этот метод берет одну строку данных из результата запроса к базе данных
     * (ResultSet) и превращает ее в удобный объект Students.
     *
     * @param rs ResultSet, из которого нужно прочитать данные текущей строки.
     * @return Объект Students, заполненный данными из текущей строки ResultSet.
     * @throws SQLException Если происходит ошибка при чтении данных из ResultSet (например, если колонки нет).
     */
    private Students mapRowToStudent(ResultSet rs) throws SQLException {
        Students student = new Students();
        student.setId(rs.getInt("id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setGroupName(rs.getString("group_name"));
        return student;
    }
}
