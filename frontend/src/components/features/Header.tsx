import Image from "next/image";
import { ModeToggle } from "@/components/features/ModeToggle";

export default function Header() {
  return (
    <header className="flex items-center justify-between p-4">
      <div className="flex items-center space-x-2">
        <Image src="/logo.svg" alt="SquadGoals Logo" width={32} height={32} />
        <h1 className="text-2xl font-bold">SquadGoals</h1>
      </div>
      <nav>
        <ul className="flex space-x-4">
          <li>
            <a href="/">Home</a>
          </li>
          <li>
            <a href="/squads">Squads</a>
          </li>
          <li>
            <a href="/goals">Goals</a>
          </li>
          <li>
            <a href="/signup">Sign Up</a>
          </li>
          <li>
            <ModeToggle />
          </li>
        </ul>
      </nav>
    </header>
  );
}
