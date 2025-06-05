"use client";

import { useEffect } from "react";
import { onIdTokenChanged } from "firebase/auth";
import { auth } from "@/firebase";

export const AuthCookieSync = () => {
  useEffect(() => {
    const unsubscribe = onIdTokenChanged(auth, async (user) => {
      if (user) {
        const idToken = await user.getIdToken();
        document.cookie = `idToken=${idToken}; path=/; max-age=${
          60 * 60 * 24 * 7
        }; samesite=strict`;
      } else {
        document.cookie = "idToken=; path=/; max-age=0";
      }
    });
    return () => unsubscribe();
  }, []);

  return null;
};
