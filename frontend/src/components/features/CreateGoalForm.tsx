"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { TagInput } from "@/components/ui/tag-input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { DateTimePicker } from "@/components/ui/datetime-picker";
import { FREQUENCY } from "@/lib/constants";

const formSchema = z.object({
  title: z.string().min(1, "Title is required"),
  description: z.string().optional(),
  frequency: z.string(),
  tags: z
    .array(z.string().min(1))
    .max(10, "Tags can only have a maximum of 10 items")
    .optional(),
  startAt: z.coerce
    .date({
      errorMap: (issue, { defaultError }) => ({
        message:
          issue.code === "invalid_date"
            ? "Start date and time is required"
            : defaultError,
      }),
    })
    .refine((data) => data > new Date(), {
      message: "Start date and time must be in the future",
    }),
});

export default function CreateGoalForm() {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      title: "",
      description: "",
      frequency: "DAILY",
      tags: [],
    },
  });

  function onSubmit(values: z.infer<typeof formSchema>) {
    console.log("Values", values);
  }

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className="w-full space-y-8 max-w-3xl mx-auto"
      >
        <h1 className="text-3xl font-bold tracking-tight">Create a Goal</h1>
        <p className="text-muted-foreground">
          Embark on a new goal with your squad (or alone)!
        </p>
        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Title</FormLabel>
              <FormControl>
                <Input placeholder="Title..." type="text" {...field} />
              </FormControl>
              <FormDescription>
                This will be the title of your goal.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Title</FormLabel>
              <FormControl>
                <Input placeholder="Description..." type="text" {...field} />
              </FormControl>
              <FormDescription>
                This will be the description of your goal. (Optional)
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="tags"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Tags</FormLabel>
              <FormControl>
                <TagInput
                  value={field.value}
                  onChange={field.onChange}
                  placeholder="Tags (press Enter or comma)..."
                />
              </FormControl>
              <FormDescription>
                These will be the tags your goal will have. (Optional)
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="frequency"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Frequency</FormLabel>
              <Select onValueChange={field.onChange} defaultValue={field.value}>
                <FormControl>
                  <SelectTrigger className="w-full">
                    <SelectValue placeholder="Select a frequency for your goal..." />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value={FREQUENCY.DAILY}>
                    {FREQUENCY.DAILY}
                  </SelectItem>
                  <SelectItem value={FREQUENCY.WEEKLY}>
                    {FREQUENCY.WEEKLY}
                  </SelectItem>
                  <SelectItem value={FREQUENCY.MONTHLY}>
                    {FREQUENCY.MONTHLY}
                  </SelectItem>
                  <SelectItem value={FREQUENCY.YEARLY}>
                    {FREQUENCY.YEARLY}
                  </SelectItem>
                </SelectContent>
              </Select>
              <FormDescription>
                This will be the frequency of reminders for your goal.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="startAt"
          render={({ field }) => (
            <FormItem className="flex flex-col">
              <FormLabel>Goal Start Date and Time</FormLabel>
              <DateTimePicker value={field.value} onChange={field.onChange} />
              <FormDescription>
                This will be the date and time your goal starts at.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <Button type="submit">Submit</Button>
      </form>
    </Form>
  );
}
