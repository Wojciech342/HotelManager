// In the template comparison, use username instead of id
import { Component, OnInit } from '@angular/core';
import { MessageService } from '../../service/message.service';
import { Message } from '../../model/message';
import { AuthService } from '../../auth/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { MessageDetailComponent } from '../message-detail/message-detail.component';
import { TokenStorageService } from '../../auth/token-storage.service';

@Component({
  selector: 'app-my-messages',
  templateUrl: './my-messages.component.html',
  styleUrls: ['./my-messages.component.css'],
})
export class MyMessagesComponent implements OnInit {
  messages: Message[] = [];
  loading = false;
  error = '';
  currentUsername: string = '';

  constructor(
    private messageService: MessageService,
    public authService: AuthService,
    private tokenStorage: TokenStorageService,
    private dialog: MatDialog
  ) {
    this.currentUsername = this.tokenStorage.getUsername() || '';
  }

  ngOnInit(): void {
    this.loadMessages();
  }

  loadMessages() {
    this.loading = true;
    this.messageService.getUserMessages().subscribe({
      next: (data) => {
        this.messages = data;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      },
    });
  }

  openMessage(message: Message) {
    const dialogRef = this.dialog.open(MessageDetailComponent, {
      width: '600px',
      data: { messageId: message.id },
    });

    dialogRef.afterClosed().subscribe(() => {
      this.loadMessages(); // Refresh messages to update read status
    });
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleString();
  }

  getMessagePreview(content: string): string {
    return content.length > 50 ? content.substring(0, 50) + '...' : content;
  }
}
