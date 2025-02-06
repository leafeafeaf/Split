"use client"

import type { UserRanking } from "@/types/ranking"

interface UserRankingCardProps {
  userRanking: UserRanking
}

export function UserRankingCard({ userRanking }: UserRankingCardProps) {
  return (
    <div className="p-6 rounded-lg bg-[#0066FF]/10 border border-[#0066FF]/20 mb-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="text-2xl font-bold text-white">#{userRanking.rank}</div>
          <div>
            <div className="font-medium text-white">{userRanking.name}</div>
            <div className="text-sm text-[#A2A2A7]">{userRanking.email}</div>
          </div>
        </div>
        <div className="flex gap-8">
          <div>
            <div className="text-sm text-[#A2A2A7]">Avg Score</div>
            <div className="text-xl font-bold text-white text-center">{userRanking.averageScore}</div>
          </div>
          <div>
            <div className="text-sm text-[#A2A2A7]">High Score</div>
            <div className="text-xl font-bold text-white text-center">{userRanking.highScore}</div>
          </div>
        </div>
      </div>
    </div>
  )
}

