"use client";

import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { useState } from "react";
import { InvitationList } from "@/components/features/InvitationList";
import { INVITATION_STATUS } from "@/lib/constants";

export const Invitations = () => {
  const [status, setStatus] = useState<INVITATION_STATUS>(
    INVITATION_STATUS.PENDING
  );
  const [pageByStatus, setPageByStatus] = useState<
    Record<INVITATION_STATUS, number>
  >({
    PENDING: 0,
    ACCEPTED: 0,
    DECLINED: 0,
  });

  const handlePageChange = (newPage: number) => {
    setPageByStatus((prev) => ({ ...prev, [status]: newPage }));
  };

  return (
    <Tabs
      value={status}
      onValueChange={(val) => setStatus(val as INVITATION_STATUS)}
    >
      <TabsList className="grid w-full grid-cols-3">
        {(
          Object.keys(INVITATION_STATUS) as Array<
            keyof typeof INVITATION_STATUS
          >
        ).map((s) => (
          <TabsTrigger key={s} value={s}>
            {s.charAt(0) + s.slice(1).toLowerCase()}
          </TabsTrigger>
        ))}
      </TabsList>

      {(
        Object.keys(INVITATION_STATUS) as Array<keyof typeof INVITATION_STATUS>
      ).map((tabStatus) => (
        <TabsContent key={tabStatus} value={tabStatus}>
          {status === tabStatus && (
            <InvitationList
              status={tabStatus as INVITATION_STATUS}
              page={pageByStatus[tabStatus]}
              setPage={handlePageChange}
            />
          )}
        </TabsContent>
      ))}
    </Tabs>
  );
};
