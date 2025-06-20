"use client";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  createUserWithEmailAndPassword,
  sendEmailVerification,
  signOut,
  validatePassword,
} from "firebase/auth";
import { auth } from "@/firebase";
import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { createUser } from "@/api/user";
import Link from "next/link";

export function SignUpForm({
  className,
  ...props
}: React.ComponentPropsWithoutRef<"div">) {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [validationError, setValidationError] = useState<string | null>(null);

  const createUserMutation = useMutation({
    mutationFn: createUser,
  });

  const handleSignUp = async (
    name: string,
    email: string,
    password: string
  ) => {
    try {
      setValidationError(null);

      const validation = await validatePassword(auth, password);

      if (!validation.isValid) {
        let msg = "Password requirements:\n";
        if (!validation.meetsMinPasswordLength)
          msg += "- At least 6 characters\n";
        if (!validation.containsUppercaseLetter)
          msg += "- At least one uppercase letter\n";
        if (!validation.containsLowercaseLetter)
          msg += "- At least one lowercase letter\n";
        if (!validation.containsNumericCharacter)
          msg += "- At least one number\n";
        if (!validation.containsNonAlphanumericCharacter)
          msg += "- At least one special character\n";
        setValidationError(msg.trim());
        return;
      }

      const userCredential = await createUserWithEmailAndPassword(
        auth,
        email,
        password
      );

      const user = userCredential.user;

      const idToken = await user.getIdToken();

      const now = new Date();

      const profile = {
        name: name,
        email: email,
        timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
        createdAt: now,
      };

      await createUserMutation.mutateAsync({ profile, idToken });

      await sendEmailVerification(user);

      await signOut(auth);
    } catch (error: unknown) {
      setValidationError(
        error instanceof Error ? error.message : "Failed to sign up"
      );
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    createUserMutation.reset();
    if (password !== confirmPassword) {
      setValidationError("Passwords do not match");
      return;
    }
    handleSignUp(name, email, password);
  };

  return (
    <div className={cn("flex flex-col gap-6", className)} {...props}>
      <Card>
        <CardHeader>
          <CardTitle className="text-2xl">Sign Up</CardTitle>
          <CardDescription>
            Enter your details below to create your account
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <div className="flex flex-col gap-6">
              <div className="grid gap-2">
                <Label htmlFor="name">Name</Label>
                <Input
                  id="name"
                  type="text"
                  placeholder="Your name"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
              </div>
              <div className="grid gap-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="email@example.com"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
              <div className="grid gap-2">
                <Label htmlFor="password">Password</Label>
                <Input
                  id="password"
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </div>
              <div className="grid gap-2">
                <Label htmlFor="confirmPassword">Confirm Password</Label>
                <Input
                  id="confirmPassword"
                  type="password"
                  required
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                />
              </div>
              <Button
                type="submit"
                className="w-full"
                disabled={createUserMutation.isPending}
              >
                {createUserMutation.isPending ? "Loading" : "Sign Up"}
              </Button>
              {(validationError || createUserMutation.error) && (
                <div className="text-red-500 text-sm text-center whitespace-pre-wrap">
                  {validationError ||
                    (createUserMutation.error instanceof Error
                      ? createUserMutation.error.message
                      : "Failed to sign up")}
                </div>
              )}
            </div>
            <div className="mt-4 text-center text-sm">
              Already have an account?{" "}
              <Link href="/login" className="underline underline-offset-4">
                Login
              </Link>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
