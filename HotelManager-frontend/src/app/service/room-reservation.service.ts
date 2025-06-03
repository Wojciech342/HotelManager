import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RoomReservation } from '../model/roomReservation';

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

  // You can add more methods for fetching, updating, deleting reservations as needed
}
