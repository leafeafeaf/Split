"use client"

import { useState, useEffect, Suspense } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import ThemeToggle from "@/components/ui/theme-toggle"
import SmallBowlingBall from "@/components/small-bowling-ball"
import { AnalysisRadarChart } from "@/components/charts/radar-chart"
import { ButtonPrimary } from "@/components/ui/button-primary"
import { HighlightWarningModal } from "@/components/modals/highlight-warning-modal"
import { useAppDispatch, useAppSelector } from "@/app/store/hooks"
import { clearGameData, setBowlingScore, uploadGame } from "@/app/features/gameSlice"
import { clearFrames, setFrames } from "@/app/features/frameSlice"
import { toast } from "sonner"
import type { FrameData } from "@/types/frame"

function GameContent() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const dispatch = useAppDispatch()
  const { frames } = useAppSelector((state) => state.frame)
  const { isLoading, error, bowlingScore } = useAppSelector((state) => state.game)
  const [showWarningModal, setShowWarningModal] = useState(false)
  const [warningAction, setWarningAction] = useState<"complete" | "next">()
  const [isInitialized, setIsInitialized] = useState(false)

  // 프레임 데이터 유지를 위한 sessionStorage 처리
  useEffect(() => {
    if (!isInitialized) {
      setIsInitialized(true)

      // 세션 스토리지에서 프레임 데이터 복원
      const savedFrames = sessionStorage.getItem('frameData')
      if (savedFrames) {
        try {
          const parsedFrames = JSON.parse(savedFrames) as FrameData[]
          if (Array.isArray(parsedFrames) && parsedFrames.length === 10) {
            // Redux 상태에 프레임 데이터 복원
            dispatch(setFrames(parsedFrames))
          }
        } catch (e) {
          console.error('Failed to parse saved frames:', e)
        }
      }

      // URL에서 점수 파라미터 확인
      const scoreParam = searchParams.get('score')
      if (scoreParam) {
        dispatch(setBowlingScore(parseInt(scoreParam, 10)))
      }

      // 프레임 데이터가 없으면 리다이렉트
      if (!savedFrames || frames.length !== 10) {
        toast.error("No complete frame data available")
        router.push("/check/measure")
        return
      }

      // 점수가 없으면 measure 페이지로 리다이렉트
      if (!scoreParam) {
        router.push("/check/measure")
        return
      }
    }
  }, [isInitialized, dispatch, searchParams, frames.length, router])

  // 프레임 데이터 저장
  useEffect(() => {
    if (frames.length === 10) {
      sessionStorage.setItem('frameData', JSON.stringify(frames))
    }
  }, [frames])

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
      // 요청 데이터 로깅
      console.log("요청 데이터:", {
        데이터: { frames, bowlingScore },
        프레임수: frames.length,
        볼링점수: bowlingScore,
      })

      // 게임 통계 로깅
      console.log("게임 통계:", {
        각도: gameStats.elbowAngleScore,
        안정성: gameStats.armStabilityScore,
        속도: gameStats.speed,
        자세점수: gameStats.poseScore,
      })

      await dispatch(uploadGame({ frames, bowlingScore })).unwrap()
      dispatch(clearFrames())
      dispatch(clearGameData())
      // 세션 스토리지 초기화
      sessionStorage.removeItem("hasShownScoreModal")
      sessionStorage.removeItem("bowlingScore")
      sessionStorage.removeItem("frameData")

      if (warningAction === "complete") {
        router.push("/home")
      } else if (warningAction === "next") {
        router.push("/check")
      }
    } catch (error: any) {
      // 에러 상세 로깅
      console.error("API 요청 실패:", {
        에러: error,
        상태: error.response?.status,
        메시지: error.response?.data,
      })
      toast.error(error.toString())
    } finally {
      setShowWarningModal(false)
    }
  }

  // 초기화 전이거나 게임 통계가 없는 경우 로딩 상태 표시
  if (!isInitialized || !gameStats) {
    return (
      <div className="min-h-screen bg-[#161622] flex items-center justify-center">
        <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-[#0066FF] border-r-transparent"></div>
      </div>
    )
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
    </div>
  )
}

