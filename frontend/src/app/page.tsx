import Image from "next/image";
import { RecentGoals } from "@/components/features/RecentGoals";
import {
  dehydrate,
  HydrationBoundary,
  QueryClient,
} from "@tanstack/react-query";
import { fetchRecentGoals } from "@/api/goal";

export default async function Home() {
  const queryClient = new QueryClient();

  await queryClient.prefetchQuery({
    queryKey: ["recent-goals"],
    queryFn: () => fetchRecentGoals({}),
  });

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
              priority={true}
            />
          </div>
        </div>
        <div className="w-full mt-8">
          <h1 className="text-3xl md:text-4xl font-bold mb-4 text-center lg:text-left">
            Explore New Goals And Send A Request To Join!
          </h1>
          <HydrationBoundary state={dehydrate(queryClient)}>
            <RecentGoals />
          </HydrationBoundary>
        </div>
      </main>
    </div>
  );
}
