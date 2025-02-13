"use client"

import { Card } from "@/components/ui/card"

interface BowlingStatItemProps {
  label: string
  value: string
  color: string
}

interface BowlingStatsProps {
  stats: BowlingStatItemProps[]
}

export function BowlingStats({ stats }: BowlingStatsProps) {
  return (
    <div className="space-y-4">
      <h2 className="text-2xl text-white text-center font-medium">Bowling Statistics</h2>
      <Card className="bg-[#1E1E2D] p-6">
        <div className="grid grid-cols-2 gap-8">
          {stats.map((stat, index) => (
            <div key={index} className="flex flex-col items-center">
              <span className="text-2xl font-medium mb-2" style={{ color: stat.color }}>
                {stat.label}
              </span>
              <span className="text-5xl font-bold text-white">{stat.value}</span>
            </div>
          ))}
        </div>
      </Card>
    </div>
  )
}

