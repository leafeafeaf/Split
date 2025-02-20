// "use client"

// import { motion } from "framer-motion"
// import { useRouter } from "next/navigation"
// import { cn } from "@/lib/utils"

// interface ProgressGaugeProps {
//   progress: number // 0-100
// }

// export function ProgressGauge({ progress }: ProgressGaugeProps) {
//   const router = useRouter()
//   const segments = Array.from({ length: 10 }, (_, i) => i)
//   const segmentAngle = 360 / segments.length

//   const handleSegmentClick = (roundNumber: number) => {
//     router.push(`/check/measure/round/${roundNumber}`)
//   }

//   return (
//     <div className="relative w-[28rem] h-[28rem]">
//       {/* Segments */}
//       {segments.map((segment) => {
//         const rotation = segment * segmentAngle
//         const isActive = progress / 10 > segment
//         const roundNumber = 10 - segment

//         return (
//           <motion.div
//             key={segment}
//             className="absolute top-0 left-0 w-full h-full"
//             style={{
//               transform: `rotate(${rotation}deg)`,
//               transformOrigin: "center center",
//               pointerEvents: 'none'
//             }}
//           >
//             <div
//               className="absolute top-4 left-1/2 transform -translate-x-1/2 flex flex-col items-center"
//               style={{
//                 transformOrigin: "center 10rem",
//               }}
//             >
//               <motion.button
//                 onClick={() => handleSegmentClick(roundNumber)}
//                 whileTap={{ scale: 0.98 }}
//                 className={cn(
//                   "w-24 h-6 rounded-sm transition-all duration-300 transform origin-left",
//                   isActive
//                     ? "bg-gradient-to-r from-[#0066FF] to-[#00AAFF]"
//                     : "bg-gradient-to-r from-[#C9C9C9] to-[#6E6E6E]"
//                 )}
//                 style={{
//                   transform: `scaleX(${isActive ? 1 : 0.85})`,
//                   pointerEvents: 'auto'
//                 }}
//                 initial={{ opacity: 0 }}
//                 animate={{
//                   opacity: 1,
//                   transition: {
//                     delay: segment * 0.1,
//                     duration: 0.3,
//                   },
//                 }}
//               />
//             </div>
//           </motion.div>
//         )
//       })}

//       {/* Center circle */}
//       <motion.div 
//         className="absolute inset-20 rounded-full bg-gradient-to-br from-[#1E1E2D] to-[#161622] flex flex-col items-center justify-center shadow-lg"
//         initial={{ opacity: 0 }}
//         animate={{ opacity: 1 }}
//         transition={{ duration: 0.5 }}
//       >
//         <motion.span 
//           className="text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#0066FF] to-[#00AAFF] mb-2"
//           initial={{ opacity: 0, y: 20 }}
//           animate={{ opacity: 1, y: 0 }}
//           transition={{ delay: 1, duration: 0.5 }}
//         >
//           {progress}%
//         </motion.span>
//         <motion.span 
//           className="text-[#A2A2A7] text-xl font-medium"
//           initial={{ opacity: 0, y: 20 }}
//           animate={{ opacity: 1, y: 0 }}
//           transition={{ delay: 1.2, duration: 0.5 }}
//         >
//           {progress < 100 ? "측정중" : "측정완료"}
//         </motion.span>
//       </motion.div>
//     </div>
//   )
// }



"use client"

import { motion } from "framer-motion"
import { useRouter } from "next/navigation"

interface ProgressGaugeProps {
  progress: number // 0-100
}

