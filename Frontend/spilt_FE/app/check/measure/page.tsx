
// "use client"

// import { useState, useEffect, useCallback } from "react"
// import { useRouter } from "next/navigation"
// import ThemeToggle from "@/components/ui/theme-toggle"
// import { UserMenu } from "@/components/user-menu"
// import { NavigationBar } from "@/components/navigation-bar"
// import { ProgressGauge } from "@/components/measure/progress-gauge"
// import { ProgressButton } from "@/components/measure/progress-button"
// import { BowlingScoreModal } from "@/components/modals/bowling-score-modal"
// import { useAppDispatch, useAppSelector } from "@/app/store/hooks"
// import { fetchFrames, updateProgress, skipFrame } from "@/app/features/frameSlice"
// import { setBowlingScore } from "@/app/features/gameSlice"
// import { toast } from "sonner"

// export default function MeasurePage() {
//   const router = useRouter()
//   const dispatch = useAppDispatch()
//   const { currentSerial } = useAppSelector((state) => state.device)
//   const { frames, progress, error, isLoading } = useAppSelector((state) => state.frame)
//   const [isScoreModalOpen, setIsScoreModalOpen] = useState(false)
//   const [hasShownModal, setHasShownModal] = useState(() => {
//     // Check both modal state and existing score
//     return sessionStorage.getItem("hasShownScoreModal") === "true" || sessionStorage.getItem("bowlingScore") !== null
//   })

//   const pollFrames = useCallback(async () => {
//     if (!currentSerial) {
//       toast.error("No device connected")
//       router.push("/check")
//       return
//     }

//     try {
//       await dispatch(fetchFrames(currentSerial)).unwrap()
//       dispatch(updateProgress())
//     } catch (error) {
//       if (typeof error === "string") {
//         toast.error(error)
//       }
//     }
//   }, [currentSerial, dispatch, router])

//   useEffect(() => {
//     // Initial fetch
//     pollFrames()

//     // Set up polling interval
//     const interval = setInterval(() => {
//       if (progress < 100) {
//         pollFrames()
//       } else if (!hasShownModal) {
//         clearInterval(interval)
//         setIsScoreModalOpen(true)
//       }
//     }, 5000) // Poll every 5 seconds

//     return () => clearInterval(interval)
//   }, [pollFrames, progress, hasShownModal])

//   const handleSegmentClick = (roundNumber: number) => {
//     router.push(`/check/measure/round/${roundNumber}`)
//   }

//   const handleProgressButtonClick = async () => {
//     // Get existing score from sessionStorage
//     const existingScore = sessionStorage.getItem("bowlingScore")

//     if (progress === 100) {
//       if (existingScore) {
//         // If score exists, redirect directly to game page
//         router.push(`/check/measure/game?score=${existingScore}`)
//       } else if (!hasShownModal) {
//         // Only show modal if no score exists and modal hasn't been shown
//         setIsScoreModalOpen(true)
//       }
//     } else if (!isLoading && currentSerial) {
//       try {
//         console.log("Skipping frame for serial:", currentSerial)
//         await dispatch(skipFrame(currentSerial)).unwrap()
//         dispatch(updateProgress())
//         toast.success("Frame skipped")
//       } catch (error: any) {
//         console.error("Error skipping frame:", error)
//         // Handle 409 error specifically
//         if (error.response?.status === 409) {
//           toast.error("Maximum frame limit reached")
//         } else {
//           toast.error(typeof error === "string" ? error : "Failed to skip frame")
//         }
//       }
//     }
//   }

//   const handleScoreSubmit = (score: number) => {
//     console.log("Score submitted:", score)
//     dispatch(setBowlingScore(score))
//     setIsScoreModalOpen(false)
//     setHasShownModal(true)
//     // Store score in sessionStorage
//     sessionStorage.setItem("bowlingScore", score.toString())
//     sessionStorage.setItem("hasShownScoreModal", "true")
//     router.push(`/check/measure/game?score=${score}`)
//   }

//   // Redirect if no device is connected
//   useEffect(() => {
//     if (!currentSerial) {
//       toast.error("No device connected")
//       router.push("/check")
//     }
//   }, [currentSerial, router])

//   return (
//     <div className="min-h-screen bg-[#161622] p-6 pb-24">
//       <div className="flex justify-between items-center mb-12">
//         <ThemeToggle />
//         <UserMenu />
//       </div>

//       <div className="max-w-md mx-auto space-y-12">
//         <h1 className="text-3xl font-bold text-white text-center mb-8">Progress</h1>

//         <div className="flex justify-center">
//           <ProgressGauge progress={progress} onSegmentClick={handleSegmentClick} />
//         </div>

//         <ProgressButton progress={progress} onClick={handleProgressButtonClick} disabled={isLoading} />
//       </div>

