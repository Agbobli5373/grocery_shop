import { forwardRef } from "react";
import { useFormContext } from "react-hook-form";
import { cn } from "@/lib/utils/cn";
import { Input } from "../ui/input";

interface FormFieldProps {
  name: string;
  label: string;
  type?: string;
  placeholder?: string;
  required?: boolean;
  className?: string;
}

export const FormField = forwardRef<HTMLInputElement, FormFieldProps>(
  (
    { name, label, type = "text", placeholder, required, className, ...props },
    ref
  ) => {
    const {
      register,
      formState: { errors },
    } = useFormContext();

    return (
      <div className="space-y-2">
        <label
          htmlFor={name}
          className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
        >
          {label}
          {required && <span className="text-red-500 ml-1">*</span>}
        </label>
        <Input
          id={name}
          type={type}
          placeholder={placeholder}
          className={cn(errors[name] && "border-red-500", className)}
          {...register(name)}
          ref={ref}
          {...props}
        />
        {errors[name] && (
          <p className="text-sm text-red-600">
            {errors[name]?.message as string}
          </p>
        )}
      </div>
    );
  }
);

FormField.displayName = "FormField";
