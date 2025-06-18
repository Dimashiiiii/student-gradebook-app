// src/app/store/grade/grade.reducer.ts
import { createReducer, on } from '@ngrx/store';
import { EntityState, createEntityAdapter } from '@ngrx/entity'; // Для управления коллекциями сущностей
import { Grade } from '../../models/grade.model'; // Модель оценки
import * as GradeActions from './grade.actions'; // Экшены для оценок

// Интерфейс состояния оценок.
export interface GradesState extends EntityState<Grade> {
  selectedGradeId: number | null; // ID выбранной оценки
  loading: boolean;      // Флаг загрузки данных
  error: any;            // Объект ошибки
}

// Адаптер для сущностей оценок.
export const gradeAdapter = createEntityAdapter<Grade>({
  selectId: (grade: Grade) => grade.id! // 'id' как уникальный ключ
});

// Начальное состояние хранилища оценок.
export const initialGradesState: GradesState = gradeAdapter.getInitialState({
  selectedGradeId: null,
  loading: false,
  error: null,
});

// Редюсер для оценок.
export const gradeReducer = createReducer(
  initialGradesState,

  // При запросе загрузки всех оценок.
  on(GradeActions.loadGrades, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),

  // При запросе загрузки оценок по ID студента.
  on(GradeActions.loadGradesByStudentId, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),

  // При успешной загрузке оценок.
  on(GradeActions.loadGradesSuccess, (state, { grades }) =>
    gradeAdapter.setAll(grades, { // Заменяем все оценки
      ...state,
      loading: false,
      error: null,
    })
  ),

  // При ошибке загрузки оценок.
  on(GradeActions.loadGradesFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error: error,
  })),

  // При успешном добавлении оценки.
  on(GradeActions.addGradeSuccess, (state, { grade }) =>
    gradeAdapter.addOne(grade, { // Добавляем одну оценку
      ...state,
      loading: false,
      error: null,
    })
  ),

  // При ошибке добавления оценки.
  on(GradeActions.addGradeFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error: error,
  })),

  // При успешном обновлении оценки.
  on(GradeActions.updateGradeSuccess, (state, { grade }) =>
    gradeAdapter.upsertOne(grade, { // Обновляем или добавляем оценку
      ...state,
      loading: false,
      error: null,
    })
  ),

  // При ошибке обновления оценки.
  on(GradeActions.updateGradeFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error: error,
  })),

  // При успешном удалении оценки.
  on(GradeActions.deleteGradeSuccess, (state, { id }) =>
    gradeAdapter.removeOne(id, { // Удаляем оценку по ID
      ...state,
      loading: false,
      error: null,
    })
  ),

  // При ошибке удаления оценки.
  on(GradeActions.deleteGradeFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error: error,
  }))
);
