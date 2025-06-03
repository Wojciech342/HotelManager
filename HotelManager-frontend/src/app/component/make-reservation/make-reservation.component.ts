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
  existingReservations: RoomReservation[] = []; // <-- Add this line
  minDate: Date; // Minimum date for the date picker

  dateFilter: (date: Date | null) => boolean = () => true; // <-- Add this line

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private roomService: RoomService,
    private router: Router,
    private roomReservationService: RoomReservationService
  ) {
    this.minDate = new Date();
  }

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
      // Check if date is not in any reserved range
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
        const start = new Date(startDate);
        const end = new Date(endDate);
        const days = (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);
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

    //const { startDate, endDate } = this.reservationForm.value;
    const reservationDate = new Date().toISOString();
    const startDate = '2025-05-10T14:00:00';
    const endDate = '2025-06-10T14:00:00';

    // ASK !!!!!!!!!
    const reservation: RoomReservation = {
      reservationDate,
      startDate,
      endDate,
      price: this.totalPrice ?? 0,
      status: 'PENDING',
      roomId: this.room.id!,
    };

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
