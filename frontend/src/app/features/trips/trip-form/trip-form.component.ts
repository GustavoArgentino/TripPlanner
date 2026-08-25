import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DateAdapter, MAT_DATE_FORMATS, MAT_NATIVE_DATE_FORMATS } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { BrDateAdapter } from '../../../core/date/br-date-adapter';
import { TripRequest } from '../../../core/trips/trip.models';
import { TripService } from '../../../core/trips/trip.service';

@Component({
  selector: 'app-trip-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule
  ],
  providers: [
    // Not MatNativeDateModule: its default NativeDateAdapter parses typed
    // dd/mm/yyyy input as US-style mm/dd/yyyy regardless of MAT_DATE_LOCALE
    // (display formatting is locale-aware, parsing typed text isn't). This
    // keeps native-date's display/format behavior via MAT_NATIVE_DATE_FORMATS
    // but fixes parsing for pt-BR.
    { provide: DateAdapter, useClass: BrDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS }
  ],
  templateUrl: './trip-form.component.html',
  styleUrl: './trip-form.component.scss'
})
export class TripFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly tripService = inject(TripService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private tripId: string | null = null;

  readonly form = this.formBuilder.group({
    name: ['', [Validators.required]],
    destination: ['', [Validators.required]],
    startDate: [null as Date | null, [Validators.required]],
    endDate: [null as Date | null, [Validators.required]],
    description: ['']
  });

  readonly isEditMode = signal(false);
  readonly isLoading = signal(false);
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    // Subscribes rather than reading route.snapshot once: Angular reuses this
    // component instance across sibling ':id/edit' activations (same
    // routeConfig), so a snapshot read would miss a direct A -> B edit
    // navigation and keep showing trip A's data. The observable completes
    // when the component is destroyed, so no manual unsubscribe is needed.
    this.route.paramMap.subscribe((params) => {
      this.tripId = params.get('id');
      this.isEditMode.set(this.tripId !== null);
      if (this.tripId) {
        this.loadTrip(this.tripId);
      } else {
        this.form.reset();
      }
    });
  }

  submit(): void {
    if (this.form.invalid || this.isSubmitting()) {
      this.form.markAllAsTouched();
      return;
    }

    const { name, destination, startDate, endDate, description } = this.form.getRawValue();

    if (startDate! > endDate!) {
      this.errorMessage.set('A data de término não pode ser anterior à data de início.');
      return;
    }

    const request: TripRequest = {
      name: name!,
      destination: destination!,
      startDate: this.toIsoDate(startDate!),
      endDate: this.toIsoDate(endDate!),
      description: description || null
    };

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const save$ = this.tripId ? this.tripService.update(this.tripId, request) : this.tripService.create(request);

    save$.subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.router.navigateByUrl('/trips');
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(
          error.status === 400
            ? 'Dados inválidos. Verifique os campos e tente novamente.'
            : 'Não foi possível salvar a viagem. Tente novamente.'
        );
      }
    });
  }

  private loadTrip(id: string): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.tripService.get(id).subscribe({
      next: (trip) => {
        this.form.setValue({
          name: trip.name,
          destination: trip.destination,
          startDate: this.fromIsoDate(trip.startDate),
          endDate: this.fromIsoDate(trip.endDate),
          description: trip.description ?? ''
        });
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessage.set('Não foi possível carregar a viagem.');
      }
    });
  }

  private toIsoDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private fromIsoDate(isoDate: string): Date {
    const [year, month, day] = isoDate.split('-').map(Number);
    return new Date(year, month - 1, day);
  }
}
