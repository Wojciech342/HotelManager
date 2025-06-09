import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RoomService } from '../../service/room.service';
import { Room } from '../../model/room';
import { RoomReview } from '../../model/roomReview';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css'],
})
export class RoomsComponent implements OnInit {
  rooms: Room[] = [];
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

  getRooms(filters: any = {}): void {
    if (this.selectedTypes.length > 0) {
      filters.type = this.selectedTypes;
    }
    this.roomService.getFilteredRooms(filters).subscribe({
      next: (rooms) => (this.rooms = rooms),
      error: (err) => console.error('Error fetching rooms:', err),
    });
  }

  onFilter(): void {
    const filters = this.filterForm.value;
    this.getRooms(filters);
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.selectedTypes = [];
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
}
