import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, formatDate } from '@angular/common';
import { EmotionLogService, EmotionLogPayload } from '../../services/emotion-log.service';

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
  private emotionLogService = inject(EmotionLogService);

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

    // 今日の日付を'yyyy-MM-dd'形式で取得
  today = formatDate(new Date(), 'yyyy-MM-dd', 'en-US');

  // リアクティブフォームの定義
  emotionForm = this.fb.group({
    // logDate、emotionLevelは必須項目
    logDate: [this.today, Validators.required],
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
      logDate: formValue.logDate!, // フォームから日付を取得
      emotionLevel: formValue.emotionLevel!, // `!` は値がnull/undefinedでないことをコンパイラに伝える
      memo: formValue.memo || null, // 空文字列の場合はnullを送る
    };

    this.emotionLogService.saveEmotionLog(payload).subscribe({
      next: (response) => {
        console.log('Successfully saved log with ID:', response.id);
        this.submissionStatus = 'success';
        // フォームのリセットはせず、成功メッセージだけ表示
        setTimeout(() => this.submissionStatus = 'idle', 3000);
      },
      error: (err) => {
        console.error('Error saving log:', err);
        this.submissionStatus = 'error';
        this.errorMessage = '記録の保存中にエラーが発生しました。';
      }
    });
  }
}
