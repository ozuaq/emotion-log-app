import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// バックエンドAPIからのレスポンスの型を定義
export interface ApiResponse {
  message: string;
  status: string;
  timestamp: string;
}

// 感情ログ作成時のリクエストボディ(ペイロード)の型
// バックエンドのEmotionLogPayloadと合わせる
export interface EmotionLogPayload {
  userId: string;
  emotionLevel: string; // 'very_good', 'good', 'neutral', 'bad', 'very_bad'
  memo?: string | null; // `?` はオプショナル、`| null` は明示的にnullを許容
  recordedAt?: string; // ISO 8601形式の文字列 (e.g., "2025-06-08T10:00:00Z")
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

  /**
  * 新しい感情ログを作成する
  * @param logData 作成する感情ログのデータ
  * @returns 作成結果を含むObservable
  */
  createEmotionLog(logData: EmotionLogPayload): Observable<{ id: number }> {
    return this.http.post<{ id: number }>(`${this.apiUrl}/logs`, logData);
  }
}
