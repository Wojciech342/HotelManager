export interface Reservation {
  id?: number;
  reservationDate?: string; // ISO string
  startDate: string; // ISO string
  endDate: string; // ISO string
  price?: number;
  status?: string;
}
