import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import { XIcon } from "lucide-react";
import { forwardRef, useEffect, useState } from "react";
import type { z } from "zod";

const parseTagOpt = (params: { tag: string; tagValidator: z.ZodString }) => {
  const { tag, tagValidator } = params;
  const parsedTag = tagValidator.safeParse(tag);

  if (parsedTag.success) {
    return parsedTag.data;
  }

  return null;
};

// Define the InputProps type
type InputProps = React.InputHTMLAttributes<HTMLInputElement>;

type TagInputProps = Omit<InputProps, "value" | "onChange"> & {
  value?: readonly string[];
  onChange: (value: readonly string[]) => void;
  tagValidator?: z.ZodString;
  tagsPosition?: "top" | "bottom";
};

const TagInput = forwardRef<HTMLInputElement, TagInputProps>((props, ref) => {
  const {
    className,
    value = [],
    onChange,
    tagValidator,
    tagsPosition = "bottom",
    ...domProps
  } = props;

  const [pendingDataPoint, setPendingDataPoint] = useState("");

  useEffect(() => {
    if (pendingDataPoint.includes(",")) {
      // Split by comma and filter/map in one pass
      const newTags = pendingDataPoint
        .split(",")
        .map((x) => x.trim())
        .filter((x) => x.length > 0)
        .map((trimmedX) => {
          if (tagValidator) {
            const validatedTag = parseTagOpt({ tag: trimmedX, tagValidator });
            return validatedTag;
          }
          return trimmedX;
        })
        .filter(Boolean) as string[]; // Type assertion to resolve the string | null issue

      // Create a Set to remove duplicates and combine with existing values
      const newDataPoints = new Set([...value, ...newTags]);
      onChange([...newDataPoints]);
      setPendingDataPoint("");
    }
  }, [pendingDataPoint, onChange, value, tagValidator]);

  const addPendingDataPoint = () => {
    if (pendingDataPoint) {
      if (tagValidator) {
        const validatedTag = parseTagOpt({
          tag: pendingDataPoint,
          tagValidator,
        });
        if (validatedTag) {
          const newDataPoints = new Set([...value, validatedTag]);
          onChange([...newDataPoints]);
          setPendingDataPoint("");
        }
      } else {
        const newDataPoints = new Set([...value, pendingDataPoint]);
        onChange([...newDataPoints]);
        setPendingDataPoint("");
      }
    }
  };

  return (
    <div className={cn("w-full", className)}>
      {tagsPosition === "top" && (
        <div className="flex flex-wrap items-center gap-2 mb-2">
          {value.map((item) => (
            <Badge
              key={item}
              variant="secondary"
              className="flex items-center gap-1 px-2 py-1 rounded-full"
            >
              <span className="truncate max-w-[100px]">{item}</span>
              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="ml-1 h-5 w-5 p-0 text-muted-foreground hover:text-destructive"
                onClick={() => {
                  onChange(value.filter((i) => i !== item));
                }}
                tabIndex={-1}
                aria-label={`Remove ${item}`}
              >
                <XIcon className="w-3 h-3" />
              </Button>
            </Badge>
          ))}
        </div>
      )}
      <Input
        className="w-full min-w-0 bg-background text-sm placeholder:text-muted-foreground"
        value={pendingDataPoint}
        onChange={(e) => setPendingDataPoint(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === "Enter" || e.key === ",") {
            e.preventDefault();
            addPendingDataPoint();
          } else if (
            e.key === "Backspace" &&
            pendingDataPoint.length === 0 &&
            value.length > 0
          ) {
            e.preventDefault();
            onChange(value.slice(0, -1));
          }
        }}
        {...domProps}
        ref={ref}
      />
      {tagsPosition === "bottom" && (
        <div className="flex flex-wrap items-center gap-2 mt-2">
          {value.map((item) => (
            <Badge
              key={item}
              variant="secondary"
              className="flex items-center gap-1 px-2 py-1 rounded-full"
            >
              <span className="truncate max-w-[100px]">{item}</span>
              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="ml-1 h-5 w-5 p-0 text-muted-foreground hover:text-destructive"
                onClick={() => {
                  onChange(value.filter((i) => i !== item));
                }}
                tabIndex={-1}
                aria-label={`Remove ${item}`}
              >
                <XIcon className="w-3 h-3" />
              </Button>
            </Badge>
          ))}
        </div>
      )}
    </div>
  );
});

TagInput.displayName = "TagInput";

export { TagInput };
