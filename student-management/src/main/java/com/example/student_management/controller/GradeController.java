package com.example.student_management.controller;

import com.example.student_management.model.Grades;
import com.example.student_management.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // MAIN URL
@CrossOrigin(origins = "http://localhost:4200") // запросы с Angular приложения
public class GradeController {

    private final GradeService gradeService;

    @Autowired
    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    /**
     * Получить все оценки, хранящиеся в системе.
     * GET /api/grades
     *
     * @return ResponseEntity со списком всех оценок.
     */
    @GetMapping("/grades")
    public ResponseEntity<List<Grades>> getAllGrades() {
        List<Grades> grades = gradeService.getAllGrades();
        return new ResponseEntity<>(grades, HttpStatus.OK);
    }

    /**
     * Получить все оценки для конкретного студента.
     * GET /api/students/{studentId}/grades
     *
     * @param studentId ID студента, чьи оценки нужно получить.
     * @return ResponseEntity со списком оценок или 404 Not Found, если студент не существует.
     */
    @GetMapping("/students/{studentId}/grades")
    public ResponseEntity<List<Grades>> getGradesByStudentId(@PathVariable Integer studentId) {
        try {
            List<Grades> grades = gradeService.getGradesByStudentId(studentId);
            return new ResponseEntity<>(grades, HttpStatus.OK); // 200 OK
        } catch (IllegalArgumentException e) {
            // студент не найден
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    /**
     * Получить одну оценку по ее ID.
     * GET /api/grades/{id}
     *
     * @param id ID оценки.
     * @return ResponseEntity с оценкой или 404 Not Found.
     */
    @GetMapping("/grades/{id}")
    public ResponseEntity<Grades> getGradeById(@PathVariable Integer id) {
        return gradeService.getGradeById(id)
                .map(grade -> new ResponseEntity<>(grade, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Добавить новую оценку студенту.
     * POST /api/students/{studentId}/grades
     *
     * @param studentId ID студента, которому добавляется оценка.
     * @param grade Объект Grade из тела запроса.
     * @return ResponseEntity с созданной оценкой и статусом 201 Created.
     */
    @PostMapping("/students/{studentId}/grades")
    public ResponseEntity<Grades> addGrade(@PathVariable Integer studentId, @RequestBody Grades grade) {
        if (grade.getStudentId() != null && !grade.getStudentId().equals(studentId)) {
            // ну если studentId в URL и в теле запроса не совпадают, то это неправильный запрос и выдает ошибку
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        grade.setStudentId(studentId); // надо устанавливать studentId из пути, чтобы быть уверенными

        try {
            Grades createdGrade = gradeService.addGrade(grade);
            return new ResponseEntity<>(createdGrade, HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException e) {
            // в случае ошибки валидации из сервиса
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    /**
     * Обновить существующую оценку.
     * PUT /api/grades/{id}
     *
     * @param id ID оценки, которую нужно обновить.
     * @param grade Объект Grade с обновленными данными.
     * @return ResponseEntity с обновленной оценкой или 404 Not Found / 400 Bad Request.
     */
    @PutMapping("/grades/{id}")
    public ResponseEntity<Grades> updateGrade(@PathVariable Integer id, @RequestBody Grades grade) {
        if (grade.getId() != null && !grade.getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        grade.setId(id); // устанавливаем ID из пути тела

        try {
            boolean updated = gradeService.updateGrade(grade);
            if (updated) {
                return gradeService.getGradeById(id)
                        .map(g -> new ResponseEntity<>(g, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Оценка не найдена
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Удалить оценку по ее ID.
     * DELETE /api/grades/{id}
     *
     * @param id ID оценки для удаления.
     * @return ResponseEntity с 204 No Content или 404 Not Found.
     */
    @DeleteMapping("/grades/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Integer id) {
        boolean deleted = gradeService.deleteGrade(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

