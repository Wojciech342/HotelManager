import { CanActivateFn, Router } from '@angular/router';
import { TokenStorageService } from '../auth/token-storage.service';
import { inject } from '@angular/core';

export const AuthGuard: CanActivateFn = (route, state) => {
  const tokenStorageService = inject(TokenStorageService);
  const router = inject(Router);

  // check if any role from authorities list is in the routing list defined
  if (tokenStorageService.getToken()) {
    return true; // Allow access if the user is logged in
  } else {
    router.navigate(['/login']); // Redirect to login if not authenticated
    return false;
  }
};
