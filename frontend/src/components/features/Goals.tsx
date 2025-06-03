"use client";

import { Button } from "@/components/ui/button";
import { IconInput } from "@/components/ui/icon-input";
import { SearchIcon } from "lucide-react";
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

export const Goals = () => {
  const form = useForm({
    defaultValues: { search: "" },
  });

  const onSubmit = (values: { search: string }) => {
    console.log("Search submitted:", values.search);
  };

  const user = auth.currentUser;

  return (
    <Card className="w-full max-w-2xl rounded-2xl shadow-lg p-8 mx-auto">
      <CardHeader className="flex flex-row items-center justify-between mb-4 p-0">
        <div>
          <CardTitle className="text-3xl font-bold tracking-tight">
            Goals
          </CardTitle>
          <CardDescription>
            Find and track your goals, stay accountable <br />
            and achieve more together.
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
              No recent goals yet.
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
