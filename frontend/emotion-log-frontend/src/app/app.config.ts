import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http'; // withInterceptorsをインポート
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor'; // 作成したインターセプターをインポート

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // ★ HttpClientにインターセプターを登録
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};
