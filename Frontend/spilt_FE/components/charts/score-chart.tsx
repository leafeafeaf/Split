"use client"

import { useState } from "react"
import { format } from "date-fns"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { LineChart, Line, XAxis, YAxis, ResponsiveContainer, Tooltip, CartesianGrid } from "recharts"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"

interface ScoreData {
  date: string // ISO date string
  score: number
}

interface ScoreChartProps {
  data: ScoreData[]
}

export function ScoreChart({ data }: ScoreChartProps) {
  const [startIndex, setStartIndex] = useState(0)
  const visibleCount = 6
  const maxStartIndex = Math.max(0, data.length - visibleCount)

  const handlePrevious = () => {
    setStartIndex(Math.max(0, startIndex - 1))
  }

  const handleNext = () => {
    setStartIndex(Math.min(maxStartIndex, startIndex + 1))
  }

  const visibleData = data.slice(startIndex, startIndex + visibleCount)

  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-[#1E1E2D] px-3 py-2 rounded-lg border border-[#2E2E3D] shadow-lg">
          <p className="text-white">{format(new Date(payload[0].payload.date), "MM-dd")}</p>
          <p className="text-[#0066FF] font-bold">Score: {payload[0].value}</p>
        </div>
      )
    }
    return null
  }

  return (
    <div className="space-y-4">
      <h2 className="text-2xl text-white text-center font-medium">Score Statistics</h2>

      <Card className="p-6 bg-[#1E1E2D] relative h-[300px]">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={visibleData} margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
            <CartesianGrid stroke="rgba(255,255,255,0.1)" vertical={false} />
            <XAxis
              dataKey="date"
              stroke="white"
              tickLine={false}
              axisLine={false}
              tick={{ fill: "white", fontSize: 12 }}
              tickFormatter={(date) => format(new Date(date), "MM-dd")}
            />
            <YAxis
              stroke="white"
              tickLine={false}
              axisLine={false}
              tick={{ fill: "white", fontSize: 12 }}
              domain={[0, 100]}
              ticks={[0, 25, 50, 75, 100]}
            />
            <Tooltip content={<CustomTooltip />} />
            <Line
              type="monotone"
              dataKey="score"
              stroke="#0066FF"
              strokeWidth={3}
              dot={{
                fill: "white",
                stroke: "#0066FF",
                strokeWidth: 2,
                r: 4,
              }}
              activeDot={{
                fill: "#0066FF",
                stroke: "white",
                strokeWidth: 2,
                r: 6,
              }}
            />
          </LineChart>
        </ResponsiveContainer>

        {/* Navigation buttons */}
        <div className="absolute inset-x-0 bottom-0 flex justify-between px-4 pb-2">
          <Button
            variant="ghost"
            size="icon"
            onClick={handlePrevious}
            disabled={startIndex === 0}
            className="text-white hover:bg-white/10"
          >
            <ChevronLeft className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            onClick={handleNext}
            disabled={startIndex >= maxStartIndex}
            className="text-white hover:bg-white/10"
          >
            <ChevronRight className="h-4 w-4" />
          </Button>
        </div>
      </Card>
    </div>
  )
}

