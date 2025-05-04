// src/app/room-create/room-create.component.ts
import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { RoomService } from '../../service/room.service';
import { Room } from '../../model/room';

@Component({
  selector: 'app-room-create',
  templateUrl: './room-create.component.html',
})
export class RoomCreateComponent {
  roomForm;

  constructor(private fb: FormBuilder, private roomService: RoomService) {
    this.roomForm = this.fb.group({
      number: [null, [Validators.required, Validators.min(1)]],
      type: ['', Validators.required],
      capacity: [null, [Validators.required, Validators.min(1)]],
      status: ['', Validators.required],
      rating: [0, [Validators.required, Validators.min(1), Validators.max(10)]],
      pricePerNight: [0, [Validators.required, Validators.min(0)]],
    });
  }

  onSubmit() {
    if (this.roomForm.invalid) {
      return;
    }
    const newRoom: Room = {
      number: this.roomForm.value.number || 0,
      type: this.roomForm.value.type || '',
      capacity: this.roomForm.value.capacity || 0,
      status: this.roomForm.value.status || '',
      rating: this.roomForm.value.rating || 0,
      pricePerNight: this.roomForm.value.pricePerNight || 0,
    };
    this.roomService.createRoom(newRoom).subscribe({
      next: (created) => {
        console.log('Room created:', created);
        this.roomForm.reset();
        // optionally navigate or show a success message
      },
      error: (err) => console.error('Error creating room', err),
    });
  }
}
