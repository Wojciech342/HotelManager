export interface Message {
  id?: number;
  subject: string;
  content: string;
  sentAt: Date;
  read: boolean;
  sender: {
    id: number;
    username: string;
  };
  recipient: {
    id: number;
    username: string;
  };
  parentMessage?: Message;
}
