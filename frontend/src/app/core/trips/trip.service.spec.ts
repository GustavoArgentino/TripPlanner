import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { API_BASE_URL } from '../config/api-config';
import { TripRequest, TripResponse } from './trip.models';
import { TripService } from './trip.service';

describe('TripService', () => {
  let service: TripService;
  let httpMock: HttpTestingController;

  const sampleTrip: TripResponse = {
    id: 'trip-1',
    name: 'Trip to Rio',
    destination: 'Rio de Janeiro',
    startDate: '2026-09-01',
    endDate: '2026-09-10',
    description: 'Vacation',
    createdAt: '2026-08-24T00:00:00Z'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(TripService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('list() GETs /trips', () => {
    service.list().subscribe((trips) => expect(trips).toEqual([sampleTrip]));

    const req = httpMock.expectOne(`${API_BASE_URL}/trips`);
    expect(req.request.method).toBe('GET');
    req.flush([sampleTrip]);
  });

  it('get() GETs /trips/{id}', () => {
    service.get('trip-1').subscribe((trip) => expect(trip).toEqual(sampleTrip));

    const req = httpMock.expectOne(`${API_BASE_URL}/trips/trip-1`);
    expect(req.request.method).toBe('GET');
    req.flush(sampleTrip);
  });

  it('create() POSTs to /trips', () => {
    const request: TripRequest = {
      name: 'Trip to Rio',
      destination: 'Rio de Janeiro',
      startDate: '2026-09-01',
      endDate: '2026-09-10',
      description: 'Vacation'
    };

    service.create(request).subscribe((trip) => expect(trip).toEqual(sampleTrip));

    const req = httpMock.expectOne(`${API_BASE_URL}/trips`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(sampleTrip);
  });

  it('update() PUTs to /trips/{id}', () => {
    const request: TripRequest = {
      name: 'Trip to Rio (updated)',
      destination: 'Rio de Janeiro',
      startDate: '2026-09-01',
      endDate: '2026-09-12',
      description: null
    };

    service.update('trip-1', request).subscribe((trip) => expect(trip).toEqual(sampleTrip));

    const req = httpMock.expectOne(`${API_BASE_URL}/trips/trip-1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);
    req.flush(sampleTrip);
  });

  it('delete() DELETEs /trips/{id}', () => {
    service.delete('trip-1').subscribe();

    const req = httpMock.expectOne(`${API_BASE_URL}/trips/trip-1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
