export const ActivityFeed = () => {
  return (
    <div>
      <div className="text-base font-semibold mb-6">Activity</div>
      <div className="space-y-4 text-sm text-muted-foreground">
        <p>No activity yet.</p>
        {/* Example */}
        <div className="flex items-center justify-between">
          <span>Shafin marked the goal as done</span>
          <span className="text-xs text-muted-foreground">2h ago</span>
        </div>
      </div>
    </div>
  );
};
