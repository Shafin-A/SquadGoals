"use client";

import { fetchInvitations } from "@/api/invitation";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card";
import { useFirebaseIdToken } from "@/hooks/useFirebaseIdToken";
import { Invitation } from "@/lib/types";
import { useQuery } from "@tanstack/react-query";
import { Frown, Inbox, Loader2 } from "lucide-react";
import { GoalItem } from "./GoalItem";

export const Invitations = () => {
  const { user, idToken } = useFirebaseIdToken();

  const isAuthenticated = !!user;

  const {
    data: invitations = [],
    isLoading,
    isError,
    error,
  } = useQuery<Invitation[], Error>({
    queryKey: ["invitations"],
    queryFn: () => fetchInvitations({ idToken }),
    enabled: isAuthenticated && !!idToken,
  });

  return (
    <Card className="w-full max-w-2xl rounded-2xl shadow-lg p-8 mx-auto">
      <CardHeader className="p-0">
        <CardTitle className="text-3xl font-bold tracking-tight">
          Invitations
        </CardTitle>
        <CardDescription className="text-foreground dark:text-muted-foreground">
          Here you can see invitations to join goals or squads. <br />
          Accept or decline to manage your collaborations!
        </CardDescription>
      </CardHeader>
      <CardContent className="p-0">
        <section className="mb-8">
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
          ) : invitations.length === 0 ? (
            <div className="flex flex-col items-center justify-center rounded-lg p-8 shadow-inner">
              <Inbox className="w-16 h-16 mb-4" />
              <p className="text-lg mb-2 text-center">
                You&apos;re all caught up! You no invitations right now.
              </p>
            </div>
          ) : (
            <div className="grid gap-4">
              {invitations.map((invitation) => (
                <Card key={invitation.id} className="w-full">
                  <CardContent className="p-6 flex items-center justify-between gap-6">
                    <div className="flex-1">
                      <GoalItem goal={invitation.goal} variant="compact" />
                    </div>
                    <div className="flex gap-2 flex-shrink-0">
                      <Button size="sm">Accept</Button>
                      <Button size="sm" variant="destructive">
                        Decline
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </section>
      </CardContent>
    </Card>
  );
};
