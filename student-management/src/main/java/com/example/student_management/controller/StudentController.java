package com.example.student_management.controller;

import com.example.student_management.model.Students;
import com.example.student_management.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Получить список всех студентов с возможностью фильтрации и сортировки.
     * GET /api/students?firstName=...&lastName=...&sortBy=...&sortOrder=...
     *
     * @param firstNameFilter Фильтр по имени студента.
     * @param lastNameFilter  Фильтр по фамилии студента.
     * @param sortBy          Поле для сортировки ("firstName", "lastName").
     * @param sortOrder       Порядок сортировки ("asc", "desc").
     * @return ResponseEntity со списком студентов.
     */
    @GetMapping //GET запросы на /api/students
    public ResponseEntity<List<Students>> getAllStudents(
            @RequestParam(required = false) String firstNameFilter, // Параметр запроса, необязательный
            @RequestParam(required = false) String lastNameFilter,  // то же самое
            @RequestParam(required = false) String sortBy,          // то же самое
            @RequestParam(required = false) String sortOrder        // то же самое
    ) {
        List<Students> students = studentService.getAllStudents(firstNameFilter, lastNameFilter, sortBy, sortOrder);
        return new ResponseEntity<>(students, HttpStatus.OK); // Возвращается список студентов и статус 200 OK
    }

    /**
     * Получить студента по его ID.
     * GET /api/students/{id}
     *
     * @param id ID студента.
     * @return ResponseEntity со студентом или 404 Not Found.
     */
    @GetMapping("/{id}") //GET запросы на /api/students/{id}
    public ResponseEntity<Students> getStudentById(@PathVariable Integer id) { // Извлекает ID из пути URL
        return studentService.getStudentById(id)
                .map(student -> new ResponseEntity<>(student, HttpStatus.OK)) // 200 OK
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found
    }

    /**
     * Добавить нового студента.
     * POST /api/students
     *
     * @param student Объект Students из тела запроса.
     * @return ResponseEntity с созданным студентом и статусом 201 Created.
     */
    @PostMapping //POST запросы на /api/students
    public ResponseEntity<Students> addStudent(@RequestBody Students student) { // Принимает Student из тела запроса (JSON)
        try {
            Students createdStudent = studentService.addStudent(student);
            return new ResponseEntity<>(createdStudent, HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException e) {
            // В случае ошибки валидации из сервиса, возвращаем 400 Bad Request
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Обновить информацию о существующем студенте.
     * PUT /api/students/{id}
     *
     * @param id ID студента, которого нужно обновить.
     * @param student Объект Student с обновленными данными.
     * @return ResponseEntity с обновленным студентом или 404 Not Found / 400 Bad Request.
     */
    @PutMapping("/{id}") // PUT запросы на /api/students/{id}
    public ResponseEntity<Students> updateStudent(@PathVariable Integer id, @RequestBody Students student) {
        // ID в пути соответствует ID в теле запроса
        if (student.getId() != null && !student.getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Несоответствие ID
        }
        student.setId(id); // надо устанавливать ID из пути URL, чтобы DAO знал, кого обновлять

        try {
            boolean updated = studentService.updateStudent(student);
            if (updated) {
                // если все успешно, то возвращаем обновленный объект и 200 OK
                return studentService.getStudentById(id)
                        .map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Ошибка валидации
        }
    }

    /**
     * Удалить студента по его ID.
     * DELETE /api/students/{id}
     *
     * @param id ID студента для удаления.
     * @return ResponseEntity с 204 No Content или 404 Not Found.
     */
    @DeleteMapping("/{id}") // DELETE запросы на /api/students/{id}
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        boolean deleted = studentService.deleteStudent(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content - успешно удалено, но нет тела ответа
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found - студент не найден для удаления
        }
    }


}
