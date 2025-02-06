"use client"

import { motion } from "framer-motion"

interface SmallBowlingBallProps {
  className?: string
}

export default function SmallBowlingBall({ className = "" }: SmallBowlingBallProps) {
  return (
    <div className={`relative w-6 h-6 ${className}`}>
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 0.5 }}
        className="w-full h-full rounded-full"
        style={{
          background: "linear-gradient(135deg, #00FFE5 0%, #0066FF 100%)",
        }}
      >
        {/* Bowling ball holes */}
        <div className="absolute top-[25%] left-1/2 transform -translate-x-1/2 flex gap-[2px]">
          <div className="w-[2px] h-[2px] rounded-full bg-[#161622]" />
          <div className="w-[2px] h-[2px] rounded-full bg-[#161622]" />
        </div>
        <div className="absolute top-[40%] left-1/2 transform -translate-x-1/2">
          <div className="w-[3px] h-[3px] rounded-full bg-[#161622]" />
        </div>
      </motion.div>
    </div>
  )
}

