import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { API_BASE_URL } from '../config/api-config';
import { ItineraryItemRequest, ItineraryItemResponse } from './itinerary.models';
import { ItineraryService } from './itinerary.service';

describe('ItineraryService', () => {
  let service: ItineraryService;
  let httpMock: HttpTestingController;

  const tripId = 'trip-1';

  const sampleItem: ItineraryItemResponse = {
    id: 'item-1',
    tripId,
    title: 'Visitar o Pão de Açúcar',
    date: '2026-09-03',
    startTime: '09:00',
    location: 'Urca',
    notes: 'Levar protetor solar'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ItineraryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('list() GETs /trips/{tripId}/itinerary-items', () => {
    service.list(tripId).subscribe((items) => expect(items).toEqual([sampleItem]));

    const req = httpMock.expectOne(`${API_BASE_URL}/trips/${tripId}/itinerary-items`);
    expect(req.request.method).toBe('GET');
    req.flush([sampleItem]);
  });

  it('get() GETs /trips/{tripId}/itinerary-items/{itemId}', () => {
    service.get(tripId, 'item-1').subscribe((item) => expect(item).toEqual(sampleItem));

    const req = httpMock.expectOne(`${API_BASE_URL}/trips/${tripId}/itinerary-items/item-1`);
    expect(req.request.method).toBe('GET');
    req.flush(sampleItem);
  });

  it('create() POSTs to /trips/{tripId}/itinerary-items', () => {
    const request: ItineraryItemRequest = {
      title: 'Visitar o Pão de Açúcar',
      date: '2026-09-03',
      startTime: '09:00',
      location: 'Urca',
      notes: 'Levar protetor solar'
    };

    service.create(tripId, request).subscribe((item) => expect(item).toEqual(sampleItem));

    const req = httpMock.expectOne(`${API_BASE_URL}/trips/${tripId}/itinerary-items`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(sampleItem);
  });

  it('update() PUTs to /trips/{tripId}/itinerary-items/{itemId}', () => {
    const request: ItineraryItemRequest = {
      title: 'Visitar o Pão de Açúcar (ajustado)',
      date: '2026-09-04',
      startTime: null,
      location: null,
      notes: null
    };

    service.update(tripId, 'item-1', request).subscribe((item) => expect(item).toEqual(sampleItem));

    const req = httpMock.expectOne(`${API_BASE_URL}/trips/${tripId}/itinerary-items/item-1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);
    req.flush(sampleItem);
  });

  it('delete() DELETEs /trips/{tripId}/itinerary-items/{itemId}', () => {
    service.delete(tripId, 'item-1').subscribe();

    const req = httpMock.expectOne(`${API_BASE_URL}/trips/${tripId}/itinerary-items/item-1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
