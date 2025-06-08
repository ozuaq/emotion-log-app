// frontend/emotion-log-frontend/src/app/app.ts
import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true, // スタンドアロンコンポーネント
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss' // styleUrl は単数形が一般的
})
export class App {
  protected title = 'emotion-log-frontend'; // title は protected のままでも良い
  error: any = null;

  constructor() { }
}
