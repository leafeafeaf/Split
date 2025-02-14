"use client"

import { motion } from "framer-motion"
import { cn } from "@/lib/utils"

interface ProgressGaugeProps {
  progress: number // 0-100
  onSegmentClick: (segment: number) => void
}

export function ProgressGauge({ progress, onSegmentClick }: ProgressGaugeProps) {
  const segments = Array.from({ length: 10 }, (_, i) => i)
  const segmentAngle = 360 / segments.length

  return (
    <div className="relative w-[28rem] h-[28rem]">
      {/* Segments */}
      {segments.map((segment) => {
        const rotation = segment * segmentAngle
        const isActive = progress / 10 > segment
        const roundNumber = 10 - segment // Reverse the round number

        return (
          <motion.button
            key={segment}
            className="absolute top-0 left-0 w-full h-full"
            style={{
              transform: `rotate(${rotation}deg)`,
              transformOrigin: "center center",
            }}
            onClick={() => onSegmentClick(roundNumber)}
            whileTap={{ scale: 0.98 }}
          >
            <div
              className="absolute top-4 left-1/2 transform -translate-x-1/2 flex flex-col items-center"
              style={{
                transformOrigin: "center 10rem",
              }}
            >
              {/* Rectangle part */}
              <div
                className={cn(
                  "w-24 h-6 rounded-sm transition-all duration-300 transform origin-left",
                  isActive
                    ? "bg-gradient-to-r from-[#0066FF] to-[#00AAFF]"
                    : "bg-gradient-to-r from-[#C9C9C9] to-[#6E6E6E]",
                )}
                style={{
                  transform: `scaleX(${isActive ? 1 : 0.85})`,
                }}
              />
            </div>
          </motion.button>
        )
      })}

      {/* Center circle */}
      <div className="absolute inset-20 rounded-full bg-gradient-to-br from-[#1E1E2D] to-[#161622] flex flex-col items-center justify-center shadow-lg">
        <span className="text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#0066FF] to-[#00AAFF] mb-2">
          {progress}%
        </span>
        <span className="text-[#A2A2A7] text-xl font-medium">{progress < 100 ? "측정중" : "측정완료"}</span>
      </div>
    </div>
  )
}

