export interface Room {
  id?: number;
  number: number;
  averageRating?: number;
  type: string;
  capacity: number;
  status: string;
  pricePerNight: number;
  imageUrl?: string;
}
