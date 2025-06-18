package com.example.student_management.dao;

import com.example.student_management.model.Grades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class GradeDAO {
    private final DataSource dataSource;

    @Autowired
    public GradeDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Находит все оценки в базе данных.
     * @return Список всех оценок.
     */
    public List<Grades> findAll() {
        List<Grades> grades = new ArrayList<>();
        String sql = "SELECT id, student_id, subject, score, grade_date FROM grades";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                grades.add(mapRowToGrade(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all grades: " + e.getMessage(), e);
        }
        return grades;
    }

    /**
     * Сохраняет новую оценку в базе данных.
     * После успешной вставки, обновляет объект оценки со сгенерированным ID.
     *
     * @param grade Объект Grade для сохранения.
     * @return Сохраненный объект Grades с присвоенным ID.
     * @throws RuntimeException В случае ошибки при работе с базой данных.
     */
    public Grades save(Grades grade) {
        String sql = "INSERT INTO grades (student_id, subject, score, grade_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, grade.getStudentId());
            ps.setString(2, grade.getSubject());
            ps.setInt(3, grade.getScore());

            ps.setDate(4, java.sql.Date.valueOf(grade.getGradeDate()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating grade failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    grade.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating grade failed, no ID obtained.");
                }
            }
            return grade;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save grade: " + e.getMessage(), e);
        }
    }

    /**
     * Извлекает все оценки для конкретного студента из базы данных.
     *
     * @param studentId ID студента, чьи оценки нужно получить.
     * @return Список объектов Grades, принадлежащих указанному студенту.
     * @throws RuntimeException В случае ошибки при работе с базой данных.
     */
    public List<Grades> findGradesByStudentId(Integer studentId) {
        List<Grades> grades = new ArrayList<>();
        String sql = "SELECT id, student_id, subject, score, grade_date FROM grades WHERE student_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapRowToGrade(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve grades for student " + studentId + ": " + e.getMessage(), e);
        }
        return grades;
    }

    /**
     * Извлекает одну оценку по ее уникальному ID.
     *
     * @param id Уникальный ID оценки.
     * @return Optional<Grades>, содержащий оценку, если найдена, или Optional.empty(), если не найдена.
     * @throws RuntimeException В случае ошибки при работе с базой данных.
     */
    public Optional<Grades> findById(Integer id) {
        String sql = "SELECT id, student_id, subject, score, grade_date FROM grades WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToGrade(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve grade by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Обновляет информацию о существующей оценке в базе данных.
     *
     * @param grade Объект Grades с обновленными данными (ID должен быть указан).
     * @return Количество затронутых строк (обычно 1, если обновление успешно, 0 если оценка не найдена).
     * @throws RuntimeException В случае ошибки при работе с базой данных.
     */
    public int update(Grades grade) {
        String sql = "UPDATE grades SET student_id = ?, subject = ?, score = ?, grade_date = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, grade.getStudentId());
            ps.setString(2, grade.getSubject());
            ps.setInt(3, grade.getScore());
            ps.setDate(4, java.sql.Date.valueOf(grade.getGradeDate()));
            ps.setInt(5, grade.getId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update grade: " + e.getMessage(), e);
        }
    }

    /**
     * Удаляет оценку из базы данных по ее ID.
     *
     * @param id ID оценки, которую нужно удалить.
     * @return Количество удаленных строк (обычно 1, если удаление успешно, 0 если оценка не найдена).
     * @throws RuntimeException В случае ошибки при работе с базой данных.
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM grades WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete grade: " + e.getMessage(), e);
        }
    }

    /**
     * Вспомогательный метод для маппинга строки ResultSet на объект Grade.
     *
     * @param rs ResultSet, из которого нужно прочитать данные.
     * @return Объект Grade, заполненный данными из текущей строки ResultSet.
     * @throws SQLException Если происходит ошибка при доступе к данным в ResultSet.
     */
    private Grades mapRowToGrade(ResultSet rs) throws SQLException {
        Grades grade = new Grades();
        grade.setId(rs.getInt("id"));
        grade.setStudentId(rs.getInt("student_id"));
        grade.setSubject(rs.getString("subject"));
        grade.setScore(rs.getInt("score"));
        // java.sql.Date в LocalDate
        grade.setGradeDate(rs.getDate("grade_date").toLocalDate());
        return grade;
    }
}

