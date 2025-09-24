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
      // TODO: call register API
      console.log("Register", data);
      // set dummy token
      authService.setToken("dummy.jwt.token");
      window.location.href = "/";
    } catch (err) {
      console.error(err);
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
                <FormField name="firstName" label="First name" required />
                <FormField name="lastName" label="Last name" required />
              </div>

              <FormField name="email" label="Email" type="email" required />

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <FormField
                  name="password"
                  label="Password"
                  type="password"
                  required
                />
                <FormField
                  name="confirmPassword"
                  label="Confirm password"
                  type="password"
                  required
                />
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
