import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Room } from '../model/room';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface ApiError {
  status: number;
  message: string;
}

export interface RoomResponse {
  content: Room[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  lastPage: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class RoomService {
  private baseUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient) {}

  createRoom(room: Room): Observable<Room> {
    return this.http.post<Room>(`${this.baseUrl}`, room);
  }

  getFilteredRooms(
    filters: any = {},
    pageNumber: number = 0,
    pageSize: number = 10,
    sortBy: string = 'number',
    sortOrder: string = 'asc'
  ): Observable<RoomResponse> {
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

    params = params.set('pageNumber', pageNumber);
    params = params.set('pageSize', pageSize);
    params = params.set('sortBy', sortBy);
    params = params.set('sortOrder', sortOrder);

    return this.http.get<RoomResponse>(this.baseUrl, { params });
  }

  getRoomById(id: number): Observable<Room> {
    return this.http.get<Room>(`${this.baseUrl}/${id}`);
  }

  addRoom(formData: FormData): Observable<Room> {
    return this.http.post<Room>(this.baseUrl, formData);
  }

  deleteRoom(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      catchError((error) => {
        let errorMessage = 'An unknown error occurred';

        if (error.status === 409) {
          errorMessage =
            'Cannot delete room with active or upcoming reservations.';
        }

        return throwError(
          () =>
            ({
              status: error.status,
              message: errorMessage,
            } as ApiError)
        );
      })
    );
  }
}