//       <NavigationBar />

//       <BowlingScoreModal
//         isOpen={isScoreModalOpen}
//         onClose={() => {
//           setIsScoreModalOpen(false)
//           setHasShownModal(true)
//           sessionStorage.setItem("hasShownScoreModal", "true")
//         }}
//         onSubmit={handleScoreSubmit}
//       />
//     </div>
//   )
// }







"use client"

import { useState, useEffect, useCallback } from "react"
import { useRouter } from "next/navigation"
import ThemeToggle from "@/components/ui/theme-toggle"
import { UserMenu } from "@/components/user-menu"
import { NavigationBar } from "@/components/navigation-bar"
import { ProgressGauge } from "@/components/measure/progress-gauge"
import { ProgressButton } from "@/components/measure/progress-button"
import { BowlingScoreModal } from "@/components/modals/bowling-score-modal"
import { useAppDispatch, useAppSelector } from "@/app/store/hooks"
import { fetchFrames, updateProgress, skipFrame, setFrames } from "@/app/features/frameSlice"
import { setBowlingScore } from "@/app/features/gameSlice"
import { toast } from "sonner"

export default function MeasurePage() {
  const router = useRouter()
  const dispatch = useAppDispatch()
  const { currentSerial } = useAppSelector((state) => state.device)
  const { frames, progress, error, isLoading } = useAppSelector((state) => state.frame)
  const [isScoreModalOpen, setIsScoreModalOpen] = useState(false)
  const [hasShownModal, setHasShownModal] = useState(() => {
    if (typeof window !== 'undefined') {
      return sessionStorage.getItem("hasShownScoreModal") === "true" || sessionStorage.getItem("bowlingScore") !== null
    }
    return false
  })

  const pollFrames = useCallback(async () => {
    if (!currentSerial) {
      toast.error("장치가 연결되지 않았습니다")
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
    pollFrames()

    const interval = setInterval(() => {
      if (progress < 100) {
        pollFrames()
      } else if (!hasShownModal) {
        clearInterval(interval)
        setIsScoreModalOpen(true)
      }
    }, 5000)

    return () => clearInterval(interval)
  }, [pollFrames, progress, hasShownModal])

  useEffect(() => {
    if (typeof window !== 'undefined') {
      const storedFrameData = sessionStorage.getItem("frameData")
      if (storedFrameData) {
        dispatch(setFrames(JSON.parse(storedFrameData)))
      }
    }
  }, [dispatch])

  useEffect(() => {
    if (typeof window !== 'undefined') {
      sessionStorage.setItem("frameData", JSON.stringify(frames))
    }
  }, [frames])

  const handleProgressButtonClick = async () => {
    const existingScore = typeof window !== 'undefined' ? sessionStorage.getItem("bowlingScore") : null

    if (progress === 100) {
      if (existingScore) {
        router.push(`/check/measure/game?score=${existingScore}`)
      } else if (!hasShownModal) {
        setIsScoreModalOpen(true)
      }
    } else if (!isLoading && currentSerial) {
      try {
        await dispatch(skipFrame(currentSerial)).unwrap()
        dispatch(updateProgress())
        toast.success("프레임을 건너뛰었습니다")
      } catch (error: any) {
        console.error("프레임 건너뛰기 오류:", error)
        if (error.response?.status === 409) {
          toast.error("최대 프레임 수에 도달했습니다")
        } else {
          toast.error(typeof error === "string" ? error : "프레임 건너뛰기에 실패했습니다")
        }
      }
    }
  }

  const handleScoreSubmit = (score: number) => {
    dispatch(setBowlingScore(score))
    setIsScoreModalOpen(false)
    setHasShownModal(true)
    if (typeof window !== 'undefined') {
      sessionStorage.setItem("bowlingScore", score.toString())
      sessionStorage.setItem("hasShownScoreModal", "true")
    }
    router.push(`/check/measure/game?score=${score}`)
  }

  useEffect(() => {
    if (!currentSerial) {
      toast.error("장치가 연결되지 않았습니다")
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
        <h1 className="text-3xl font-bold text-white text-center mb-8">진행 상황</h1>

        <div className="flex justify-center">
          <ProgressGauge progress={progress} />
        </div>

        <ProgressButton progress={progress} onClick={handleProgressButtonClick} disabled={isLoading} />
      </div>

      <NavigationBar />

      <BowlingScoreModal
        isOpen={isScoreModalOpen}
        onClose={() => {
          setIsScoreModalOpen(false)
          setHasShownModal(true)
          if (typeof window !== 'undefined') {
            sessionStorage.setItem("hasShownScoreModal", "true")
          }
        }}
        onSubmit={handleScoreSubmit}
      />
    </div>
  )
}
