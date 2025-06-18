package com.example.student_management.service;

import com.example.student_management.dao.GradeDAO;
import com.example.student_management.dao.StudentDAO;
import com.example.student_management.model.Grades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GradeService {
    private final GradeDAO gradeDAO;
    private final StudentDAO studentDAO;

    @Autowired
    public GradeService(GradeDAO gradeDAO, StudentDAO studentDAO) {
        this.gradeDAO = gradeDAO;
        this.studentDAO = studentDAO;
    }

    /**
     * Получает все оценки из базы данных.
     * @return Список всех оценок.
     */
    public List<Grades> getAllGrades() {
        return gradeDAO.findAll();
    }

    /**
     * Добавляет новую оценку студенту.
     * Включает валидацию и проверку существования студента.
     *
     * @param grade Объект Grade для добавления.
     * @return Сохраненный объект Grade с присвоенным ID.
     * @throws IllegalArgumentException Если данные оценки невалидны или студент не найден.
     */

    public Grades addGrade(Grades grade) {
        // Бизнес-валидация для оценки
        if (grade.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID must be provided for a grade.");
        }
        if (grade.getSubject() == null || grade.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty.");
        }

        // Оценка должна быть от 1 до 5 или от 0 до 100
        if (grade.getScore() == null || grade.getScore() < 0 || grade.getScore() > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100.");
        }
        if (grade.getGradeDate() == null) {
            // проверка, что дата не в будущем
            throw new IllegalArgumentException("Grade date cannot be empty.");
        }
        if (grade.getGradeDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Grade date cannot be in the future.");
        }

        if (studentDAO.findById(grade.getStudentId()).isEmpty()) {
            throw new IllegalArgumentException("Student with ID " + grade.getStudentId() + " not found.");
        }

        return gradeDAO.save(grade);
    }

    /**
     * Получает все оценки для конкретного студента.
     *
     * @param studentId ID студента.
     * @return Список оценок для данного студента.
     * @throws IllegalArgumentException Если студент с указанным ID не существует.
     */
    public List<Grades> getGradesByStudentId(Integer studentId) {

        if (studentDAO.findById(studentId).isEmpty()) {
            throw new IllegalArgumentException("Student with ID " + studentId + " not found.");
        }
        return gradeDAO.findGradesByStudentId(studentId);
    }

    /**
     * Получает оценку по ее ID.
     *
     * @param id ID оценки.
     * @return Optional<Grade>, содержащий оценку, если найдена.
     */
    public Optional<Grades> getGradeById(Integer id) {
        return gradeDAO.findById(id);
    }

    /**
     * Обновляет существующую оценку.
     * Включает валидацию и проверку существования оценки.
     *
     * @param grade Объект Grade с обновленными данными (ID должен быть указан).
     * @return true, если оценка была успешно обновлена; false, если оценка не найдена.
     * @throws IllegalArgumentException Если данные оценки невалидны или оценка не найдена.
     */
    public boolean updateGrade(Grades grade) {
        if (grade.getId() == null) {
            throw new IllegalArgumentException("Grade ID cannot be null for update.");
        }
        if (grade.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID must be provided for a grade.");
        }
        if (grade.getSubject() == null || grade.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty.");
        }
        if (grade.getScore() == null || grade.getScore() < 0 || grade.getScore() > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100.");
        }
        if (grade.getGradeDate() == null) {
            throw new IllegalArgumentException("Grade date cannot be empty.");
        }
        if (grade.getGradeDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Grade date cannot be in the future.");
        }

        if (gradeDAO.findById(grade.getId()).isEmpty()) {
            return false; // Оценка не найдена
        }


        if (studentDAO.findById(grade.getStudentId()).isEmpty()) {
            throw new IllegalArgumentException("Student with ID " + grade.getStudentId() + " not found for this grade.");
        }

        return gradeDAO.update(grade) > 0;
    }

    /**
     * Удаляет оценку по ее ID.
     *
     * @param id ID оценки для удаления.
     * @return true, если оценка была успешно удалена; false, если оценка не найдена.
     */
    public boolean deleteGrade(Integer id) {
        return gradeDAO.deleteById(id) > 0;
    }

}
