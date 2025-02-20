import { Card } from "@/components/ui/card"

interface StatItemProps {
  label: string
  value: string | number
  color?: string
}

function StatItem({ label, value, color = "#5C8DD7" }: StatItemProps) {
  return (
    <div className="flex flex-col items-center">
      <span className="text-sm" style={{ color }}>
        {label}
      </span>
      <span className="text-xl font-bold" style={{ color }}>
        {value}
      </span>
    </div>
  )
}

interface StatsGridProps {
  stats: {
    label: string
    value: string | number
    color?: string
  }[]
}

export function StatsGrid({ stats }: StatsGridProps) {
  return (
    <Card className="w-full">
      <div className="grid grid-cols-3 gap-4">
        {stats.map((stat, index) => (
          <StatItem key={index} label={stat.label} value={stat.value} color={stat.color} />
        ))}
      </div>
    </Card>
  )
}

