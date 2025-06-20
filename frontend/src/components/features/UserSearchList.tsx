import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Loader2 } from "lucide-react";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import { User } from "@/lib/types";

type UserSearchListProps = {
  query: string;
  setQuery: (query: string) => void;
  loading: boolean;
  options: User[];
  selected: User[];
  handleSelect: (user: User) => void;
};

export const UserSearchList = ({
  query,
  setQuery,
  loading,
  options,
  selected,
  handleSelect,
}: UserSearchListProps) => {
  return (
    <Command className="w-full">
      <CommandInput
        placeholder="Search users..."
        value={query}
        onValueChange={setQuery}
      />
      <CommandList>
        {loading ? (
          <div className="p-4 flex items-center justify-center">
            <Loader2 className="w-4 h-4 animate-spin" />
          </div>
        ) : (
          <>
            <CommandEmpty>No users found.</CommandEmpty>
            <CommandGroup heading="Users">
              {options
                .filter((u) => !selected.some((s: User) => s.id === u.id))
                .map((user) => (
                  <CommandItem
                    key={user.id}
                    onSelect={() => handleSelect(user)}
                    value={`${user.name} ${user.email}`}
                  >
                    <div className="flex items-center gap-2">
                      <Avatar className="w-6 h-6">
                        {user.profilePicture ? (
                          <AvatarImage
                            src={user.profilePicture}
                            alt={user.name}
                          />
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
                      <span>{`${user.name} | ${user.email}`}</span>
                    </div>
                  </CommandItem>
                ))}
            </CommandGroup>
          </>
        )}
      </CommandList>
    </Command>
  );
};
