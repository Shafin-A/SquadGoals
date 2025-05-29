import Image from "next/image";
import { ModeToggle } from "@/components/features/ModeToggle";
import Link from "next/link";

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
            <Link href="/">Home</Link>
          </li>
          <li>
            <Link href="/squads">Squads</Link>
          </li>
          <li>
            <Link href="/goals">Goals</Link>
          </li>
          <li>
            <Link href="/signup">Sign Up</Link>
          </li>
          <li>
            <ModeToggle />
          </li>
        </ul>
      </nav>
    </header>
  );
}
