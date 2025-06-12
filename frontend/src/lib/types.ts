import { FREQUENCY, NOTIFICATION_TYPE } from "./constants";

export type User = {
  id: string;
  name: string;
  email: string;
  timezone: string;
  profilePicture?: string;
};

export type Goal = {
  id: number;
  title: string;
  description: string;
  createdBy: User;
  timezone: string;
  startAt: Date;
  frequency: FREQUENCY;
  squad: User[];
  tags: string[];
  isPublic: boolean;
  createdAt: Date;
  updatedAt: Date;
  nextDueAt: Date;
};

export type Notification = {
  id: number;
  notificationType: NOTIFICATION_TYPE;
  createdAt: Date;
  read: boolean;
  senderName?: string;
  senderProfilePicture?: string;
  goalTitle: string;
};

export type Invitation = {
  id: number;
  goal: Goal;
  inviterName: string;
};
