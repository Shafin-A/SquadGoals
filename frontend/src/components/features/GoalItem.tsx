import { Badge } from "@/components/ui/badge";
import { Goal } from "@/lib/types";
import { format } from "date-fns";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardDescription,
} from "@/components/ui/card";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Calendar, Clock } from "lucide-react";

interface GoalItemProps {
  goal: Goal;
  variant?: "default" | "compact";
}

export const GoalItem = ({ goal, variant = "default" }: GoalItemProps) => {
  if (variant === "compact") {
    return (
      <div className="space-y-2">
        <div className="flex items-center gap-3">
          <Avatar className="w-10 h-10">
            <AvatarImage
              src={goal.createdBy.profilePicture}
              alt={goal.createdBy.name}
            />
            <AvatarFallback>
              {goal.createdBy.name
                .split(" ")
                .map((n) => n[0])
                .join("")
                .toUpperCase()
                .slice(0, 2)}
            </AvatarFallback>
          </Avatar>
          <div>
            <h3 className="font-semibold text-base">{goal.title}</h3>
            <p className="text-sm text-muted-foreground">
              by {goal.createdBy.name}
            </p>
          </div>
        </div>
        <p className="text-sm text-muted-foreground line-clamp-2">
          {goal.description || <i>No description provided.</i>}
        </p>
        <div className="flex items-center gap-4 text-sm text-muted-foreground">
          <div className="flex items-center gap-1">
            <Calendar className="w-4 h-4" />
            {goal.startAt
              ? format(new Date(goal.startAt), "MMM d, yyyy")
              : "N/A"}
          </div>
          <div className="flex items-center gap-1">
            <Clock className="w-4 h-4" />
            {goal.frequency}
          </div>
        </div>
        <div className="flex gap-2 flex-wrap">
          {goal.tags?.slice(0, 3).map((tag: string) => (
            <Badge key={tag} variant="secondary" className="text-xs">
              {tag}
            </Badge>
          ))}
          {goal.tags?.length > 3 && (
            <Badge variant="secondary" className="text-xs">
              +{goal.tags.length - 3} more
            </Badge>
          )}
        </div>
      </div>
    );
  }

  return (
    <Card className="bg-secondary h-full">
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg truncate">{goal.title}</CardTitle>
          <span className="text-xs text-muted-foreground">{goal.timezone}</span>
        </div>
        <CardDescription className="truncate">
          {goal.description || <i>No description provided.</i>}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-2 gap-x-6 gap-y-1 text-xs text-muted-foreground">
          <div>
            <div className="font-semibold">Created by:</div>
            <span className="inline-flex items-center gap-1 mt-1">
              <Avatar className="w-5 h-5">
                <AvatarImage
                  src={goal.createdBy.profilePicture}
                  alt={goal.createdBy.name}
                />
                <AvatarFallback className="truncate">
                  {goal.createdBy.name
                    .split(" ")
                    .map((n) => n[0])
                    .join("")
                    .toUpperCase()
                    .slice(0, 2)}
                </AvatarFallback>
              </Avatar>
              {goal.createdBy?.name}
            </span>
          </div>
          <div>
            <div className="font-semibold">Start:</div>
            <div className="mt-1">
              {goal.startAt ? format(new Date(goal.startAt), "PPP") : "N/A"}
            </div>
          </div>
          <div>
            <div className="font-semibold">Frequency:</div>
            <div className="mt-1">{goal.frequency}</div>
          </div>
          <div>
            <div className="font-semibold">Squad:</div>
            <div className="mt-1">{goal.squad?.length ?? 0}</div>
          </div>
        </div>
        <div className="flex gap-2 flex-wrap mt-8">
          {goal.tags?.map((tag: string) => (
            <Badge key={tag}>{tag}</Badge>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};
