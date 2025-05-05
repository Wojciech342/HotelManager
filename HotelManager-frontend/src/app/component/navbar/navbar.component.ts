import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../service/auth.service';
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  public isBrowser: boolean;
  constructor(
    @Inject(PLATFORM_ID) private platformId: any,
    public auth: AuthService
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }
}
