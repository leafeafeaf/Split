"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Mail, Lock, Eye, EyeOff, User, Check, X } from 'lucide-react'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import BackButton from "@/components/back-button"
import api from "@/lib/api"
import { toast } from "sonner"
import { useDebouncedCallback } from "use-debounce"

// Gender mapping as per API specification
const GENDER_MAP = {
  male: 1,
  female: 2,
  unspecified: 3,
} as const

type GenderKey = keyof typeof GENDER_MAP

interface FormData {
  email: string
  password: string
  confirmPassword: string
  gender: GenderKey | ""
  height: string
  nickname: string
}

interface NicknameValidation {
  isChecking: boolean
  isValid: boolean | null
  message: string
}

export default function SignUpPage() {
  const router = useRouter()
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [nicknameValidation, setNicknameValidation] = useState<NicknameValidation>({
    isChecking: false,
    isValid: null,
    message: "",
  })
  const [formData, setFormData] = useState<FormData>({
    email: "",
    password: "",
    confirmPassword: "",
    gender: "",
    height: "170",
    nickname: "",
  })

  const checkNickname = useDebouncedCallback(async (nickname: string) => {
    if (!nickname) {
      setNicknameValidation({
        isChecking: false,
        isValid: null,
        message: "",
      })
      return
    }

    setNicknameValidation((prev) => ({ ...prev, isChecking: true }))

    try {
      await api.get(`/user/check-nickname/${nickname}`)
      setNicknameValidation({
        isChecking: false,
        isValid: true,
        message: "Nickname is available",
      })
    } catch (error: any) {
      if (error.response?.status === 409) {
        setNicknameValidation({
          isChecking: false,
          isValid: false,
          message: "Nickname is already taken",
        })
      } else {
        setNicknameValidation({
          isChecking: false,
          isValid: false,
          message: "Failed to check nickname",
        })
      }
    }
  }, 500)

  const handleNicknameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newNickname = e.target.value
    setFormData((prev) => ({ ...prev, nickname: newNickname }))
    checkNickname(newNickname)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (formData.password !== formData.confirmPassword) {
      toast.error("Passwords do not match!")
      return
    }

    if (!nicknameValidation.isValid) {
      toast.error("Please choose a valid nickname")
      return
    }

    setIsLoading(true)

    try {
      const formDataToSend = new FormData()
      formDataToSend.append("email", formData.email)
      formDataToSend.append("password", formData.password)
      formDataToSend.append("nickname", formData.nickname)
      formDataToSend.append("gender", formData.gender ? GENDER_MAP[formData.gender].toString() : "")
      formDataToSend.append("height", formData.height)

      const response = await api.post("user", formDataToSend, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })

      if (response.data.code === "SUCCESS") {
        toast.success("Successfully registered!")
        router.push("/login")
      }
    } catch (error: any) {
      if (error.response?.status === 409) {
        if (error.response.data.code === "EMAIL_ALREADY_EXISTS") {
          toast.error("This email is already registered")
        } else if (error.response.data.code === "NICKNAME_ALREADY_EXISTS") {
          toast.error("This nickname is already taken")
        }
      } else if (error.response?.status === 400) {
        toast.error("Please check your email and password format")
      } else {
        toast.error("An error occurred. Please try again.")
      }
    } finally {
      setIsLoading(false)
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
                disabled={isLoading}
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-[#A2A2A7] hover:text-white transition-colors"
                disabled={isLoading}
              >
                {showConfirmPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
              </button>
            </div>
          </div>

          <div className="space-y-2">
            <Label className="text-[#A2A2A7]">Gender</Label>
            <Select
              value={formData.gender}
              onValueChange={(value: GenderKey) => setFormData({ ...formData, gender: value })}
            >
              <SelectTrigger className="bg-transparent border-[#A2A2A7] text-white">
                <SelectValue placeholder="Select your gender" />
              </SelectTrigger>
              <SelectContent
                className="bg-[#1E1E2D] border-[#2E2E3D] text-white"
                position="popper"
                sideOffset={5}
              >
                <SelectItem value="male">Male</SelectItem>
                <SelectItem value="female">Female</SelectItem>
                <SelectItem value="unspecified">Prefer not to say</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label className="text-[#A2A2A7]">Height</Label>
            <Select
              value={formData.height}
              onValueChange={(value) => setFormData({ ...formData, height: value })}
            >
              <SelectTrigger className="bg-transparent border-[#A2A2A7] text-white">
                <SelectValue placeholder="Select your height" />
              </SelectTrigger>
              <SelectContent
                className="bg-[#1E1E2D] border-[#2E2E3D] text-white max-h-[200px] overflow-y-auto"
                position="popper"
                sideOffset={5}
              >
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
                onChange={handleNicknameChange}
                className={`bg-transparent border-[#A2A2A7] pl-10 pr-10 text-white ${
                  nicknameValidation.isValid === true
                    ? "border-green-500"
                    : nicknameValidation.isValid === false
                    ? "border-red-500"
                    : ""
                }`}
                placeholder="Enter your nickname"
                required
                disabled={isLoading}
              />
              <div className="absolute right-3 top-1/2 -translate-y-1/2">
                {nicknameValidation.isChecking ? (
                  <div className="h-5 w-5 animate-spin rounded-full border-2 border-[#A2A2A7] border-t-transparent" />
                ) : nicknameValidation.isValid === true ? (
                  <Check className="h-5 w-5 text-green-500" />
                ) : nicknameValidation.isValid === false ? (
                  <X className="h-5 w-5 text-red-500" />
                ) : null}
              </div>
            </div>
            {nicknameValidation.message && (
              <p className={`text-sm ${nicknameValidation.isValid ? "text-green-500" : "text-red-500"}`}>
                {nicknameValidation.message}
              </p>
            )}
          </div>

          <Button
            type="submit"
            className="w-full bg-[#0066FF] hover:bg-[#0066FF]/90 text-white"
            disabled={isLoading || nicknameValidation.isChecking || nicknameValidation.isValid === false}
          >
            {isLoading ? "Signing up..." : "Sign Up"}
          </Button>
        </form>
      </div>
    </div>
  )
}

