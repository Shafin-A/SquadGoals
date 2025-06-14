import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  acceptInvitation,
  fetchInvitations,
  rejectInvitation,
} from "@/api/invitation";
import { useFirebaseIdToken } from "@/hooks/useFirebaseIdToken";
import { Invitation, PaginatedResponse } from "@/lib/types";
import { INVITATION_STATUS } from "@/lib/constants";

export function useInvitations(pageSize: number = 10) {
  const { user, idToken } = useFirebaseIdToken();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);

  const isAuthenticated = !!user;

  const invitationsQuery = useQuery<PaginatedResponse<Invitation>, Error>({
    queryKey: ["invitations", page, pageSize],
    queryFn: () =>
      fetchInvitations({
        idToken,
        page,
        size: pageSize,
        status: INVITATION_STATUS.PENDING,
      }),
    enabled: isAuthenticated && !!idToken,
  });

  const acceptMutation = useMutation({
    mutationFn: (invitationId: number) =>
      acceptInvitation(invitationId, idToken),
    onMutate: async (invitationId) => {
      await queryClient.cancelQueries({ queryKey: ["invitations"] });

      const previousData = queryClient.getQueryData<
        PaginatedResponse<Invitation>
      >(["invitations", page, pageSize]);

      queryClient.setQueryData<PaginatedResponse<Invitation>>(
        ["invitations", page, pageSize],
        (old) =>
          old
            ? {
                ...old,
                content: old.content.map((invitation) =>
                  invitation.id === invitationId
                    ? { ...invitation, status: INVITATION_STATUS.ACCEPTED }
                    : invitation
                ),
              }
            : old
      );

      return { previousData };
    },
    onError: (err, variables, context) => {
      if (context?.previousData) {
        queryClient.setQueryData(
          ["invitations", page, pageSize],
          context.previousData
        );
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ["invitations"] });
    },
  });

  const declineMutation = useMutation({
    mutationFn: (invitationId: number) =>
      rejectInvitation(invitationId, idToken),
    onMutate: async (invitationId) => {
      await queryClient.cancelQueries({ queryKey: ["invitations"] });

      const previousData = queryClient.getQueryData<
        PaginatedResponse<Invitation>
      >(["invitations", page, pageSize]);

      queryClient.setQueryData<PaginatedResponse<Invitation>>(
        ["invitations", page, pageSize],
        (old) =>
          old
            ? {
                ...old,
                content: old.content.map((invitation) =>
                  invitation.id === invitationId
                    ? { ...invitation, status: INVITATION_STATUS.DECLINED }
                    : invitation
                ),
              }
            : old
      );

      return { previousData };
    },
    onError: (err, variables, context) => {
      if (context?.previousData) {
        queryClient.setQueryData(
          ["invitations", page, pageSize],
          context.previousData
        );
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ["invitations"] });
    },
  });

  const handleAcceptInvitation = (invitationId: number) => {
    acceptMutation.mutate(invitationId);
  };

  const handleDeclineInvitation = (invitationId: number) => {
    declineMutation.mutate(invitationId);
  };

  return {
    invitationsQuery,
    handleAcceptInvitation,
    acceptMutation: {
      isPending: acceptMutation.isPending,
    },
    handleDeclineInvitation,
    rejectMutation: {
      isPending: declineMutation.isPending,
    },
    page,
    setPage,
    pageSize,
  };
}
