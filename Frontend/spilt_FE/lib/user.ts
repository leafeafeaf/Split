import { create } from "zustand"
import api from "./api"
import { toast } from "sonner"
import type { UserData } from "@/types/user"

interface UserStore {
  user: UserData | null
  isLoading: boolean
  fetchUser: () => Promise<void>
  logout: () => Promise<void>
}

export const useUserStore = create<UserStore>((set) => ({
  user: null,
  isLoading: false,
  fetchUser: async () => {
    set({ isLoading: true })
    try {
      const response = await api.get(
        "user",
        // Add headers when token is implemented
        // {
        //   headers: {
        //     Authorization: `Bearer ${token}`,
        //   },
        // }
      )

      if (response.data.code === "SUCCESS") {
        set({ user: response.data.data })
      }
    } catch (error: any) {
      if (error.response?.status === 404) {
        toast.error("User not found")
      } else if (error.response?.status === 401) {
        // Token related errors - commented out for now
        // if (error.response.data.code === "TOKEN_EXPIRED") {
        //   toast.error("Session expired. Please login again.")
        // } else if (error.response.data.code === "INVALID_TOKEN") {
        //   toast.error("Invalid session. Please login again.")
        // } else if (error.response.data.code === "TOKEN_MISSING") {
        //   toast.error("Authorization required. Please login.")
        // }
      } else {
        toast.error("Failed to fetch user data")
      }
    } finally {
      set({ isLoading: false })
    }
  },
  logout: async () => {
    set({ isLoading: true })
    try {
      const response = await api.post(
        "logout",
        // Add headers when token is implemented
        // {
        //   headers: {
        //     Authorization: `Bearer ${token}`,
        //   },
        // }
      )

      if (response.data.code === "SUCCESS") {
        set({ user: null })
        toast.success("Successfully logged out")
        window.location.href = "/login"
      }
    } catch (error: any) {
      toast.error("Failed to logout")
      set({ user: null })
      window.location.href = "/login"
    } finally {
      set({ isLoading: false })
    }
  },
}))

