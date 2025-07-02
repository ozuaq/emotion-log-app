import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, formatDate } from '@angular/common';
import { EmotionLogService, EmotionLogPayload } from '../../services/emotion-log.service';

@Component({
  selector: 'app-emotion-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './emotion-form.component.html',
  styleUrls: ['./emotion-form.component.scss']
})
export class EmotionFormComponent {
  private fb = inject(FormBuilder);
  private emotionLogService = inject(EmotionLogService);

  emotionLevels = [
    { value: 'very_good', label: '最高😄', color: 'bg-yellow-400' },
    { value: 'good', label: '良い😊', color: 'bg-green-400' },
    { value: 'neutral', label: '普通😐', color: 'bg-blue-400' },
    { value: 'bad', label: '悪い😟', color: 'bg-purple-400' },
    { value: 'very_bad', label: '最悪😡', color: 'bg-red-400' }
  ];
  submissionStatus: 'idle' | 'submitting' | 'success' | 'error' = 'idle';
  errorMessage: string | null = null;

  today = formatDate(new Date(), 'yyyy-MM-dd', 'en-US');

  emotionForm = this.fb.group({
    logDate: [this.today, Validators.required],
    emotionLevel: ['', Validators.required],
    memo: ['']
  });

  get selectedEmotionLevel() { return this.emotionForm.get('emotionLevel'); }
  selectEmotion(level: string) { this.selectedEmotionLevel?.setValue(level); }

  onSubmit(): void {
    if (this.emotionForm.invalid) { return; }

    this.submissionStatus = 'submitting';
    this.errorMessage = null;
    const formValue = this.emotionForm.value;

    const payload: EmotionLogPayload = {
      logDate: formValue.logDate!,
      emotionLevel: formValue.emotionLevel!,
      memo: formValue.memo || null
    };

    this.emotionLogService.saveEmotionLog(payload).subscribe({
      next: (response) => {
        console.log('Successfully saved log with ID:', response.id);
        this.submissionStatus = 'success';
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
