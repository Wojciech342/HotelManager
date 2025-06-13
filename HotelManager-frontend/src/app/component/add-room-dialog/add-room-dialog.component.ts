import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { RoomService } from '../../service/room.service';
import { ROOM_TYPES } from '../../constants/room-types';

@Component({
  selector: 'app-add-room-dialog',
  templateUrl: './add-room-dialog.component.html',
  styleUrls: ['./add-room-dialog.component.css'],
})
export class AddRoomDialogComponent {
  roomTypes: string[] = ROOM_TYPES;
  roomForm: FormGroup;
  selectedFile: File | null = null;
  previewUrl: string | ArrayBuffer | null = null;
  loading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddRoomDialogComponent>,
    private roomService: RoomService
  ) {
    this.roomForm = this.fb.group({
      number: [1, Validators.required],
      type: ['', Validators.required],
      capacity: [1, [Validators.required, Validators.min(1)]],
      pricePerNight: [0, [Validators.required, Validators.min(0)]],
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = (e) => (this.previewUrl = reader.result);
      reader.readAsDataURL(this.selectedFile);
    }
  }

  onSubmit() {
    if (this.roomForm.invalid) return;
    this.loading = true;
    this.errorMessage = null;

    const formData = new FormData();
    formData.append('room', JSON.stringify(this.roomForm.value));
    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }

    this.roomService.addRoom(formData).subscribe({
      next: (room) => {
        this.loading = false;
        this.dialogRef.close(room);
      },
      error: (err) => {
        console.log(err);
        this.loading = false;
        if (err.status === 409) {
          this.errorMessage = 'A room with this number already exists.';
        } else {
          this.errorMessage = 'An unexpected error occurred. Please try again.';
        }
      },
    });
  }
}
