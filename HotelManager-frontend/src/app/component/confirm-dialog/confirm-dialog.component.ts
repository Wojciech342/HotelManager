import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'warning' | 'danger' | 'success' | 'info';
  confirmIcon?: string;
  cancelIcon?: string;
}

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.css'],
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData
  ) {
    // Set defaults if not provided
    this.data.confirmText = this.data.confirmText || 'Confirm';
    this.data.cancelText = this.data.cancelText || 'Cancel';
    this.data.type = this.data.type || 'warning';
    this.data.confirmIcon = this.data.confirmIcon || 'check';
    this.data.cancelIcon = this.data.cancelIcon || 'close';
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  // Helper method to get the appropriate color based on type
  getColor(): string {
    switch (this.data.type) {
      case 'danger':
        return 'warn';
      case 'success':
        return 'primary';
      case 'info':
        return 'accent';
      case 'warning':
      default:
        return 'warn';
    }
  }
}
