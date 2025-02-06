"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Mail, Lock, Eye, EyeOff, User } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import BackButton from "@/components/back-button"

export default function SignUpPage() {
  const router = useRouter()
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [formData, setFormData] = useState({
    email: "",
    password: "",
    confirmPassword: "",
    gender: "",
    height: "170",
    nickname: "",
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      if (formData.password !== formData.confirmPassword) {
        alert("Passwords do not match!")
        return
      }
      // Add your signup logic here
      router.push("/login")
    } catch (error) {
      console.error("Signup error:", error)
    }
  }

  const heightOptions = Array.from({ length: 300 }, (_, i) => (i + 1).toString())

  return (
    <div className="min-h-screen bg-[#161622] p-6">
      <BackButton />

      <div className="mt-8 max-w-md mx-auto">
        <h1 className="text-white text-3xl font-bold mb-8">Sign Up</h1>

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
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-[#A2A2A7] hover:text-white transition-colors"
              >
                {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
              </button>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="confirmPassword" className="text-[#A2A2A7]">
              Confirm Password
            </Label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-[#A2A2A7]" />
              <Input
                id="confirmPassword"
                type={showConfirmPassword ? "text" : "password"}
                value={formData.confirmPassword}
                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                className="bg-transparent border-[#A2A2A7] pl-10 pr-10 text-white"
                required
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-[#A2A2A7] hover:text-white transition-colors"
              >
                {showConfirmPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
              </button>
            </div>
          </div>

          <div className="space-y-2">
            <Label className="text-[#A2A2A7]">Gender</Label>
            <Select value={formData.gender} onValueChange={(value) => setFormData({ ...formData, gender: value })}>
              <SelectTrigger className="bg-transparent border-[#A2A2A7] text-white">
                <SelectValue placeholder="Select your gender" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="male">Male</SelectItem>
                <SelectItem value="female">Female</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label className="text-[#A2A2A7]">Height</Label>
            <Select value={formData.height} onValueChange={(value) => setFormData({ ...formData, height: value })}>
              <SelectTrigger className="bg-transparent border-[#A2A2A7] text-white">
                <SelectValue placeholder="Select your height" />
              </SelectTrigger>
              <SelectContent className="max-h-[200px] overflow-y-auto">
                {heightOptions.map((height) => (
                  <SelectItem key={height} value={height}>
                    {height} cm
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="nickname" className="text-[#A2A2A7]">
              Nickname
            </Label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-[#A2A2A7]" />
              <Input
                id="nickname"
                type="text"
                value={formData.nickname}
                onChange={(e) => setFormData({ ...formData, nickname: e.target.value })}
                className="bg-transparent border-[#A2A2A7] pl-10 text-white"
                placeholder="Enter your nickname"
                required
              />
            </div>
          </div>

          <Button type="submit" className="w-full bg-[#0066FF] hover:bg-[#0066FF]/90 text-white">
            Sign Up
          </Button>
        </form>
      </div>
    </div>
  )
}

