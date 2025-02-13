"use client"

import { Provider } from "react-redux"
import { store } from "./store/store"
import { Toaster } from "sonner"
import { useEffect } from "react"
import type { ReactNode } from "react"

export function Providers({ children }: { children: ReactNode }) {
  // Move client-side initialization here
  useEffect(() => {
    // Check system theme preference
    const systemPrefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches
    document.documentElement.classList.toggle("dark", systemPrefersDark)
  }, [])

  return (
    <Provider store={store}>
      {children}
      <Toaster position="top-center" theme="dark" />
    </Provider>
  )
}

