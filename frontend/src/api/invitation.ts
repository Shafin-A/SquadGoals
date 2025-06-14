import { INVITATION_STATUS } from "@/lib/constants";
import { Invitation, PaginatedResponse } from "@/lib/types";

export const fetchInvitations = async ({
  idToken,
  status = INVITATION_STATUS.PENDING,
  page = 0,
  size = 10,
}: {
  idToken: string;
  status?: string;
  page?: number;
  size?: number;
}): Promise<PaginatedResponse<Invitation>> => {
  const res = await fetch(
    `http://localhost:8080/api/invitations?status=${status}&page=${page}&size=${size}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${idToken}`,
      },
    }
  );

  if (!res.ok) {
    throw new Error("Failed to fetch invitations");
  }

  return res.json();
};

export const acceptInvitation = async (
  invitationId: number,
  idToken: string
) => {
  const res = await fetch(
    `http://localhost:8080/api/invitations/${invitationId}/accept`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${idToken}`,
      },
    }
  );

  if (!res.ok) {
    throw new Error("Failed to accept invitation");
  }

  return res.json();
};

export const declineInvitation = async (
  invitationId: number,
  idToken: string
) => {
  const res = await fetch(
    `http://localhost:8080/api/invitations/${invitationId}/decline`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${idToken}`,
      },
    }
  );

  if (!res.ok) {
    throw new Error("Failed to decline invitation");
  }

  return res.json();
};
