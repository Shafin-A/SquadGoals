import { fetchRecentGoals } from "@/api/goal";
import { Goals } from "@/components/features/Goals";
import {
  dehydrate,
  HydrationBoundary,
  QueryClient,
} from "@tanstack/react-query";
import Image from "next/image";

export default async function Page() {
  const queryClient = new QueryClient();

  await queryClient.prefetchQuery({
    queryKey: ["recent-goals"],
    queryFn: () => fetchRecentGoals({}),
  });

  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-2xl">
        <div className="relative w-full aspect-[2/1] max-w-3xl">
          <Image
            src="/working_together.svg"
            alt="People working together"
            fill
            style={{ objectFit: "contain" }}
            sizes="(max-width: 1024px) 100vw, 50vw"
            priority={true}
          />
        </div>
        <HydrationBoundary state={dehydrate(queryClient)}>
          <Goals />
        </HydrationBoundary>
      </div>
    </div>
  );
}
