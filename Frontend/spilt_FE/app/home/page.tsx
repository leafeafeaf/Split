"use client"

import { useEffect } from "react"
import ThemeToggle from "@/components/ui/theme-toggle"
import { UserMenu } from "@/components/user-menu"
import { NavigationBar } from "@/components/navigation-bar"
import { AnalysisRadarChart } from "@/components/charts/radar-chart"
import { ScoreChart } from "@/components/charts/score-chart"
import { StatsSection } from "@/components/statistics/stats-section"
import { BowlingStats } from "@/components/statistics/bowling-stats"
import { HighlightPlayer } from "@/components/video/highlight-player"
import { useUserStore } from "@/lib/user"
import { Card } from "@/components/ui/card"

export default function HomePage() {
  const { user, isLoading, fetchUser } = useUserStore()

  useEffect(() => {
    fetchUser()
  }, [fetchUser])

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#161622] p-6 pb-24">
        <div className="flex justify-between items-center mb-8">
          <ThemeToggle />
          <UserMenu />
        </div>
        <div className="flex items-center justify-center h-[calc(100vh-200px)]">
          <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-[#0066FF] border-r-transparent"></div>
        </div>
      </div>
    )
  }

  if (!user) {
    return (
      <div className="min-h-screen bg-[#161622] p-6 pb-24">
        <div className="flex justify-between items-center mb-8">
          <ThemeToggle />
          <UserMenu />
        </div>
        <div className="flex items-center justify-center h-[calc(100vh-200px)]">
          <Card className="p-6 bg-[#1E1E2D]">
            <p className="text-white">Failed to load user data. Please try again later.</p>
          </Card>
        </div>
      </div>
    )
  }

  const handleDownload = () => {
    const link = document.createElement("a")
    link.href = user.highlight
    link.download = `highlight-${user.nickname}.mp4`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  return (
    <div className="min-h-screen bg-[#161622] p-6 pb-24">
      <div className="flex justify-between items-center mb-8">
        <ThemeToggle />
        <UserMenu />
      </div>

      <div className="space-y-6 max-w-md mx-auto">
        <AnalysisRadarChart
          data={{
            angle: user.elbowAngleScore,
            stability: user.armStabilityScore,
            speed: user.armSpeedScore,
          }}
        />
        <ScoreChart />
        <StatsSection
          scoreStats={[
            { label: "Average Score", value: user.totalPoseAvgscore.toFixed(1), color: "#2D3EF9" },
            { label: "High Score", value: user.totalPoseHighscore.toFixed(1), color: "#2D3EF9" },
          ]}
          performanceStats={[
            { label: "Angle", value: user.elbowAngleScore.toFixed(1), color: "#9CB1D1" },
            { label: "Stability", value: user.armStabilityScore.toFixed(1), color: "#9CB1D1" },
            { label: "Speed", value: user.armSpeedScore.toFixed(1), color: "#9CB1D1" },
          ]}
        />
        <BowlingStats
          stats={[
            { label: "Average Score", value: user.avgBowlingScore.toString(), color: "#ECE929" },
            { label: "Current Score", value: user.currBowlingScore.toString(), color: "#ECE929" },
          ]}
        />
        <HighlightPlayer videoUrl={user.highlight} onDownload={handleDownload} />
      </div>

      <NavigationBar />
    </div>
  )
}

