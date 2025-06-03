import Image from "next/image";
import CreateGoalForm from "@/components/features/CreateGoalForm";

export default function Page() {
  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-2xl">
        <div className="relative w-full aspect-[2/1] max-w-3xl">
          <Image
            src="/people_together.svg"
            alt="People gathered together"
            fill
            style={{ objectFit: "contain" }}
            sizes="(max-width: 1024px) 100vw, 50vw"
            priority={true}
          />
        </div>
        <div className="w-full max-w-2xl">
          <CreateGoalForm />
        </div>
      </div>
    </div>
  );
}
