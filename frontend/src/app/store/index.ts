// src/app/store/index.ts
import { ActionReducerMap, MetaReducer } from '@ngrx/store';
import { isDevMode } from '@angular/core';

// редюсер и состояние студентов
import { studentReducer, StudentsState } from './student/student.reducer'; 
import { gradeReducer, GradesState } from './grade/grade.reducer';

// Определяем основной интерфейс состояния всего приложения
export interface AppState {
  students: StudentsState; // состояние студентов
  grades: GradesState;
}

// Определяем карту редюсеров
export const reducers: ActionReducerMap<AppState> = {
  students: studentReducer, // редюсер студентов
  grades: gradeReducer,
};

// Мета-редюсеры (например, для логирования в режиме разработки)
export const metaReducers: MetaReducer<AppState>[] = isDevMode() ? [] : [];

// Для использования в app.config.ts
