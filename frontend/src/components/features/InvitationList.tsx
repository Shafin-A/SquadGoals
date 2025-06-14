import { GoalItem } from "@/components/features/GoalItem";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { useInvitations } from "@/hooks/useInvitations";
import { INVITATION_STATUS } from "@/lib/constants";
import { Frown, Inbox, Loader2 } from "lucide-react";

interface InvitationListProps {
  status: INVITATION_STATUS;
  page: number;
  setPage: (newPage: number) => void;
}

export function InvitationList({ status, page, setPage }: InvitationListProps) {
  const {
    invitationsQuery,
    handleAcceptInvitation,
    acceptMutation,
    handleDeclineInvitation,
    declineMutation,
  } = useInvitations({ status });

  const {
    data: paginatedInvitations,
    isLoading,
    isError,
    error,
  } = invitationsQuery;

  const invitations = paginatedInvitations?.content ?? [];

  if (isLoading) {
    return (
      <div className="flex justify-center p-8">
        <Loader2 className="animate-spin w-6 h-6" />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex justify-center items-center flex-col p-8">
        <Frown className="w-10 h-10 mb-2" />
        <p className="text-destructive">{error.message}</p>
      </div>
    );
  }

  if (invitations.length === 0) {
    const friendlyMessages: Record<INVITATION_STATUS, string> = {
      [INVITATION_STATUS.PENDING]:
        "You're all caught up! No pending invitations right now.",
      [INVITATION_STATUS.ACCEPTED]:
        "You haven't accepted any invitations yet. Once you do, they'll show up here.",
      [INVITATION_STATUS.DECLINED]:
        "No declined invitations yet. If you decline any, they'll be listed here.",
    };

    return (
      <div className="flex justify-center items-center flex-col p-8">
        <Inbox className="w-10 h-10 mb-2" />
        <p className="text-center">{friendlyMessages[status]}</p>
      </div>
    );
  }

  return (
    <div className="space-y-4 mt-4">
      {invitations.map((invitation) => (
        <Card key={invitation.id}>
          <CardContent className="flex justify-between items-center gap-4">
            <GoalItem goal={invitation.goal} variant="compact" />
            {status === "PENDING" && (
              <div className="flex gap-2">
                <Button
                  size="sm"
                  onClick={() => handleAcceptInvitation(invitation.id)}
                  disabled={
                    acceptMutation.isPending || declineMutation.isPending
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
                    acceptMutation.isPending || declineMutation.isPending
                  }
                >
                  {declineMutation.isPending ? (
                    <Loader2 className="w-4 h-4 animate-spin mr-2" />
                  ) : (
                    "Decline"
                  )}
                </Button>
              </div>
            )}
          </CardContent>
        </Card>
      ))}

      {paginatedInvitations && paginatedInvitations.totalPages > 1 && (
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                onClick={() => setPage(page - 1)}
                className={page === 0 ? "pointer-events-none opacity-50" : ""}
              />
            </PaginationItem>

            {Array.from({ length: paginatedInvitations.totalPages }).map(
              (_, index) => (
                <PaginationItem key={index}>
                  <PaginationLink
                    isActive={index === page}
                    onClick={() => setPage(index)}
                  >
                    {index + 1}
                  </PaginationLink>
                </PaginationItem>
              )
            )}

            <PaginationItem>
              <PaginationNext
                onClick={() => setPage(page + 1)}
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
    </div>
  );
}
