import { Routes } from '@angular/router';
import { EmotionFormComponent } from './components/emotion-form/emotion-form.component';
import { EmotionListComponent } from './components/emotion-list/emotion-list.component';
import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { authGuard } from './guards/auth.guard'; // ★ 作成したガードをインポート

export const routes: Routes = [
  // 認証関連のルート
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },

  // ★ 認証が必要なルート
  {
    path: 'new',
    component: EmotionFormComponent,
    canActivate: [authGuard] // ★ このルートにガードを適用
  },
  {
    path: 'logs',
    component: EmotionListComponent,
    canActivate: [authGuard] // ★ このルートにガードを適用
  },

  // デフォルトルート
  { path: '', redirectTo: '/logs', pathMatch: 'full' },
  // どのルートにも一致しない場合
  { path: '**', redirectTo: '/logs' }
];
