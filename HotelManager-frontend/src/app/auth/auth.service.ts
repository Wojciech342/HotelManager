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
  private loginUrl = 'http://localhost:8080/api/auth/signin';
  private signupUrl = 'http://localhost:8080/api/auth/signup';

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
    return token != null && token !== '{}'; // Check if a valid token exists
  }

  logout(): void {
    this.tokenStorage.signOut(); // Clear token and other user data
  }
}
