import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RoomReservation } from '../model/roomReservation';
import { RoomReview } from '../model/roomReview';

export interface RoomReservationResponse {
  content: RoomReservation[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  lastPage: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class RoomReservationService {
  private apiUrl = 'http://localhost:8080/api/roomReservations';

  constructor(private http: HttpClient) {}

  createRoomReservation(
    username: string,
    roomId: number,
    reservation: RoomReservation
  ): Observable<RoomReservation> {
    return this.http.post<RoomReservation>(
      `${this.apiUrl}?username=${username}&roomId=${roomId}`,
      reservation
    );
  }

  getReservationsByRoomId(roomId: number): Observable<RoomReservation[]> {
    return this.http.get<RoomReservation[]>(`${this.apiUrl}/rooms/${roomId}`);
  }

  getReservationsByUsername(
    username: string,
    pageNumber: number = 0,
    pageSize: number = 10,
    sortBy: string = 'number',
    sortOrder: string = 'asc'
  ): Observable<RoomReservationResponse> {
    let params = new HttpParams()
      .set('pageNumber', pageNumber)
      .set('pageSize', pageSize)
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);
    return this.http.get<RoomReservationResponse>(
      `${this.apiUrl}/users/${username}`,
      { params }
    );
  }

  updateReservation(
    reservationId: number,
    roomReservation: RoomReservation
  ): Observable<RoomReservation> {
    return this.http.put<RoomReservation>(
      `${this.apiUrl}/${reservationId}`,
      roomReservation
    );
  }

  addReviewToReservation(review: RoomReview, roomReservationId: number) {
    return this.http.post(
      `http://localhost:8080/api/room-reviews?roomReservationId=${roomReservationId}`,
      review
    );
  }

  getRoomReservations(
    pageNumber: number = 0,
    pageSize: number = 10,
    sortBy: string = 'number',
    sortOrder: string = 'asc'
  ): Observable<RoomReservationResponse> {
    let params = new HttpParams()
      .set('pageNumber', pageNumber)
      .set('pageSize', pageSize)
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    return this.http.get<RoomReservationResponse>(this.apiUrl, { params });
  }
}
