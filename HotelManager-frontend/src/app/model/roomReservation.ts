import { Reservation } from './reservation';

export interface RoomReservation extends Reservation {
  roomId: number;
}
