"use client"

import { useState, useEffect, useCallback } from "react"
import { useRouter } from "next/navigation"
import { useAppDispatch, useAppSelector } from "@/app/store/hooks"
import { fetchFrames, updateProgress, skipFrame } from "@/app/features/frameSlice"
import ThemeToggle from "@/components/ui/theme-toggle"
import { UserMenu } from "@/components/user-menu"
import { NavigationBar } from "@/components/navigation-bar"
import { ProgressGauge } from "@/components/measure/progress-gauge"
import { ProgressButton } from "@/components/measure/progress-button"
import { BowlingScoreModal } from "@/components/modals/bowling-score-modal"
import { toast } from "sonner"

export default function MeasurePage() {
  const router = useRouter()
  const dispatch = useAppDispatch()
  const { currentSerial } = useAppSelector((state) => state.device)
  const { frames, progress, error, isLoading } = useAppSelector((state) => state.frame)
  const [isScoreModalOpen, setIsScoreModalOpen] = useState(false)

  const pollFrames = useCallback(async () => {
    if (!currentSerial) {
      toast.error("No device connected")
      router.push("/check")
      return
    }

    try {
      await dispatch(fetchFrames(currentSerial)).unwrap()
      dispatch(updateProgress())
    } catch (error) {
      if (typeof error === "string") {
        toast.error(error)
      }
    }
  }, [currentSerial, dispatch, router])

  useEffect(() => {
    // Initial fetch
    pollFrames()

    // Set up polling interval
    const interval = setInterval(() => {
      if (progress < 100) {
        pollFrames()
      } else {
        clearInterval(interval)
      }
    }, 5000) // Poll every 5 seconds

    return () => clearInterval(interval)
  }, [pollFrames, progress])

  const handleSegmentClick = (roundNumber: number) => {
    router.push(`/check/measure/round/${roundNumber}`)
  }

  const handleProgressButtonClick = async () => {
    if (progress === 100) {
      setIsScoreModalOpen(true)
    } else if (!isLoading && currentSerial) {
      try {
        await dispatch(skipFrame(currentSerial)).unwrap()
        dispatch(updateProgress())
        toast.success("Frame skipped")
      } catch (error) {
        if (typeof error === "string") {
          toast.error(error)
        } else {
          toast.error("Failed to skip frame")
        }
      }
    }
  }

  const handleScoreSubmit = (score: number) => {
    setIsScoreModalOpen(false)
    router.push(`/check/measure/game?score=${score}`)
  }

  // Redirect if no device is connected
  useEffect(() => {
    if (!currentSerial) {
      toast.error("No device connected")
      router.push("/check")
    }
  }, [currentSerial, router])

  return (
    <div className="min-h-screen bg-[#161622] p-6 pb-24">
      <div className="flex justify-between items-center mb-12">
        <ThemeToggle />
        <UserMenu />
      </div>

      <div className="max-w-md mx-auto space-y-12">
        <h1 className="text-3xl font-bold text-white text-center mb-8">Progress</h1>

        <div className="flex justify-center">
          <ProgressGauge progress={progress} onSegmentClick={handleSegmentClick} />
        </div>

        <ProgressButton progress={progress} onClick={handleProgressButtonClick} disabled={isLoading} />
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

