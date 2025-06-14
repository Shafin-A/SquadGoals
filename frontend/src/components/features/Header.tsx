"use client";

import Image from "next/image";
import { ModeToggle } from "@/components/features/ModeToggle";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Menu } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { auth } from "@/firebase";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { NotificationDropdown } from "./NotificationDropdown";
import { useQueryClient } from "@tanstack/react-query";

export default function Header() {
  const router = useRouter();
  const [user, setUser] = useState(() => auth.currentUser);

  const queryClient = useQueryClient();

  useEffect(() => {
    const unsubscribe = auth.onAuthStateChanged(setUser);
    return () => unsubscribe();
  }, []);

  const isAuthenticated = !!user;

  const handleSignOut = async () => {
    try {
      await auth.signOut();
      document.cookie = "idToken=; path=/; max-age=0";
      queryClient.clear();
      router.push("/");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  return (
    <header className="flex items-center justify-between p-4 border-b">
      <Link href="/" className="flex items-center space-x-2">
        <Image src="/logo.svg" alt="SquadGoals Logo" width={32} height={32} />
        <h1 className="text-2xl font-bold">SquadGoals</h1>
      </Link>
      {/* Hamburger nav for mobile */}
      <div className="flex space-x-4 items-center">
        {isAuthenticated && (
          <NotificationDropdown align="end" buttonClassName="lg:hidden" />
        )}
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
              <Link href="/goals">Goals</Link>
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            {isAuthenticated ? (
              <DropdownMenuItem onSelect={handleSignOut}>
                Sign Out
              </DropdownMenuItem>
            ) : (
              <DropdownMenuItem asChild>
                <Link href="/signup">Sign Up</Link>
              </DropdownMenuItem>
            )}
            <DropdownMenuSeparator />
            <DropdownMenuItem onSelect={(e) => e.preventDefault()}>
              <ModeToggle />
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      {/* Desktop nav */}
      <nav className="hidden lg:block">
        <ul className="flex space-x-4 items-center">
          <li>
            <Link href="/">Home</Link>
          </li>
          <li>
            <Link href="/goals">Goals</Link>
          </li>
          {isAuthenticated && (
            <li>
              <NotificationDropdown />
            </li>
          )}
          <li>
            {isAuthenticated ? (
              <Button variant="outline" size="sm" onClick={handleSignOut}>
                Sign Out
              </Button>
            ) : (
              <Button asChild variant="outline" size="sm">
                <Link href="/signup">Sign Up</Link>
              </Button>
            )}
          </li>
          <li>
            <ModeToggle />
          </li>
        </ul>
      </nav>
    </header>
  );
}
