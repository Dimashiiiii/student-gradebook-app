// src/app/models/grade.model.ts

export interface Grade {
  id?: number;
  studentId: number;
  subject: string;
  score: number;
  gradeDate: string;        
}
