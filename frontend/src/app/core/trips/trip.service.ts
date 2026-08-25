import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api-config';
import { TripRequest, TripResponse } from './trip.models';

@Injectable({ providedIn: 'root' })
export class TripService {
  constructor(private readonly http: HttpClient) {}

  list(): Observable<TripResponse[]> {
    return this.http.get<TripResponse[]>(`${API_BASE_URL}/trips`);
  }

  get(id: string): Observable<TripResponse> {
    return this.http.get<TripResponse>(`${API_BASE_URL}/trips/${id}`);
  }

  create(request: TripRequest): Observable<TripResponse> {
    return this.http.post<TripResponse>(`${API_BASE_URL}/trips`, request);
  }

  update(id: string, request: TripRequest): Observable<TripResponse> {
    return this.http.put<TripResponse>(`${API_BASE_URL}/trips/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/trips/${id}`);
  }
}
