export interface ItineraryItemRequest {
  title: string;
  date: string;
  startTime: string | null;
  location: string | null;
  notes: string | null;
}

export interface ItineraryItemResponse {
  id: string;
  tripId: string;
  title: string;
  date: string;
  startTime: string | null;
  location: string | null;
  notes: string | null;
}
