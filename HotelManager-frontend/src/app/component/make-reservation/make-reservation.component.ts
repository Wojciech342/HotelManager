import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RoomService } from '../../service/room.service';
import { Room } from '../../model/room';
import { RoomReservationService } from '../../service/room-reservation.service';
import { RoomReservation } from '../../model/roomReservation';
import { start } from 'repl';

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
      // Only allow today or future dates, and not in any reserved range
      if (date < this.minDate) return false;
      return !this.existingReservations.some(
        (res) =>
          new Date(res.startDate) <= date && date <= new Date(res.endDate)
      );
    };
  }

  calculateTotal() {
    if (this.room) {
      const { startDate, endDate } = this.reservationForm.value;
      if (startDate && endDate) {
        // Always convert to yyyy-MM-dd string, then to Date at local midnight
        const startStr =
          typeof startDate === 'string'
            ? startDate
            : startDate.toLocaleDateString('en-CA');
        const endStr =
          typeof endDate === 'string'
            ? endDate
            : endDate.toLocaleDateString('en-CA');
        const start = new Date(startStr + 'T00:00:00');
        const end = new Date(endStr + 'T00:00:00');
        const days = Math.round(
          (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)
        );
        this.totalPrice = days > 0 ? days * this.room.pricePerNight : null;
      } else {
        this.totalPrice = null;
      }
    }
  }

  onSubmit() {
    if (this.reservationForm.invalid || !this.room) return;

    const username = localStorage.getItem('AuthUsername');
    if (!username) {
      alert('You must be logged in to make a reservation.');
      return;
    }

    const { startDate, endDate } = this.reservationForm.value;
    const reservationDate = new Date().toISOString();

    console.log(startDate);
    console.log(endDate);

    // Use yyyy-MM-dd format for startDate and endDate if backend expects LocalDate
    const formattedStartDate =
      typeof startDate === 'string'
        ? startDate
        : startDate.toLocaleDateString('en-CA'); // 'yyyy-MM-dd'
    const formattedEndDate =
      typeof endDate === 'string'
        ? endDate
        : endDate.toLocaleDateString('en-CA');

    const reservation: RoomReservation = {
      reservationDate,
      startDate: formattedStartDate,
      endDate: formattedEndDate,
      price: this.totalPrice ?? 0,
      status: 'PENDING',
    };

    console.log(formattedStartDate);
    console.log(formattedEndDate);

    this.roomReservationService
      .createRoomReservation(username, this.room.id!, reservation)
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
