import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { SignupInfo } from '../../auth/signup-info';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  signupInfo?: SignupInfo;
  isSignedUp = false;
  isSignUpFailed = false;
  errorMessage = '';

  private authService = inject(AuthService);
  private fb = inject(FormBuilder);

  ngOnInit() {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { username, password } = this.registerForm.value;
    this.signupInfo = new SignupInfo(username, password);

    this.authService.signUp(this.signupInfo).subscribe({
      next: (data) => {
        this.isSignedUp = true;
        this.isSignUpFailed = false;
      },
      error: (error) => {
        this.errorMessage = error.error.message;
        this.isSignUpFailed = true;
      },
    });
  }
}
