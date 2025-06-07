import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService, EmotionLogPayload } from '../../services/api.service';

@Component({
  selector: 'app-emotion-form',
  standalone: true,
  imports: [
    CommonModule, // NgClassなどの基本的なディレクティブのためにインポート
    ReactiveFormsModule // リアクティブフォームを利用するためにインポート
  ],
  templateUrl: './emotion-form.component.html',
  styleUrls: ['./emotion-form.component.scss']
})
export class EmotionFormComponent {
  // サービスのインジェクション (新しい inject() 関数を使う方法)
  private fb = inject(FormBuilder);
  private apiService = inject(ApiService);

  // 感情レベルの選択肢
  emotionLevels = [
    { value: 'very_good', label: '最高😄', color: 'bg-yellow-400' },
    { value: 'good', label: '良い😊', color: 'bg-green-400' },
    { value: 'neutral', label: '普通😐', color: 'bg-blue-400' },
    { value: 'bad', label: '悪い😟', color: 'bg-purple-400' },
    { value: 'very_bad', label: '最悪😡', color: 'bg-red-400' }
  ];

  // フォームの状態を管理
  submissionStatus: 'idle' | 'submitting' | 'success' | 'error' = 'idle';
  errorMessage: string | null = null;

  // リアクティブフォームの定義
  emotionForm = this.fb.group({
    // emotionLevelは必須項目
    emotionLevel: ['', Validators.required],
    // memoは任意項目
    memo: ['']
  });

  // フォームのemotionLevelコントロールに簡単にアクセスするためのゲッター
  get selectedEmotionLevel() {
    return this.emotionForm.get('emotionLevel');
  }

  // 感情レベルボタンがクリックされたときの処理
  selectEmotion(level: string) {
    this.selectedEmotionLevel?.setValue(level);
  }

  // フォームの送信処理
  onSubmit(): void {
    // バリデーションチェック
    if (this.emotionForm.invalid) {
      console.error('Form is invalid');
      return;
    }

    this.submissionStatus = 'submitting';
    this.errorMessage = null;

    // フォームの値を取得
    const formValue = this.emotionForm.value;

    // APIに送信するデータを作成
    const payload: EmotionLogPayload = {
      userId: 'user123', // TODO: 将来的に動的なユーザーIDに置き換える
      emotionLevel: formValue.emotionLevel!, // `!` は値がnull/undefinedでないことをコンパイラに伝える
      memo: formValue.memo || null, // 空文字列の場合はnullを送る
      recordedAt: new Date().toISOString() // 現在時刻をISO文字列で設定
    };

    // APIサービスを呼び出す
    this.apiService.createEmotionLog(payload).subscribe({
      next: (response) => {
        console.log('Successfully created log with ID:', response.id);
        this.submissionStatus = 'success';
        this.emotionForm.reset(); // フォームをリセット
        // 3秒後にステータスをリセット
        setTimeout(() => this.submissionStatus = 'idle', 3000);
      },
      error: (err) => {
        console.error('Error creating log:', err);
        this.submissionStatus = 'error';
        this.errorMessage = '記録の保存中にエラーが発生しました。';
      }
    });
  }
}
