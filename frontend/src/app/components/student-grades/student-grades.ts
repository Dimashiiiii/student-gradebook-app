// src/app/components/student-grades/student-grades.component.ts
import { Component, OnInit, ChangeDetectionStrategy, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Observable, Subscription, combineLatest } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';

// Angular Material Imports
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';

// NgRx Imports
import { Store } from '@ngrx/store';
import { AppState } from '../../store';
import * as StudentActions from '../../store/student/student.actions';
import * as StudentSelectors from '../../store/student/student.selectors';
import * as GradeActions from '../../store/grade/grade.actions';
import * as GradeSelectors from '../../store/grade/grade.selectors';

import { Student } from '../../models/student.model';
import { Grade } from '../../models/grade.model';

@Component({
  selector: 'app-student-grades',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatToolbarModule,
    MatDividerModule,
    MatTooltipModule
  ],
  templateUrl: './student-grades.html',
  styleUrl: './student-grades.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StudentGradesComponent implements OnInit, OnDestroy {
  student$: Observable<Student | undefined>;
  grades$: Observable<Grade[]>;
  loadingStudents$: Observable<boolean>;
  loadingGrades$: Observable<boolean>;
  errorStudents$: Observable<any>;
  errorGrades$: Observable<any>;

  gradeForm: FormGroup;
  isEditMode: boolean = false;
  currentGradeId: number | null = null;
  currentStudentId: number | null = null;

  displayedColumns: string[] = ['subject', 'score', 'date', 'actions'];

  private subscriptions = new Subscription();

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private store: Store<AppState>
  ) {
    this.gradeForm = this.fb.group({
      id: [null],
      studentId: [null, Validators.required],
      subject: ['', Validators.required],
      score: [null, [Validators.required, Validators.min(0), Validators.max(100)]],
      gradeDate: [new Date(), Validators.required]
    });

    this.loadingStudents$ = this.store.select(StudentSelectors.selectStudentsLoading);
    this.errorStudents$ = this.store.select(StudentSelectors.selectStudentsError);
    this.loadingGrades$ = this.store.select(GradeSelectors.selectGradesLoading);
    this.errorGrades$ = this.store.select(GradeSelectors.selectGradesError);

    this.student$ = this.route.paramMap.pipe(
      map(params => params.get('id')),
      filter(id => !!id),
      map(id => +id!),
      tap(studentId => {
        this.currentStudentId = studentId;
        this.store.dispatch(StudentActions.loadStudents({}));
        this.store.dispatch(GradeActions.loadGradesByStudentId({ studentId }));
        this.gradeForm.patchValue({ studentId: this.currentStudentId });
      }),
      switchMap(studentId => this.store.select(StudentSelectors.selectStudentById(studentId)))
    );

    this.grades$ = combineLatest([
      this.route.paramMap.pipe(
        map(params => params.get('id')),
        filter(id => !!id),
        map(id => +id!)
      ),
      this.store.select(GradeSelectors.selectAllGrades)
    ]).pipe(
      map(([studentId, allGrades]) => allGrades.filter(grade => grade.studentId === studentId)),
      tap(grades => console.log('Grades for current student:', grades))
    );
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.route.paramMap.pipe(
        map(params => params.get('id')),
        filter(id => !!id),
        map(id => +id!),
        tap(studentId => {
          console.log(`ngOnInit: Dispatching loadGradesByStudentId for student ID: ${studentId}`);
          this.store.dispatch(GradeActions.loadGradesByStudentId({ studentId }));
        })
      ).subscribe()
    );

    this.subscriptions.add(
      this.student$.subscribe(student => {
        if (!student) {
          this.store.dispatch(StudentActions.loadStudents({}));
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  editGrade(grade: Grade): void {
    this.isEditMode = true;
    this.currentGradeId = grade.id || null;
    this.gradeForm.patchValue({
      id: grade.id,
      studentId: grade.studentId,
      subject: grade.subject,
      score: grade.score,
      gradeDate: new Date(grade.gradeDate)
    });
    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
  }

  onSubmit(): void {
    if (this.gradeForm.valid) {
      const gradeData: Grade = {
        ...this.gradeForm.value,
        gradeDate: this.gradeForm.value.gradeDate.toISOString().split('T')[0]
      };

      if (this.isEditMode && gradeData.id) {
        this.store.dispatch(GradeActions.updateGrade({ grade: gradeData }));
      } else {
        if (!gradeData.studentId && this.currentStudentId) {
            gradeData.studentId = this.currentStudentId;
        }
        this.store.dispatch(GradeActions.addGrade({ grade: gradeData }));
      }
      this.resetForm();
    } else {
      console.warn('Grade form is invalid.');
      this.gradeForm.markAllAsTouched();
    }
  }

  deleteGrade(id: number): void {
    if (confirm('Вы уверены, что хотите удалить эту оценку?')) {
      this.store.dispatch(GradeActions.deleteGrade({ id }));
    }
  }

  resetForm(): void {
    this.isEditMode = false;
    this.currentGradeId = null;
    this.gradeForm.reset({
      id: null,
      studentId: this.currentStudentId,
      subject: '',
      score: null,
      gradeDate: new Date()
    });
    this.gradeForm.markAsUntouched();
  }

  goBackToStudents(): void {
    this.router.navigate(['/students']);
  }
}
