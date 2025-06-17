import { User } from "@/lib/types";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

interface SquadList {
  squad: User[];
}

export const SquadList = ({ squad }: SquadList) => {
  return (
    <div>
      <div className="text-base font-semibold mb-6">Squad</div>
      <div className="flex gap-2">
        {squad.map((member) => (
          <Avatar key={member.id} className="w-12 h-12">
            <AvatarImage src={member.profilePicture} alt={member.name} />
            <AvatarFallback>
              {member.name
                .split(" ")
                .map((n) => n[0])
                .join("")
                .toUpperCase()
                .slice(0, 2)}
            </AvatarFallback>
          </Avatar>
        ))}
      </div>
    </div>
  );
};
