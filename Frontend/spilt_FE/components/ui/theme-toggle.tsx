"use client"

import { motion } from "framer-motion"
import { useThemeStore } from "@/lib/theme"

export default function ThemeToggle() {
  const { theme, setTheme, isLoading } = useThemeStore()
  const isDark = theme === "dark"

  const handleToggle = async () => {
    if (!isLoading) {
      await setTheme(isDark ? "light" : "dark")
    }
  }

  return (
    <button
      onClick={handleToggle}
      className={`relative w-12 h-6 rounded-full border-2 border-white ${
        isLoading ? "opacity-50 cursor-not-allowed" : ""
      }`}
      aria-label="Toggle theme"
      disabled={isLoading}
    >
      <motion.div
        className="absolute top-0.5 left-0.5 w-4 h-4 bg-white rounded-full"
        animate={{
          x: isDark ? 0 : 24,
        }}
        transition={{
          type: "spring",
          stiffness: 500,
          damping: 30,
        }}
      />
    </button>
  )
}

