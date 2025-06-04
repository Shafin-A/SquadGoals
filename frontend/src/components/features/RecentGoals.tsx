"use client";

import { useQuery } from "@tanstack/react-query";
import { Loader2, Frown } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card";
import { Goal } from "@/lib/types";
import { GoalItem } from "@/components/features/GoalItem";
import { fetchRecentGoals } from "@/api/goal";

export const RecentGoals = () => {
  const {
    data: goals = [],
    isLoading,
    isError,
    error,
  } = useQuery<Goal[], Error>({
    queryKey: ["recent-goals"],
    queryFn: fetchRecentGoals,
  });

  return (
    <Card className="w-full max-w-2xl mx-auto mt-8">
      <CardHeader className="flex flex-row items-start justify-between mb-2">
        <div>
          <CardTitle className="text-2xl">Recent Goals</CardTitle>
          <CardDescription>
            Discover the latest public goals looking for squad members. <br />
            Join a goal that inspires you or start your own!
          </CardDescription>
        </div>
        <Button asChild>
          <Link href="/goals/new">+ Create New Goal</Link>
        </Button>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="flex justify-center items-center p-8">
            <Loader2 className="w-4 h-4 animate-spin" />
          </div>
        ) : isError ? (
          <div className="flex flex-col items-center justify-center rounded-lg p-8 shadow-inner">
            <Frown className="w-16 h-16 mb-4" />
            <p className="text-lg mb-2 text-center text-destructive">
              {error?.message || "Unexpected error occurred"}
            </p>
          </div>
        ) : goals.length === 0 ? (
          <div className="flex flex-col items-center justify-center rounded-lg p-8 shadow-inner">
            <Frown className="w-16 h-16 mb-4" />
            <p className="text-lg mb-2 text-center">
              No goals are currently looking for squad members. <br />
              Be the first to create a goal and find your squad!
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {goals.map((goal) => (
              <Link
                href={`/goals/${goal.id}`}
                key={goal.id}
                className="no-underline"
              >
                <GoalItem goal={goal} />
              </Link>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
};
