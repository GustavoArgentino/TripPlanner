import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { ItineraryItemRequest, ItineraryItemResponse } from '../../../core/itinerary/itinerary.models';
import { ItineraryService } from '../../../core/itinerary/itinerary.service';
import { TripResponse } from '../../../core/trips/trip.models';
import { TripService } from '../../../core/trips/trip.service';
import { formatDate } from '../trip-date.util';

@Component({
  selector: 'app-trip-detail',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './trip-detail.component.html',
  styleUrl: './trip-detail.component.scss'
})
export class TripDetailComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly tripService = inject(TripService);
  private readonly itineraryService = inject(ItineraryService);
  private readonly route = inject(ActivatedRoute);

  private tripId: string | null = null;

  readonly trip = signal<TripResponse | null>(null);
  readonly items = signal<ItineraryItemResponse[]>([]);
  readonly isLoading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  readonly isFormOpen = signal(false);
  readonly editingItemId = signal<string | null>(null);
  readonly isSubmitting = signal(false);
  readonly formError = signal<string | null>(null);
  readonly deletingId = signal<string | null>(null);

  readonly form = this.formBuilder.group({
    title: ['', [Validators.required]],
    date: ['', [Validators.required]],
    startTime: [''],
    location: [''],
    notes: ['']
  });

  readonly formatDate = formatDate;

  ngOnInit(): void {
    // Subscribes rather than reading route.snapshot once, for the same
    // reason as TripFormComponent: this component instance can be reused
    // across sibling ':id' activations. Everything tied to the previous
    // trip (loaded data, open edit form) is reset on every emission so a
    // direct trip-A -> trip-B navigation can't leak state between them.
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (!id) {
        return;
      }
      this.tripId = id;
      this.trip.set(null);
      this.items.set([]);
      this.isLoading.set(true);
      this.errorMessage.set(null);
      this.closeForm();
      this.loadTrip(id);
      this.loadItems(id);
    });
  }

  openCreateForm(): void {
    this.editingItemId.set(null);
    this.form.reset();
    this.formError.set(null);
    this.isFormOpen.set(true);
  }

  openEditForm(item: ItineraryItemResponse): void {
    this.editingItemId.set(item.id);
    this.form.setValue({
      title: item.title,
      date: item.date,
      startTime: item.startTime ?? '',
      location: item.location ?? '',
      notes: item.notes ?? ''
    });
    this.formError.set(null);
    this.isFormOpen.set(true);
  }

  closeForm(): void {
    this.isFormOpen.set(false);
    this.editingItemId.set(null);
  }

  submit(): void {
    if (this.form.invalid || this.isSubmitting() || !this.tripId) {
      this.form.markAllAsTouched();
      return;
    }

    const { title, date, startTime, location, notes } = this.form.getRawValue();

    const trip = this.trip();
    if (trip && (date! < trip.startDate || date! > trip.endDate)) {
      this.formError.set('A data deve estar dentro do período da viagem.');
      return;
    }

    const request: ItineraryItemRequest = {
      title: title!,
      date: date!,
      startTime: startTime || null,
      location: location || null,
      notes: notes || null
    };

    this.isSubmitting.set(true);
    this.formError.set(null);

    const itemId = this.editingItemId();
    const save$ = itemId
      ? this.itineraryService.update(this.tripId, itemId, request)
      : this.itineraryService.create(this.tripId, request);

    save$.subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.closeForm();
        this.loadItems(this.tripId!);
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        this.formError.set(
          error.status === 400
            ? 'Dados inválidos. Verifique se a data está dentro do período da viagem.'
            : 'Não foi possível salvar o item. Tente novamente.'
        );
      }
    });
  }

  deleteItem(item: ItineraryItemResponse): void {
    if (!this.tripId || !confirm(`Excluir o item "${item.title}"?`)) {
      return;
    }

    this.deletingId.set(item.id);
    this.errorMessage.set(null);
    this.itineraryService.delete(this.tripId, item.id).subscribe({
      next: () => {
        this.deletingId.set(null);
        this.items.update((items) => items.filter((i) => i.id !== item.id));
      },
      error: () => {
        this.deletingId.set(null);
        this.errorMessage.set('Não foi possível excluir o item. Tente novamente.');
      }
    });
  }

  private loadTrip(tripId: string): void {
    this.tripService.get(tripId).subscribe({
      next: (trip) => {
        this.trip.set(trip);
        this.isLoading.set(false);
      },
      error: () => {
        this.trip.set(null);
        this.isLoading.set(false);
        this.errorMessage.set('Não foi possível carregar a viagem.');
      }
    });
  }

  private loadItems(tripId: string): void {
    this.itineraryService.list(tripId).subscribe({
      next: (items) => this.items.set(items),
      error: () => {
        this.items.set([]);
        this.errorMessage.set('Não foi possível carregar o itinerário.');
      }
    });
  }
}
