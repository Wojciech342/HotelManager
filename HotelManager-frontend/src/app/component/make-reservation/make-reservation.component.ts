import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RoomService } from '../../service/room.service';
import { Room } from '../../model/room';
import { RoomReservationService } from '../../service/room-reservation.service';
import { RoomReservation } from '../../model/roomReservation';

@Component({
  selector: 'app-make-reservation',
  templateUrl: './make-reservation.component.html',
  styleUrls: ['./make-reservation.component.css'],
})
export class MakeReservationComponent implements OnInit {
  reservationForm!: FormGroup;
  room: Room | null = null;
  totalPrice: number | null = null;
  existingReservations: RoomReservation[] = [];
  minDate: Date = new Date();

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private roomService: RoomService,
    private router: Router,
    private roomReservationService: RoomReservationService
  ) {}

  ngOnInit(): void {
    this.loadRoomData();

    this.reservationForm = this.fb.group({
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
    });

    this.reservationForm
      .get('startDate')
      ?.valueChanges.subscribe((startDate) => {
        if (startDate) {
          setTimeout(() => {
            const currentEndDate = this.reservationForm.get('endDate')?.value;
            if (
              currentEndDate &&
              (currentEndDate <= startDate ||
                !this.endDateFilter(currentEndDate))
            ) {
              this.reservationForm.get('endDate')?.setValue(null);
            }
          }, 0);
        }
      });

    this.reservationForm.valueChanges.subscribe(() => this.calculateTotal());
  }

  private loadRoomData(): void {
    const roomId = this.route.snapshot.paramMap.get('roomId');
    if (roomId) {
      this.roomService.getRoomById(+roomId).subscribe({
        next: (room) => {
          this.room = room;
          this.loadReservations(room.id!);
        },
        error: () => (this.room = null),
      });
    }
  }

  private loadReservations(roomId: number): void {
    this.roomReservationService
      .getReservationsByRoomId(roomId)
      .subscribe((reservations) => {
        this.existingReservations = reservations;
      });
  }

  private getActiveReservations(): RoomReservation[] {
    return this.existingReservations.filter(
      (res) => res.status !== 'CANCELLED' && res.status !== 'REJECTED'
    );
  }

  private isDateBooked(dateString: string): boolean {
    return this.getActiveReservations().some(
      (res) => dateString >= res.startDate! && dateString < res.endDate!
    );
  }

  private hasOverlappingReservation(
    startDateStr: string,
    endDateStr: string
  ): boolean {
    return this.getActiveReservations().some((res) => {
      return !(res.endDate! <= startDateStr || res.startDate! >= endDateStr);
    });
  }

  startDateFilter = (date: Date | null): boolean => {
    if (!date || date < this.minDate) return false;
    return !this.isDateBooked(this.formatDate(date));
  };

  endDateFilter = (date: Date | null): boolean => {
    if (!date || date < this.minDate) return false;

    const startDate = this.reservationForm.get('startDate')?.value;
    if (!startDate) return false;

    if (date <= startDate) return false;

    return !this.hasOverlappingReservation(
      this.formatDate(startDate),
      this.formatDate(date)
    );
  };

  calculateTotal(): void {
    if (!this.room) return;

    const { startDate, endDate } = this.reservationForm.value;
    if (!startDate || !endDate) {
      this.totalPrice = null;
      return;
    }

    const startMs = new Date(this.formatDate(startDate)).getTime();
    const endMs = new Date(this.formatDate(endDate)).getTime();
    const nights = Math.round((endMs - startMs) / (1000 * 60 * 60 * 24));

    this.totalPrice = nights > 0 ? nights * this.room.pricePerNight : null;
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    // padStart ensures two digits for month and day: yyyy-MM-dd format
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onSubmit(): void {
    if (this.reservationForm.invalid || !this.room) {
      return;
    }

    const { startDate, endDate } = this.reservationForm.value;

    if (
      this.hasOverlappingReservation(
        this.formatDate(startDate),
        this.formatDate(endDate)
      )
    ) {
      alert('Your selected date range includes already booked dates.');
      return;
    }

    const reservation: RoomReservation = {
      reservationDate: new Date().toISOString(),
      startDate: this.formatDate(startDate),
      endDate: this.formatDate(endDate),
      price: this.totalPrice ?? 0,
      status: 'PENDING',
    };

    const username = localStorage.getItem('AuthUsername');

    this.roomReservationService
      .createRoomReservation(username!, this.room.id!, reservation)
      .subscribe({
        next: () => {
          this.router.navigate(['/rooms']);
        },
        error: (err) => {
          alert(
            'Failed to create reservation: ' +
              (err.error?.message || err.message)
          );
        },
      });
  }
}
