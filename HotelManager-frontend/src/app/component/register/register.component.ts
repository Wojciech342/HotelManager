import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { SignupInfo } from '../../auth/signup-info';
import { AbstractControl, ValidationErrors } from '@angular/forms';

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
      username: ['', [this.usernameValidator]],
      password: ['', [this.passwordValidator]],
    });
  }

  usernameValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';
    if (!value) return { required: true };
    if (!value.trim()) return { whitespace: true };
    if (value.length < 3 || value.length > 20) return { length: true };
    if (!/^[a-zA-Z0-9]+$/.test(value)) return { invalidChars: true };
    return null;
  }

  passwordValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value || '';
    if (!value) return { required: true };
    if (!value.trim()) return { whitespace: true };
    if (value.length < 6) return { length: true };
    return null;
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { username, password } = this.registerForm.value;
    this.signupInfo = new SignupInfo(username, password);

    if (username === 'admin') {
      this.signupInfo.role.push('admin');
    }

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
