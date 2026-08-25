import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { TripResponse } from '../../../core/trips/trip.models';
import { TripService } from '../../../core/trips/trip.service';

@Component({
  selector: 'app-trip-list',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './trip-list.component.html',
  styleUrl: './trip-list.component.scss'
})
export class TripListComponent implements OnInit {
  private readonly tripService = inject(TripService);

  readonly trips = signal<TripResponse[]>([]);
  readonly isLoading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly deletingId = signal<string | null>(null);

  ngOnInit(): void {
    this.loadTrips();
  }

  formatDate(isoDate: string): string {
    const [year, month, day] = isoDate.split('-');
    return `${day}/${month}/${year}`;
  }

  deleteTrip(trip: TripResponse): void {
    if (!confirm(`Excluir a viagem "${trip.name}"? Essa ação não pode ser desfeita.`)) {
      return;
    }

    this.deletingId.set(trip.id);
    this.errorMessage.set(null);
    this.tripService.delete(trip.id).subscribe({
      next: () => {
        this.deletingId.set(null);
        this.trips.update((trips) => trips.filter((t) => t.id !== trip.id));
      },
      error: () => {
        this.deletingId.set(null);
        this.errorMessage.set('Não foi possível excluir a viagem. Tente novamente.');
      }
    });
  }

  private loadTrips(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.tripService.list().subscribe({
      next: (trips) => {
        this.trips.set(trips);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessage.set('Não foi possível carregar suas viagens. Tente novamente.');
      }
    });
  }
}
