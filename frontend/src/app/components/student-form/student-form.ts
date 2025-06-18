// src/app/components/student-form/student-form.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // директив NgIf/NgFor
import { ActivatedRoute, Router } from '@angular/router'; // работы с маршрутами (получение ID, навигация)
import { FormGroup, FormControl, Validators, ReactiveFormsModule, FormBuilder } from '@angular/forms';

// Импорты Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';

import { StudentService } from '../../services/student.service'; // Cервис
import { Student } from '../../models/student.model'; // модель студента

@Component({
  selector: 'app-student-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatToolbarModule,
    MatIconModule
  ],
  templateUrl: './student-form.html',
  styleUrl: './student-form.css'
})
export class StudentFormComponent implements OnInit {
  studentForm: FormGroup;
  isEditMode: boolean = false;
  studentId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private studentService: StudentService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    // Инициализация формы с использованием FormBuilder.group
    this.studentForm = this.fb.group({ 
      id: [null],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      groupName: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.studentId = +id;
        this.loadStudent(this.studentId);
      }
    });
  }

  loadStudent(id: number): void {
    this.studentService.getStudentById(id).subscribe({
      next: (student) => {
        this.studentForm.patchValue(student);
      },
      error: (err) => {
        console.error('Error loading student for edit:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.studentForm.valid) {
      const studentData: Student = this.studentForm.value;

      if (this.isEditMode && studentData.id) {
        this.studentService.updateStudent(studentData).subscribe({
          next: () => {
            console.log('Student updated successfully!');
            this.router.navigate(['/students']);
          },
          error: (err) => {
            console.error('Error updating student:', err);
          }
        });
      } else {
        const newStudent: Omit<Student, 'id'> = {
          firstName: studentData.firstName,
          lastName: studentData.lastName,
          groupName: studentData.groupName
        };
        this.studentService.createStudent(newStudent).subscribe({
          next: () => {
            console.log('Student created successfully!');
            this.router.navigate(['/students']);
          },
          error: (err) => {
            console.error('Error creating student:', err);
          }
        });
      }
    } else {
      console.warn('Form is invalid. Please fill in all required fields.');
    }
  }

  onCancel(): void {
    this.router.navigate(['/students']);
  }
}
