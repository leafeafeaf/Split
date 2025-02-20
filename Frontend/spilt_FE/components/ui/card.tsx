import type React from "react"

interface CardProps {
  children: React.ReactNode
  className?: string
}

export function Card({ children, className = "" }: CardProps) {
  return <div className={`bg-[#1E1E2D] rounded-lg p-4 ${className}`}>{children}</div>
}

