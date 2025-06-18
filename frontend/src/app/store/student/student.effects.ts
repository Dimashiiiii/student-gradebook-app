// src/app/store/student/student.effects.ts
import { Injectable, inject } from '@angular/core'; // inject для DI внутри createEffect
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import * as StudentActions from './student.actions'; // Экшены для студентов
import { StudentService } from '../../services/student.service'; // Сервис для работы с API
import { Router } from '@angular/router'; // Для навигации

@Injectable()
export class StudentEffects {
  constructor(private router: Router) {} // Внедряем Router в конструкторе

  // Эффект для загрузки студентов (с фильтрацией/сортировкой)
  loadStudents$ = createEffect(() => {
    const actions$ = inject(Actions);       // Получаем Actions через inject
    const studentService = inject(StudentService); // Получаем StudentService через inject

    return actions$.pipe(
      ofType(StudentActions.loadStudents), // Слушаем экшен 'loadStudents'
      mergeMap(action => // Action содержит параметры фильтрации/сортировки
        studentService.getStudents(action).pipe( // Выполняем GET-запрос с параметрами
          map(students => StudentActions.loadStudentsSuccess({ students })), // Успех: диспатчим success-экшен
          catchError(error => {
            console.error('Ошибка загрузки студентов:', error); // Логируем ошибку
            return of(StudentActions.loadStudentsFailure({ error })); // Ошибка: диспатчим failure-экшен
          })
        )
      )
    );
  });

  // Эффект для добавления студента
  addStudent$ = createEffect(() => {
    const actions$ = inject(Actions);
    const studentService = inject(StudentService);
    const router = inject(Router);

    return actions$.pipe(
      ofType(StudentActions.addStudent), // Слушаем экшен 'addStudent'
      mergeMap(action =>
        studentService.createStudent(action.student).pipe( // Выполняем POST-запрос для создания
          map(student => {
            console.log('Студент успешно добавлен!', student);
            router.navigate(['/students']); // Навигация к списку студентов
            return StudentActions.addStudentSuccess({ student }); // Успех: диспатчим success-экшен
          }),
          catchError(error => {
            console.error('Ошибка добавления студента:', error);
            return of(StudentActions.addStudentFailure({ error })); // Ошибка: диспатчим failure-экшен
          })
        )
      )
    );
  });

  // Эффект для обновления студента
  updateStudent$ = createEffect(() => {
    const actions$ = inject(Actions);
    const studentService = inject(StudentService);
    const router = inject(Router);

    return actions$.pipe(
      ofType(StudentActions.updateStudent), // Слушаем экшен 'updateStudent'
      mergeMap(action =>
        studentService.updateStudent(action.student).pipe( // Выполняем PUT-запрос для обновления
          map(student => {
            console.log('Студент успешно обновлен!', student);
            router.navigate(['/students']); // Навигация к списку студентов
            return StudentActions.updateStudentSuccess({ student }); // Успех: диспатчим success-экшен
          }),
          catchError(error => {
            console.error('Ошибка обновления студента:', error);
            return of(StudentActions.updateStudentFailure({ error })); // Ошибка: диспатчим failure-экшен
          })
        )
      )
    );
  });

  // Эффект для удаления студента
  deleteStudent$ = createEffect(() => {
    const actions$ = inject(Actions);
    const studentService = inject(StudentService);

    return actions$.pipe(
      ofType(StudentActions.deleteStudent), // Слушаем экшен 'deleteStudent'
      mergeMap(action =>
        studentService.deleteStudent(action.id).pipe( // Выполняем DELETE-запрос для удаления
          map(() => {
            console.log('Студент успешно удален!', action.id);
            return StudentActions.deleteStudentSuccess({ id: action.id }); // Успех: диспатчим success-экшен
          }),
          catchError(error => {
            console.error('Ошибка удаления студента:', error);
            return of(StudentActions.deleteStudentFailure({ error })); // Ошибка: диспатчим failure-экшен
          })
        )
      )
    );
  });
}
