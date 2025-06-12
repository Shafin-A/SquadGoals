import { Invitation } from "@/lib/types";

export const fetchInvitations = async ({
  idToken,
}: {
  idToken: string;
}): Promise<Invitation[]> => {
  const res = await fetch(`http://localhost:8080/api/invitations`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${idToken}`,
    },
  });

  if (!res.ok) {
    throw new Error("Failed to fetch invitations");
  }

  return res.json();
};
