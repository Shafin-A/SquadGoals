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

interface GoalItemProps {
  goal: Goal;
}

export const GoalItem = ({ goal }: GoalItemProps) => (
  <Card className="bg-secondary h-full">
    <CardHeader>
      <div className="flex items-center justify-between">
        <CardTitle className="text-lg truncate">{goal.title}</CardTitle>
        <span className="text-xs text-muted-foreground">{goal.timezone}</span>
      </div>
      <CardDescription className="truncate">
        {goal.description ? goal.description : <i>No description provided.</i>}
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
