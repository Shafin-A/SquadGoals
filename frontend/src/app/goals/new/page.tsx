import CreateGoalForm from "@/components/features/CreateGoalForm";

export default function Page() {
  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-2xl">
        <div className="w-full max-w-2xl rounded-2xl border bg-card shadow-lg p-8 flex flex-col gap-8">
          <CreateGoalForm />
        </div>
      </div>
    </div>
  );
}
