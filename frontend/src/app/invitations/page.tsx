import { Invitations } from "@/components/features/Invitations";
import Image from "next/image";

export default async function Page() {
  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-2xl">
        <div className="relative w-full aspect-[2/1] max-w-3xl">
          <Image
            src="/like_dislike.svg"
            alt="Person thinking whether to like or dislike something"
            fill
            style={{ objectFit: "contain" }}
            sizes="(max-width: 1024px) 100vw, 50vw"
            priority={true}
          />
        </div>
        <Invitations />
      </div>
    </div>
  );
}
