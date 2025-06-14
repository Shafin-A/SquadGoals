"use client";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { useInvitations } from "@/hooks/useInvitations";
import { Frown, Inbox, Loader2 } from "lucide-react";
import { GoalItem } from "@/components/features/GoalItem";

export const Invitations = () => {
  const {
    invitationsQuery,
    handleAcceptInvitation,
    acceptMutation,
    handleDeclineInvitation,
    rejectMutation,
    page,
    setPage,
  } = useInvitations();

  const {
    data: paginatedInvitations,
    isLoading,
    isError,
    error,
  } = invitationsQuery;

  const invitations = paginatedInvitations?.content ?? [];

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
            <>
              <div className="grid gap-4">
                {invitations.map((invitation) => (
                  <Card key={invitation.id} className="w-full">
                    <CardContent className="p-6 flex items-center justify-between gap-6">
                      <div className="flex-1">
                        <GoalItem goal={invitation.goal} variant="compact" />
                      </div>
                      <div className="flex gap-2 flex-shrink-0">
                        <Button
                          size="sm"
                          onClick={() => handleAcceptInvitation(invitation.id)}
                          disabled={
                            acceptMutation.isPending || rejectMutation.isPending
                          }
                        >
                          {acceptMutation.isPending ? (
                            <Loader2 className="w-4 h-4 animate-spin mr-2" />
                          ) : (
                            "Accept"
                          )}
                        </Button>
                        <Button
                          size="sm"
                          variant="destructive"
                          onClick={() => handleDeclineInvitation(invitation.id)}
                          disabled={
                            acceptMutation.isPending || rejectMutation.isPending
                          }
                        >
                          {rejectMutation.isPending ? (
                            <Loader2 className="w-4 h-4 animate-spin mr-2" />
                          ) : (
                            "Decline"
                          )}
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>

              {paginatedInvitations && paginatedInvitations.totalPages > 1 && (
                <Pagination className="mt-4">
                  <PaginationContent>
                    <PaginationItem>
                      <PaginationPrevious
                        onClick={() => setPage((prev) => prev - 1)}
                        className={
                          page === 0 ? "pointer-events-none opacity-50" : ""
                        }
                      />
                    </PaginationItem>

                    {Array.from({
                      length: paginatedInvitations.totalPages,
                    }).map((_, index) => (
                      <PaginationItem key={index}>
                        <PaginationLink
                          isActive={index === page}
                          onClick={() => setPage(index)}
                        >
                          {index + 1}
                        </PaginationLink>
                      </PaginationItem>
                    ))}

                    <PaginationItem>
                      <PaginationNext
                        onClick={() => setPage((prev) => prev + 1)}
                        className={
                          paginatedInvitations.last
                            ? "pointer-events-none opacity-50"
                            : ""
                        }
                      />
                    </PaginationItem>
                  </PaginationContent>
                </Pagination>
              )}
            </>
          )}
        </section>
      </CardContent>
    </Card>
  );
};
