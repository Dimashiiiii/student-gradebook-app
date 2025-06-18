// src/app/store/student/student.reducer.ts
import { createReducer, on } from '@ngrx/store';
import { EntityState, createEntityAdapter } from '@ngrx/entity'; // Для управления коллекцией сущностей
import { Student } from '../../models/student.model'; // модель студента
import * as StudentActions from './student.actions'; // все Actions для студентов

// Определяем интерфейс состояния для студентов
export interface StudentsState extends EntityState<Student> {
  selectedStudentId: number | null; // ID выбранного студента
  loading: boolean;      // Флаг загрузки данных
  error: any;            // Объект ошибки
}

// Создаем адаптер для сущностей студентов.
export const studentAdapter = createEntityAdapter<Student>();

// Определяем начальное состояние
export const initialStudentsState: StudentsState = studentAdapter.getInitialState({
  selectedStudentId: null,
  loading: false,
  error: null,
});

// Создаем редюсер для студентов
export const studentReducer = createReducer(
  initialStudentsState,

  // Обработка Action: loadStudents (при запросе загрузки)
  on(StudentActions.loadStudents, (state) => ({
    ...state,
    loading: true, // Устанавливаем флаг загрузки в true
    error: null,   // Сбрасываем ошибки
  })),

  // Обработка Action: loadStudentsSuccess (при успешной загрузке)
  on(StudentActions.loadStudentsSuccess, (state, { students }) =>
    studentAdapter.setAll(students, { // Заменяем всех студентов в Store
      ...state,
      loading: false, // Загрузка завершена
      error: null,
    })
  ),

  // Обработка Action: loadStudentsFailure (при ошибке загрузки)
  on(StudentActions.loadStudentsFailure, (state, { error }) => ({
    ...state,
    loading: false, // Загрузка завершена с ошибкой
    error: error,    // Сохраняем ошибку
  })),

  // Обработка Action: addStudentSuccess (при успешном добавлении)
  on(StudentActions.addStudentSuccess, (state, { student }) =>
    studentAdapter.addOne(student, { // Добавляем одного студента
      ...state,
      loading: false,
      error: null,
    })
  ),

  // Обработка Action: addStudentFailure (при ошибке добавления)
  on(StudentActions.addStudentFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error: error,
  })),

  // Обработка Action: updateStudentSuccess (при успешном обновлении)
  on(StudentActions.updateStudentSuccess, (state, { student }) =>
    studentAdapter.upsertOne(student, { // Обновляем или добавляем студента
      ...state,
      loading: false,
      error: null,
    })
  ),

  // Обработка Action: updateStudentFailure (при ошибке обновления)
  on(StudentActions.updateStudentFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error: error,
  })),

  // Обработка Action: deleteStudentSuccess (при успешном удалении)
  on(StudentActions.deleteStudentSuccess, (state, { id }) =>
    studentAdapter.removeOne(id, { // Удаляем студента по ID
      ...state,
      loading: false,
      error: null,
    })
  ),

  // Обработка Action: deleteStudentFailure (при ошибке удаления)
  on(StudentActions.deleteStudentFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error: error,
  }))
);
