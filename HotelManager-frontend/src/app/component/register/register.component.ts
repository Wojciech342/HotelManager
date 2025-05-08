import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { SignupInfo } from '../../auth/signup-info';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent implements OnInit {
  form: any = {};
  signupInfo?: SignupInfo;
  isSignedUp = false;
  isSignUpFailed = false;
  errorMessage = '';

  private authService = inject(AuthService);

  ngOnInit() {}

  onSubmit() {
    console.log(this.form);

    this.signupInfo = new SignupInfo(this.form.username, this.form.password);

    this.authService.signUp(this.signupInfo).subscribe({
      next: (data) => {
        console.log(data);
        this.isSignedUp = true;
        this.isSignUpFailed = false;
      },
      error: (error) => {
        console.log(error);
        this.errorMessage = error.error.message;
        this.isSignUpFailed = true;
      },
    });
  }
}
