import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Settings } from "lucide-react";
import { DropdownMenuItem } from "@/components/ui/dropdown-menu";
import Link from "next/link";
import { formatDistanceToNow } from "date-fns";
import { NOTIFICATION_TYPE } from "@/lib/constants";
import { Notification } from "@/lib/types";

interface NotificationItemProps {
  notification: Notification;
  onClick: (notification: Notification) => void;
}

export const NotificationItem = ({
  notification,
  onClick,
}: NotificationItemProps) => {
  const unread = !notification.read;

  const notificationContent = (
    <div className="flex items-center gap-3 py-2 w-full">
      <div className="flex-shrink-0">
        <Avatar className="h-8 w-8">
          <AvatarImage
            src={notification.senderProfilePicture}
            alt={notification.senderName || "User"}
          />
          <AvatarFallback>
            {notification.notificationType === NOTIFICATION_TYPE.INVITE ? (
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
      <div className="flex flex-col">
        {notification.notificationType === NOTIFICATION_TYPE.INVITE ? (
          <span className="text-xs dark:text-muted-foreground">
            <span className="font-bold">{notification.senderName}</span> invited
            you to join the goal&nbsp;
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

  if (notification.notificationType === NOTIFICATION_TYPE.INVITE) {
    return (
      <DropdownMenuItem
        asChild
        key={notification.id}
        onClick={() => onClick(notification)}
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

  return (
    <DropdownMenuItem
      key={notification.id}
      onClick={() => onClick(notification)}
      className="p-0"
    >
      {notificationContent}
    </DropdownMenuItem>
  );
};
