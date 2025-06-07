import { Routes } from '@angular/router';
import { EmotionFormComponent } from './components/emotion-form/emotion-form.component';

export const routes: Routes = [
  // /new パスにアクセスしたらEmotionFormComponentを表示する
  { path: 'new', component: EmotionFormComponent },
  
  // デフォルトのルートを設定（例：/newにリダイレクト）
  { path: '', redirectTo: '/new', pathMatch: 'full' },

  // TODO: 将来的にログ一覧ページなどを追加
  // { path: 'logs', component: EmotionLogListComponent },
];
