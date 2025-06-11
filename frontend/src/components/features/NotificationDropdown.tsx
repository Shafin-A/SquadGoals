import { NotificationItem } from "@/components/features/NotificationIem";
import { MarkAllAsReadButton } from "@/components/features/MarkAllAsReadButton";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Bell, BellDot, Loader2 } from "lucide-react";
import { useNotifications } from "@/hooks/useNotifications";

interface NotificationDropdownProps {
  buttonClassName?: string;
  align?: "start" | "center" | "end";
}

export function NotificationDropdown({
  buttonClassName = "",
  align = "end",
}: NotificationDropdownProps) {
  const {
    isAuthenticated,
    notifications,
    notificationsLoading,
    hasUnreads,
    handleMarkAsRead,
    handleMarkAllAsRead,
    markAllAsReadMutation,
  } = useNotifications();

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
      <DropdownMenuContent align={align} className="w-80">
        <DropdownMenuLabel>Notifications</DropdownMenuLabel>
        <DropdownMenuSeparator />
        <ScrollArea className="max-h-[300px]">
          {notificationsLoading ? (
            <DropdownMenuItem className="items-center justify-center">
              <Loader2 className="w-4 h-4 animate-spin" />
            </DropdownMenuItem>
          ) : notifications && notifications.length > 0 ? (
            notifications.map((notification) => (
              <NotificationItem
                key={notification.id}
                notification={notification}
                onClick={(n) => handleMarkAsRead(n)}
              />
            ))
          ) : (
            <DropdownMenuItem className="text-foreground dark:text-muted-foreground italic">
              No notifications
            </DropdownMenuItem>
          )}
          <DropdownMenuSeparator />
        </ScrollArea>
        <MarkAllAsReadButton
          onClick={handleMarkAllAsRead}
          disabled={!hasUnreads}
          isPending={markAllAsReadMutation.isPending}
        />
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
