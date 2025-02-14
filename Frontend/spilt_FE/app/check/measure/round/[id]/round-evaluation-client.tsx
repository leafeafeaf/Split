"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { ChevronLeft, Download } from "lucide-react"
import ThemeToggle from "@/components/ui/theme-toggle"
import SmallBowlingBall from "@/components/small-bowling-ball"
import { AnalysisRadarChart } from "@/components/charts/radar-chart"
import { Card } from "@/components/ui/card"
import { ButtonPrimary } from "@/components/ui/button-primary"

// Mock data (replace with actual API calls in production)
const mockRoundData = {
  roundNumber: 3,
  angle: 75,
  stability: 85,
  speed: 90,
  score: 180,
  aiComment: "안정적인 자세로 투구했습니다. 스피드를 조금 더 올리면 좋을 것 같습니다.",
  highlightVideoUrl: "/placeholder-video.mp4",
}

export default function RoundEvaluationClient({ params }: { params: { id: string } }) {
  const router = useRouter()
  const [isSaving, setIsSaving] = useState(false)
  const [isHighlightSetting, setIsHighlightSetting] = useState(false)

  const handleBack = () => router.back()

  const handleSaveVideo = async () => {
    setIsSaving(true)
    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 1500))
    setIsSaving(false)
    alert("Video saved successfully!")
  }

  const handleSetHighlight = async () => {
    setIsHighlightSetting(true)
    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 1500))
    setIsHighlightSetting(false)
    alert("Highlight set successfully!")
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
        <h1 className="text-3xl font-bold text-white text-center">{mockRoundData.roundNumber} Set</h1>

        <AnalysisRadarChart data={mockRoundData} />

        <div className="space-y-4">
          <h2 className="text-2xl text-white text-center font-medium">Statistics</h2>
          <div className="grid grid-cols-3 gap-4">
            <div className="bg-[#1E1E2D] p-4 rounded-lg">
              <p className="text-[#9CB1D1] text-center text-sm">Angle</p>
              <p className="text-2xl font-bold text-white text-center">{mockRoundData.angle}</p>
            </div>
            <div className="bg-[#1E1E2D] p-4 rounded-lg">
              <p className="text-[#9CB1D1] text-center text-sm">Stability</p>
              <p className="text-2xl font-bold text-white text-center">{mockRoundData.stability}</p>
            </div>
            <div className="bg-[#1E1E2D] p-4 rounded-lg">
              <p className="text-[#9CB1D1] text-center text-sm">Score</p>
              <p className="text-2xl font-bold text-white text-center">{mockRoundData.score}</p>
            </div>
          </div>
        </div>

        <Card className="bg-[#1E1E2D] p-6">
          <div className="space-y-4 text-center">
            <p className="text-red-500 text-xl mb-2">AI Comment</p>
            <p className="text-white text-lg">{mockRoundData.aiComment}</p>
          </div>
        </Card>

        <div className="space-y-4">
          <h2 className="text-2xl text-white text-center font-medium">Highlight</h2>
          <Card className="bg-[#1E1E2D] p-6 space-y-4">
            <div className="aspect-video rounded-lg overflow-hidden bg-black">
              <video src={mockRoundData.highlightVideoUrl} controls className="w-full h-full object-contain" />
            </div>
            <ButtonPrimary onClick={handleSaveVideo} disabled={isSaving} className="w-full">
              {isSaving ? "Saving..." : "Save Video"}
              <Download className="ml-2 h-4 w-4" />
            </ButtonPrimary>
          </Card>
        </div>

        <ButtonPrimary onClick={handleSetHighlight} disabled={isHighlightSetting} className="w-full py-4">
          {isHighlightSetting ? "Setting..." : "하이라이트 영상 지정"}
        </ButtonPrimary>
      </div>
    </div>
  )
}

