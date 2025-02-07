"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import ThemeToggle from "@/components/ui/theme-toggle"
import SmallBowlingBall from "@/components/small-bowling-ball"
import { AnalysisRadarChart } from "@/components/charts/radar-chart"
import { ButtonPrimary } from "@/components/ui/button-primary"
import { HighlightWarningModal } from "@/components/modals/highlight-warning-modal"

// Mock game data with correct structure for radar chart
const mockGameData = {
  // Radar chart data
  angle: 36,
  stability: 36,
  speed: 36,
  // Additional stats
  gameScore: 36,
  bowlingScore: 160,
}

export default function GameEvaluationPage() {
  const router = useRouter()
  const [showWarningModal, setShowWarningModal] = useState(false)
  const [warningAction, setWarningAction] = useState<"complete" | "next">()

  const handleComplete = () => {
    setWarningAction("complete")
    setShowWarningModal(true)
  }

  const handleNext = () => {
    setWarningAction("next")
    setShowWarningModal(true)
  }

  const handleBack = () => {
    router.push("/check/measure")
  }

  const handleWarningConfirm = () => {
    if (warningAction === "complete") {
      router.push("/home")
    } else if (warningAction === "next") {
      router.push("/check")
    }
    setShowWarningModal(false)
  }

  return (
    <div className="min-h-screen bg-[#161622] p-6">
      <div className="flex justify-between items-center mb-8">
        <ThemeToggle />
        <SmallBowlingBall />
      </div>

      <div className="max-w-md mx-auto space-y-8">
        <AnalysisRadarChart data={mockGameData} />

        <div className="space-y-4">
          <h2 className="text-2xl text-white text-center font-medium">Statistics</h2>
          <div className="bg-[#1E1E2D] rounded-lg p-4">
            <div className="grid grid-cols-3 gap-4 mb-4">
              <div className="text-center">
                <p className="text-[#0066FF] text-xl">Angle</p>
                <p className="text-white text-2xl tabular-nums">{mockGameData.angle}.00</p>
              </div>
              <div className="text-center">
                <p className="text-[#0066FF] text-xl">Stability</p>
                <p className="text-white text-2xl tabular-nums">{mockGameData.stability}.00</p>
              </div>
              <div className="text-center">
                <p className="text-[#0066FF] text-xl">Score</p>
                <p className="text-white text-2xl tabular-nums">{mockGameData.speed}.00</p>
              </div>
            </div>
          </div>

          <div className="bg-[#1E1E2D] rounded-lg p-4">
            <div className="text-center">
              <p className="text-[#9CB1D1] text-xl">Game Score</p>
              <p className="text-white text-2xl tabular-nums">{mockGameData.gameScore}.00</p>
            </div>
          </div>

          <div className="bg-[#1E1E2D] rounded-lg p-4">
            <div className="text-center">
              <p className="text-[#ECE929] text-xl">Bowling Score</p>
              <p className="text-white text-2xl">{mockGameData.bowlingScore}</p>
            </div>
          </div>
        </div>

        <div className="space-y-4 pt-8">
          <ButtonPrimary onClick={handleComplete} className="w-full py-4">
            측정 완료
          </ButtonPrimary>
          <ButtonPrimary onClick={handleBack} className="w-full py-4">
            돌아가기
          </ButtonPrimary>
          <ButtonPrimary onClick={handleNext} className="w-full py-4">
            다음 경기 측정
          </ButtonPrimary>
        </div>
      </div>

      <HighlightWarningModal
        isOpen={showWarningModal}
        onClose={() => setShowWarningModal(false)}
        onConfirm={handleWarningConfirm}
      />
    </div>
  )
}

