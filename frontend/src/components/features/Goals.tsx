"use client";

import { Button } from "@/components/ui/button";
import { IconInput } from "@/components/ui/icon-input";
import { Frown, Loader2, SearchIcon } from "lucide-react";
import { Form, FormField, FormItem, FormControl } from "@/components/ui/form";
import { useForm } from "react-hook-form";
import { auth } from "@/firebase";
import Link from "next/link";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card";
import { useQuery } from "@tanstack/react-query";
import { Goal } from "@/lib/types";
import { fetchRecentGoals } from "@/api/goal";
import { GoalItem } from "./GoalItem";

export const Goals = () => {
  const form = useForm({
    defaultValues: { search: "" },
  });

  const onSubmit = (values: { search: string }) => {
    // eslint-disable-next-line no-console
    console.log("Search submitted:", values.search);
  };

  const {
    data: goals = [],
    isLoading,
    isError,
    error,
  } = useQuery<Goal[], Error>({
    queryKey: ["recent-goals"],
    queryFn: () => fetchRecentGoals({}),
  });

  const user = auth.currentUser;

  return (
    <Card className="w-full max-w-2xl rounded-2xl shadow-lg p-8 mx-auto">
      <CardHeader className="flex flex-row items-center justify-between mb-4 p-0">
        <div>
          <CardTitle className="text-3xl font-bold tracking-tight">
            Goals
          </CardTitle>
          <CardDescription>
            Find and track your goals. <br />
            Stay accountable and achieve more together.
          </CardDescription>
        </div>
        <Button asChild>
          <Link href="/goals/new">+ Create New Goal</Link>
        </Button>
      </CardHeader>
      <CardContent className="p-0">
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="flex w-full gap-2 mb-8"
          >
            <FormField
              control={form.control}
              name="search"
              render={({ field }) => (
                <FormItem className="w-full">
                  <FormControl>
                    <IconInput
                      className="w-full"
                      type="search"
                      icon={SearchIcon}
                      iconProps={{ behavior: "prepend" }}
                      placeholder="Search goals..."
                      {...field}
                    />
                  </FormControl>
                </FormItem>
              )}
            />
            <Button type="submit" disabled={!form.watch("search").trim()}>
              Search
            </Button>
          </form>
        </Form>

        <section className="mb-8">
          <h2 className="text-lg font-semibold mb-2 text-muted-foreground">
            Recent Goals
          </h2>
          <Card className="mb-4">
            <CardContent className="p-4 text-muted-foreground">
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
              ) : goals.length === 0 ? (
                <div className="flex flex-col items-center justify-center rounded-lg p-8 shadow-inner">
                  <Frown className="w-16 h-16 mb-4" />
                  <p className="text-lg mb-2 text-center">
                    No goals are currently looking for squad members. <br />
                    Be the first to create a goal and find your squad!
                  </p>
                </div>
              ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {goals.map((goal) => (
                    <Link
                      href={`/goals/${goal.id}`}
                      key={goal.id}
                      className="no-underline"
                    >
                      <GoalItem goal={goal} />
                    </Link>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </section>

        {user && (
          <section>
            <h2 className="text-lg font-semibold mb-2 text-muted-foreground">
              Your Goals
            </h2>
            <Card>
              <CardContent className="p-4 text-muted-foreground">
                You have no current goals.
              </CardContent>
            </Card>
          </section>
        )}
      </CardContent>
    </Card>
  );
};
