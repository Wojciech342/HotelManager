import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from '../model/message';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  private baseUrl = 'http://localhost:8080/api/messages';

  constructor(private http: HttpClient) {}

  getUserMessages(): Observable<Message[]> {
    return this.http.get<Message[]>(this.baseUrl);
  }

  getMessage(id: number): Observable<Message> {
    return this.http.get<Message>(`${this.baseUrl}/${id}`);
  }

  getMessageThread(id: number): Observable<Message[]> {
    return this.http.get<Message[]>(`${this.baseUrl}/thread/${id}`);
  }

  sendContactMessage(message: string): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/contact`, {
      message,
    });
  }

  replyToMessage(messageId: number, message: string): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/reply/${messageId}`, {
      message,
    });
  }

  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/unread-count`);
  }
}
