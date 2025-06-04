"use client";

import { useEffect, useState } from "react";
import { ControllerRenderProps, FieldValues, Path } from "react-hook-form";
import {
  Popover,
  PopoverTrigger,
  PopoverContent,
} from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { X } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  Drawer,
  DrawerContent,
  DrawerDescription,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";
import { useMediaQuery } from "@/hooks/useMediaQuery";
import { useDebounce } from "@/hooks/useDebounce";
import { UserSearchList } from "@/components/features/UserSearchList";
import { User } from "@/lib/types";

type UserMultiSelectAsyncProps<
  TFieldValues extends FieldValues,
  TName extends Path<TFieldValues>
> = {
  id?: string;
  field: ControllerRenderProps<TFieldValues, TName>;
  loadUsers: (query: string) => Promise<User[]>;
  error?: string;
};

export function UserMultiSelectAsync<
  TFieldValues extends FieldValues,
  TName extends Path<TFieldValues>
>({
  id,
  field,
  loadUsers,
  error,
}: UserMultiSelectAsyncProps<TFieldValues, TName>) {
  const selectedIds: string[] = field.value || [];

  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const [options, setOptions] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);

  const isDesktop = useMediaQuery("(min-width: 1024px)");

  const debouncedQuery = useDebounce(query, 300);

  useEffect(() => {
    if (debouncedQuery.length < 2) {
      setOptions([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    loadUsers(debouncedQuery)
      .then((res) => setOptions(res))
      .finally(() => setLoading(false));
  }, [debouncedQuery, loadUsers]);

  const selectedUsers: User[] = selectedIds
    .map((id) => options.find((u) => u.id === id))
    .filter(Boolean) as User[];

  const handleSelect = (user: User) => {
    if (!selectedIds.includes(user.id)) {
      field.onChange([...selectedIds, user.id]);
    }
    setOpen(false);
  };

  const handleRemove = (id: string) => {
    field.onChange(selectedIds.filter((uid) => uid !== id));
  };

  return (
    <div>
      {isDesktop ? (
        <Popover open={open} onOpenChange={setOpen}>
          <PopoverTrigger asChild>
            <Button
              id={id}
              variant="outline"
              className={`w-full justify-start ${
                error ? "border-destructive" : ""
              }`}
              onClick={() => setOpen(true)}
            >
              + Add User
            </Button>
          </PopoverTrigger>
          <PopoverContent className="min-w-[622px] p-0">
            <UserSearchList
              query={query}
              setQuery={setQuery}
              loading={loading}
              options={options}
              selected={selectedUsers}
              handleSelect={handleSelect}
            />
          </PopoverContent>
        </Popover>
      ) : (
        <Drawer open={open} onOpenChange={setOpen}>
          <DrawerTrigger asChild>
            <Button
              id={id}
              variant="outline"
              className={`w-full justify-start ${
                error ? "border-destructive" : ""
              }`}
              onClick={() => setOpen(true)}
            >
              + Add User
            </Button>
          </DrawerTrigger>
          <DrawerContent>
            <DrawerHeader>
              <DrawerTitle>Add User</DrawerTitle>
              <DrawerDescription>
                Select users to add to your squad.
              </DrawerDescription>
            </DrawerHeader>
            <UserSearchList
              query={query}
              setQuery={setQuery}
              loading={loading}
              options={options}
              selected={selectedUsers}
              handleSelect={handleSelect}
            />
          </DrawerContent>
        </Drawer>
      )}
      {/* Selected users */}
      <div className="flex flex-wrap gap-2">
        {selectedUsers.map((user: User) => (
          <div
            key={user.id}
            className="flex items-center px-3 py-1 rounded-full shadow-sm border hover:shadow-md transition-all group bg-muted"
          >
            <Avatar className="w-7 h-7 mr-2 border shadow">
              {user.profilePicture ? (
                <AvatarImage src={user.profilePicture} alt={user.name} />
              ) : null}
              <AvatarFallback>
                {user.name
                  .split(" ")
                  .map((n) => n[0])
                  .join("")
                  .toUpperCase()
                  .slice(0, 2)}
              </AvatarFallback>
            </Avatar>
            <span className="font-medium mr-1 max-w-[100px] truncate">
              {user.name}
            </span>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => handleRemove(user.id)}
              className="ml-1 text-muted-foreground hover:text-destructive opacity-60 group-hover:opacity-100 p-1 h-6 w-6"
              aria-label={`Remove ${user.name}`}
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
        ))}
      </div>
      {error && <p className="text-sm text-destructive">{error}</p>}
    </div>
  );
}
