import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { API_BASE_URL } from '../config/api-config';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('attaches the Authorization header for API requests when a token exists', () => {
    authServiceSpy.getToken.and.returnValue('my-token');

    http.get(`${API_BASE_URL}/users/me`).subscribe();

    const req = httpMock.expectOne(`${API_BASE_URL}/users/me`);
    expect(req.request.headers.get('Authorization')).toBe('Bearer my-token');
    req.flush({});
  });

  it('does not attach a header for API requests when there is no token', () => {
    authServiceSpy.getToken.and.returnValue(null);

    http.get(`${API_BASE_URL}/users/me`).subscribe();

    const req = httpMock.expectOne(`${API_BASE_URL}/users/me`);
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('does not attach a header for requests outside the API base URL', () => {
    http.get('https://external.example.com/data').subscribe();

    const req = httpMock.expectOne('https://external.example.com/data');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    expect(authServiceSpy.getToken).not.toHaveBeenCalled();
    req.flush({});
  });
});
