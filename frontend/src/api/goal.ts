import { Goal } from "@/lib/types";

interface CreateGoalBody {
  title: string;
  description?: string;
  frequency: string;
  tagNames?: string[];
  startAt: Date;
  squadUserIds?: number[];
  isPublic: boolean;
  timezone: string;
}

export const fetchRecentGoals = async ({
  recent = true,
  limit = 6,
}: {
  recent?: boolean;
  limit?: number;
}): Promise<Goal[]> => {
  const res = await fetch(
    `http://localhost:8080/api/goals?recent=${recent}&limit=${limit}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

  if (!res.ok) {
    throw new Error("Failed to fetch goals");
  }

  return res.json();
};

export const createGoal = async (
  body: CreateGoalBody,
  idToken: string
): Promise<Goal> => {
  const res = await fetch("http://localhost:8080/api/goals", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${idToken}`,
    },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const errorData = await res.json();
    throw new Error(errorData.message || "Failed to create goal");
  }

  return res.json();
};

export const fetchGoalById = async ({
  goalId,
  idToken,
}: {
  goalId: string;
  idToken?: string;
}): Promise<Goal> => {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };

  if (idToken) {
    headers.Authorization = `Bearer ${idToken}`;
  }

  const res = await fetch(`http://localhost:8080/api/goals/${goalId}`, {
    method: "GET",
    headers,
  });

  if (!res.ok) {
    throw new Error("Failed to fetch goal");
  }

  return res.json();
};
