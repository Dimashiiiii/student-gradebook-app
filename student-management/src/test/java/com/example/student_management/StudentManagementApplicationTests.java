package com.example.student_management;

import com.example.student_management.dao.GradeDAO;
import com.example.student_management.dao.StudentDAO;
import com.example.student_management.model.Grades;
import com.example.student_management.model.Students;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudentManagementApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private StudentDAO studentDAO;

	@Autowired
	private GradeDAO gradeDAO;

	/**
	 * Тест для проверки, что контекст приложения Spring Boot успешно загружается.
	 * Это базовый "санити" тест. Если контекст не загружается, что-то фундаментальное не так.
	 */
	@Test
	void contextLoads() {
		assertNotNull(applicationContext, "Application context should be loaded.");
		System.out.println("Spring Boot Application Context loaded successfully!");
	}

	/**
	 * Тест для проверки, что StudentDAO корректно внедряется и может взаимодействовать
	 * с базой данных, выполняя простой запрос (например, получение всех студентов).
	 */
	@Test
	void studentDAOLoadsAndCanFindAll() {
		assertNotNull(studentDAO, "StudentDAO should be autowired and not null.");
		try {
			List<Students> students = studentDAO.findAll(null, null, null, null);
			assertNotNull(students, "List of students should not be null.");
			System.out.println("StudentDAO successfully called findAll(). Initial number of students: " + students.size());
		} catch (Exception e) {
			fail("StudentDAO.findAll() threw an exception: " + e.getMessage());
		}
	}

	/**
	 * Тест для проверки полного цикла CRUD (Создание, Чтение, Обновление, Удаление) для Students.
	 */
	@Test
	void studentDAOCrudOperationsTest() {
		// --- 1. Создание (Save) ---
		Students newStudent = new Students();
		newStudent.setFirstName("ТестИмя");
		newStudent.setLastName("ТестФамилия");
		newStudent.setGroupName("ТестГруппа");

		Students savedStudent = studentDAO.save(newStudent);
		assertNotNull(savedStudent.getId(), "Saved student should have an ID.");
		assertEquals("ТестИмя", savedStudent.getFirstName());
		assertEquals("ТестФамилия", savedStudent.getLastName());
		assertEquals("ТестГруппа", savedStudent.getGroupName());
		System.out.println("Student saved successfully with ID: " + savedStudent.getId());

		// --- 2. Чтение (FindById) ---
		Optional<Students> foundStudent = studentDAO.findById(savedStudent.getId());
		assertTrue(foundStudent.isPresent(), "Student should be found by ID.");
		assertEquals(savedStudent.getId(), foundStudent.get().getId());
		assertEquals(savedStudent.getFirstName(), foundStudent.get().getFirstName());
		System.out.println("Student found by ID: " + foundStudent.get().getFirstName());

		// --- 3. Обновление (Update) ---
		foundStudent.get().setLastName("ОбновленнаяФамилия");
		foundStudent.get().setGroupName("ОбновленнаяГруппа");
		int updatedRows = studentDAO.update(foundStudent.get());
		assertEquals(1, updatedRows, "Exactly one row should be updated.");
		System.out.println("Student updated successfully.");

		Optional<Students> updatedStudent = studentDAO.findById(savedStudent.getId());
		assertTrue(updatedStudent.isPresent(), "Updated student should still be present.");
		assertEquals("ОбновленнаяФамилия", updatedStudent.get().getLastName());
		assertEquals("ОбновленнаяГруппа", updatedStudent.get().getGroupName());
		System.out.println("Student data verified after update.");

	}


	/**
	 * Тест для проверки полного цикла CRUD (Создание, Чтение, Обновление, Удаление) для Grades.
	 * Требует предварительного создания студента.
	 */
	@Test
	void gradeDAOCrudOperationsTest() {
		// временный студента для привязки оценок
		Students tempStudent = new Students();
		tempStudent.setFirstName("Оценочный");
		tempStudent.setLastName("Студент");
		tempStudent.setGroupName("ТестГруппа");
		Students savedTempStudent = studentDAO.save(tempStudent);
		assertNotNull(savedTempStudent.getId(), "Temporary student must have an ID.");
		System.out.println("Temporary student created for grade tests: " + savedTempStudent.getId());

		try {
			// --- 1. Создание (Save) ---
			Grades newGrade = new Grades();
			newGrade.setStudentId(savedTempStudent.getId());
			newGrade.setSubject("История");
			newGrade.setScore(85);
			newGrade.setGradeDate(LocalDate.of(2023, 10, 20));

			Grades savedGrade = gradeDAO.save(newGrade);
			assertNotNull(savedGrade.getId(), "Saved grade should have an ID.");
			assertEquals(savedTempStudent.getId(), savedGrade.getStudentId());
			assertEquals("История", savedGrade.getSubject());
			assertEquals(85, savedGrade.getScore());
			assertEquals(LocalDate.of(2023, 10, 20), savedGrade.getGradeDate());
			System.out.println("Grade saved successfully with ID: " + savedGrade.getId());

			// --- 2. Чтение (FindById) ---
			Optional<Grades> foundGrade = gradeDAO.findById(savedGrade.getId());
			assertTrue(foundGrade.isPresent(), "Grade should be found by ID.");
			assertEquals(savedGrade.getId(), foundGrade.get().getId());
			assertEquals(savedGrade.getSubject(), foundGrade.get().getSubject());
			System.out.println("Grade found by ID: " + foundGrade.get().getId());

			// --- 3. Чтение (FindGradesByStudentId) ---
			List<Grades> studentGrades = gradeDAO.findGradesByStudentId(savedTempStudent.getId());
			assertFalse(studentGrades.isEmpty(), "List of student's grades should not be empty.");
			assertTrue(studentGrades.stream().anyMatch(g -> g.getId().equals(savedGrade.getId())), "Student's grades should contain the saved grade.");
			System.out.println("Grades retrieved by student ID. Count: " + studentGrades.size());

			// --- 4. Обновление (Update) ---
			foundGrade.get().setScore(90);
			foundGrade.get().setSubject("История (Обновлено)");
			int updatedRows = gradeDAO.update(foundGrade.get());
			assertEquals(1, updatedRows, "Exactly one grade row should be updated.");
			System.out.println("Grade updated successfully.");

			Optional<Grades> updatedGrade = gradeDAO.findById(savedGrade.getId());
			assertTrue(updatedGrade.isPresent(), "Updated grade should still be present.");
			assertEquals(90, updatedGrade.get().getScore());
			assertEquals("История (Обновлено)", updatedGrade.get().getSubject());
			System.out.println("Grade data verified after update.");

			// --- 5. Удаление (DeleteById) ---
			int deletedRows = gradeDAO.deleteById(savedGrade.getId());
			assertEquals(1, deletedRows, "Exactly one grade row should be deleted.");
			System.out.println("Grade deleted successfully with ID: " + savedGrade.getId());

			Optional<Grades> deletedCheck = gradeDAO.findById(savedGrade.getId());
			assertFalse(deletedCheck.isPresent(), "Deleted grade should not be found.");
			System.out.println("Grade deletion verified.");

		} finally {
			// Удаляем временного студента после всех тестов, чтобы все было четко на базе
			if (savedTempStudent != null && savedTempStudent.getId() != null) {
				studentDAO.deleteById(savedTempStudent.getId());
				System.out.println("Temporary student " + savedTempStudent.getId() + " deleted after grade tests.");
			}
		}

		// лучше тестировать с mvn test! а то не все тестируются
	}
}
