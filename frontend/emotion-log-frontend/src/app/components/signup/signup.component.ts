import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // フォームの状態を管理
  submissionStatus: 'idle' | 'submitting' | 'success' | 'error' = 'idle';
  errorMessage: string | null = null;

  signupForm = this.fb.group({
    name: [''], // 名前は任意項目
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  onSubmit(): void {
    if (this.signupForm.invalid) return;

    this.submissionStatus = 'submitting';
    this.errorMessage = null;

    const signupData = this.signupForm.getRawValue();

    this.authService.signUp(signupData).subscribe({
      next: () => {
        this.submissionStatus = 'success';
        // 成功したら、2秒後にログインページに自動で遷移する
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        console.error('Signup failed', err);
        if (err.status === 409) { // 409 Conflict (メールアドレス重複)
          this.errorMessage = 'このメールアドレスは既に使用されています。';
        } else {
          this.errorMessage = '登録中にエラーが発生しました。時間をおいて再度お試しください。';
        }
        this.submissionStatus = 'error';
      }
    });
  }
}
