"use client"

import { useRef, useCallback, useState } from "react"
import type { UserRanking } from "@/types/ranking"
import { UserHighlightModal } from "@/components/modals/user-highlight-modal"

interface RankingListProps {
  rankings: UserRanking[]
  currentUserId: string
  hasMore: boolean
  isLoading: boolean
  onLoadMore: () => void
}

function getRankColor(rank: number) {
  switch (rank) {
    case 1:
      return "text-yellow-400"
    case 2:
      return "text-gray-400"
    case 3:
      return "text-amber-700"
    default:
      return "text-white"
  }
}

function getRankLabel(rank: number) {
  switch (rank) {
    case 1:
      return "1st"
    case 2:
      return "2nd"
    case 3:
      return "3rd"
    default:
      return `${rank}th`
  }
}

export function RankingList({ rankings, currentUserId, hasMore, isLoading, onLoadMore }: RankingListProps) {
  const observer = useRef<IntersectionObserver>()
  const [selectedUser, setSelectedUser] = useState<UserRanking | null>(null)

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

  const handleUserClick = (user: UserRanking) => {
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
          const isCurrentUser = ranking.id === currentUserId

          return (
            <div
              key={ranking.id}
              ref={isLastElement ? lastElementRef : null}
              className={`p-4 rounded-lg transition-transform transform hover:scale-[1.02] ${
                isCurrentUser ? "bg-[#0066FF]/10" : "bg-[#1E1E2D]"
              }`}
            >
              <div className="grid grid-cols-4 items-center">
                <div className={`font-medium ${getRankColor(ranking.rank)}`}>{getRankLabel(ranking.rank)}</div>
                <div className="overflow-hidden">
                  <button
                    onClick={() => handleUserClick(ranking)}
                    className="font-medium text-white hover:text-[#0066FF] transition-colors truncate text-left"
                  >
                    {ranking.name}
                  </button>
                  <div className="text-xs text-[#A2A2A7] truncate">{ranking.email}</div>
                </div>
                <div className="text-white text-center">{ranking.averageScore}</div>
                <div className="text-white text-center">{ranking.highScore}</div>
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
          userName={selectedUser.name}
          videoUrl={`https://your-s3-bucket.s3.amazonaws.com/highlights/${selectedUser.id}.mp4`}
        />
      )}
    </>
  )
}

