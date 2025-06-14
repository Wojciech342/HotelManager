import { User } from './user';

export interface Reservation {
  id?: number;
  reservationDate?: string;
  startDate?: string;
  endDate: string;
  price?: number;
  status?: string;
  user?: User;
}
