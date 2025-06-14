import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  acceptInvitation,
  fetchInvitations,
  declineInvitation,
} from "@/api/invitation";
import { useFirebaseIdToken } from "@/hooks/useFirebaseIdToken";
import { Invitation, PaginatedResponse } from "@/lib/types";
import { INVITATION_STATUS } from "@/lib/constants";

export function useInvitations({
  status = INVITATION_STATUS.PENDING,
  page = 0,
  pageSize = 10,
}: {
  status?: string;
  page?: number;
  pageSize?: number;
}) {
  const { user, idToken } = useFirebaseIdToken();
  const queryClient = useQueryClient();

  const isAuthenticated = !!user;

  const invitationsQuery = useQuery<PaginatedResponse<Invitation>, Error>({
    queryKey: ["invitations", status, page, pageSize],
    queryFn: () =>
      fetchInvitations({
        idToken,
        status,
        page,
        size: pageSize,
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
      declineInvitation(invitationId, idToken),
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
    declineMutation: {
      isPending: declineMutation.isPending,
    },
  };
}
