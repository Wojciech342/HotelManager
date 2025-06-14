import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms'; // <-- import
import { RoomReservation } from '../../model/roomReservation';
import { RoomReservationService } from '../../service/room-reservation.service';
import { RoomReview } from '../../model/roomReview';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-my-reservations',
  templateUrl: './my-reservations.component.html',
  styleUrls: ['./my-reservations.component.css'],
})
export class MyReservationsComponent implements OnInit {
  reservations: RoomReservation[] = [];
  totalElements = 0;
  totalPages = 0;
  pageNumber = 0;
  pageSize = 10;
  lastPage = false;

  sortBy: string = 'startDate';
  sortOrder: string = 'asc';

  sortOptions = [
    { value: 'startDate', label: 'Start Date' },
    { value: 'reservationDate', label: 'Reservation Date' },
    { value: 'status', label: 'Status' },
  ];

  orderOptions = [
    { value: 'asc', label: 'Ascending' },
    { value: 'desc', label: 'Descending' },
  ];

  username: string | null = null;

  // Modal state
  showReviewModal = false;
  reviewReservation: RoomReservation | null = null;
  submittingReview = false;

  reviewForm!: FormGroup;

  constructor(
    private reservationService: RoomReservationService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    if (typeof window !== 'undefined') {
      this.username = localStorage.getItem('AuthUsername');
      if (this.username) {
        this.loadReservations();
      }
    }
    this.reviewForm = this.fb.group({
      description: ['', Validators.required],
      rating: [
        null,
        [Validators.required, Validators.min(1), Validators.max(5)],
      ],
    });
  }

  loadReservations() {
    this.reservationService
      .getReservationsByUsername(
        this.username!,
        this.pageNumber,
        this.pageSize,
        this.sortBy,
        this.sortOrder
      )
      .subscribe({
        next: (res) => {
          this.reservations = res.content;
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
          this.lastPage = res.lastPage;
        },
        error: (err) => console.error('Failed to load reservations', err),
      });
  }

  canMakeReview(reservation: RoomReservation): boolean {
    if (reservation.review !== null) return false;
    if (reservation.status !== 'ACCEPTED') return false;
    if (reservation.room === null) return false;
    const today = new Date();
    const start = new Date(reservation.startDate + 'T00:00:00');
    return today >= start && !reservation.review;
  }

  openReviewModal(reservation: RoomReservation) {
    this.reviewReservation = reservation;
    this.reviewForm.reset();
    this.showReviewModal = true;
  }

  closeReviewModal() {
    this.showReviewModal = false;
    this.reviewReservation = null;
    this.reviewForm.reset();
  }

  submitReview() {
    if (!this.reviewReservation || !this.username || this.reviewForm.invalid)
      return;
    this.submittingReview = true;
    const review: RoomReview = {
      description: this.reviewForm.value.description,
      rating: this.reviewForm.value.rating,
      username: this.username,
    };

    this.reservationService
      .addReviewToReservation(review, this.reviewReservation.id!)
      .subscribe({
        next: () => {
          this.submittingReview = false;
          this.closeReviewModal();
          this.loadReservations();
        },
        error: (err) => {
          alert(
            'Failed to submit review: ' + (err.error?.message || err.message)
          );
          this.submittingReview = false;
        },
      });
  }

  canCancel(reservation: RoomReservation): boolean {
    const today = new Date();
    const start = new Date(reservation.startDate + 'T00:00:00');
    return today < start && reservation.status !== 'CANCELLED';
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

  onMatPageChange(event: PageEvent) {
    this.pageNumber = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadReservations();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onSortChange() {
    this.pageNumber = 0;
    this.loadReservations();
  }
}
