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
import { FREQUENCY, VISIBILITY } from "@/lib/constants";
import { UserMultiSelectAsync } from "@/components/features/UserMultiSelectAsync";
import { auth } from "@/firebase";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useMutation } from "@tanstack/react-query";
import { createGoal } from "@/api/goal";
import { searchUsers } from "@/api/user";

const formSchema = z.object({
  title: z.string().min(1, "Title is required"),
  description: z.string().optional(),
  frequency: z.nativeEnum(FREQUENCY),
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
  squadUserIds: z
    .array(z.string().min(1))
    .max(10, "Squads can only have a maximum of 10 members")
    .optional(),
  visibility: z.nativeEnum(VISIBILITY),
});

export default function CreateGoalForm() {
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      title: "",
      description: "",
      frequency: FREQUENCY.DAILY,
      tags: [],
      startAt: new Date(),
      squadUserIds: [],
      visibility: VISIBILITY.PUBLIC,
    },
  });

  const createGoalMutation = useMutation({
    mutationFn: async (values: z.infer<typeof formSchema>) => {
      const user = auth.currentUser;

      if (!user) {
        throw new Error("Error: User is not authenticated");
      }

      const createGoalBody = {
        title: values.title,
        description: values.description || "",
        frequency: values.frequency,
        tagNames: values.tags || [],
        startAt: values.startAt,
        squadUserIds: values.squadUserIds || [],
        isPublic: values.visibility === VISIBILITY.PUBLIC,
        timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      };

      const idToken = await user.getIdToken();

      return createGoal(createGoalBody, idToken);
    },
    onError: (error: unknown) => {
      form.setError("root", {
        type: "server",
        message:
          error instanceof Error
            ? error.message
            : "An unexpected error occurred.",
      });
    },
  });

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    createGoalMutation.mutate(values);
  };

  return (
    <Card className="w-full max-w-3xl mx-auto">
      <CardHeader>
        <CardTitle className="text-3xl font-bold tracking-tight">
          Create a Goal
        </CardTitle>
        <CardDescription>
          Embark on a new goal with your squad (or alone)!
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
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
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Input
                      placeholder="Description..."
                      type="text"
                      {...field}
                    />
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
                  <FormLabel htmlFor="frequency-select">Frequency</FormLabel>
                  <Select
                    name="frequency"
                    onValueChange={field.onChange}
                    defaultValue={field.value}
                  >
                    <FormControl>
                      <SelectTrigger
                        id="frequency-select"
                        name="frequency"
                        className="w-full"
                      >
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
                  <FormLabel htmlFor="start-date-picker">Start Date</FormLabel>
                  <FormControl>
                    <DateTimePicker
                      id="start-date-picker"
                      value={field.value}
                      onChange={field.onChange}
                    />
                  </FormControl>
                  <FormDescription>
                    This will be the date and time your goal starts at.
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="squadUserIds"
              render={({ field }) => (
                <FormItem className="flex flex-col">
                  <FormLabel htmlFor="squad-select">Squad Members</FormLabel>
                  <FormControl>
                    <UserMultiSelectAsync
                      id="squad-select"
                      field={field}
                      loadUsers={async (inputValue: string) => {
                        if (!inputValue) return [];
                        const user = auth.currentUser;
                        if (!user) return [];
                        const idToken = await user.getIdToken();
                        return searchUsers({ query: inputValue, idToken });
                      }}
                    />
                  </FormControl>
                  <FormDescription>
                    These users will be asked to be part of your goal&apos;s
                    squad. (Optional)
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="visibility"
              render={({ field }) => (
                <FormItem>
                  <FormLabel asChild>
                    <legend>Visibility</legend>
                  </FormLabel>
                  <FormControl>
                    <RadioGroup
                      name="visibility"
                      value={field.value}
                      onValueChange={field.onChange}
                      className="flex"
                    >
                      <FormItem className="flex items-center gap-3">
                        <FormControl>
                          <RadioGroupItem
                            id="visibility-public"
                            value={VISIBILITY.PUBLIC}
                          />
                        </FormControl>
                        <FormLabel htmlFor="visibility-public">
                          {VISIBILITY.PUBLIC}
                        </FormLabel>
                      </FormItem>

                      <FormItem className="flex items-center gap-3">
                        <FormControl>
                          <RadioGroupItem
                            id="visibility-private"
                            value={VISIBILITY.PRIVATE}
                          />
                        </FormControl>
                        <FormLabel htmlFor="visibility-private">
                          {VISIBILITY.PRIVATE}
                        </FormLabel>
                      </FormItem>
                    </RadioGroup>
                  </FormControl>
                  <FormDescription>
                    Public goals are visible to everyone. Private goals are only
                    visible to you and squad members.
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" disabled={createGoalMutation.isPending}>
              {createGoalMutation.isPending ? "Loading" : "Create"}
            </Button>
            {form.formState.errors.root && (
              <div className="text-destructive text-sm">
                {form.formState.errors.root.message}
              </div>
            )}
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
