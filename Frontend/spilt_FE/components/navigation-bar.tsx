"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { Home, BarChart2, Timer } from "lucide-react"

export function NavigationBar() {
  const pathname = usePathname()

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-[#1E1E2D] border-t border-[#2E2E3D] px-6 py-2">
      <nav className="flex justify-between items-center max-w-md mx-auto">
        <Link
          href="/home"
          className={`flex flex-col items-center gap-1 ${pathname === "/home" ? "text-[#0066FF]" : "text-[#A2A2A7]"}`}
        >
          <Home className="w-6 h-6" />
          <span className="text-xs">Home</span>
        </Link>
        <Link
          href="/ranking"
          className={`flex flex-col items-center gap-1 ${
            pathname === "/ranking" ? "text-[#0066FF]" : "text-[#A2A2A7]"
          }`}
        >
          <BarChart2 className="w-6 h-6" />
          <span className="text-xs">Ranking</span>
        </Link>
        <Link
          href="/check"
          className={`flex flex-col items-center gap-1 ${pathname === "/check" ? "text-[#0066FF]" : "text-[#A2A2A7]"}`}
        >
          <Timer className="w-6 h-6" />
          <span className="text-xs">Check</span>
        </Link>
      </nav>
    </div>
  )
}

