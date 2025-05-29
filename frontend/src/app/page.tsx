import Image from "next/image";

export default function Home() {
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
        <div className="w-full lg:w-1/2 mt-8">
          <h1 className="text-3xl md:text-4xl font-bold mb-4 text-center lg:text-left">
            Recent Goals Looking For Squad Members
          </h1>
          <div>goals go here...</div>
        </div>
      </main>
    </div>
  );
}
