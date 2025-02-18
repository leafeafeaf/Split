"use client"

import { motion } from "framer-motion"
import { useState } from "react"

export default function BowlingBall() {
  const [isRolling, setIsRolling] = useState(false)

  const handleClick = () => {
    setIsRolling(true)
    setTimeout(() => setIsRolling(false), 500)
  }

  return (
    <div className="relative w-24 h-24" onClick={handleClick}>
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{
          scale: 1,
          opacity: 1,
          rotate: isRolling ? 720 : 0,
        }}
        whileHover={{ scale: 1.1 }}
        transition={{
          duration: 0.5,
          ease: "easeInOut",
        }}
        className="relative w-full h-full rounded-full shadow-lg cursor-pointer"
        style={{
          background: "radial-gradient(circle at 30% 30%, #00FFE5, #0066FF)",
          boxShadow: "inset 2px 2px 4px rgba(255,255,255,0.5), inset -2px -2px 4px rgba(0,0,0,0.5)",
        }}
      >
        {/* Bowling ball holes container that rotates with the ball */}
        <motion.div
          className="absolute inset-0"
          animate={{
            rotate: isRolling ? 720 : 0,
          }}
          transition={{
            duration: 0.5,
            ease: "easeInOut",
          }}
        >
          {/* Top two smaller holes */}
          <div
            className="absolute w-2.5 h-2.5 rounded-full bg-[#161622] opacity-80"
            style={{
              top: "25%",
              left: "35%",
            }}
          />
          <div
            className="absolute w-2.5 h-2.5 rounded-full bg-[#161622] opacity-80"
            style={{
              top: "25%",
              right: "35%",
            }}
          />
          {/* Bottom larger hole */}
          <div
            className="absolute w-3.5 h-3.5 rounded-full bg-[#161622] opacity-80"
            style={{
              top: "45%",
              left: "50%",
              transform: "translateX(-50%)",
            }}
          />
        </motion.div>
      </motion.div>
    </div>
  )
}



