import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MessageService } from '../../service/message.service';
import { Message } from '../../model/message';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { TokenStorageService } from '../../auth/token-storage.service';

@Component({
  selector: 'app-message-detail',
  templateUrl: './message-detail.component.html',
  styleUrls: ['./message-detail.component.css'],
})
export class MessageDetailComponent implements OnInit {
  message: Message | null = null;
  replyForm: FormGroup;
  loading = true;
  replying = false;
  error = '';
  thread: Message[] = [];
  currentUsername: string = '';

  constructor(
    private dialogRef: MatDialogRef<MessageDetailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { messageId: number },
    private messageService: MessageService,
    private formBuilder: FormBuilder,
    public authService: AuthService,
    private tokenStorage: TokenStorageService
  ) {
    this.replyForm = this.formBuilder.group({
      replyMessage: ['', [Validators.required, Validators.minLength(5)]],
    });
    this.currentUsername = this.tokenStorage.getUsername() || '';
  }

  ngOnInit(): void {
    this.loadMessageThread();
  }

  loadMessageThread() {
    this.loading = true;
    this.messageService.getMessageThread(this.data.messageId).subscribe({
      next: (data) => {
        this.thread = data;
        if (data.length > 0) {
          this.message = data[0]; // Root message
        }
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      },
    });
  }

  sendReply() {
    if (this.replyForm.invalid || !this.message) {
      return;
    }

    this.replying = true;
    this.error = ''; // Clear any previous errors
    const replyMessage = this.replyForm.get('replyMessage')?.value;

    this.messageService
      .replyToMessage(this.message.id!, replyMessage)
      .subscribe({
        next: (data) => {
          this.thread.push(data);
          this.replyForm.reset();
          this.replying = false;
        },
        error: (error) => {
          this.error =
            'Failed to send reply: ' +
            (error.error || error.message || 'Unknown error');
          this.replying = false;
        },
      });
  }

  close(): void {
    this.dialogRef.close();
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleString();
  }

  canReply(): boolean {
    if (!this.message) return false;

    const currentUsername = this.tokenStorage.getUsername() || '';

    // User can reply if they are part of the conversation
    return (
      this.message.sender.username === currentUsername ||
      this.message.recipient.username === currentUsername
    );
  }

  // Add helper method to show who the reply will go to
  getReplyRecipient(): string {
    const isAdmin = this.tokenStorage.getAuthorities().includes('ROLE_ADMIN');

    if (isAdmin) {
      // Admin's reply will go to the user in the conversation
      if (this.message?.sender.username === 'admin') {
        return this.message.recipient.username;
      } else {
        return this.message?.sender.username || '';
      }
    } else {
      // User's reply always goes to admin
      return 'admin';
    }
  }
}
