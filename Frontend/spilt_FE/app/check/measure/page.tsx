"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import ThemeToggle from "@/components/ui/theme-toggle"
import { UserMenu } from "@/components/user-menu"
import { NavigationBar } from "@/components/navigation-bar"
import { ProgressGauge } from "@/components/measure/progress-gauge"
import { ProgressButton } from "@/components/measure/progress-button"
import { BowlingScoreModal } from "@/components/modals/bowling-score-modal"

// Mock user data
const mockUserData = {
  nickname: "James",
  gender: "Male",
  height: 180,
}

export default function MeasurePage() {
  const router = useRouter()
  const [progress, setProgress] = useState(0)
  const [isScoreModalOpen, setIsScoreModalOpen] = useState(false)

  const handleLogout = () => {
    console.log("Logging out...")
  }

  const handleSegmentClick = (roundNumber: number) => {
    router.push(`/check/measure/round/${roundNumber}`)
  }

  const handleProgressButtonClick = () => {
    if (progress === 100) {
      setIsScoreModalOpen(true)
    } else {
      setProgress((prev) => Math.min(100, prev + 10))
    }
  }

  const handleScoreSubmit = (score: number) => {
    setIsScoreModalOpen(false)
    router.push(`/check/measure/game?score=${score}`)
  }

  // Simulating BE signal for progress update
  useState(() => {
    const interval = setInterval(() => {
      setProgress((prev) => {
        if (prev < 100) {
          return Math.min(100, prev + 10)
        }
        return prev
      })
    }, 3000)

    return () => clearInterval(interval)
  })

  return (
    <div className="min-h-screen bg-[#161622] p-6 pb-24">
      <div className="flex justify-between items-center mb-12">
        <ThemeToggle />
        <UserMenu userData={mockUserData} onLogout={handleLogout} />
      </div>

      <div className="max-w-md mx-auto space-y-12">
        <h1 className="text-3xl font-bold text-white text-center mb-8">Progress</h1>

        <div className="flex justify-center">
          <ProgressGauge progress={progress} onSegmentClick={handleSegmentClick} />
        </div>

        <ProgressButton progress={progress} onClick={handleProgressButtonClick} />
      </div>

      <NavigationBar />

      <BowlingScoreModal
        isOpen={isScoreModalOpen}
        onClose={() => setIsScoreModalOpen(false)}
        onSubmit={handleScoreSubmit}
      />
    </div>
  )
}

