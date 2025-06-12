import { NextRequest, NextResponse } from "next/server";

export const config = {
  matcher: ["/goals/new", "/profile", "/invitations"],
};

export function middleware(request: NextRequest) {
  const idToken = request.cookies.get("idToken")?.value;

  if (!idToken) {
    const redirectUrl = new URL("/login", request.url);
    redirectUrl.searchParams.set("redirect", request.nextUrl.pathname);
    return NextResponse.redirect(redirectUrl);
  }

  return NextResponse.next();
}
