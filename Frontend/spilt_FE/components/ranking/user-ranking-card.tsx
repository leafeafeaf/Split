"use client"

import type { RankingData } from "@/types/ranking"
import { format } from "date-fns"

interface UserRankingCardProps {
  userRanking: RankingData
}

export function UserRankingCard({ userRanking }: UserRankingCardProps) {
  return (
    <div className="p-6 rounded-lg bg-[#0066FF]/10 border border-[#0066FF]/20 mb-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="text-2xl font-bold text-white">{userRanking.nickname}</div>
          <div className="text-sm text-[#A2A2A7]">{format(new Date(userRanking.gameDate), "yyyy-MM-dd")}</div>
        </div>
        <div className="flex gap-8">
          <div>
            <div className="text-sm text-[#A2A2A7]">Avg Score</div>
            <div className="text-xl font-bold text-white text-center">{userRanking.poseAvgscore.toFixed(1)}</div>
          </div>
          <div>
            <div className="text-sm text-[#A2A2A7]">High Score</div>
            <div className="text-xl font-bold text-white text-center">{userRanking.poseHighscore.toFixed(1)}</div>
          </div>
        </div>
      </div>
    </div>
  )
}

