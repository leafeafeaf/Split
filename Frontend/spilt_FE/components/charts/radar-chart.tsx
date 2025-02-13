"use client"

import { Radar, RadarChart, PolarGrid, PolarAngleAxis, ResponsiveContainer } from "recharts"
import { Card } from "@/components/ui/card"

interface RadarChartProps {
  data: {
    angle: number
    stability: number
    speed: number
  }
}

export function AnalysisRadarChart({ data }: RadarChartProps) {
  const chartData = [
    { subject: "Speed", value: data.speed },
    { subject: "Stability", value: data.stability },
    { subject: "Angle", value: data.angle },
  ]

  return (
    <div className="space-y-4">
      <h2 className="text-2xl text-white text-center font-medium">Analysis Graph</h2>
      <Card className="p-6 bg-[#1E1E2D] aspect-square relative overflow-hidden">
        <ResponsiveContainer width="100%" height="100%">
          <RadarChart data={chartData}>
            <PolarGrid gridType="circle" stroke="rgba(255, 255, 255, 0.1)" strokeWidth={1} radialLines={false} />
            <PolarAngleAxis
              dataKey="subject"
              tick={{
                fill: "white",
                fontSize: 16,
                fontWeight: 500,
              }}
              axisLine={false}
              tickLine={false}
            />
            <Radar
              name="Stats"
              dataKey="value"
              stroke="#0066FF"
              strokeWidth={2}
              fill="#0066FF"
              fillOpacity={0.15}
              dot={(props) => {
                const { cx, cy } = props
                return (
                  <g>
                    <circle cx={cx} cy={cy} r={4} fill="white" stroke="#0066FF" strokeWidth={2} />
                  </g>
                )
              }}
            />
          </RadarChart>
        </ResponsiveContainer>
      </Card>
    </div>
  )
}

