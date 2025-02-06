"use client"

import { Card } from "@/components/ui/card"

interface StatItemProps {
  label: string
  value: string | number
  color: string
}

function StatItem({ label, value, color }: StatItemProps) {
  return (
    <div className="flex flex-col items-center">
      <span className="text-2xl font-medium mb-2" style={{ color }}>
        {label}
      </span>
      <span className="text-4xl font-bold text-white">{value}</span>
    </div>
  )
}

interface StatsGroupProps {
  items: StatItemProps[]
}

function StatsGroup({ items }: StatsGroupProps) {
  return (
    <Card className="bg-[#1E1E2D] p-6">
      <div className="grid grid-cols-2 gap-8">
        {items.map((item, index) => (
          <StatItem key={index} {...item} />
        ))}
      </div>
    </Card>
  )
}

interface PerformanceGroupProps {
  items: StatItemProps[]
}

function PerformanceGroup({ items }: PerformanceGroupProps) {
  return (
    <Card className="bg-[#1E1E2D] p-6">
      <div className="grid grid-cols-3 gap-8">
        {items.map((item, index) => (
          <StatItem key={index} {...item} />
        ))}
      </div>
    </Card>
  )
}

interface StatsSectionProps {
  scoreStats: StatItemProps[]
  performanceStats: StatItemProps[]
}

export function StatsSection({ scoreStats, performanceStats }: StatsSectionProps) {
  return (
    <div className="space-y-4">
      <h2 className="text-2xl text-white text-center font-medium">Statistics</h2>
      <div className="space-y-4">
        <StatsGroup items={scoreStats} />
        <PerformanceGroup items={performanceStats} />
      </div>
    </div>
  )
}

