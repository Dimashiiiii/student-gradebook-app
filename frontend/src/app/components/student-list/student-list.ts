// src/app/components/student-list/student-list.component.ts
import { Component, OnInit, ChangeDetectionStrategy, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

// NgRx Imports
import { Store } from '@ngrx/store';
import { AppState } from '../../store';
import * as StudentActions from '../../store/student/student.actions';
import * as StudentSelectors from '../../store/student/student.selectors';
import { LoadStudentsParams } from '../../store/student/student.actions';

// Angular Material module imports
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';

import { Student } from '../../models/student.model';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatToolbarModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDividerModule,
    MatTooltipModule,
    ReactiveFormsModule
  ],
  templateUrl: './student-list.html',
  styleUrl: './student-list.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StudentListComponent implements OnInit, OnDestroy {
  students$: Observable<Student[]>;
  loading$: Observable<boolean>;
  error$: Observable<any>;

  filterSortForm: FormGroup;
  private subscriptions = new Subscription();

  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'groupName', 'actions'];

  constructor(
    private router: Router,
    private store: Store<AppState>,
    private fb: FormBuilder
  ) {
    this.students$ = this.store.select(StudentSelectors.selectAllStudents);
    this.loading$ = this.store.select(StudentSelectors.selectStudentsLoading);
    this.error$ = this.store.select(StudentSelectors.selectStudentsError);

    this.filterSortForm = this.fb.group({
      firstNameFilter: [''],
      lastNameFilter: [''],
      sortBy: [''],
      sortOrder: ['asc']
    });
  }

  ngOnInit(): void {
    this.dispatchLoadStudents();

    this.subscriptions.add(
      this.filterSortForm.valueChanges.pipe(
        debounceTime(300),
        distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr))
      ).subscribe(() => {
        this.dispatchLoadStudents();
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  dispatchLoadStudents(): void {
    const params: LoadStudentsParams = this.filterSortForm.value;
    this.store.dispatch(StudentActions.loadStudents(params));
  }

  addStudent(): void {
    console.log('Add Student button clicked (NgRx)');
    this.router.navigate(['/students/add']);
  }

  editStudent(student: Student): void {
    console.log('Edit Student:', student, '(NgRx)');
    this.router.navigate(['/students/edit', student.id]);
  }

  viewGrades(studentId: number): void {
    console.log(`Navigating to grades for student ID: ${studentId}`);
    this.router.navigate(['/students', studentId, 'grades']);
  }

  deleteStudent(id: number): void {
    if (confirm('Вы уверены, что хотите удалить этого студента?')) {
      this.store.dispatch(StudentActions.deleteStudent({ id }));
    }
  }

  resetFiltersAndSort(): void {
    this.filterSortForm.reset({
      firstNameFilter: '',
      lastNameFilter: '',
      sortBy: '',
      sortOrder: 'asc'
    });
  }
}
