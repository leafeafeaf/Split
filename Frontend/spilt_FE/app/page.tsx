"use client"

import { useRouter } from "next/navigation"
import { motion } from "framer-motion"
import BowlingBall from "@/components/bowling-ball"

export default function SplashScreen() {
  const router = useRouter()

  return (
    <div
      className="min-h-screen bg-[#161622] flex flex-col items-center justify-center cursor-pointer"
      onClick={() => router.push("/login")}
    >
      <motion.div
        initial={{ y: -20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.5 }}
        className="flex flex-col items-center gap-4"
      >
        <BowlingBall />
        <motion.h1
          initial={{ y: 20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.2 }}
          className="text-white text-4xl font-bold tracking-wider"
        >
          SPLIT
        </motion.h1>
      </motion.div>
    </div>
  )
}

