// src/app/services/student.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../models/student.model';
import { LoadStudentsParams } from '../store/student/student.actions';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private apiUrl = '/api/students'; // Базовый URL для API студентов

  constructor(private http: HttpClient) { }

  /**
   * Получает список студентов с бэкенда, с опциональной фильтрацией и сортировкой.
   * @param params Параметры фильтрации и сортировки.
   * @returns Observable с массивом студентов.
   */
  getStudents(params?: LoadStudentsParams): Observable<Student[]> {
    let httpParams = new HttpParams();

    // параметры фильтрации
    if (params?.firstNameFilter) {
      httpParams = httpParams.set('firstNameFilter', params.firstNameFilter);
    }
    if (params?.lastNameFilter) {
      httpParams = httpParams.set('lastNameFilter', params.lastNameFilter);
    }

    // параметры сортировки
    if (params?.sortBy) {
      httpParams = httpParams.set('sortBy', params.sortBy);
    }
    if (params?.sortOrder) {
      httpParams = httpParams.set('sortOrder', params.sortOrder);
    }

    console.log('StudentService: Making GET request to', this.apiUrl, 'with params:', httpParams.toString());
    return this.http.get<Student[]>(this.apiUrl, { params: httpParams }); // Передаем параметры в запрос
  }

  /**
   * Получает студента по его ID.
   * @param id ID студента.
   * @returns Observable с данными студента.
   */
  getStudentById(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/${id}`);
  }

  /**
   * Создает нового студента.
   * @param student Объект студента без ID.
   * @returns Observable с созданным студентом (включая ID).
   */
  createStudent(student: Omit<Student, 'id'>): Observable<Student> {
    return this.http.post<Student>(this.apiUrl, student);
  }

  /**
   * Обновляет существующего студента.
   * @param student Объект студента с ID.
   * @returns Observable с обновленным студентом.
   */
  updateStudent(student: Student): Observable<Student> {
    return this.http.put<Student>(`${this.apiUrl}/${student.id}`, student);
  }

  /**
   * Удаляет студента по его ID.
   * @param id ID студента для удаления.
   * @returns Observable с результатом удаления (обычно пустой ответ).
   */
  deleteStudent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
