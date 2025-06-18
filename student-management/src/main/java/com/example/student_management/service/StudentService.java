package com.example.student_management.service;

import com.example.student_management.dao.StudentDAO;
import com.example.student_management.model.Students;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentDAO studentDao;

    @Autowired
    public StudentService(StudentDAO studentDao) {
        this.studentDao = studentDao;
    }

    /**
     * Добавляет нового студента.
     * Здесь можно добавить бизнес-валидацию, например, проверку на уникальность имени.
     *
     * @param student Объект Student для добавления.
     * @return Сохраненный объект Student с присвоенным ID.
     * @throws IllegalArgumentException Если данные студента невалидны (например, нет имени).
     */
    public Students addStudent(Students student) {
        // Пример бизнес-валидации:
        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }
        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        if (student.getGroupName() == null || student.getGroupName().trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        return studentDao.save(student); // Делегируем сохранение DAO-слою
    }

    /**
     * Получает список всех студентов с возможностью фильтрации и сортировки.
     *
     * @param firstNameFilter Фильтр по имени.
     * @param lastNameFilter  Фильтр по фамилии.
     * @param sortBy          Поле для сортировки ("firstName", "lastName").
     * @param sortOrder       Порядок сортировки ("asc", "desc").
     * @return Список найденных студентов.
     */

    public List<Students> getAllStudents(String firstNameFilter, String lastNameFilter, String sortBy, String sortOrder) {
        return studentDao.findAll(firstNameFilter, lastNameFilter, sortBy, sortOrder);
    }

    /**
     * Получает студента по его ID.
     *
     * @param id ID студента.
     * @return Optional<Student>, содержащий студента, если найден.
     */
    public Optional<Students> getStudentById(Integer id) {
        return studentDao.findById(id);
    }

    /**
     * Обновляет информацию о существующем студенте.
     * Может включать бизнес-логику, например, проверку на то, что студент существует.
     *
     * @param student Объект Student с обновленными данными.
     * @return true, если студент был успешно обновлен; false, если студент не найден.
     * @throws IllegalArgumentException Если данные студента невалидны.
     */
    public boolean updateStudent(Students student) {
        // Пример бизнес-валидации перед обновлением:
        if (student.getId() == null) {
            throw new IllegalArgumentException("Student ID cannot be null for update.");
        }
        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }
        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        if (student.getGroupName() == null || student.getGroupName().trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        if (!studentDao.findById(student.getId()).isPresent()) {
            return false; // Студент не найден
        }

        return studentDao.update(student) > 0; // Возвращаем true, если обновлена хотя бы 1 строка
    }

    /**
     * Удаляет студента по его ID.
     *
     * @param id ID студента для удаления.
     * @return true, если студент был успешно удален; false, если студент не найден.
     */
    public boolean deleteStudent(Integer id) {
        // в этом DAO настроен CASCADE DELETE, так что оценки удалятся автоматически.
        return studentDao.deleteById(id) > 0; // Возвращаем true, если удалена хотя бы 1 строка
    }
}
