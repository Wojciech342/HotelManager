import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageService } from '../../service/message.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { TokenStorageService } from '../../auth/token-storage.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css'],
})
export class ContactComponent {
  contactForm: FormGroup;
  loading = false;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private messageService: MessageService,
    private snackBar: MatSnackBar,
    private authService: AuthService,
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {
    this.contactForm = this.formBuilder.group({
      message: ['', [Validators.required, Validators.minLength(10)]],
    });
  }

  get f() {
    return this.contactForm.controls;
  }

  onSubmit() {
    this.submitted = true;

    if (this.contactForm.invalid) {
      return;
    }

    if (!this.authService.isLoggedIn) {
      this.snackBar.open('Please log in to send a message.', 'Close', {
        duration: 5000,
        panelClass: ['warn-snackbar'],
      });
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;

    // Get the current username
    const username = this.tokenStorage.getUsername() || '';

    this.messageService
      .sendContactMessage(this.contactForm.get('message')?.value)
      .subscribe({
        next: () => {
          this.snackBar.open('Your message has been sent!', 'Close', {
            duration: 5000,
            panelClass: ['success-snackbar'],
          });
          this.contactForm.reset();
          this.submitted = false;
          this.loading = false;
        },
        error: (error) => {
          this.snackBar.open(
            'Failed to send message: ' + error.message,
            'Close',
            {
              duration: 5000,
              panelClass: ['error-snackbar'],
            }
          );
          this.loading = false;
        },
      });
  }
}
