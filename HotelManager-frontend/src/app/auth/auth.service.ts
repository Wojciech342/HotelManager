import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { LoginInfo } from './login-info';
import { Observable } from 'rxjs';
import { JwtResponse } from './jwt-response';
import { SignupInfo } from './signup-info';
import { TokenStorageService } from './token-storage.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private loginUrl = 'http://localhost:8080/api/auth/login';
  private signupUrl = 'http://localhost:8080/api/auth/register';

  constructor(
    private http: HttpClient,
    private tokenStorage: TokenStorageService
  ) {}

  attemptAuth(credentials: LoginInfo): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(this.loginUrl, credentials, httpOptions);
  }

  signUp(info: SignupInfo): Observable<string> {
    return this.http.post<string>(this.signupUrl, info, httpOptions);
  }

  get isLoggedIn(): boolean {
    const token = this.tokenStorage.getToken();
    return !!token;
  }

  logout(): void {
    this.tokenStorage.signOut();
  }

  isAdmin(): boolean {
    const authorities = localStorage.getItem('AuthAuthorities');
    if (!authorities) return false;
    try {
      const roles = JSON.parse(authorities);
      return roles.some((role: any) => role.authority === 'ROLE_ADMIN');
    } catch {
      return false;
    }
  }
}
