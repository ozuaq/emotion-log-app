import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const authToken = authService.getToken();

  // トークンが存在する場合
  if (authToken) {
    // 元のリクエストをクローンし、Authorizationヘッダーを追加
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${authToken}`
      }
    });
    // 新しいリクエストで処理を続行
    return next(authReq);
  }

  // トークンがない場合は、元のリクエストをそのまま流す
  return next(req);
};
