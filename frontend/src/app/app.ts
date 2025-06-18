// src/app/app.component.ts (или src/app/app.ts)
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    MatToolbarModule
  ],
  templateUrl: './app.html', 
  styleUrl: './app.css' 
})
export class App {
  protected title = 'frontend';
}