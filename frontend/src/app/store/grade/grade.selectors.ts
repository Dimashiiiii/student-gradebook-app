// src/app/store/grade/grade.selectors.ts
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AppState } from '../index'; // корневое состояние AppState
import { GradesState, gradeAdapter } from './grade.reducer'; // состояние оценок и адаптер
import { Grade } from '../../models/grade.model'; 

// 1. Создаем Feature Selector
// Получает под-состояние 'grades' из корневого AppState
export const selectGradesState = createFeatureSelector<AppState, GradesState>(
  'grades'
);

// 2. Используем Entity Adapter для получения базовых селекторов
// getSelectors() возвращает объект со следующими селекторами:
// - selectIds: возвращает массив ID всех оценок
// - selectEntities: возвращает объект, где ключом является ID, а значением - объект оценки
// - selectAll: возвращает массив всех оценок
// - selectTotal: возвращает общее количество оценок
const {
  selectIds,
  selectEntities,
  selectAll,
  selectTotal,
} = gradeAdapter.getSelectors();

// 3. Создаем композитные селекторы

// Селектор для получения всех оценок (в виде массива)
export const selectAllGrades = createSelector(
  selectGradesState,
  selectAll // Используем селектор `selectAll` от адаптера
);

// Селектор для получения флага загрузки оценок
export const selectGradesLoading = createSelector(
  selectGradesState,
  (state: GradesState) => state.loading
);

// Селектор для получения объекта ошибки оценок
export const selectGradesError = createSelector(
  selectGradesState,
  (state: GradesState) => state.error
);

// Селектор для получения конкретной оценки по ID
export const selectGradeById = (gradeId: number) => createSelector(
  selectGradesState,
  (state: GradesState) => state.entities[gradeId]
);

// Селектор для получения оценок конкретного студента
// Этот селектор будет фильтровать все оценки по studentId
export const selectGradesByStudentId = (studentId: number) => createSelector(
  selectAllGrades, // Используем селектор, который уже получает все оценки
  (grades: Grade[]) => grades.filter(grade => grade.studentId === studentId)
);
