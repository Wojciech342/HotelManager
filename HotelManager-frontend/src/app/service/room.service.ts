import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Room } from '../model/room';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RoomService {
  private baseUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient) {}

  createRoom(room: Room): Observable<Room> {
    return this.http.post<Room>(`${this.baseUrl}`, room);
  }

  getFilteredRooms(filters: any): Observable<Room[]> {
    let params = new HttpParams();
    if (filters.type && Array.isArray(filters.type)) {
      filters.type.forEach((type: string) => {
        params = params.append('type', type);
      });
    }
    if (filters.minPrice != null)
      params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice != null)
      params = params.set('maxPrice', filters.maxPrice);
    if (filters.minRating != null)
      params = params.set('minRating', filters.minRating);

    return this.http.get<Room[]>(this.baseUrl, { params });
  }

  getRoomById(id: number): Observable<Room> {
    return this.http.get<Room>(`${this.baseUrl}/${id}`);
  }
}
