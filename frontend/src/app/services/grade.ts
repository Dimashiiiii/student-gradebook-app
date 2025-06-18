// src/app/services/grade.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Grade } from '../models/grade.model';

@Injectable({
  providedIn: 'root'
})
export class GradeService {
  // Базовый URL для API. Благодаря прокси, мы можем использовать /api.
  // Бэкенд на 8888, фронтенд на 4200. Прокси перенаправит /api на 8888.
  private baseUrl = '/api'; // Базовый URL для всех API-запросов, который будет проксироваться

  constructor(private http: HttpClient) { }

  /**
   * Получает список всех оценок с бэкенда.
   * @returns Observable с массивом оценок.
   */
  getGrades(): Observable<Grade[]> {
    return this.http.get<Grade[]>(`${this.baseUrl}/grades`); // Пример: /api/grades
  }

  /**
   * Получает оценки для конкретного студента по его ID.
   * @param studentId ID студента.
   * @returns Observable с массивом оценок для этого студента.
   */
  getGradesByStudentId(studentId: number): Observable<Grade[]> {
    const url = `${this.baseUrl}/students/${studentId}/grades`; // Пример: /api/students/4/grades
    console.log(`GradeService: Sending GET request to: ${url}`);
    return this.http.get<Grade[]>(url);
  }

  /**
   * Получает оценку по ее ID.
   * @param id ID оценки.
   * @returns Observable с оценкой.
   */
  getGradeById(id: number): Observable<Grade> {
    return this.http.get<Grade>(`${this.baseUrl}/grades/${id}`);
  }

  /**
   * Создает новую оценку.
   * @param grade Объект оценки без ID (ID будет сгенерирован бэкендом).
   * @returns Observable с созданной оценкой (включая ID).
   */
  createGrade(grade: Omit<Grade, 'id'>): Observable<Grade> {
    // URL для POST-запроса: /api/students/{studentId}/grades
    const url = `${this.baseUrl}/students/${grade.studentId}/grades`;
    console.log(`GradeService: Sending POST request to: ${url} with data:`, grade); // Логирование для отладки
    return this.http.post<Grade>(url, grade);
  }

  /**
   * Обновляет существующую оценку.
   * @param grade Объект оценки с ID.
   * @returns Observable с обновленной оценкой.
   */
  updateGrade(grade: Grade): Observable<Grade> {
    const url = `${this.baseUrl}/grades/${grade.id}`;
    console.log(`GradeService: Sending PUT request to: ${url} with data:`, grade); // Логирование для отладки
    return this.http.put<Grade>(url, grade);
  }

  /**
   * Удаляет оценку по ее ID.
   * @param id ID оценки для удаления.
   * @returns Observable с результатом удаления (обычно пустой ответ).
   */
  deleteGrade(id: number): Observable<void> {
    const url = `${this.baseUrl}/grades/${id}`;
    console.log(`GradeService: Sending DELETE request to: ${url}`); // Логирование для отладки
    return this.http.delete<void>(url);
  }
}