export function ProgressGauge({ progress }: ProgressGaugeProps) {
  const router = useRouter()
  const segments = Array.from({ length: 10 }, (_, i) => i)

  const handleSegmentClick = (roundNumber: number) => {
    router.push(`/check/measure/round/${roundNumber}`)
  }

  return (
    <div className="relative w-[500px] h-[500px]">
      <motion.svg
        viewBox="0 0 500 500"
        className="w-full h-full"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5 }}
      >
        <defs>
          <linearGradient id="activeGradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style={{ stopColor: "#0066FF" }} />
            <stop offset="100%" style={{ stopColor: "#00AAFF" }} />
          </linearGradient>
        </defs>

        {/* Background circle */}
        <circle cx="250" cy="250" r="220" fill="#161622" />

        {/* Segments */}
        <g>
          {segments.map((segment) => {
            const isActive = progress / 10 > segment
            const roundNumber = 10 - segment
            const angle = (segment * 36) - 90 // Start from top (-90 degrees)
            const startAngle = (angle * Math.PI) / 180
            const endAngle = ((angle + 28) * Math.PI) / 180 // 28 degrees for gap
            
            // Calculate arc coordinates
            const outerRadius = 200
            const innerRadius = 170
            const x1 = 250 + outerRadius * Math.cos(startAngle)
            const y1 = 250 + outerRadius * Math.sin(startAngle)
            const x2 = 250 + outerRadius * Math.cos(endAngle)
            const y2 = 250 + outerRadius * Math.sin(endAngle)
            const x3 = 250 + innerRadius * Math.cos(endAngle)
            const y3 = 250 + innerRadius * Math.sin(endAngle)
            const x4 = 250 + innerRadius * Math.cos(startAngle)
            const y4 = 250 + innerRadius * Math.sin(startAngle)

            const path = `
              M ${x1} ${y1}
              A ${outerRadius} ${outerRadius} 0 0 1 ${x2} ${y2}
              L ${x3} ${y3}
              A ${innerRadius} ${innerRadius} 0 0 0 ${x4} ${y4}
              Z
            `

            return (
              <motion.path
                key={segment}
                className="cursor-pointer transition-all duration-300"
                d={path}
                fill={isActive ? "url(#activeGradient)" : "#2E2E3D"}
                onClick={() => handleSegmentClick(roundNumber)}
                whileHover={{ opacity: 0.8 }}
                whileTap={{ scale: 0.98 }}
                initial={{ opacity: 0 }}
                animate={{
                  opacity: 1,
                  transition: {
                    delay: segment * 0.1,
                    duration: 0.3,
                  },
                }}
              />
            )
          })}
        </g>

        {/* Center content */}
        <foreignObject x="100" y="100" width="300" height="300">
          <div className="h-full flex flex-col items-center justify-center">
            <motion.span
              className="text-7xl font-bold text-[#0066FF]"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 1, duration: 0.5 }}
            >
              {progress}%
            </motion.span>
            <motion.span
              className="text-[#A2A2A7] text-2xl mt-2"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 1.2, duration: 0.5 }}
            >
              {progress < 100 ? "측정중" : "측정완료"}
            </motion.span>
          </div>
        </foreignObject>
      </motion.svg>
    </div>
  )
}








// "use client"

// import { motion } from "framer-motion"
// import { cn } from "@/lib/utils"

// interface ProgressGaugeProps {
//   progress: number // 0-100
//   onSegmentClick: (segment: number) => void
// }

// export function ProgressGauge({ progress, onSegmentClick }: ProgressGaugeProps) {
//   const segments = Array.from({ length: 10 }, (_, i) => i)
//   const segmentAngle = 360 / segments.length

//   return (
//     <div className="relative w-[28rem] h-[28rem]">
//       {/* Segments */}
//       {segments.map((segment) => {
//         const rotation = segment * segmentAngle
//         const isActive = progress / 10 > segment
//         const roundNumber = 10 - segment // Reverse the round number

//         return (
//           <motion.div // button을 div로 변경
//             key={segment}
//             className="absolute top-0 left-0 w-full h-full"
//             style={{
//               transform: `rotate(${rotation}deg)`,
//               transformOrigin: "center center",
//               pointerEvents: 'none' // 여기는 none 유지
//             }}
//           >
//             <div
//               className="absolute top-4 left-1/2 transform -translate-x-1/2 flex flex-col items-center"
//               style={{
//                 transformOrigin: "center 10rem",
//               }}
//             >
//               <motion.button // 실제 클릭 가능한 부분을 button으로 변경
//                 onClick={() => onSegmentClick(roundNumber)}
//                 whileTap={{ scale: 0.98 }}
//                 className={cn(
//                   "w-24 h-6 rounded-sm transition-all duration-300 transform origin-left",
//                   isActive
//                     ? "bg-gradient-to-r from-[#0066FF] to-[#00AAFF]"
//                     : "bg-gradient-to-r from-[#C9C9C9] to-[#6E6E6E]",
//                 )}
//                 style={{
//                   transform: `scaleX(${isActive ? 1 : 0.85})`,
//                   pointerEvents: 'auto' // 여기만 auto로 설정
//                 }}
//               />
//             </div>
//           </motion.div>

//         )
//       })}

//       {/* Center circle */}
//       <div className="absolute inset-20 rounded-full bg-gradient-to-br from-[#1E1E2D] to-[#161622] flex flex-col items-center justify-center shadow-lg">
//         <span className="text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#0066FF] to-[#00AAFF] mb-2">
//           {progress}%
//         </span>
//         <span className="text-[#A2A2A7] text-xl font-medium">{progress < 100 ? "측정중" : "측정완료"}</span>
//       </div>
//     </div>
//   )
// }