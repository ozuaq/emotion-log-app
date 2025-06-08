import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { EmotionLogService, EmotionLog } from '../../services/emotion-log.service';
import { EmotionChartComponent } from '../emotion-chart/emotion-chart.component';

@Component({
  selector: 'app-emotion-list',
  standalone: true,
  imports: [CommonModule, EmotionChartComponent],
  providers: [DatePipe], // DatePipeをプロバイダーとして登録
  templateUrl: './emotion-list.component.html',
  styleUrls: ['./emotion-list.component.scss']
})
export class EmotionListComponent implements OnInit {
  private emotionLogService = inject(EmotionLogService);
  private datePipe = inject(DatePipe); // DatePipeをインジェクト

  private allLogs: EmotionLog[] = []; // 全てのログを保持
  public filteredLogs: EmotionLog[] = []; // フィルタリング後のログ
  public currentMonth: Date = new Date(); // 現在表示している月
  
  loadingState: 'loading' | 'loaded' | 'error' = 'loading';
  errorMessage: string | null = null;
  emotionDisplayMap: { [key: string]: { label: string; color: string; emoji: string } } = {
    'very_good': { label: '最高', color: 'bg-yellow-400', emoji: '😄' },
    'good':      { label: '良い', color: 'bg-green-400', emoji: '😊' },
    'neutral':   { label: '普通', color: 'bg-blue-400', emoji: '😐' },
    'bad':       { label: '悪い', color: 'bg-purple-400', emoji: '😟' },
    'very_bad':  { label: '最悪', color: 'bg-red-400', emoji: '😡' }
  };

  ngOnInit(): void {
    this.loadAllLogs();
  }

  // 全てのログを一度だけ取得するメソッド
  loadAllLogs(): void {
    this.loadingState = 'loading';
    const userId = 'user123'; // TODO: 動的なユーザーIDに

    this.emotionLogService.getEmotionLogs(userId).subscribe({
      next: (data) => {
        this.allLogs = data;
        this.filterLogsForCurrentMonth(); // 取得後に現在の月でフィルタリング
        this.loadingState = 'loaded';
      },
      error: (err) => {
        console.error('Error fetching logs:', err);
        this.errorMessage = '記録の読み込み中にエラーが発生しました。';
        this.loadingState = 'error';
      }
    });
  }

    /**
   * allLogsの中からcurrentMonthに合致するログをフィルタリングする
   */
  filterLogsForCurrentMonth(): void {
    const year = this.currentMonth.getFullYear();
    const month = this.currentMonth.getMonth(); // 0-11

    this.filteredLogs = this.allLogs.filter(log => {
      const logDate = new Date(log.logDate);
      return logDate.getFullYear() === year && logDate.getMonth() === month;
    });
  }

  /**
   * 表示する月を変更する
   * @param offset -1で前月、1で次月
   */
  changeMonth(offset: number): void {
    this.currentMonth.setMonth(this.currentMonth.getMonth() + offset);
    // Dateオブジェクトが変更されたことをAngularに検知させるために新しいインスタンスを作成
    this.currentMonth = new Date(this.currentMonth);
    this.filterLogsForCurrentMonth();
  }

  /**
   * 表示用の月の文字列を返す (例: 2025年6月)
   */
  get currentMonthDisplay(): string {
    return this.datePipe.transform(this.currentMonth, 'yyyy年 M月') || '';
  }

  // emotionLevelに対応する表示情報を取得するヘルパーメソッド
  getEmotionDisplay(level: string) {
    return this.emotionDisplayMap[level] || { label: '不明', color: 'bg-gray-400', emoji: '🤷' };
  }

    // 日付をフォーマットするヘルパーメソッド
  formatDate(dateString: string): string {
    // 'yyyy/MM/dd (E)' の形式でフォーマット (例: 2025/06/08 (日))
    return this.datePipe.transform(dateString, 'yyyy/MM/dd (E)') || '';
  }
}
