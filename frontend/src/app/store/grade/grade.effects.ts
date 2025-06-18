// src/app/store/grade/grade.effects.ts
import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, tap } from 'rxjs/operators';
import * as GradeActions from './grade.actions'; // Экшены для оценок
import { GradeService } from '../../services/grade'; // Сервис для работы с API 
import { Router } from '@angular/router'; // Для навигации

@Injectable()
export class GradeEffects {
  constructor(
    // Сервисы внедряются через конструктор, если они используются напрямую в конструкторе,
    // или через inject() внутри функций createEffect.
    private router: Router // Используется для навигации после операций
  ) {}

  // Эффект для загрузки всех оценок
  loadAllGrades$ = createEffect(() => {
    const actions$ = inject(Actions);
    const gradeService = inject(GradeService);

    return actions$.pipe(
      ofType(GradeActions.loadGrades), // Слушаем экшен 'loadGrades'
      mergeMap(() =>
        gradeService.getGrades().pipe( // HTTP GET-запрос для всех оценок
          map(grades => GradeActions.loadGradesSuccess({ grades })), // Успех: диспатчим success-экшен
          catchError(error => {
            console.error('Ошибка загрузки всех оценок:', error); // Логируем ошибку
            return of(GradeActions.loadGradesFailure({ error })); // Ошибка: диспатчим failure-экшен
          })
        )
      )
    );
  });

  // Эффект для загрузки оценок по ID студента
  loadGradesByStudentId$ = createEffect(() => {
    const actions$ = inject(Actions);
    const gradeService = inject(GradeService);

    return actions$.pipe(
      ofType(GradeActions.loadGradesByStudentId), // Слушаем экшен 'loadGradesByStudentId'
      mergeMap(action =>
        gradeService.getGradesByStudentId(action.studentId).pipe( // HTTP GET-запрос по studentId
          map(grades => GradeActions.loadGradesSuccess({ grades })), // Успех: диспатчим success-экшен
          catchError(error => {
            console.error(`Ошибка загрузки оценок для студента ${action.studentId}:`, error);
            return of(GradeActions.loadGradesFailure({ error })); // Ошибка: диспатчим failure-экшен
          })
        )
      )
    );
  });

  // Эффект для добавления оценки
  addGrade$ = createEffect(() => {
    const actions$ = inject(Actions);
    const gradeService = inject(GradeService);
    const router = inject(Router);

    return actions$.pipe(
      ofType(GradeActions.addGrade), // Слушаем экшен 'addGrade'
      mergeMap(action =>
        gradeService.createGrade(action.grade).pipe( // HTTP POST-запрос для создания оценки
          map(grade => {
            console.log(`Оценка для студента ${grade.studentId} успешно добавлена!`, grade);
            router.navigate(['/students']); // Навигация к списку студентов
            return GradeActions.addGradeSuccess({ grade }); // Успех: диспатчим success-экшен
          }),
          catchError(error => {
            console.error(`Ошибка добавления оценки для студента ${action.grade.studentId}:`, error);
            return of(GradeActions.addGradeFailure({ error })); // Ошибка: диспатчим failure-экшен
          })
        )
      )
    );
  });

  // Эффект для обновления оценки
  updateGrade$ = createEffect(() => {
    const actions$ = inject(Actions);
    const gradeService = inject(GradeService);
    const router = inject(Router);

    return actions$.pipe(
      ofType(GradeActions.updateGrade), // Слушаем экшен 'updateGrade'
      mergeMap(action =>
        gradeService.updateGrade(action.grade).pipe( // HTTP PUT-запрос для обновления оценки
          map(grade => {
            console.log(`Оценка с ID: ${grade.id} успешно обновлена!`, grade);
            router.navigate(['/students']); // Навигация к списку студентов
            return GradeActions.updateGradeSuccess({ grade }); // Успех: диспатчим success-экшен
          }),
          catchError(error => {
            console.error(`Ошибка обновления оценки с ID: ${action.grade.id}:`, error);
            return of(GradeActions.updateGradeFailure({ error })); // Ошибка: диспатчим failure-экшен
          })
        )
      )
    );
  });

  // Эффект для удаления оценки
  deleteGrade$ = createEffect(() => {
    const actions$ = inject(Actions);
    const gradeService = inject(GradeService);

    return actions$.pipe(
      ofType(GradeActions.deleteGrade), // Слушаем экшен 'deleteGrade'
      tap(action => console.log(`Попытка удалить оценку с ID: ${action.id}`)), // Лог начала запроса
      mergeMap(action =>
        gradeService.deleteGrade(action.id).pipe( // HTTP DELETE-запрос для удаления
          map(() => {
            console.log(`Оценка с ID: ${action.id} успешно удалена!`); // Лог успешного удаления
            return GradeActions.deleteGradeSuccess({ id: action.id }); // Успех: диспатчим success-экшен
          }),
          catchError(error => {
            console.error(`Ошибка удаления оценки с ID: ${action.id}:`, error); // Лог ошибки
            return of(GradeActions.deleteGradeFailure({ error })); // Ошибка: диспатчим failure-экшен
          })
        )
      )
    );
  });
}
