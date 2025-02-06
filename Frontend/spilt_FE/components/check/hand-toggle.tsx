"use client"

import { motion } from "framer-motion"

interface HandToggleProps {
  isRightHand: boolean
  onToggle: () => void
}

export function HandToggle({ isRightHand, onToggle }: HandToggleProps) {
  return (
    <button
      onClick={onToggle}
      className="relative w-12 h-6 rounded-full border-2 border-white"
      aria-label="Toggle throwing hand"
    >
      <motion.div
        className="absolute top-0.5 left-0.5 w-4 h-4 bg-white rounded-full"
        animate={{
          x: isRightHand ? 24 : 0,
        }}
        transition={{
          type: "spring",
          stiffness: 500,
          damping: 30,
        }}
      />
      <span className="sr-only">{isRightHand ? "Right hand" : "Left hand"}</span>
    </button>
  )
}

