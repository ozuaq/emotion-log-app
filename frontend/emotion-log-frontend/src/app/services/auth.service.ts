import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

// ユーザー情報の型定義
export interface UserProfile {
  id: string;
  email: string;
  name?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:9000/api';
  private readonly TOKEN_KEY = 'auth_token';

  // 認証状態を管理するためのSignal
  public isAuthenticated = signal<boolean>(this.hasToken());
  public currentUser = signal<UserProfile | null>(null);

  constructor(private http: HttpClient, private router: Router) {
    // アプリケーション起動時にトークンがあればユーザー情報を取得
    if (this.hasToken()) {
      this.loadUserProfile();
    }
  }

  // --- 認証APIの呼び出し ---

  signUp(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, userData);
  }

  login(credentials: any): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => this.storeToken(response.token)) // 成功したらトークンを保存
    );
  }

  getProfile(): Observable<UserProfile> {
    // JWTをヘッダーに付けてプロフィール情報を取得
    // 注: このAPI呼び出しは後ほどHTTPインターセプターで共通化します
    return this.http.get<UserProfile>(`${this.apiUrl}/profile`);
  }

  // --- トークンと認証状態の管理 ---

  private storeToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.isAuthenticated.set(true);
    this.loadUserProfile(); // ログイン成功後にプロフィールを読み込む
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.isAuthenticated.set(false);
    this.currentUser.set(null);
    this.router.navigate(['/login']); // ログインページにリダイレクト
  }

  private loadUserProfile(): void {
    if (!this.hasToken()) return;

    this.getProfile().subscribe({
      next: (profile) => this.currentUser.set(profile),
      error: (err) => {
        // トークンが無効な場合などはログアウトさせる
        console.error('Failed to load user profile', err);
        this.logout();
      }
    });
  }
}
