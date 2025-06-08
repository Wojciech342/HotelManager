import { Room } from './room';

export interface RoomReview {
  id?: number;
  username: string;
  room?: Room;
  description: string;
  rating: number;
}
