"use client"

import { motion } from "framer-motion"

export default function BowlingBall() {
  return (
    <div className="relative w-24 h-24">
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 0.5 }}
        className="w-full h-full rounded-full"
        style={{
          background: "linear-gradient(135deg, #00FFE5 0%, #0066FF 100%)",
        }}
      />
      {/* Bowling ball holes */}
      <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 flex gap-1">
        <div className="w-2 h-2 rounded-full bg-[#161622]" />
        <div className="w-2 h-2 rounded-full bg-[#161622]" />
        <div className="w-2 h-2 rounded-full bg-[#161622]" />
      </div>
    </div>
  )
}

