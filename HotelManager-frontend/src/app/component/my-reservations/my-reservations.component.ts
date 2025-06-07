import { Component, OnInit } from '@angular/core';
import { RoomReservation } from '../../model/roomReservation';
import { RoomReservationService } from '../../service/room-reservation.service';

@Component({
  selector: 'app-my-reservations',
  templateUrl: './my-reservations.component.html',
  styleUrls: ['./my-reservations.component.css'],
})
export class MyReservationsComponent implements OnInit {
  reservations: RoomReservation[] = [];
  username: string | null = null;

  constructor(private reservationService: RoomReservationService) {}

  ngOnInit(): void {
    if (typeof window !== 'undefined') {
      this.username = localStorage.getItem('AuthUsername');
      if (this.username) {
        this.loadReservations();
      }
    }
  }

  loadReservations() {
    this.reservationService
      .getReservationsByUsername(this.username!)
      .subscribe({
        next: (res) => (this.reservations = res),
        error: (err) => console.error('Failed to load reservations', err),
      });
  }

  canMakeReview(reservation: RoomReservation): boolean {
    const today = new Date();
    const start = new Date(reservation.startDate);
    return today >= start && !reservation.roomReview;
  }

  canCancel(reservation: RoomReservation): boolean {
    // const today = new Date();
    // const start = new Date(reservation.startDate);
    // return today < start && reservation.status !== 'CANCELLED';
    return true;
  }

  onCancel(reservation: RoomReservation) {
    if (confirm('Are you sure you want to cancel this reservation?')) {
      reservation.status = 'CANCELLED';
      this.reservationService
        .updateReservation(reservation.id!, reservation)
        .subscribe({
          next: () => {},
          error: (err) =>
            alert(
              'Failed to cancel reservation: ' +
                (err.error?.message || err.message)
            ),
        });
    }
  }

  onMakeReview(reservation: RoomReservation) {
    // TODO: Implement review logic
  }
}
