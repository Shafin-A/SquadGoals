"use client";

import Image from "next/image";
import { ModeToggle } from "@/components/features/ModeToggle";
import Link from "next/link";
import { Button } from "../ui/button";
import { Menu } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "../ui/dropdown-menu";

export default function Header() {
  return (
    <header className="flex items-center justify-between p-4 border-b">
      <div className="flex items-center space-x-2">
        <Image src="/logo.svg" alt="SquadGoals Logo" width={32} height={32} />
        <h1 className="text-2xl font-bold">SquadGoals</h1>
      </div>
      {/* Hamburger nav for mobile */}
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button
            className="lg:hidden p-2"
            variant="outline"
            size="icon"
            aria-label="Toggle navigation"
          >
            <Menu />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent className="lg:hidden" align="end">
          <DropdownMenuItem asChild>
            <Link href="/">Home</Link>
          </DropdownMenuItem>
          <DropdownMenuItem asChild>
            <Link href="/squads">Squads</Link>
          </DropdownMenuItem>
          <DropdownMenuItem asChild>
            <Link href="/goals">Goals</Link>
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem asChild>
            <Link href="/signup">Sign Up</Link>
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem>
            <ModeToggle />
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
      {/* Desktop nav */}
      <nav className="hidden lg:block">
        <ul className="flex space-x-4 items-center">
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
            <Button variant="outline" size="sm">
              <Link href="/signup">Sign Up</Link>
            </Button>
          </li>
          <li>
            <ModeToggle />
          </li>
        </ul>
      </nav>
    </header>
  );
}
