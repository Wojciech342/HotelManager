import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'], // optional
})
export class LoginComponent {
  // our Reactive Form
  loginForm: FormGroup;

  // for displaying a server-side error message
  errorMsg: string | null = null;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    // build the form
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    const { username, password } = this.loginForm.value;
    this.auth.login(username, password).subscribe({
      next: () => {
        // redirect after successful login
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        // show error message
        this.errorMsg = err.error?.message || 'Login failed';
      },
    });
  }
}
