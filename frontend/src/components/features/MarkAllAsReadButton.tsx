import { Eye, Loader2 } from "lucide-react";
import { DropdownMenuItem } from "@/components/ui/dropdown-menu";

interface MarkAllAsReadButtonProps {
  onClick: () => void;
  disabled: boolean;
  isPending: boolean;
}

export const MarkAllAsReadButton = ({
  onClick,
  disabled,
  isPending,
}: MarkAllAsReadButtonProps) => {
  return (
    <DropdownMenuItem
      className="text-primary font-medium text-center justify-center group hover:bg-primary/10"
      onClick={onClick}
      disabled={disabled || isPending}
    >
      {isPending ? (
        <Loader2 className="w-4 h-4 animate-spin mr-2" />
      ) : (
        <>
          <Eye className="w-4 h-4 mr-2 text-primary group-hover:scale-110 transition-transform" />
          Mark all as read
        </>
      )}
    </DropdownMenuItem>
  );
};
