"use client"

import { useState, useEffect } from "react"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import SmallBowlingBall from "@/components/small-bowling-ball"
import { useUserStore } from "@/lib/user"

const GENDER_MAP = {
  1: "Male",
  2: "Female",
  3: "Prefer not to say",
}

export function UserMenu() {
  const [isOpen, setIsOpen] = useState(false)
  const { user, isLoading, fetchUser, logout } = useUserStore()

  useEffect(() => {
    if (isOpen && !user && !isLoading) {
      fetchUser()
    }
  }, [isOpen, user, isLoading, fetchUser])

  const handleLogout = async () => {
    setIsOpen(false)
    await logout()
  }

  return (
    <DropdownMenu open={isOpen} onOpenChange={setIsOpen}>
      <DropdownMenuTrigger asChild>
        <button
          className={`p-1 hover:opacity-80 transition-opacity ${isLoading ? "opacity-50 cursor-wait" : ""}`}
          aria-label="User menu"
          disabled={isLoading}
        >
          <SmallBowlingBall />
        </button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-48 bg-[#1E1E2D] border-[#2E2E3D] mt-2">
        {user ? (
          <>
            <DropdownMenuItem className="flex flex-col items-start py-2 text-white cursor-default">
              <span className="text-lg font-medium">{user.nickname}</span>
            </DropdownMenuItem>
            <DropdownMenuItem className="py-2 text-[#A2A2A7] cursor-default">
              Gender: {GENDER_MAP[user.gender as keyof typeof GENDER_MAP]}
            </DropdownMenuItem>
            <DropdownMenuItem className="py-2 text-[#A2A2A7] cursor-default">Height: {user.height}cm</DropdownMenuItem>
            <DropdownMenuItem
              className="py-2 text-red-400 cursor-pointer hover:text-red-300 hover:bg-white/5"
              onClick={handleLogout}
              disabled={isLoading}
            >
              {isLoading ? "Logging out..." : "Logout"}
            </DropdownMenuItem>
          </>
        ) : (
          <DropdownMenuItem className="py-2 text-[#A2A2A7] cursor-default">
            {isLoading ? "Loading..." : "Failed to load user data"}
          </DropdownMenuItem>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

