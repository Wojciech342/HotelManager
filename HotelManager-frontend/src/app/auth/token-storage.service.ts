import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

const TOKEN_KEY = 'AuthToken';
const USERNAME_KEY = 'AuthUsername';
const AUTHORITIES_KEY = 'AuthAuthorities';

@Injectable({
  providedIn: 'root',
})
export class TokenStorageService {
  private roles: Array<string> = [];

  constructor(@Inject(PLATFORM_ID) private platformId: any) {}

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  signOut() {
    if (this.isBrowser()) {
      localStorage.clear();
    }
  }

  public saveToken(token: string) {
    if (this.isBrowser()) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.setItem(TOKEN_KEY, token);
    }
  }

  public getToken(): string | null {
    if (this.isBrowser()) {
      return localStorage.getItem(TOKEN_KEY);
    }
    return null;
  }

  public saveUsername(username: string) {
    if (this.isBrowser()) {
      localStorage.removeItem(USERNAME_KEY);
      localStorage.setItem(USERNAME_KEY, username);
    }
  }

  public getUsername(): string {
    if (this.isBrowser()) {
      return localStorage.getItem(USERNAME_KEY) || '{}';
    }
    return '{}';
  }

  public saveAuthorities(authorities: string[]) {
    if (this.isBrowser()) {
      localStorage.removeItem(AUTHORITIES_KEY);
      localStorage.setItem(AUTHORITIES_KEY, JSON.stringify(authorities));
    }
  }

  public getAuthorities(): string[] {
    this.roles = [];
    if (this.isBrowser() && localStorage.getItem(AUTHORITIES_KEY)) {
      JSON.parse(localStorage.getItem(AUTHORITIES_KEY) || '[]').forEach(
        (authority: { authority: string }) => {
          this.roles.push(authority.authority);
        }
      );
    }
    return this.roles;
  }
}
