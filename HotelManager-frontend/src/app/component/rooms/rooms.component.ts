import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RoomService, RoomResponse } from '../../service/room.service';
import { Room } from '../../model/room';
import { RoomReview } from '../../model/roomReview';
import { PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { AddRoomDialogComponent } from '../add-room-dialog/add-room-dialog.component';
import { ROOM_TYPES } from '../../constants/room-types';
import { AuthService } from '../../auth/auth.service';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { ErrorDialogComponent } from '../error-dialog/error-dialog.component';
import { ApiError } from '../../service/room.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SuccessDialogComponent } from '../success-dialog/success-dialog.component';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css'],
})
export class RoomsComponent implements OnInit {
  rooms: Room[] = [];
  totalElements = 0;
  totalPages = 0;
  pageNumber = 0;
  pageSize = 5;
  lastPage = false;
  filterForm: FormGroup;
  roomTypes: string[] = ROOM_TYPES;
  selectedTypes: string[] = [];
  isBrowser = typeof window !== 'undefined';

  minValue: number = 0;
  maxValue: number = 1000;
  sliderOptions: any = {
    floor: 0,
    ceil: 1000,
    step: 10,
    translate: (value: number): string => `${value}`,
  };

  sortBy: string = 'number';
  sortOrder: string = 'asc';

  sortOptions = [
    { value: 'number', label: 'Room Number' },
    { value: 'pricePerNight', label: 'Price' },
    { value: 'averageRating', label: 'Rating' },
    { value: 'capacity', label: 'Capacity' },
  ];

  orderOptions = [
    { value: 'asc', label: 'Ascending' },
    { value: 'desc', label: 'Descending' },
  ];

  showReviewsModal = false;
  selectedRoomReviews: RoomReview[] = [];

  constructor(
    private roomService: RoomService,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.filterForm = this.fb.group({
      minPrice: [null],
      maxPrice: [null],
      minRating: [null],
    });
  }

  ngOnInit(): void {
    this.getRooms();
    this.filterForm.patchValue({
      minPrice: this.minValue,
      maxPrice: this.maxValue,
    });
  }

  getRooms(filters: any = {}) {
    if (this.selectedTypes.length > 0) {
      filters.type = this.selectedTypes;
    } else {
      filters.type = [];
    }
    this.roomService
      .getFilteredRooms(
        filters,
        this.pageNumber,
        this.pageSize,
        this.sortBy,
        this.sortOrder
      )
      .subscribe({
        next: (res) => {
          this.rooms = res.content;
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
          this.lastPage = res.lastPage;
        },
        error: (err) => console.error('Error fetching rooms:', err),
      });
  }

  deleteRoom(roomId: number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete Room',
        message:
          'Are you sure you want to delete this room? This action cannot be undone.',
        confirmText: 'Delete',
        type: 'danger',
        confirmIcon: 'delete',
        cancelText: 'Cancel',
        cancelIcon: 'close',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.roomService.deleteRoom(roomId).subscribe({
          next: () => {
            this.getRooms(); // Refresh the list after deletion

            // Open success dialog
            this.dialog.open(SuccessDialogComponent, {
              width: 'auto',
              panelClass: 'success-dialog-panel',
              position: { top: '100px' }, // Only specify top, making it centered horizontally
              hasBackdrop: false, // No darkened backdrop
              data: {
                message: 'Room deleted successfully',
              },
            });
          },
          error: (err: ApiError) => {
            if (err.status === 409) {
              this.dialog.open(ErrorDialogComponent, {
                width: '450px',
                position: { top: '100px' },
                data: {
                  title: 'Cannot Delete Room',
                  message:
                    'This room has active or upcoming reservations and cannot be deleted. Please cancel all reservations first.',
                },
              });
            } else {
              this.dialog.open(ErrorDialogComponent, {
                width: '400px',
                position: { top: '100px' },
                data: {
                  title: 'Error',
                  message: 'Failed to delete room. Please try again later.',
                },
              });
            }
          },
        });
      }
    });
  }

  onFilter(): void {
    this.pageNumber = 0;
    const filters = this.filterForm.value;
    this.getRooms(filters);
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.selectedTypes = [];
    this.pageNumber = 0;
    this.getRooms();
  }

  onTypeChange(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.selectedTypes.push(checkbox.value);
    } else {
      this.selectedTypes = this.selectedTypes.filter(
        (type) => type !== checkbox.value
      );
    }
  }

  onSortChange() {
    this.pageNumber = 0;
    this.getRooms(this.filterForm.value);
  }

  roundToOneDecimal(value: number | null | undefined): string {
    if (value == null) return 'N/A';
    return value.toFixed(1);
  }

  openReviewsModal(room: Room | undefined) {
    this.selectedRoomReviews = room?.reviews ?? [];
    this.showReviewsModal = true;
  }

  closeReviewsModal() {
    this.showReviewsModal = false;
    this.selectedRoomReviews = [];
  }

  onMatPageChange(event: PageEvent) {
    this.pageNumber = event.pageIndex;
    this.pageSize = event.pageSize;
    this.getRooms(this.filterForm.value);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  openAddRoomDialog() {
    const dialogRef = this.dialog.open(AddRoomDialogComponent, {
      width: '400px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.getRooms(); // Refresh the list
      }
    });
  }

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }
}
