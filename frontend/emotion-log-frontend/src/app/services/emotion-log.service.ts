import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// 感情ログ作成時のリクエストボディ(ペイロード)の型
// バックエンドのEmotionLogPayloadと合わせる
export interface EmotionLogPayload {
  userId: string;
  logDate: string; // 'yyyy-MM-dd'形式の文字列
  emotionLevel: string; // 'very_good', 'good', 'neutral', 'bad', 'very_bad'
  memo?: string | null; // `?` はオプショナル、`| null` は明示的にnullを許容
}

// バックエンドから受け取る感情ログの型
export interface EmotionLog {
  id: number;
  userId: string;
  logDate: string;
  emotionLevel: string;
  memo: string | null;
  recordedAt: string; // ISO 8601 形式の文字列
  // createdAt と updatedAt もバックエンドから送られてくる場合は追加
}

@Injectable({
  providedIn: 'root'
})
export class EmotionLogService {
  private apiUrl = 'http://localhost:9000/api'; // バックエンドAPIのベースURL

  constructor(private http: HttpClient) { }

  /**
   * 感情ログを保存（新規作成または更新）する
   * @param logData 保存する感情ログのデータ
   */
  // ★メソッド名をcreateからsaveに変更
  saveEmotionLog(logData: EmotionLogPayload): Observable<{id: number}> {
    // POST /api/logs は upsert アクションを指している
    return this.http.post<{id: number}>(`${this.apiUrl}/logs`, logData);
  }

  /**
   * 特定ユーザーの感情ログ一覧を取得する
   * @param userId 取得するユーザーのID
   * @returns 感情ログの配列を含むObservable
   */
  getEmotionLogs(userId: string): Observable<EmotionLog[]> {
    return this.http.get<EmotionLog[]>(`${this.apiUrl}/logs/user/${userId}`);
  }
}
