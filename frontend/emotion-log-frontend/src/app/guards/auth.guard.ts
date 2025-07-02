import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  // AuthServiceとRouterをインジェクトして取得
  const authService = inject(AuthService);
  const router = inject(Router);

  // AuthServiceの認証状態をチェック
  if (authService.isAuthenticated()) {
    // ログイン済みの場合、アクセスを許可
    return true;
  } else {
    // 未ログインの場合、ログインページにリダイレクトし、アクセスを拒否
    console.log('Access denied, redirecting to login...');
    router.navigate(['/login']);
    return false;
  }
};
