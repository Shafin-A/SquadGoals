import { Notification } from "@/lib/types";

export const fetchRecentNotifications = async ({
  recent = true,
  limit = 6,
  idToken,
}: {
  recent?: boolean;
  limit?: number;
  idToken: string;
}): Promise<Notification[]> => {
  const res = await fetch(
    `http://localhost:8080/api/notifications?recent=${recent}&limit=${limit}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${idToken}`,
      },
    }
  );

  if (!res.ok) {
    throw new Error("Failed to fetch notifications");
  }

  return res.json();
};

export const markNotificationAsRead = async (
  notificationId: number,
  idToken: string
): Promise<unknown> => {
  const res = await fetch(
    `http://localhost:8080/api/notifications/${notificationId}`,
    {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${idToken}`,
        body: JSON.stringify({
          read: true,
        }),
      },
    }
  );

  if (!res.ok) {
    throw new Error("Failed to mark notification as read");
  }

  return res.json();
};

export const markAllNotificationsAsRead = async (idToken: string) => {
  const res = await fetch(`http://localhost:8080/notifications/mark-all-read`, {
    method: "PATCH",
    headers: {
      Authorization: `Bearer ${idToken}`,
      "Content-Type": "application/json",
    },
  });

  if (!res.ok) {
    throw new Error("Failed to mark all notifications as read");
  }

  return res.json();
};
