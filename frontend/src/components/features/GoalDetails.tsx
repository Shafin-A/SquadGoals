import { Goal } from "@/lib/types";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Badge } from "../ui/badge";

export const GoalDetails = ({ goal }: { goal: Goal }) => {
  return (
    <Card className="bg-secondary w-full max-w-3xl mx-auto">
      <CardHeader>
        <CardTitle>Details</CardTitle>
      </CardHeader>
      <CardContent className="space-y-2 text-sm text-muted-foreground">
        <div>
          <span className="text-foreground">Description:</span>
          <div className="break-words">
            {goal.description || "No description provided."}
          </div>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-2">
          <div>
            <span className="text-foreground block">Start Date:</span>
            <div>{new Date(goal.startAt).toLocaleDateString()}</div>
          </div>
          <div>
            <span className="text-foreground block">Frequency:</span>
            <div>{goal.frequency}</div>
          </div>
          <div>
            <span className="text-foreground block">Timezone:</span>
            <div className="truncate">{goal.timezone}</div>
          </div>
          <div>
            <span className="text-foreground block">Visibility:</span>
            <div>{goal.isPublic ? "Public" : "Private"}</div>
          </div>
        </div>
        {goal.tags?.length > 0 && (
          <div>
            <span className="text-foreground block mb-1">Tags:</span>
            <div className="flex flex-wrap gap-2">
              {goal.tags.map((tag: string) => (
                <Badge key={tag}>{tag}</Badge>
              ))}
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};
