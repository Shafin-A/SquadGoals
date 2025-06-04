import { Goal } from "@/lib/types";

export const fetchRecentGoals = async (): Promise<Goal[]> => {
  const res = await fetch(
    "http://localhost:8080/api/goals?recent=true&limit=6",
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
