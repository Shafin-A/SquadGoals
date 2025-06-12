import { useEffect, useState } from "react";
import { auth } from "@/firebase";
import type { User } from "firebase/auth";

export function useFirebaseIdToken() {
  const [user, setUser] = useState<User | null>(() => auth.currentUser);
  const [idToken, setIdToken] = useState<string>("");

  useEffect(() => {
    const unsubscribe = auth.onAuthStateChanged(setUser);
    return () => unsubscribe();
  }, []);

  useEffect(() => {
    let isMounted = true;

    const fetchIdToken = async () => {
      if (user) {
        const token = await user.getIdToken();
        if (isMounted) setIdToken(token);
      } else {
        if (isMounted) setIdToken("");
      }
    };

    fetchIdToken();
    return () => {
      isMounted = false;
    };
  }, [user]);

  return { user, idToken };
}
