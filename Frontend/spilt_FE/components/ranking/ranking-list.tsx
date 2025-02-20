"use client"

import { useRef, useCallback } from "react"
import type { RankingData } from "@/types/ranking"
import { UserHighlightModal } from "@/components/modals/user-highlight-modal"
import { useState } from "react"
import { format } from "date-fns"

interface RankingListProps {
  rankings: RankingData[]
  currentUserNickname?: string | null
  hasMore: boolean
  isLoading: boolean
  onLoadMore: () => void
}

function getRankColor(index: number) {
  switch (index) {
    case 0:
      return "text-yellow-400"
    case 1:
      return "text-gray-400"
    case 2:
      return "text-amber-700"
    default:
      return "text-white"
  }
}

function getRankLabel(index: number) {
  switch (index) {
    case 0:
      return "1st"
    case 1:
      return "2nd"
    case 2:
      return "3rd"
    default:
      return `${index + 1}th`
  }
}

export function RankingList({ rankings, currentUserNickname, hasMore, isLoading, onLoadMore }: RankingListProps) {
  const observer = useRef<IntersectionObserver>()
  const [selectedUser, setSelectedUser] = useState<RankingData | null>(null)

  const lastElementRef = useCallback(
    (node: HTMLDivElement) => {
      if (isLoading) return
      if (observer.current) observer.current.disconnect()
      observer.current = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && hasMore) {
          onLoadMore()
        }
      })
      if (node) observer.current.observe(node)
    },
    [isLoading, hasMore, onLoadMore],
  )

  const handleUserClick = (user: RankingData) => {
    setSelectedUser(user)
  }

  const handleCloseModal = () => {
    setSelectedUser(null)
  }

  return (
    <>
      <div className="space-y-2">
        {rankings.map((ranking, index) => {
          const isLastElement = index === rankings.length - 1
          const isCurrentUser = currentUserNickname !== null && ranking.nickname === currentUserNickname

          return (
            <div
              key={`${ranking.gameId}-${ranking.userId}`}
              ref={isLastElement ? lastElementRef : null}
              className={`p-4 rounded-lg transition-transform transform hover:scale-[1.02] ${
                isCurrentUser ? "bg-[#0066FF]/10" : "bg-[#1E1E2D]"
              }`}
            >
              <div className="grid grid-cols-4 items-center">
                <div className={`font-medium ${getRankColor(index)}`}>{getRankLabel(index)}</div>
                <div className="overflow-hidden">
                  <button
                    onClick={() => handleUserClick(ranking)}
                    className="font-medium text-white hover:text-[#0066FF] transition-colors truncate text-left"
                  >
                    {ranking.nickname}
                  </button>
                  <div className="text-xs text-[#A2A2A7] truncate">
                    {format(new Date(ranking.gameDate), "yyyy-MM-dd")}
                  </div>
                </div>
                <div className="text-white text-center">{ranking.poseAvgscore.toFixed(1)}</div>
                <div className="text-white text-center">{ranking.poseHighscore.toFixed(1)}</div>
              </div>
            </div>
          )
        })}
        {isLoading && (
          <div className="p-4 text-center">
            <div className="inline-block h-6 w-6 animate-spin rounded-full border-2 border-[#0066FF] border-t-transparent" />
          </div>
        )}
      </div>
      {selectedUser && (
        <UserHighlightModal
          isOpen={!!selectedUser}
          onClose={handleCloseModal}
          userName={selectedUser.nickname}
          videoUrl={selectedUser.highlight}
        />
      )}
    </>
  )
}

