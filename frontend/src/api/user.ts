import { User } from "@/lib/types";

interface CreateUser {
  name: string;
  email: string;
  timezone: string;
  createdAt: Date;
}

export const createUser = async ({
  profile,
  idToken,
}: {
  profile: CreateUser;
  idToken: string;
}): Promise<User> => {
  const res = await fetch("http://localhost:8080/api/users", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${idToken}`,
    },
    body: JSON.stringify(profile),
  });

  if (!res.ok) {
    throw new Error("Failed to create user");
  }

  return res.json();
};

export const searchUsers = async ({
  query,
  limit = 10,
  idToken,
}: {
  query: string;
  limit?: number;
  idToken: string;
}): Promise<User[]> => {
  const res = await fetch(
    `http://localhost:8080/api/users/search?query=${encodeURIComponent(
      query
    )}&limit=${limit}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${idToken}`,
      },
    }
  );

  if (!res.ok) {
    throw new Error("Failed to search users");
  }

  return res.json();
};
