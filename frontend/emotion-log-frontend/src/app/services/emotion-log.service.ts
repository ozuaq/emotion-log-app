import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// 感情ログ保存時のリクエストペイロード (userIdが不要に)
export interface EmotionLogPayload {
  logDate: string;
  emotionLevel: string;
  memo?: string | null;
}

// バックエンドから受け取る感情ログの型
export interface EmotionLog {
  id: number;
  userId: number;
  logDate: string;
  emotionLevel: string;
  memo: string | null;
  recordedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmotionLogService {
  private apiUrl = 'http://localhost:9000/api';

  constructor(private http: HttpClient) { }

  /**
   * 感情ログを保存（新規作成または更新）する
   * @param logData 保存する感情ログのデータ
   */
  saveEmotionLog(logData: EmotionLogPayload): Observable<{ id: number }> {
    return this.http.post<{ id: number }>(`${this.apiUrl}/logs`, logData);
  }

  /**
   * 認証済みユーザーの感情ログ一覧を取得する
   */
  getEmotionLogs(): Observable<EmotionLog[]> {
    return this.http.get<EmotionLog[]>(`${this.apiUrl}/logs`);
  }
}
