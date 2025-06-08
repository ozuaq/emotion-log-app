// frontend/emotion-log-frontend/src/app/components/emotion-chart/emotion-chart.component.ts
import { Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
// ★ NgChartsModule を削除し、BaseChartDirective のみをインポート
import { BaseChartDirective } from 'ng2-charts';
import {
  Chart, // ★ Chart.jsのコアをインポート
  ChartConfiguration,
  ChartData,
  ChartType,
  // ★ 円グラフに必要な部品をインポート
  ArcElement,
  PieController,
  Legend,
  Tooltip,
} from 'chart.js';
import { EmotionLog } from '../../services/emotion-log.service';

@Component({
  selector: 'app-emotion-chart',
  standalone: true,
  // ★ NgChartsModule を BaseChartDirective に変更
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './emotion-chart.component.html',
  styleUrls: ['./emotion-chart.component.scss']
})
export class EmotionChartComponent implements OnChanges {
  // コンストラクタで、必要な部品をChart.jsに登録する
  constructor() {
    Chart.register(ArcElement, PieController, Legend, Tooltip);
  }

  // 親コンポーネントから感情ログのデータを受け取るためのInputプロパティ
  @Input() logs: EmotionLog[] = [];
  
  // ng2-chartsのディレクティブにアクセスするために使用
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;

  // 感情レベルと表示スタイルのマッピング
  private emotionDisplayMap: { [key: string]: { label: string; color: string; } } = {
    'very_good': { label: '最高', color: '#FFD700' }, // Gold
    'good':      { label: '良い', color: '#4CAF50' }, // Green
    'neutral':   { label: '普通', color: '#2196F3' }, // Blue
    'bad':       { label: '悪い', color: '#9C27B0' }, // Purple
    'very_bad':  { label: '最悪', color: '#F44336' }  // Red
  };

  // 円グラフの設定
  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'top',
      },
    },
  };
  public pieChartType: ChartType = 'pie';
  public pieChartData: ChartData<'pie', number[], string | string[]> = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: [],
      hoverBackgroundColor: [],
      borderColor: '#ffffff',
      borderWidth: 2,
    }]
  };

  // 親から渡されたlogsデータが変更されたときに実行されるライフサイクルフック
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['logs'] && this.logs.length > 0) {
      this.updateChartData();
    }
  }

  // ログデータを集計してグラフデータを更新するメソッド
  private updateChartData(): void {
    const emotionCounts = this.logs.reduce((acc, log) => {
      acc[log.emotionLevel] = (acc[log.emotionLevel] || 0) + 1;
      return acc;
    }, {} as { [key: string]: number });

    const labels: string[] = [];
    const data: number[] = [];
    const colors: string[] = [];

    for (const level in emotionCounts) {
      if (this.emotionDisplayMap[level]) {
        labels.push(this.emotionDisplayMap[level].label);
        data.push(emotionCounts[level]);
        colors.push(this.emotionDisplayMap[level].color);
      }
    }
    
    this.pieChartData = {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: colors,
        hoverBackgroundColor: colors,
        borderColor: '#ffffff',
        borderWidth: 2,
      }]
    };

    this.chart?.update();
  }
}
