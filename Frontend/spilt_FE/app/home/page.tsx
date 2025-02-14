"use client"

import ThemeToggle from "@/components/ui/theme-toggle"
import { UserMenu } from "@/components/user-menu"
import { NavigationBar } from "@/components/navigation-bar"
import { AnalysisRadarChart } from "@/components/charts/radar-chart"
import { ScoreChart } from "@/components/charts/score-chart"
import { StatsSection } from "@/components/statistics/stats-section"
import { BowlingStats } from "@/components/statistics/bowling-stats"
import { HighlightPlayer } from "@/components/video/highlight-player"

// Mock user data
const mockUserData = {
  nickname: "James",
  gender: "Male",
  height: 180,
}

// Mock analysis data
const mockAnalysisData = {
  angle: 75,
  stability: 85,
  speed: 90,
}

// Mock score data
const mockScoreData = [
  { date: "2024-02-01", score: 65 },
  { date: "2024-02-02", score: 78 },
  { date: "2024-02-03", score: 45 },
  { date: "2024-02-04", score: 89 },
  { date: "2024-02-05", score: 72 },
  { date: "2024-02-06", score: 93 },
]

// Mock stats data
const mockScoreStats = [
  { label: "average Score", value: "36.00", color: "#2D3EF9" },
  { label: "High Score", value: "36.00", color: "#2D3EF9" },
]

const mockPerformanceStats = [
  { label: "Angle", value: "36.00", color: "#9CB1D1" },
  { label: "Stability", value: "36.00", color: "#9CB1D1" },
  { label: "Speed", value: "36.00", color: "#9CB1D1" },
]

// Mock bowling stats
const mockBowlingStats = [
  { label: "Average Score", value: "154", color: "#ECE929" },
  { label: "Current Score", value: "160", color: "#ECE929" },
]

export default function HomePage() {
  const handleLogout = () => {
    // Add your logout logic here
    console.log("Logging out...")
  }

  const handleDownload = () => {
    // Add your download logic here
    console.log("Downloading video...")
  }

  return (
    <div className="min-h-screen bg-[#161622] p-6 pb-24">
      <div className="flex justify-between items-center mb-8">
        <ThemeToggle />
        <UserMenu userData={mockUserData} onLogout={handleLogout} />
      </div>

      <div className="space-y-6 max-w-md mx-auto">
        <AnalysisRadarChart data={mockAnalysisData} />
        <ScoreChart data={mockScoreData} />
        <StatsSection scoreStats={mockScoreStats} performanceStats={mockPerformanceStats} />
        <BowlingStats stats={mockBowlingStats} />
        <HighlightPlayer videoUrl="/placeholder-video.mp4" onDownload={handleDownload} />
      </div>

      <NavigationBar />
    </div>
  )
}

