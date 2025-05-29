"use client";

import Image from "next/image";
import { Frown } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";

export default function Home() {
  const [goals, setGoals] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    setTimeout(() => {
      setGoals([]);
      setLoading(false);
    }, 800);
  }, []);

  return (
    <div className="min-h-screen flex">
      <main className="mx-4 my-10 md:mx-20 md:my-20 w-full">
        <div className="flex flex-col lg:flex-row gap-8 justify-between">
          <div className="w-full lg:w-1/2">
            <h1 className="text-3xl md:text-4xl font-bold mb-4 text-center lg:text-left">
              Squad Up. Stay Accountable. Hit Your Goals.
            </h1>
            <p className="text-base md:text-lg text-center lg:text-left">
              Find a squad that motivates you, set ambitious goals,
              <span className="hidden lg:inline">
                <br />
              </span>
              track your progress, and achieve more together every step of the
              way.
            </p>
          </div>
          <div className="relative w-full aspect-[2/1] max-w-3xl">
            <Image
              src="/people.svg"
              alt="People working together"
              fill
              style={{ objectFit: "contain" }}
              sizes="(max-width: 1024px) 100vw, 50vw"
            />
          </div>
        </div>
        <div className="w-full mt-8">
          <h1 className="text-3xl md:text-4xl font-bold mb-4 text-center lg:text-left">
            Recent Goals Looking For Squad Members
          </h1>
          {loading ? (
            <div className="flex justify-center items-center p-8">
              <span>Loading...</span>
            </div>
          ) : goals.length === 0 ? (
            <div className="flex flex-col items-center justify-center rounded-lg p-8 shadow-inner">
              <Frown className="w-16 h-16 mb-4" />
              <p className="text-lg mb-2 text-center">
                No goals are currently looking for squad members. <br />
                Be the first to create a goal and find your squad!
              </p>
              <Button className="mt-4">Create Goal</Button>
            </div>
          ) : (
            <div className="grid gap-4">
              {/* actual goals component here... */}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
