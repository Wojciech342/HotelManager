import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginInfo } from '../../auth/login-info';
import { AuthService } from '../../auth/auth.service';
import { TokenStorageService } from '../../auth/token-storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  isLoginFailed = false;
  errorMessage = '';
  private loginInfo?: LoginInfo;

  public authService = inject(AuthService);
  private tokenStorage = inject(TokenStorageService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  ngOnInit() {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { username, password } = this.loginForm.value;
    this.loginInfo = new LoginInfo(username, password);

    this.authService.attemptAuth(this.loginInfo).subscribe({
      next: (data) => {
        if (data.token) {
          this.tokenStorage.saveToken(data.token);
        }
        if (data.username) {
          this.tokenStorage.saveUsername(data.username);
        }
        if (data.authorities) {
          this.tokenStorage.saveAuthorities(data.authorities);
        }

        this.isLoginFailed = false;
        this.loginForm.reset();
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.errorMessage = error.error.message;
        this.isLoginFailed = true;
      },
    });
  }
}
