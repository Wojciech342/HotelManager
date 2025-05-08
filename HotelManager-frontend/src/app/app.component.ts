import { Component } from '@angular/core';
import { TokenStorageService } from './auth/token-storage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'HotelManager-frontend';

  private roles?: string[];
  authority?: string;

  constructor(private tokenStorage: TokenStorageService) {}

  ngOnInit() {
    console.log('init');
    if (this.tokenStorage.getToken()) {
      console.log(this.tokenStorage.getToken());
      this.roles = this.tokenStorage.getAuthorities();
      this.roles.every((role) => {
        if (role === 'ROLE_ADMIN') {
          this.authority = 'admin';
          return false;
        }
        this.authority = 'user';
        return true;
      });
    }
  }
}
