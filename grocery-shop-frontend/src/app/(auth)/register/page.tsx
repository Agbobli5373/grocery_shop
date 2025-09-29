"use client";

import React from "react";
import { z } from "zod";
import Link from "next/link";
import { useForm, FormProvider } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { registerSchema } from "@/lib/validations/schemas";
import { FormField } from "@/components/forms/form-field";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { authService } from "@/lib/auth/service";

type RegisterValues = z.infer<typeof registerSchema>;

export default function RegisterPage() {
  const methods = useForm<RegisterValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      confirmPassword: "",
    },
  });

  const onSubmit = async (data: RegisterValues) => {
    try {
      const { confirmPassword, ...registerData } = data;
      await authService.register(registerData);
      // After successful registration, redirect to login
      window.location.href = "/login?message=Registration successful! Please log in.";
    } catch (err) {
      console.error(err);
      // show error toast in real app
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <Card className="w-full max-w-lg">
        <CardContent className="p-8">
          <h2 className="text-2xl font-bold mb-2">Create an account</h2>
          <p className="text-sm text-gray-600 mb-6">
            Sign up to start shopping
          </p>

          <FormProvider {...methods}>
            <form
              onSubmit={methods.handleSubmit(onSubmit)}
              className="space-y-4"
            >
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <label
                    htmlFor="firstName"
                    className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                  >
                    First name*
                  </label>
                  <input
                    id="firstName"
                    type="text"
                    className={`flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${
                      methods.formState.errors.firstName ? "border-red-500" : ""
                    }`}
                    {...methods.register("firstName")}
                  />
                  {methods.formState.errors.firstName && (
                    <p className="text-sm text-red-600">
                      {methods.formState.errors.firstName.message}
                    </p>
                  )}
                </div>
                <div className="space-y-2">
                  <label
                    htmlFor="lastName"
                    className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                  >
                    Last name*
                  </label>
                  <input
                    id="lastName"
                    type="text"
                    className={`flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${
                      methods.formState.errors.lastName ? "border-red-500" : ""
                    }`}
                    {...methods.register("lastName")}
                  />
                  {methods.formState.errors.lastName && (
                    <p className="text-sm text-red-600">
                      {methods.formState.errors.lastName.message}
                    </p>
                  )}
                </div>
              </div>

              <div className="space-y-2">
                <label
                  htmlFor="email"
                  className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                >
                  Email*
                </label>
                <input
                  id="email"
                  type="email"
                  className={`flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${
                    methods.formState.errors.email ? "border-red-500" : ""
                  }`}
                  {...methods.register("email")}
                />
                {methods.formState.errors.email && (
                  <p className="text-sm text-red-600">
                    {methods.formState.errors.email.message}
                  </p>
                )}
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <label
                    htmlFor="password"
                    className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                  >
                    Password*
                  </label>
                  <input
                    id="password"
                    type="password"
                    className={`flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${
                      methods.formState.errors.password ? "border-red-500" : ""
                    }`}
                    {...methods.register("password")}
                  />
                  {methods.formState.errors.password && (
                    <p className="text-sm text-red-600">
                      {methods.formState.errors.password.message}
                    </p>
                  )}
                </div>
                <div className="space-y-2">
                  <label
                    htmlFor="confirmPassword"
                    className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                  >
                    Confirm password*
                  </label>
                  <input
                    id="confirmPassword"
                    type="password"
                    className={`flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${
                      methods.formState.errors.confirmPassword ? "border-red-500" : ""
                    }`}
                    {...methods.register("confirmPassword")}
                  />
                  {methods.formState.errors.confirmPassword && (
                    <p className="text-sm text-red-600">
                      {methods.formState.errors.confirmPassword.message}
                    </p>
                  )}
                </div>
              </div>

              <div className="flex items-center justify-between">
                <Link href="/login" className="text-sm text-green-700">
                  Already have an account?
                </Link>
                <Button
                  type="submit"
                  className="bg-green-600 hover:bg-green-700"
                >
                  Create account
                </Button>
              </div>
            </form>
          </FormProvider>
        </CardContent>
      </Card>
    </div>
  );
}
