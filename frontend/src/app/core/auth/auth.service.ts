import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { API_BASE_URL } from '../config/api-config';
import { AuthResponse, LoginRequest, RegisterRequest } from './auth.models';
import { decodeJwtPayload, isJwtExpired } from './jwt.util';

const TOKEN_STORAGE_KEY = 'tripplanner.auth.token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenSignal = signal<string | null>(this.readStoredToken());

  // Plain methods, not computed() signals: expiry depends on wall-clock time,
  // not just on tokenSignal, so a memoized computed() would cache a stale
  // result until the token itself changes (login/logout).
  readonly isAuthenticated = (): boolean => {
    const token = this.tokenSignal();
    return token !== null && !isJwtExpired(token);
  };

  readonly currentUserEmail = (): string | null => {
    const token = this.tokenSignal();
    if (!token || isJwtExpired(token)) {
      return null;
    }
    return decodeJwtPayload(token)?.sub ?? null;
  };

  constructor(private readonly http: HttpClient) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_BASE_URL}/auth/register`, request)
      .pipe(tap((response) => this.storeToken(response.token)));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_BASE_URL}/auth/login`, request)
      .pipe(tap((response) => this.storeToken(response.token)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    this.tokenSignal.set(null);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  private storeToken(token: string): void {
    localStorage.setItem(TOKEN_STORAGE_KEY, token);
    this.tokenSignal.set(token);
  }

  private readStoredToken(): string | null {
    return localStorage.getItem(TOKEN_STORAGE_KEY);
  }
}
