// src/app/store/student/student.selectors.ts
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AppState } from '../index'; // корневое состояние AppState
import { StudentsState, studentAdapter } from './student.reducer'; // Импортируем состояние студентов и адаптер

// 1. Создаем Feature Selector
// Получает под-состояние 'students' из корневого AppState
export const selectStudentsState = createFeatureSelector<AppState, StudentsState>(
  'students'
);

// 2. Используем Entity Adapter для получения базовых селекторов
// getSelectors() возвращает объект со следующими селекторами:
// - selectIds: возвращает массив ID всех студентов (например, [1, 2, 3])
// - selectEntities: возвращает объект, где ключом является ID, а значением - объект студента
// - selectAll: возвращает массив всех студентов (как [Student, Student, ...])
// - selectTotal: возвращает общее количество студентов
const {
  selectIds,
  selectEntities,
  selectAll,
  selectTotal,
} = studentAdapter.getSelectors();

// 3. Создаем композитные селекторы
// Селектор для получения всех студентов (в виде массива)
export const selectAllStudents = createSelector(
  selectStudentsState,
  selectAll // Используем селектор `selectAll` от адаптера
);

// Селектор для получения флага загрузки
export const selectStudentsLoading = createSelector(
  selectStudentsState,
  (state: StudentsState) => state.loading
);

// Селектор для получения объекта ошибки
export const selectStudentsError = createSelector(
  selectStudentsState,
  (state: StudentsState) => state.error
);

// Селектор для получения конкретного студента по ID (если нужно)
// Это более продвинутый селектор, который принимает аргументы
export const selectStudentById = (studentId: number) => createSelector(
  selectStudentsState,
  (state: StudentsState) => state.entities[studentId]
);
