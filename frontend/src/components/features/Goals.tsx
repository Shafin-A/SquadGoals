"use client";

import { Button } from "@/components/ui/button";
import { IconInput } from "@/components/ui/icon-input";
import { SearchIcon } from "lucide-react";
import { Form, FormField, FormItem, FormControl } from "@/components/ui/form";
import { useForm } from "react-hook-form";
import { auth } from "@/firebase";
import Link from "next/link";

export const Goals = () => {
  const form = useForm({
    defaultValues: { search: "" },
  });

  const onSubmit = (values: { search: string }) => {
    console.log("Search submitted:", values.search);
  };

  const user = auth.currentUser;

  return (
    <div className="w-full max-w-2xl rounded-2xl border bg-card shadow-lg p-8 flex flex-col gap-8">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-3xl font-bold tracking-tight">Goals</h1>
        <Button variant="default">
          <Link href="/goals/new">+ Create New Goal</Link>
        </Button>
      </div>

      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="flex w-full gap-2"
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

      <section>
        <h2 className="text-lg font-semibold mb-2 text-muted-foreground">
          Recent Goals
        </h2>
        <div className="flex flex-col gap-2">
          <div className="p-4 rounded-lg bg-muted text-muted-foreground border">
            No recent goals yet.
          </div>
        </div>
      </section>

      {user && (
        <section>
          <h2 className="text-lg font-semibold mb-2 text-muted-foreground">
            Your Goals
          </h2>
          <div className="flex flex-col gap-2">
            <div className="p-4 rounded-lg bg-muted text-muted-foreground border">
              You have no current goals.
            </div>
          </div>
        </section>
      )}
    </div>
  );
};
