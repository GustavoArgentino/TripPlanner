import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { API_BASE_URL } from '../config/api-config';
import { AuthService } from './auth.service';

function makeToken(payload: Record<string, unknown>): string {
  const encode = (obj: unknown) => {
    const bytes = new TextEncoder().encode(JSON.stringify(obj));
    let binary = '';
    bytes.forEach((byte) => (binary += String.fromCharCode(byte)));
    return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  };

  return `${encode({ alg: 'none' })}.${encode(payload)}.signature`;
}

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('starts unauthenticated with no stored token', () => {
    expect(service.isAuthenticated()).toBeFalse();
    expect(service.currentUserEmail()).toBeNull();
  });

  it('register stores the returned token and authenticates the user', () => {
    const token = makeToken({ sub: 'new@example.com', exp: Math.floor(Date.now() / 1000) + 3600 });

    service.register({ email: 'new@example.com', password: 'password123', name: 'New User' }).subscribe();

    const req = httpMock.expectOne(`${API_BASE_URL}/auth/register`);
    expect(req.request.method).toBe('POST');
    req.flush({ token, tokenType: 'Bearer' });

    expect(service.isAuthenticated()).toBeTrue();
    expect(service.currentUserEmail()).toBe('new@example.com');
    expect(localStorage.getItem('tripplanner.auth.token')).toBe(token);
  });

  it('login stores the returned token and authenticates the user', () => {
    const token = makeToken({ sub: 'user@example.com', exp: Math.floor(Date.now() / 1000) + 3600 });

    service.login({ email: 'user@example.com', password: 'password123' }).subscribe();

    const req = httpMock.expectOne(`${API_BASE_URL}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({ token, tokenType: 'Bearer' });

    expect(service.isAuthenticated()).toBeTrue();
  });

  it('isAuthenticated re-evaluates expiry on every call, not just when the token changes', () => {
    const realNow = Date.now();
    const nowSpy = spyOn(Date, 'now').and.returnValue(realNow);
    const nowSeconds = Math.floor(realNow / 1000);
    const token = makeToken({ sub: 'user@example.com', exp: nowSeconds + 1 });

    service.login({ email: 'user@example.com', password: 'password123' }).subscribe();
    httpMock.expectOne(`${API_BASE_URL}/auth/login`).flush({ token, tokenType: 'Bearer' });

    expect(service.isAuthenticated()).toBeTrue();

    // Simulate time passing past expiry without calling login()/logout() again —
    // a memoized computed() keyed only on the token would still report true here.
    nowSpy.and.returnValue(realNow + 2000);

    expect(service.isAuthenticated()).toBeFalse();
    expect(service.currentUserEmail()).toBeNull();
  });

  it('logout clears the stored token', () => {
    const token = makeToken({ sub: 'user@example.com', exp: Math.floor(Date.now() / 1000) + 3600 });
    service.login({ email: 'user@example.com', password: 'password123' }).subscribe();
    httpMock.expectOne(`${API_BASE_URL}/auth/login`).flush({ token, tokenType: 'Bearer' });

    service.logout();

    expect(service.isAuthenticated()).toBeFalse();
    expect(localStorage.getItem('tripplanner.auth.token')).toBeNull();
  });
});
