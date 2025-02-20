import { create } from "zustand"
import api from "./api"
import { toast } from "sonner"

interface ThemeStore {
  theme: "light" | "dark"
  isLoading: boolean
  setTheme: (theme: "light" | "dark") => Promise<void>
}

export const useThemeStore = create<ThemeStore>((set) => ({
  theme: "dark", // Default theme
  isLoading: false,
  setTheme: async (newTheme) => {
    set({ isLoading: true })
    try {
      const themeValue = newTheme === "light" ? 1 : 2

      await api.patch(
        "user/thema",
        { thema: themeValue },
        // Add headers when token is implemented
        // {
        //   headers: {
        //     Authorization: `Bearer ${token}`,
        //   },
        // }
      )

      set({ theme: newTheme })
      document.documentElement.classList.toggle("dark", newTheme === "dark")
    } catch (error: any) {
      // Handle different error cases
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
        toast.error("Failed to update theme")
      }
    } finally {
      set({ isLoading: false })
    }
  },
}))

