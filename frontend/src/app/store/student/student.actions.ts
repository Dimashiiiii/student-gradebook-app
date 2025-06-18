// src/app/store/student/student.actions.ts
import { createAction, props } from '@ngrx/store';
import { Student } from '../../models/student.model'; // Модель студента

// Интерфейс для параметров загрузки студентов (фильтрация/сортировка)
export interface LoadStudentsParams {
  firstNameFilter?: string;
  lastNameFilter?: string;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

// Запросить загрузку студентов с опциональными параметрами.
export const loadStudents = createAction(
  '[Students Page] Load Students',
  props<LoadStudentsParams>() // Принимаем параметры
);

// Успешная загрузка студентов.
export const loadStudentsSuccess = createAction(
  '[Students API] Load Students Success',
  props<{ students: Student[] }>()
);

// Ошибка при загрузке студентов.
export const loadStudentsFailure = createAction(
  '[Students API] Load Students Failure',
  props<{ error: any }>()
);

// Запросить добавление студента.
export const addStudent = createAction(
  '[Student Form] Add Student',
  props<{ student: Omit<Student, 'id'> }>() // Передаем студента без ID
);

// Успешное добавление студента.
export const addStudentSuccess = createAction(
  '[Student API] Add Student Success',
  props<{ student: Student }>()
);

// Ошибка при добавлении студента.
export const addStudentFailure = createAction(
  '[Student API] Add Student Failure',
  props<{ error: any }>()
);

// Запросить обновление студента.
export const updateStudent = createAction(
  '[Student Form] Update Student',
  props<{ student: Student }>()
);

// Успешное обновление студента.
export const updateStudentSuccess = createAction(
  '[Student API] Update Student Success',
  props<{ student: Student }>()
);

// Ошибка при обновлении студента.
export const updateStudentFailure = createAction(
  '[Student API] Update Student Failure',
  props<{ error: any }>()
);

// Запросить удаление студента.
export const deleteStudent = createAction(
  '[Student List] Delete Student',
  props<{ id: number }>()
);

// Успешное удаление студента.
export const deleteStudentSuccess = createAction(
  '[Student API] Delete Student Success',
  props<{ id: number }>()
);

// Ошибка при удалении студента.
export const deleteStudentFailure = createAction(
  '[Student API] Delete Student Failure',
  props<{ error: any }>()
);
