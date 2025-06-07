import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// バックエンドAPIからのレスポンスの型を定義
export interface ApiResponse {
  message: string;
  status: string;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:9000/api'; // バックエンドAPIのベースURL

  constructor(private http: HttpClient) { }

  // バックエンドの /api エンドポイントからデータを取得するメソッド
  getApiData(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(this.apiUrl);
  }

  // バックエンドの /api/health エンドポイントからデータを取得するメソッド
  getHealthCheck(): Observable<{ status: string }> {
    return this.http.get<{ status: string }>(`${this.apiUrl}/health`);
  }
}

