import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common'; // DatePipeをインポート
import { ApiService, EmotionLog } from '../../services/api.service';

@Component({
  selector: 'app-emotion-list',
  standalone: true,
  imports: [CommonModule],
  providers: [DatePipe], // DatePipeをプロバイダーとして登録
  templateUrl: './emotion-list.component.html',
  styleUrls: ['./emotion-list.component.scss']
})
export class EmotionListComponent implements OnInit {
  private apiService = inject(ApiService);
  private datePipe = inject(DatePipe); // DatePipeをインジェクト

  // コンポーネントの状態を管理
  logs: EmotionLog[] = [];
  loadingState: 'loading' | 'loaded' | 'error' = 'loading';
  errorMessage: string | null = null;
  
  // 感情レベルと対応する表示スタイルをマッピング
  emotionDisplayMap: { [key: string]: { label: string; color: string; emoji: string } } = {
    'very_good': { label: '最高', color: 'bg-yellow-400', emoji: '😄' },
    'good':      { label: '良い', color: 'bg-green-400', emoji: '😊' },
    'neutral':   { label: '普通', color: 'bg-blue-400', emoji: '😐' },
    'bad':       { label: '悪い', color: 'bg-purple-400', emoji: '😟' },
    'very_bad':  { label: '最悪', color: 'bg-red-400', emoji: '😡' }
  };

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.loadingState = 'loading';
    const userId = 'user123'; // TODO: 将来的に動的なユーザーIDに置き換える

    this.apiService.getEmotionLogs(userId).subscribe({
      next: (data) => {
        this.logs = data;
        this.loadingState = 'loaded';
      },
      error: (err) => {
        console.error('Error fetching logs:', err);
        this.errorMessage = '記録の読み込み中にエラーが発生しました。';
        this.loadingState = 'error';
      }
    });
  }

  // emotionLevelに対応する表示情報を取得するヘルパーメソッド
  getEmotionDisplay(level: string) {
    return this.emotionDisplayMap[level] || { label: '不明', color: 'bg-gray-400', emoji: '🤷' };
  }

  // 日付をフォーマットするヘルパーメソッド
  formatDate(dateString: string): string {
    // 'yyyy/MM/dd HH:mm' の形式でフォーマット
    return this.datePipe.transform(dateString, 'yyyy/MM/dd HH:mm') || '';
  }
}
