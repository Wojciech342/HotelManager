import { Component, OnInit } from '@angular/core';
import { RoomReservation } from '../../model/roomReservation';
import { RoomReservationService } from '../../service/room-reservation.service';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { SuccessDialogComponent } from '../success-dialog/success-dialog.component';

@Component({
  selector: 'app-pending-reservations',
  templateUrl: './pending-reservations.component.html',
  styleUrls: ['./pending-reservations.component.css'],
})
export class PendingReservationsComponent implements OnInit {
  pendingReservations: RoomReservation[] = [];
  loading = false;
  error = '';

  constructor(
    private reservationService: RoomReservationService,
    private authService: AuthService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    // Check if user is admin
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/']);
      return;
    }

    this.loadPendingReservations();
  }

  loadPendingReservations(): void {
    this.loading = true;
    this.reservationService.getPendingReservations().subscribe({
      next: (reservations) => {
        console.log(reservations);
        this.pendingReservations = reservations;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load pending reservations';
        this.loading = false;
        console.error(err);
      },
    });
  }

  // Update the confirmAction method
  confirmAction(id: number, action: 'accept' | 'reject'): void {
    const isAccept = action === 'accept';

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: `${isAccept ? 'Accept' : 'Reject'} Reservation`,
        message: `Are you sure you want to ${action} this reservation? This action cannot be undone.`,
        confirmText: isAccept ? 'Accept' : 'Reject',
        type: isAccept ? 'success' : 'danger',
        confirmIcon: isAccept ? 'check_circle' : 'cancel',
        cancelText: 'Cancel',
        cancelIcon: 'close',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        if (isAccept) {
          this.acceptReservation(id);
        } else {
          this.rejectReservation(id);
        }
      }
    });
  }

  acceptReservation(id: number): void {
    this.reservationService.updateReservationStatus(id, 'ACCEPTED').subscribe({
      next: () => {
        this.showSuccessNotification('Reservation accepted successfully');
        this.pendingReservations = this.pendingReservations.filter(
          (res) => res.id !== id
        );
      },
      error: (err) => {
        this.error = 'Failed to accept reservation';
        console.error(err);
      },
    });
  }

  rejectReservation(id: number): void {
    this.reservationService.updateReservationStatus(id, 'REJECTED').subscribe({
      next: () => {
        this.showSuccessNotification('Reservation rejected successfully');
        this.pendingReservations = this.pendingReservations.filter(
          (res) => res.id !== id
        );
      },
      error: (err) => {
        this.error = 'Failed to reject reservation';
        console.error(err);
      },
    });
  }

  showSuccessNotification(message: string): void {
    this.dialog.open(SuccessDialogComponent, {
      width: 'auto',
      panelClass: 'success-dialog-panel',
      position: { top: '100px' },
      hasBackdrop: false,
      data: { message },
    });
  }

  formatDate(date: any): string {
    return new Date(date).toLocaleDateString();
  }

  calculateDuration(start: any, end: any): number {
    const startDate = new Date(start);
    const endDate = new Date(end);
    const diff = Math.abs(endDate.getTime() - startDate.getTime());
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }
}
