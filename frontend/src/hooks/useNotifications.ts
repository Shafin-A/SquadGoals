import { auth } from "@/firebase";
import { useEffect, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  fetchRecentNotifications,
  markAllNotificationsAsRead,
  markNotificationAsRead,
} from "@/api/notification";
import { Notification } from "@/lib/types";

export function useNotifications() {
  const [user, setUser] = useState(() => auth.currentUser);
  const [idToken, setIdToken] = useState<string>("");
  const queryClient = useQueryClient();

  useEffect(() => {
    const unsubscribe = auth.onAuthStateChanged(setUser);
    return () => unsubscribe();
  }, []);

  useEffect(() => {
    let isMounted = true;

    const fetchIdToken = async () => {
      if (user) {
        const token = await user.getIdToken();
        if (isMounted) setIdToken(token);
      } else {
        setIdToken("");
      }
    };

    fetchIdToken();

    return () => {
      isMounted = false;
    };
  }, [user]);

  const isAuthenticated = !!user;

  const { data: notifications, isLoading: notificationsLoading } = useQuery({
    queryKey: ["notifications"],
    queryFn: () => fetchRecentNotifications({ idToken }),
    enabled: isAuthenticated && !!idToken,
  });

  const markAsReadMutation = useMutation({
    mutationFn: (notificationId: number) =>
      markNotificationAsRead(notificationId, idToken),
    onMutate: async (notificationId) => {
      await queryClient.cancelQueries({ queryKey: ["notifications"] });
      const previousNotifications = queryClient.getQueryData<Notification[]>([
        "notifications",
      ]);

      queryClient.setQueryData<Notification[]>(["notifications"], (old) =>
        old?.map((notification) =>
          notification.id === notificationId
            ? { ...notification, read: true }
            : notification
        )
      );

      return { previousNotifications };
    },
    onError: (err, variables, context) => {
      if (context?.previousNotifications) {
        queryClient.setQueryData(
          ["notifications"],
          context.previousNotifications
        );
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });

  const markAllAsReadMutation = useMutation({
    mutationFn: () => markAllNotificationsAsRead(idToken),
    onMutate: async () => {
      await queryClient.cancelQueries({ queryKey: ["notifications"] });
      const previousNotifications = queryClient.getQueryData<Notification[]>([
        "notifications",
      ]);

      queryClient.setQueryData<Notification[]>(["notifications"], (old) =>
        old?.map((notification) => ({ ...notification, read: true }))
      );

      return { previousNotifications };
    },
    onError: (err, variables, context) => {
      if (context?.previousNotifications) {
        queryClient.setQueryData(
          ["notifications"],
          context.previousNotifications
        );
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });

  const hasUnreads = notifications?.some((notification) => !notification.read);

  const handleMarkAsRead = (
    notification: Notification,
    callback?: () => void
  ) => {
    if (!notification.read) {
      markAsReadMutation.mutate(notification.id);
    }
    if (callback) callback();
  };

  const handleMarkAllAsRead = () => {
    if (notifications?.some((n) => !n.read)) {
      markAllAsReadMutation.mutate();
    }
  };

  return {
    isAuthenticated,
    notifications,
    notificationsLoading,
    hasUnreads,
    handleMarkAsRead,
    handleMarkAllAsRead,
    markAllAsReadMutation: {
      isPending: markAllAsReadMutation.isPending,
    },
  };
}
