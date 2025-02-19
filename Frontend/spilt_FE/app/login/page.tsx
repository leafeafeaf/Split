"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Mail, Lock, Eye, EyeOff } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import BackButton from "@/components/back-button"
import api from "@/lib/api"
import { toast } from "sonner"
import { useAppDispatch } from "@/app/store/hooks"
import { setTokens } from "@/app/features/authSlice"

export default function LoginPage() {
  const router = useRouter()
  const dispatch = useAppDispatch()
  const [showPassword, setShowPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      const formDataToSend = new FormData()
      formDataToSend.append("email", formData.email)
      formDataToSend.append("password", formData.password)

      const response = await api.post("login", formDataToSend, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })

      if (response.data.code === "SUCCESS") {
        // Store tokens in Redux
        dispatch(
          setTokens({
            accessToken: response.headers["authorization"],
            refreshToken: response.headers["refresh-token"],
          }),
        )

        router.push("/home")
      }
    } catch (error: any) {
      if (error.response?.status === 401) {
        toast.error("Invalid email or password")
      } else {
        toast.error("An error occurred. Please try again.")
      }
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-[#161622] p-6">
      <BackButton />

      <div className="mt-8 max-w-md mx-auto">
        <h1 className="text-white text-3xl font-bold mb-8">Sign In</h1>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-2">
            <Label htmlFor="email" className="text-[#A2A2A7]">
              Email Address
            </Label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-[#A2A2A7]" />
              <Input
                id="email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="bg-transparent border-[#A2A2A7] pl-10 text-white"
                placeholder="example@email.com"
                required
                disabled={isLoading}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="password" className="text-[#A2A2A7]">
              Password
            </Label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-[#A2A2A7]" />
              <Input
                id="password"
                type={showPassword ? "text" : "password"}
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                className="bg-transparent border-[#A2A2A7] pl-10 pr-10 text-white"
                required
                disabled={isLoading}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-[#A2A2A7] hover:text-white transition-colors"
                disabled={isLoading}
              >
                {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
              </button>
            </div>
          </div>

          <Button type="submit" className="w-full bg-[#0066FF] hover:bg-[#0066FF]/90 text-white" disabled={isLoading}>
            {isLoading ? "Signing in..." : "Sign In"}
          </Button>
        </form>

        <p className="mt-6 text-center text-[#A2A2A7]">
          I&apos;m a new user.{" "}
          <Link href="/signup" className="text-[#0066FF] hover:text-[#0066FF]/90 transition-colors">
            Sign Up
          </Link>
        </p>
      </div>
    </div>
  )
}

