import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  errorMessage: string | null = null;

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) return;
    this.errorMessage = null;

    const credentials = this.loginForm.getRawValue();

    this.authService.login(credentials).subscribe({
      next: () => {
        // ログイン成功後、一覧ページに遷移
        this.router.navigate(['/logs']);
      },
      error: (err) => {
        console.error('Login failed', err);
        this.errorMessage = 'メールアドレスまたはパスワードが正しくありません。';
      }
    });
  }
}
