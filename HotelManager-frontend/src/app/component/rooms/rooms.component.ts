import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RoomService } from '../../service/room.service';
import { Room } from '../../model/room';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css'],
})
export class RoomsComponent implements OnInit {
  rooms: Room[] = [];
  filterForm: FormGroup;
  roomTypes: string[] = ['SINGLE', 'DOUBLE', 'FAMILY', 'SUITE', 'DELUXE']; // Available room types
  selectedTypes: string[] = []; // Tracks selected room types

  constructor(private roomService: RoomService, private fb: FormBuilder) {
    this.filterForm = this.fb.group({
      minPrice: [null],
      maxPrice: [null],
      minRating: [null],
    });
  }

  ngOnInit(): void {
    this.getRooms();
  }

  getRooms(filters: any = {}): void {
    if (this.selectedTypes.length > 0) {
      filters.type = this.selectedTypes.join(','); // Send selected types as a comma-separated string
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
}
