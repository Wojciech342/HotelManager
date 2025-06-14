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
  minDate: Date = new Date(new Date().setDate(new Date().getDate() - 7));

  dateFilter: (date: Date | null) => boolean = () => true;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private roomService: RoomService,
    private router: Router,
    private roomReservationService: RoomReservationService
  ) {}

  ngOnInit(): void {
    const roomId = this.route.snapshot.paramMap.get('roomId');
    if (roomId) {
      this.roomService.getRoomById(+roomId).subscribe({
        next: (room) => {
          this.room = room;
          this.roomReservationService
            .getReservationsByRoomId(this.room.id!)
            .subscribe((reservations) => {
              this.existingReservations = reservations;
            });
        },
        error: () => (this.room = null),
      });
    }

    this.reservationForm = this.fb.group({
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
    });

    this.reservationForm.valueChanges.subscribe(() => this.calculateTotal());

    this.dateFilter = (date: Date | null): boolean => {
      if (!date) return false;
      // Only allow today or future dates, and not in any reserved range (except CANCELLED)
      if (date < this.minDate) return false;
      return !this.existingReservations
        .filter((res) => res.status !== 'CANCELLED')
        .some(
          (res) =>
            new Date(res.startDate) <= date && date <= new Date(res.endDate)
        );
    };
  }

  calculateTotal() {
    if (this.room) {
      const { startDate, endDate } = this.reservationForm.value;
      if (startDate && endDate) {
        const start = startDate as Date;
        const end = endDate as Date;
        const days = Math.round(
          (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)
        );
        this.totalPrice = days > 0 ? days * this.room.pricePerNight : null;
      } else {
        this.totalPrice = null;
      }
    }
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    // padStart ensures two digits for month and day: yyyy-MM-dd format
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onSubmit() {
    if (this.reservationForm.invalid || !this.room) return;

    const { startDate, endDate } = this.reservationForm.value;
    const reservationDate = new Date().toISOString();

    const reservation: RoomReservation = {
      reservationDate,
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
