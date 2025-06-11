import Link from "next/link";
import { Bell, BellDot, Loader2 } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Notification } from "@/lib/types";
import { auth } from "@/firebase";
import { useEffect, useState } from "react";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Settings } from "lucide-react";
import {
  fetchRecentNotifications,
  markNotificationAsRead,
} from "@/api/notification";
import { NOTIFICATION_TYPE } from "@/lib/constants";
import { formatDistanceToNow } from "date-fns";

interface NotificationDropdownProps {
  buttonClassName?: string;
  align?: "start" | "center" | "end";
}

export function NotificationDropdown({
  buttonClassName = "",
  align = "end",
}: NotificationDropdownProps) {
  const [user, setUser] = useState(() => auth.currentUser);

  useEffect(() => {
    const unsubscribe = auth.onAuthStateChanged(setUser);
    return () => unsubscribe();
  }, []);

  const isAuthenticated = !!user;

  const [idToken, setIdToken] = useState<string>("");

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

  const queryClient = useQueryClient();

  const { data: notifications, isLoading: notificationsLoading } = useQuery({
    queryKey: ["notifications"],
    queryFn: () => fetchRecentNotifications({ idToken }),
    enabled: isAuthenticated && !!idToken,
  });

  const markAsReadMutation = useMutation({
    mutationFn: (notificationId: number) =>
      markNotificationAsRead(notificationId, idToken),
    onSuccess: () => {
      // Refetch notifications to get updated read state from backend
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });

  const hasUnreads = notifications?.some(
    (notification: Notification) => !notification.read
  );

  const handleNotificationClick = (
    notification: Notification,
    onClick?: () => void
  ) => {
    if (!notification.read) {
      markAsReadMutation.mutate(notification.id);
    }
    if (onClick) onClick();
  };

  if (!isAuthenticated) return null;

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="outline"
          size="icon"
          aria-label="Notifications"
          className={buttonClassName}
        >
          {hasUnreads ? <BellDot /> : <Bell />}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent
        align={align}
        className="w-80 max-h-96 overflow-y-auto"
      >
        <DropdownMenuLabel>Notifications</DropdownMenuLabel>
        <DropdownMenuSeparator />
        {notificationsLoading ? (
          <DropdownMenuItem className="items-center justify-center">
            <Loader2 className="w-4 h-4 animate-spin" />
          </DropdownMenuItem>
        ) : notifications && notifications.length > 0 ? (
          notifications.map((notification: Notification, i: number) => {
            const unread = !notification.read;

            const notificationContent = (
              <div className="flex items-center gap-3 py-2 w-full">
                {/* Avatar */}
                <div className="flex-shrink-0">
                  <Avatar className="h-8 w-8">
                    <AvatarImage
                      src={notification.senderProfilePicture}
                      alt={notification.senderName || "User"}
                    />
                    <AvatarFallback>
                      {notification.notificationType ===
                      NOTIFICATION_TYPE.INVITE ? (
                        (notification.senderName || "?")
                          .split(" ")
                          .map((n) => n[0])
                          .join("")
                          .toUpperCase()
                          .slice(0, 2)
                      ) : (
                        <Settings className="w-4 h-4 text-muted-foreground" />
                      )}
                    </AvatarFallback>
                  </Avatar>
                </div>
                {/* Message + Timestamp */}
                <div className="flex flex-col">
                  {notification.notificationType ===
                  NOTIFICATION_TYPE.INVITE ? (
                    <span className="text-xs dark:text-muted-foreground">
                      <span className="font-bold">
                        {notification.senderName}
                      </span>{" "}
                      invited you to join the goal&nbsp;
                      <span className="font-bold">
                        &quot;{notification.goalTitle}&quot;
                      </span>
                      !
                    </span>
                  ) : (
                    <span className="text-xs dark:text-muted-foreground">
                      Your goal{" "}
                      <span className="font-bold">
                        &quot;{notification.goalTitle}&quot;
                      </span>{" "}
                      is due soon.
                    </span>
                  )}
                  {notification.createdAt && (
                    <span className="text-[10px] dark:text-muted-foreground mt-1">
                      {formatDistanceToNow(new Date(notification.createdAt), {
                        addSuffix: true,
                      })}
                    </span>
                  )}
                </div>
                {/* Unread Dot */}
                <div className="flex items-center justify-center w-11">
                  {unread && (
                    <span
                      className="inline-block h-2 w-2 rounded-full bg-amber-600"
                      aria-label="Unread"
                    />
                  )}
                </div>
              </div>
            );

            // For INVITE, wrap in Link
            if (notification.notificationType === NOTIFICATION_TYPE.INVITE) {
              return (
                <DropdownMenuItem
                  asChild
                  key={notification.id || i}
                  onClick={() => handleNotificationClick(notification)}
                  className="p-0"
                >
                  <Link
                    href="/invitations"
                    className="no-underline w-full hover:bg-accent"
                  >
                    {notificationContent}
                  </Link>
                </DropdownMenuItem>
              );
            }

            // For system notification
            return (
              <DropdownMenuItem
                key={notification.id || i}
                onClick={() => handleNotificationClick(notification)}
                className="p-0"
              >
                {notificationContent}
              </DropdownMenuItem>
            );
          })
        ) : (
          <DropdownMenuItem className="text-muted-foreground italic">
            No notifications
          </DropdownMenuItem>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
