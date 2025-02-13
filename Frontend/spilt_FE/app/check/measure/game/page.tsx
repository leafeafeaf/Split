"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import ThemeToggle from "@/components/ui/theme-toggle"
import SmallBowlingBall from "@/components/small-bowling-ball"
import { AnalysisRadarChart } from "@/components/charts/radar-chart"
import { ButtonPrimary } from "@/components/ui/button-primary"
import { HighlightWarningModal } from "@/components/modals/highlight-warning-modal"
import { BowlingScoreModal } from "@/components/modals/bowling-score-modal"
import { useAppDispatch, useAppSelector } from "@/app/store/hooks"
import { uploadGame, clearGameData, setBowlingScore } from "@/app/features/gameSlice"
import { clearFrames } from "@/app/features/frameSlice"
import { toast } from "sonner"

export default function GameEvaluationPage() {
  const router = useRouter()
  const dispatch = useAppDispatch()
  const { frames } = useAppSelector((state) => state.frame)
  const { isLoading, error, bowlingScore } = useAppSelector((state) => state.game)
  const [showWarningModal, setShowWarningModal] = useState(false)
  const [showScoreModal, setShowScoreModal] = useState(false)
  const [warningAction, setWarningAction] = useState<"complete" | "next">()

  // Calculate statistics from frames
  const gameStats =
    frames.length === 10
      ? {
          poseScore: frames.reduce((sum, frame) => sum + frame.poseScore, 0) / frames.length,
          elbowAngleScore: frames.reduce((sum, frame) => sum + frame.elbowAngleScore, 0) / frames.length,
          armStabilityScore: frames.reduce((sum, frame) => sum + frame.armStabilityScore, 0) / frames.length,
          speed: frames.reduce((sum, frame) => sum + frame.speed, 0) / frames.length,
        }
      : null

  useEffect(() => {
    // Redirect if no frames data
    if (frames.length !== 10) {
      toast.error("No complete frame data available")
      router.push("/check/measure")
    }
  }, [frames.length, router])

  useEffect(() => {
    // Show bowling score modal if not set
    if (frames.length === 10 && bowlingScore === null) {
      setShowScoreModal(true)
    }
  }, [frames.length, bowlingScore])

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

  const handleWarningConfirm = async () => {
    if (!gameStats || bowlingScore === null) return

    try {
      await dispatch(uploadGame({ frames, bowlingScore })).unwrap()
      dispatch(clearFrames())
      dispatch(clearGameData())

      if (warningAction === "complete") {
        router.push("/home")
      } else if (warningAction === "next") {
        router.push("/check")
      }
    } catch (error) {
      if (typeof error === "string") {
        toast.error(error)
      } else {
        toast.error("Failed to upload game data")
      }
    } finally {
      setShowWarningModal(false)
    }
  }

  if (!gameStats) {
    return null // Early return while redirecting
  }

  return (
    <div className="min-h-screen bg-[#161622] p-6">
      <div className="flex justify-between items-center mb-8">
        <ThemeToggle />
        <SmallBowlingBall />
      </div>

      <div className="max-w-md mx-auto space-y-8">
        <AnalysisRadarChart
          data={{
            angle: gameStats.elbowAngleScore,
            stability: gameStats.armStabilityScore,
            speed: gameStats.speed,
          }}
        />

        <div className="space-y-4">
          <h2 className="text-2xl text-white text-center font-medium">Statistics</h2>
          <div className="grid grid-cols-3 gap-4 mb-4">
            <div className="text-center">
              <p className="text-[#0066FF] text-xl">Angle</p>
              <p className="text-white text-2xl tabular-nums">{gameStats.elbowAngleScore.toFixed(2)}</p>
            </div>
            <div className="text-center">
              <p className="text-[#0066FF] text-xl">Stability</p>
              <p className="text-white text-2xl tabular-nums">{gameStats.armStabilityScore.toFixed(2)}</p>
            </div>
            <div className="text-center">
              <p className="text-[#0066FF] text-xl">Speed</p>
              <p className="text-white text-2xl tabular-nums">{gameStats.speed.toFixed(2)}</p>
            </div>
          </div>
        </div>

        <div className="bg-[#1E1E2D] rounded-lg p-4">
          <div className="text-center">
            <p className="text-[#9CB1D1] text-xl">Game Score</p>
            <p className="text-white text-2xl tabular-nums">{gameStats.poseScore.toFixed(2)}</p>
          </div>
        </div>

        {bowlingScore !== null && (
          <div className="bg-[#1E1E2D] rounded-lg p-4">
            <div className="text-center">
              <p className="text-[#ECE929] text-xl">Bowling Score</p>
              <p className="text-white text-2xl">{bowlingScore}</p>
            </div>
          </div>
        )}

        <div className="space-y-4 pt-8">
          <ButtonPrimary onClick={handleComplete} className="w-full py-4" disabled={isLoading || bowlingScore === null}>
            {isLoading ? "Uploading..." : "측정 완료"}
          </ButtonPrimary>
          <ButtonPrimary onClick={handleBack} className="w-full py-4" disabled={isLoading}>
            돌아가기
          </ButtonPrimary>
          <ButtonPrimary onClick={handleNext} className="w-full py-4" disabled={isLoading || bowlingScore === null}>
            다음 경기 측정
          </ButtonPrimary>
        </div>
      </div>

      <HighlightWarningModal
        isOpen={showWarningModal}
        onClose={() => setShowWarningModal(false)}
        onConfirm={handleWarningConfirm}
      />

      <BowlingScoreModal
        isOpen={showScoreModal}
        onClose={() => setShowScoreModal(false)}
        onSubmit={(score) => dispatch(setBowlingScore(score))}
      />
    </div>
  )
}

