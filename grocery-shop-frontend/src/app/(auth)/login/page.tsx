"use client";

import React from "react";
import { z } from "zod";
import Link from "next/link";
import { useForm, FormProvider } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { loginSchema } from "@/lib/validations/schemas";
import { FormField } from "@/components/forms/form-field";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { useUserStore } from "@/lib/store/user";
import { authService } from "@/lib/auth/service";

type LoginValues = z.infer<typeof loginSchema>;

export default function LoginPage() {
  const methods = useForm<LoginValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: "", password: "" },
  });

  const { login } = useUserStore();

  const onSubmit = async (data: LoginValues) => {
    try {
      await login({ email: data.email, password: data.password });
      // In a real app we'd set tokens here; for now store a dummy token
      authService.setToken("dummy.jwt.token");
      window.location.href = "/";
    } catch (err) {
      console.error(err);
      // show error toast in real app
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4">
      <Card className="w-full max-w-md">
        <CardContent className="p-8">
          <h2 className="text-2xl font-bold mb-2">Sign in to your account</h2>
          <p className="text-sm text-gray-600 mb-6">
            Enter your credentials to continue
          </p>

          <FormProvider {...methods}>
            <form
              onSubmit={methods.handleSubmit(onSubmit)}
              className="space-y-4"
            >
              <FormField name="email" label="Email" type="email" required />
              <FormField
                name="password"
                label="Password"
                type="password"
                required
              />

              <div className="flex items-center justify-between">
                <Link href="/register" className="text-sm text-green-700">
                  Create an account
                </Link>
                <Button
                  type="submit"
                  className="bg-green-600 hover:bg-green-700"
                >
                  Sign In
                </Button>
              </div>
            </form>
          </FormProvider>
        </CardContent>
      </Card>
    </div>
  );
}
