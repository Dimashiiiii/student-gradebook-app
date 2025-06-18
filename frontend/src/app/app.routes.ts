// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { StudentListComponent } from './components/student-list/student-list';
import { StudentFormComponent } from './components/student-form/student-form';
import { StudentGradesComponent } from './components/student-grades/student-grades';


export const routes: Routes = [
  { path: 'students', component: StudentListComponent },
  { path: 'students/add', component: StudentFormComponent },     // <-- Маршрут для добавления
  { path: 'students/edit/:id', component: StudentFormComponent }, // <-- Маршрут для редактирования с ID
  { path: 'students/:id/grades', component: StudentGradesComponent },
  { path: '', redirectTo: '/students', pathMatch: 'full' }, // Перенаправление по умолчанию
  // { path: '**', component: NotFoundComponent }
];
