export interface Room {
  id?: number;
  number: number;
  type: string;
  capacity: number;
  status: string;
  rating: number;
  pricePerNight: number;
  imageUrl?: string;
}
