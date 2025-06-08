import { Routes } from '@angular/router';
import { EmotionFormComponent } from './components/emotion-form/emotion-form.component';
import { EmotionListComponent } from './components/emotion-list/emotion-list.component';

export const routes: Routes = [
  // /new パスにアクセスしたらEmotionFormComponentを表示する
  { path: 'new', component: EmotionFormComponent },
   // /logs パスにアクセスしたらEmotionListComponentを表示
  { path: 'logs', component: EmotionListComponent },
  // デフォルトのルートを設定（一覧ページをデフォルトにするなど変更も可能）
  { path: '', redirectTo: '/logs', pathMatch: 'full' },
];
