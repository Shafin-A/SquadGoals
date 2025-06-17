import { User } from "@/lib/types";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

interface GoalHeaderProps {
  title: string;
  creator: User;
}

export const GoalHeader = ({ title, creator }: GoalHeaderProps) => {
  return (
    <div className="flex items-start justify-between gap-4">
      <div className="flex flex-col gap-2">
        <h1 className="text-2xl font-semibold">{title}</h1>
        <p className="text-sm text-muted-foreground flex items-center gap-2">
          <span>Created by</span>
          <Avatar className="w-8 h-8">
            <AvatarImage src={creator.profilePicture} alt={creator.name} />
            <AvatarFallback>
              {creator.name
                .split(" ")
                .map((n) => n[0])
                .join("")
                .toUpperCase()
                .slice(0, 2)}
            </AvatarFallback>
          </Avatar>
          <span>{creator.name}</span>
        </p>
      </div>
      <div className="flex gap-2">
        <Button variant="outline">Leave</Button>
      </div>
    </div>
  );
};
