// frontend/emotion-log-frontend/src/app/app.ts
import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { JsonPipe } from '@angular/common'; // JsonPipe をインポート
import { ApiService, ApiResponse } from './services/api.service'; // 作成したサービスと型をインポート

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
export class App implements OnInit {
  protected title = 'emotion-log-frontend'; // title は protected のままでも良い
  apiData: ApiResponse | null = null;
  healthStatus: string | null = null;
  error: any = null;

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.apiService.getApiData().subscribe({
      next: (data) => {
        this.apiData = data;
        console.log('API Data:', data);
      },
      error: (err) => {
        this.error = err;
        console.error('Error fetching API data:', err);
      }
    });

    this.apiService.getHealthCheck().subscribe({
      next: (data) => {
        this.healthStatus = data.status;
        console.log('Health Check:', data);
      },
      error: (err) => {
        this.error = err;
        console.error('Error fetching health check:', err);
      }
    });
  }
}
