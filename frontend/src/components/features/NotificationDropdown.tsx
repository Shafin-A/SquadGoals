import Link from "next/link";
import { Bell, Loader2 } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { useQuery } from "@tanstack/react-query";
import { Notification } from "@/lib/types";
import { auth } from "@/firebase";
import { useEffect, useState } from "react";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Settings } from "lucide-react";
import { fetchRecentNotifications } from "@/api/notification";
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

  const { data: notifications, isLoading: notificationsLoading } = useQuery({
    queryKey: ["notifications"],
    queryFn: () => fetchRecentNotifications({ idToken }),
    enabled: isAuthenticated && !!idToken,
  });

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
          <Bell />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent
        align={align}
        className="w-80 max-h-96 overflow-y-auto"
      >
        <DropdownMenuLabel>Notifications</DropdownMenuLabel>
        <DropdownMenuSeparator />
        {notificationsLoading ? (
          <DropdownMenuItem>
            <Loader2 className="w-4 h-4 animate-spin" />
          </DropdownMenuItem>
        ) : notifications && notifications.length > 0 ? (
          notifications.map((notification: Notification, i: number) => (
            <DropdownMenuItem
              key={notification.id || i}
              className="flex items-center gap-3 py-2"
            >
              <Avatar className="h-8 w-8">
                <AvatarImage
                  src={notification.senderProfilePicture}
                  alt={notification.senderName || "User"}
                />
                <AvatarFallback>
                  {notification.notificationType ===
                  NOTIFICATION_TYPE.SYSTEM ? (
                    <Settings className="w-4 h-4 text-muted-foreground" />
                  ) : (
                    (notification.senderName || "?")
                      .split(" ")
                      .map((n) => n[0])
                      .join("")
                      .toUpperCase()
                      .slice(0, 2)
                  )}
                </AvatarFallback>
              </Avatar>
              <div className="flex flex-col">
                <span className="text-xs dark:text-muted-foreground">
                  {notification.notificationType ===
                  NOTIFICATION_TYPE.INVITE ? (
                    <>
                      <span className="font-bold">
                        {notification.senderName}
                      </span>{" "}
                      invited you to join the goal&nbsp;
                      <span className="font-bold">
                        &quot;{notification.goalTitle}&quot;
                      </span>
                      !
                    </>
                  ) : (
                    <>
                      Your goal{" "}
                      <span className="font-bold">
                        &quot;{notification.goalTitle}&quot;
                      </span>{" "}
                      is due soon.
                    </>
                  )}
                </span>
                {notification.createdAt && (
                  <span className="text-[10px] dark:text-muted-foreground mt-1">
                    {formatDistanceToNow(new Date(notification.createdAt), {
                      addSuffix: true,
                    })}
                  </span>
                )}
              </div>
            </DropdownMenuItem>
          ))
        ) : (
          <DropdownMenuItem className="text-muted-foreground italic">
            No notifications
          </DropdownMenuItem>
        )}
        <DropdownMenuSeparator />
        <DropdownMenuItem asChild>
          <Link
            href="/notifications"
            className="w-full text-center text-primary font-medium"
          >
            View All Notifications
          </Link>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
