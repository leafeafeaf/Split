"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import SmallBowlingBall from "@/components/small-bowling-ball"

interface UserData {
  nickname: string
  gender: string
  height: number
}

interface UserMenuProps {
  userData: UserData
  onLogout: () => void
}

export function UserMenu({ userData, onLogout }: UserMenuProps) {
  const [isOpen, setIsOpen] = useState(false)
  const router = useRouter()

  const handleLogout = () => {
    onLogout()
    router.push("/login")
  }

  return (
    <DropdownMenu open={isOpen} onOpenChange={setIsOpen}>
      <DropdownMenuTrigger asChild>
        <button className="p-1 hover:opacity-80 transition-opacity" aria-label="User menu">
          <SmallBowlingBall />
        </button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-48 bg-[#1E1E2D] border-[#2E2E3D] mt-2">
        <DropdownMenuItem className="flex flex-col items-start py-2 text-white cursor-default">
          <span className="text-lg font-medium">{userData.nickname}</span>
        </DropdownMenuItem>
        <DropdownMenuItem className="py-2 text-[#A2A2A7] cursor-default">Gender: {userData.gender}</DropdownMenuItem>
        <DropdownMenuItem className="py-2 text-[#A2A2A7] cursor-default">Height: {userData.height}cm</DropdownMenuItem>
        <DropdownMenuItem
          className="py-2 text-red-400 cursor-pointer hover:text-red-300 hover:bg-white/5"
          onClick={handleLogout}
        >
          Logout
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

