export interface TripRequest {
  name: string;
  destination: string;
  startDate: string;
  endDate: string;
  description: string | null;
}

export interface TripResponse {
  id: string;
  name: string;
  destination: string;
  startDate: string;
  endDate: string;
  description: string | null;
  createdAt: string;
}
