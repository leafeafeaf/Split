"use client"

import { motion } from "framer-motion"

interface ProgressButtonProps {
  progress: number
  onClick: () => void
  disabled?: boolean
}

export function ProgressButton({ progress, onClick, disabled = false }: ProgressButtonProps) {
  const isComplete = progress === 100

  return (
    <div className="relative w-full h-14 rounded-2xl overflow-hidden">
      {/* Background */}
      <div className="absolute inset-0 bg-[#2E2E3D]" />

      {/* Progress fill */}
      <motion.div
        className="absolute inset-0 bg-[#0066FF]"
        initial={{ width: 0 }}
        animate={{ width: `${progress}%` }}
        transition={{ duration: 0.3 }}
      />

      {/* Button */}
      <button
        onClick={onClick}
        disabled={disabled}
        className={`absolute inset-0 flex items-center justify-center text-white font-medium text-lg transition-transform active:scale-95 
          ${disabled ? "opacity-50 cursor-not-allowed" : ""}`}
      >
        {isComplete ? "Finish" : disabled ? "Processing..." : "Skip"}
      </button>
    </div>
  )
}