export default function GameEvaluationPage() {
  return (
    <Suspense 
      fallback={
        <div className="min-h-screen bg-[#161622] flex items-center justify-center">
          <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-[#0066FF] border-r-transparent"></div>
        </div>
      }
    >
      <GameContent />
    </Suspense>
  )
}






// "use client"

// import { useState, useEffect } from "react"
// import { useRouter, useSearchParams } from "next/navigation"
// import ThemeToggle from "@/components/ui/theme-toggle"
// import SmallBowlingBall from "@/components/small-bowling-ball"
// import { AnalysisRadarChart } from "@/components/charts/radar-chart"
// import { ButtonPrimary } from "@/components/ui/button-primary"
// import { HighlightWarningModal } from "@/components/modals/highlight-warning-modal"
// import { useAppDispatch, useAppSelector } from "@/app/store/hooks"
// import { clearGameData, setBowlingScore, uploadGame } from "@/app/features/gameSlice"
// import { clearFrames } from "@/app/features/frameSlice"
// import { toast } from "sonner"

// export default function GameEvaluationPage() {
//   const router = useRouter()
//   const searchParams = useSearchParams()
//   const dispatch = useAppDispatch()
//   const { frames } = useAppSelector((state) => state.frame)
//   const { isLoading, error, bowlingScore } = useAppSelector((state) => state.game)
//   const [showWarningModal, setShowWarningModal] = useState(false)
//   const [warningAction, setWarningAction] = useState<"complete" | "next">()
//   const [isInitialized, setIsInitialized] = useState(false)

//   // 프레임 데이터 로깅
//   useEffect(() => {
//     console.log("현재 프레임 상태:", {
//       프레임수: frames.length,
//       프레임데이터: frames,
//     })
//   }, [frames])

//   // Calculate statistics from frames
//   const gameStats =
//     frames.length === 10
//       ? {
//           poseScore: frames.reduce((sum, frame) => sum + frame.poseScore, 0) / frames.length,
//           elbowAngleScore: frames.reduce((sum, frame) => sum + frame.elbowAngleScore, 0) / frames.length,
//           armStabilityScore: frames.reduce((sum, frame) => sum + frame.armStabilityScore, 0) / frames.length,
//           speed: frames.reduce((sum, frame) => sum + frame.speed, 0) / frames.length,
//         }
//       : null

//   // 초기화 및 리다이렉트 처리
//   useEffect(() => {
//     if (!isInitialized) {
//       setIsInitialized(true)

//       // URL에서 점수 파라미터 확인
//       const scoreParam = searchParams.get('score')
//       if (scoreParam) {
//         dispatch(setBowlingScore(parseInt(scoreParam, 10)))
//       }

//       if (frames.length !== 10) {
//         toast.error("No complete frame data available")
//         router.push("/check/measure")
//         return
//       }

//       // 점수가 없으면 measure 페이지로 리다이렉트
//       if (!scoreParam) {
//         router.push("/check/measure")
//         return
//       }
//     }
//   }, [frames.length, router, isInitialized, dispatch, searchParams])

//   const handleComplete = () => {
//     setWarningAction("complete")
//     setShowWarningModal(true)
//   }

//   const handleNext = () => {
//     setWarningAction("next")
//     setShowWarningModal(true)
//   }

//   const handleBack = () => {
//     router.push("/check/measure")
//   }

//   const handleWarningConfirm = async () => {
//     if (!gameStats || bowlingScore === null) return

//     try {
//       // 요청 데이터 로깅
//       console.log("요청 데이터:", {
//         데이터: { frames, bowlingScore },
//         프레임수: frames.length,
//         볼링점수: bowlingScore,
//       })

//       // 게임 통계 로깅
//       console.log("게임 통계:", {
//         각도: gameStats.elbowAngleScore,
//         안정성: gameStats.armStabilityScore,
//         속도: gameStats.speed,
//         자세점수: gameStats.poseScore,
//       })

//       await dispatch(uploadGame({ frames, bowlingScore })).unwrap()
//       dispatch(clearFrames())
//       dispatch(clearGameData())
//       // 세션 스토리지 초기화
//       sessionStorage.removeItem("hasShownScoreModal")

//       if (warningAction === "complete") {
//         router.push("/home")
//       } else if (warningAction === "next") {
//         router.push("/check")
//       }
//     } catch (error: any) {
//       // 에러 상세 로깅
//       console.error("API 요청 실패:", {
//         에러: error,
//         상태: error.response?.status,
//         메시지: error.response?.data,
//       })
//       toast.error(error.toString())
//     } finally {
//       setShowWarningModal(false)
//     }
//   }

//   // 초기화 전이거나 게임 통계가 없는 경우 로딩 상태 표시
//   if (!isInitialized || !gameStats) {
//     return (
//       <div className="min-h-screen bg-[#161622] flex items-center justify-center">
//         <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-[#0066FF] border-r-transparent"></div>
//       </div>
//     )
//   }

//   return (
//     <div className="min-h-screen bg-[#161622] p-6">
//       <div className="flex justify-between items-center mb-8">
//         <ThemeToggle />
//         <SmallBowlingBall />
//       </div>

//       <div className="max-w-md mx-auto space-y-8">
//         <AnalysisRadarChart
//           data={{
//             angle: gameStats.elbowAngleScore,
//             stability: gameStats.armStabilityScore,
//             speed: gameStats.speed,
//           }}
//         />

//         <div className="space-y-4">
//           <h2 className="text-2xl text-white text-center font-medium">Statistics</h2>
//           <div className="grid grid-cols-3 gap-4 mb-4">
//             <div className="text-center">
//               <p className="text-[#0066FF] text-xl">Angle</p>
//               <p className="text-white text-2xl tabular-nums">{gameStats.elbowAngleScore.toFixed(2)}</p>
//             </div>
//             <div className="text-center">
//               <p className="text-[#0066FF] text-xl">Stability</p>
//               <p className="text-white text-2xl tabular-nums">{gameStats.armStabilityScore.toFixed(2)}</p>
//             </div>
//             <div className="text-center">
//               <p className="text-[#0066FF] text-xl">Speed</p>
//               <p className="text-white text-2xl tabular-nums">{gameStats.speed.toFixed(2)}</p>
//             </div>
//           </div>
//         </div>

//         <div className="bg-[#1E1E2D] rounded-lg p-4">
//           <div className="text-center">
//             <p className="text-[#9CB1D1] text-xl">Game Score</p>
//             <p className="text-white text-2xl tabular-nums">{gameStats.poseScore.toFixed(2)}</p>
//           </div>
//         </div>

//         {bowlingScore !== null && (
//           <div className="bg-[#1E1E2D] rounded-lg p-4">
//             <div className="text-center">
//               <p className="text-[#ECE929] text-xl">Bowling Score</p>
//               <p className="text-white text-2xl">{bowlingScore}</p>
//             </div>
//           </div>
//         )}

//         <div className="space-y-4 pt-8">
//           <ButtonPrimary onClick={handleComplete} className="w-full py-4" disabled={isLoading || bowlingScore === null}>
//             {isLoading ? "Uploading..." : "측정 완료"}
//           </ButtonPrimary>
//           <ButtonPrimary onClick={handleBack} className="w-full py-4" disabled={isLoading}>
//             돌아가기
//           </ButtonPrimary>
//           <ButtonPrimary onClick={handleNext} className="w-full py-4" disabled={isLoading || bowlingScore === null}>
//             다음 경기 측정
//           </ButtonPrimary>
//         </div>
//       </div>

//       <HighlightWarningModal
//         isOpen={showWarningModal}
//         onClose={() => setShowWarningModal(false)}
//         onConfirm={handleWarningConfirm}
//       />
//     </div>
//   )
// }