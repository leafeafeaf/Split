"use client"

import { SortAscendingIcon, SortDescendingIcon } from "@/components/icons/sort-icons"
import type { SortField, SortOrder } from "@/types/ranking"

interface RankingHeaderProps {
  sortField: SortField
  sortOrder: SortOrder
  onSortFieldChange: (field: SortField) => void
}

export function RankingHeader({ sortField, sortOrder, onSortFieldChange }: RankingHeaderProps) {
  return (
    <div className="flex items-center justify-between px-2 mb-6">
      <div className="grid grid-cols-4 w-full text-sm">
        <div className="text-[#A2A2A7]">Rank</div>
        <div className="text-[#A2A2A7]">User</div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => onSortFieldChange("poseAvgscore")}
            className={`${
              sortField === "poseAvgscore" ? "text-[#0066FF]" : "text-[#A2A2A7]"
            } hover:text-[#0066FF] transition-colors flex items-center gap-1`}
          >
            Avg Score
            {sortField === "poseAvgscore" &&
              (sortOrder === "asc" ? (
                <SortAscendingIcon className="w-4 h-4" />
              ) : (
                <SortDescendingIcon className="w-4 h-4" />
              ))}
          </button>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => onSortFieldChange("poseHighscore")}
            className={`${
              sortField === "poseHighscore" ? "text-[#0066FF]" : "text-[#A2A2A7]"
            } hover:text-[#0066FF] transition-colors flex items-center gap-1`}
          >
            High Score
            {sortField === "poseHighscore" &&
              (sortOrder === "asc" ? (
                <SortAscendingIcon className="w-4 h-4" />
              ) : (
                <SortDescendingIcon className="w-4 h-4" />
              ))}
          </button>
        </div>
      </div>
    </div>
  )
}

