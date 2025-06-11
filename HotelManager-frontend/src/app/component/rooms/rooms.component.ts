import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RoomService, RoomResponse } from '../../service/room.service';
import { Room } from '../../model/room';
import { RoomReview } from '../../model/roomReview';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css'],
})
export class RoomsComponent implements OnInit {
  rooms: Room[] = [];
  totalElements = 0;
  totalPages = 0;
  page = 0;
  size = 1;
  lastPage = false;
  filterForm: FormGroup;
  roomTypes: string[] = ['SINGLE', 'DOUBLE', 'FAMILY', 'SUITE', 'DELUXE'];
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

  constructor(private roomService: RoomService, private fb: FormBuilder) {
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
        this.page,
        this.size,
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

  onFilter(): void {
    this.page = 0;
    const filters = this.filterForm.value;
    this.getRooms(filters);
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.selectedTypes = [];
    this.page = 0;
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
    this.page = 0;
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
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.getRooms(this.filterForm.value);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
