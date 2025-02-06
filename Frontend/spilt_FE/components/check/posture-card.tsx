"use client"

import { motion } from "framer-motion"
import { cn } from "@/lib/utils"

interface PostureCardProps {
  imageUrl: string
  isSelected: boolean
  onClick: () => void
}

export function PostureCard({ imageUrl, isSelected, onClick }: PostureCardProps) {
  return (
    <motion.button
      whileTap={{ scale: 0.95 }}
      onClick={onClick}
      className={cn(
        "relative w-full aspect-[3/4] rounded-xl overflow-hidden",
        "bg-gradient-to-b from-[#D1D1D1] to-[#A8A8A8]",
        isSelected && "ring-2 ring-[#0066FF]",
      )}
    >
      <img src={imageUrl || "/placeholder.svg"} alt="Bowling posture" className="w-full h-full object-contain" />
    </motion.button>
  )
}

