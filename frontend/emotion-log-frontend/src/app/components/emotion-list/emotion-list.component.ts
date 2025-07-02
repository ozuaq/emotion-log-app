import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { EmotionLogService, EmotionLog } from '../../services/emotion-log.service';
import { EmotionChartComponent } from '../emotion-chart/emotion-chart.component';

@Component({
  selector: 'app-emotion-list',
  standalone: true,
  imports: [CommonModule, EmotionChartComponent],
  providers: [DatePipe],
  templateUrl: './emotion-list.component.html',
  styleUrls: ['./emotion-list.component.scss']
})
export class EmotionListComponent implements OnInit {
  private emotionLogService = inject(EmotionLogService);
  private datePipe = inject(DatePipe);

  private allLogs: EmotionLog[] = [];
  public filteredLogs: EmotionLog[] = [];
  public currentMonth: Date = new Date();

  loadingState: 'loading' | 'loaded' | 'error' = 'loading';
  errorMessage: string | null = null;
  emotionDisplayMap: { [key: string]: { label: string; color: string; emoji: string } } = {
    'very_good': { label: '最高', color: 'bg-yellow-400', emoji: '😄' },
    'good': { label: '良い', color: 'bg-green-400', emoji: '😊' },
    'neutral': { label: '普通', color: 'bg-blue-400', emoji: '😐' },
    'bad': { label: '悪い', color: 'bg-purple-400', emoji: '😟' },
    'very_bad': { label: '最悪', color: 'bg-red-400', emoji: '😡' }
  };

  ngOnInit(): void {
    this.loadAllLogs();
  }

  loadAllLogs(): void {
    this.loadingState = 'loading';
    this.emotionLogService.getEmotionLogs().subscribe({
      next: (data) => {
        this.allLogs = data;
        this.filterLogsForCurrentMonth();
        this.loadingState = 'loaded';
      },
      error: (err) => {
        console.error('Error fetching logs:', err);
        this.errorMessage = '記録の読み込み中にエラーが発生しました。';
      }
    });
  }

  filterLogsForCurrentMonth(): void {
    const year = this.currentMonth.getFullYear();
    const month = this.currentMonth.getMonth();

    this.filteredLogs = this.allLogs.filter(log => {
      const logDate = new Date(log.logDate);
      return logDate.getFullYear() === year && logDate.getMonth() === month;
    });
  }

  changeMonth(offset: number): void {
    this.currentMonth.setMonth(this.currentMonth.getMonth() + offset);
    this.currentMonth = new Date(this.currentMonth);
    this.filterLogsForCurrentMonth();
  }

  get currentMonthDisplay(): string {
    return this.datePipe.transform(this.currentMonth, 'yyyy年 M月') || '';
  }

  getEmotionDisplay(level: string) {
    return this.emotionDisplayMap[level] || { label: '不明', color: 'bg-gray-400', emoji: '🤷' };
  }

  formatDate(dateString: string): string {
    return this.datePipe.transform(dateString, 'yyyy/MM/dd (E)') || '';
  }
}
