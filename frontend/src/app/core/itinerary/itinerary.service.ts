import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api-config';
import { ItineraryItemRequest, ItineraryItemResponse } from './itinerary.models';

@Injectable({ providedIn: 'root' })
export class ItineraryService {
  constructor(private readonly http: HttpClient) {}

  list(tripId: string): Observable<ItineraryItemResponse[]> {
    return this.http.get<ItineraryItemResponse[]>(`${API_BASE_URL}/trips/${tripId}/itinerary-items`);
  }

  get(tripId: string, itemId: string): Observable<ItineraryItemResponse> {
    return this.http.get<ItineraryItemResponse>(`${API_BASE_URL}/trips/${tripId}/itinerary-items/${itemId}`);
  }

  create(tripId: string, request: ItineraryItemRequest): Observable<ItineraryItemResponse> {
    return this.http.post<ItineraryItemResponse>(`${API_BASE_URL}/trips/${tripId}/itinerary-items`, request);
  }

  update(tripId: string, itemId: string, request: ItineraryItemRequest): Observable<ItineraryItemResponse> {
    return this.http.put<ItineraryItemResponse>(`${API_BASE_URL}/trips/${tripId}/itinerary-items/${itemId}`, request);
  }

  delete(tripId: string, itemId: string): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/trips/${tripId}/itinerary-items/${itemId}`);
  }
}
