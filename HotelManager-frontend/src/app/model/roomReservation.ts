import { Reservation } from './reservation';
import { Room } from './room';
import { RoomReview } from './roomReview';

export interface RoomReservation extends Reservation {
  room?: Room;
  roomReview?: RoomReview;
}
