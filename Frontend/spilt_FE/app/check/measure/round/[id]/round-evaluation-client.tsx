"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { ChevronLeft, Download } from "lucide-react"
import ThemeToggle from "@/components/ui/theme-toggle"
import SmallBowlingBall from "@/components/small-bowling-ball"
import { AnalysisRadarChart } from "@/components/charts/radar-chart"
import { Card } from "@/components/ui/card"
import { ButtonPrimary } from "@/components/ui/button-primary"
import { useAppDispatch, useAppSelector } from "@/app/store/hooks"
import { fetchSingleFrame, clearCurrentFrame } from "@/app/features/frameSlice"
import { updateHighlight } from "@/app/features/userSlice"
import { HighlightWarningModal } from "@/components/modals/highlight-warning-modal"
import { toast } from "sonner"

export default function RoundEvaluationClient({ params }: { params: { id: string } }) {
  const router = useRouter()
  const dispatch = useAppDispatch()
  const { currentSerial } = useAppSelector((state) => state.device)
  const { currentFrame, isLoading, error } = useAppSelector((state) => state.frame)
  const { user, highlightUpdateLoading } = useAppSelector((state) => state.user)
  const [isSaving, setIsSaving] = useState(false)
  const [showWarningModal, setShowWarningModal] = useState(false)

  useEffect(() => {
    if (!currentSerial) {
      toast.error("No device connected")
      router.push("/check")
      return
    }

    const frameNum = Number.parseInt(params.id)
    dispatch(fetchSingleFrame({ serial: currentSerial, frameNum }))

    return () => {
      dispatch(clearCurrentFrame())
    }
  }, [currentSerial, params.id, dispatch, router])

  const handleBack = () => router.back()

  const handleSaveVideo = async () => {
    if (!currentFrame?.video) return

    setIsSaving(true)
    try {
      const link = document.createElement("a")
      link.href = currentFrame.video
      link.download = `frame-${currentFrame.num}.mp4`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      toast.success("Video saved successfully!")
    } catch (error) {
      toast.error("Failed to save video")
    } finally {
      setIsSaving(false)
    }
  }

  const handleSetHighlight = () => {
    setShowWarningModal(true)
  }

  const handleHighlightConfirm = async () => {
    if (!currentFrame?.video || !user) return

    try {
      // Check if user already has a highlight video
      const isUpdate = !!user.highlight
      await dispatch(updateHighlight({ highlight: currentFrame.video, isUpdate })).unwrap()
      toast.success(`Highlight video ${isUpdate ? "updated" : "set"} successfully!`)
      setShowWarningModal(false)
    } catch (error) {
      if (typeof error === "string") {
        toast.error(error)
      } else {
        toast.error("Failed to set highlight video")
      }
    }
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#161622] p-6 flex items-center justify-center">
        <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-[#0066FF] border-r-transparent"></div>
      </div>
    )
  }

  if (error || !currentFrame) {
    return (
      <div className="min-h-screen bg-[#161622] p-6">
        <button onClick={handleBack} className="text-white hover:text-[#A2A2A7] transition-colors">
          <ChevronLeft className="h-8 w-8" />
        </button>
        <div className="flex items-center justify-center h-[calc(100vh-100px)]">
          <Card className="p-6 bg-[#1E1E2D]">
            <p className="text-white">{error || "Failed to load frame data"}</p>
          </Card>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-[#161622] p-6 pb-24">
      <div className="flex justify-between items-center mb-8">
        <button onClick={handleBack} className="text-white hover:text-[#A2A2A7] transition-colors">
          <ChevronLeft className="h-8 w-8" />
        </button>
        <div className="flex items-center space-x-4">
          <ThemeToggle />
          <SmallBowlingBall />
        </div>
      </div>

      <div className="max-w-2xl mx-auto space-y-8">
        <h1 className="text-3xl font-bold text-white text-center">{currentFrame.num} Set</h1>

        <AnalysisRadarChart
          data={{
            angle: currentFrame.elbowAngleScore,
            stability: currentFrame.armStabilityScore,
            speed: currentFrame.speed,
          }}
        />

        <div className="space-y-4">
          <h2 className="text-2xl text-white text-center font-medium">Statistics</h2>
          <div className="grid grid-cols-3 gap-4">
            <div className="bg-[#1E1E2D] p-4 rounded-lg">
              <p className="text-[#9CB1D1] text-center text-sm">Angle</p>
              <p className="text-2xl font-bold text-white text-center">{currentFrame.elbowAngleScore}</p>
            </div>
            <div className="bg-[#1E1E2D] p-4 rounded-lg">
              <p className="text-[#9CB1D1] text-center text-sm">Stability</p>
              <p className="text-2xl font-bold text-white text-center">{currentFrame.armStabilityScore}</p>
            </div>
            <div className="bg-[#1E1E2D] p-4 rounded-lg">
              <p className="text-[#9CB1D1] text-center text-sm">Speed</p>
              <p className="text-2xl font-bold text-white text-center">{currentFrame.speed}</p>
            </div>
          </div>
        </div>

        <Card className="bg-[#1E1E2D] p-6 mb-4">
          <div className="text-center">
            <p className="text-[#9CB1D1] text-xl mb-2">Score</p>
            <p className="text-4xl font-bold text-white">{currentFrame.poseScore}</p>
          </div>
        </Card>

        <Card className="bg-[#1E1E2D] p-6">
          <div className="space-y-4 text-center">
            <p className="text-red-500 text-xl mb-2">AI Comment</p>
            <p className="text-white text-lg">{currentFrame.feedback}</p>
          </div>
        </Card>

        <div className="space-y-4">
          <h2 className="text-2xl text-white text-center font-medium">Highlight</h2>
          <Card className="bg-[#1E1E2D] p-6 space-y-4">
            <div className="aspect-video rounded-lg overflow-hidden bg-black">
              <video src={currentFrame.video} controls className="w-full h-full object-contain" />
            </div>
            <ButtonPrimary onClick={handleSaveVideo} disabled={isSaving} className="w-full">
              {isSaving ? "Saving..." : "Save Video"}
              <Download className="ml-2 h-4 w-4" />
            </ButtonPrimary>
          </Card>
        </div>

        <ButtonPrimary onClick={handleSetHighlight} disabled={highlightUpdateLoading} className="w-full py-4">
          {highlightUpdateLoading
            ? "Setting..."
            : `${user?.highlight ? "하이라이트 영상 수정" : "하이라이트 영상 지정"}`}
        </ButtonPrimary>
      </div>

      <HighlightWarningModal
        isOpen={showWarningModal}
        onClose={() => setShowWarningModal(false)}
        onConfirm={handleHighlightConfirm}
      />
    </div>
  )
}

