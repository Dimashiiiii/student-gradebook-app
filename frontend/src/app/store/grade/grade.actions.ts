// src/app/store/grade/grade.actions.ts
import { createAction, props } from '@ngrx/store'; // Для создания экшенов NgRx
import { Grade } from '../../models/grade.model'; // Модель оценки

// --- Экшены загрузки оценок ---

// Запросить загрузку всех оценок.
export const loadGrades = createAction(
  '[Grades Page] Load All Grades'
);

// Запросить загрузку оценок для конкретного студента по ID.
export const loadGradesByStudentId = createAction(
  '[Student Grades Page] Load Grades By Student ID',
  props<{ studentId: number }>()
);

// Успешная загрузка оценок из API.
export const loadGradesSuccess = createAction(
  '[Grades API] Load Grades Success',
  props<{ grades: Grade[] }>()
);

// Ошибка при загрузке оценок.
export const loadGradesFailure = createAction(
  '[Grades API] Load Grades Failure',
  props<{ error: any }>()
);

// --- Экшены добавления оценки ---

// Запросить добавление новой оценки.
export const addGrade = createAction(
  '[Grade Form] Add Grade',
  props<{ grade: Omit<Grade, 'id'> }>()
);

// Успешное добавление оценки.
export const addGradeSuccess = createAction(
  '[Grades API] Add Grade Success',
  props<{ grade: Grade }>()
);

// Ошибка при добавлении оценки.
export const addGradeFailure = createAction(
  '[Grades API] Add Grade Failure',
  props<{ error: any }>()
);

// --- Экшены обновления оценки ---

// Запросить обновление существующей оценки.
export const updateGrade = createAction(
  '[Grade Form] Update Grade',
  props<{ grade: Grade }>()
);

// Успешное обновление оценки.
export const updateGradeSuccess = createAction(
  '[Grades API] Update Grade Success',
  props<{ grade: Grade }>()
);

// Ошибка при обновлении оценки.
export const updateGradeFailure = createAction(
  '[Grades API] Update Grade Failure',
  props<{ error: any }>()
);

// --- Экшены удаления оценки ---

// Запросить удаление оценки по ID.
export const deleteGrade = createAction(
  '[Grade List] Delete Grade',
  props<{ id: number }>()
);

// Успешное удаление оценки.
export const deleteGradeSuccess = createAction(
  '[Grades API] Delete Grade Success',
  props<{ id: number }>()
);

// Ошибка при удалении оценки.
export const deleteGradeFailure = createAction(
  '[Grades API] Delete Grade Failure',
  props<{ error: any }>()
);
