import { Component } from '@angular/core';

@Component({
  selector: 'app-access-denied',
  template: `
    <div class="container py-5 text-center">
      <div class="row justify-content-center">
        <div class="col-md-8">
          <div class="card border-danger shadow">
            <div class="card-body p-5">
              <h1 class="text-danger mb-4">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                Access Denied
              </h1>
              <p class="lead mb-4">
                You don't have permission to access this page. This area is
                restricted to administrators only.
              </p>
              <button class="btn btn-primary" routerLink="/">
                Return to Home Page
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class AccessDeniedComponent {}
