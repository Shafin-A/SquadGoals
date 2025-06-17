"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { fetchGoalById } from "@/api/goal";
import { useFirebaseIdToken } from "@/hooks/useFirebaseIdToken";
import { Goal } from "@/lib/types";
import Image from "next/image";
import { GoalHeader } from "@/components/features/GoalHeader";
import { SquadList } from "@/components/features/SquadList";
import { ActivityFeed } from "@/components/features/ActivityFeed";
import { GoalDetails } from "@/components/features/GoalDetails";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

export default function Page() {
  const { goalId } = useParams<{ goalId: string }>();

  const { idToken } = useFirebaseIdToken();

  const {
    data: goal,
    isLoading,
    isError,
    error,
  } = useQuery<Goal>({
    queryKey: ["goal", goalId],
    queryFn: () => fetchGoalById({ goalId, idToken }),
    enabled: !!goalId,
  });

  if (isLoading) return <p>Loading...</p>;
  if (isError) return <p>Error: {(error as Error).message}</p>;
  if (!goal) return <p>Goal not found.</p>;

  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-3xl">
        <div className="relative w-full aspect-[2/1] max-w-3xl">
          <Image
            src="/project_board.svg"
            alt="People working on a project board"
            fill
            style={{ objectFit: "contain" }}
            sizes="(max-width: 1024px) 100vw, 50vw"
            priority={true}
          />
        </div>
        <Card className="p-6">
          <GoalHeader title={goal.title} creator={goal.createdBy} />
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 space-y-6 p-6 lg:p-0">
              <SquadList squad={goal.squad} />
              <Button className="w-full">Check In</Button>
              <ActivityFeed />
            </div>
            <GoalDetails goal={goal} />
          </div>
        </Card>
      </div>
    </div>
  );
}
