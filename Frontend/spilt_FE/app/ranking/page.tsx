"use client"

import { useState, useCallback, useEffect } from "react"
import { useAppDispatch, useAppSelector } from "@/app/store/hooks"
import { fetchRankings, clearRankings } from "@/app/features/rankingSlice"
import ThemeToggle from "@/components/ui/theme-toggle"
import { UserMenu } from "@/components/user-menu"
import { NavigationBar } from "@/components/navigation-bar"
import { RankingHeader } from "@/components/ranking/ranking-header"
import { RankingList } from "@/components/ranking/ranking-list"
import { UserRankingCard } from "@/components/ranking/user-ranking-card"
import type { SortField, SortOrder } from "@/types/ranking"

// Number of items to load per page
const PAGE_SIZE = 20

export default function RankingPage() {
  const dispatch = useAppDispatch()
  const { rankings, isLoading, error, hasMore } = useAppSelector((state) => state.ranking)
  const [sortField, setSortField] = useState<SortField>("poseHighscore")
  const [sortOrder, setSortOrder] = useState<SortOrder>("desc")
  const [page, setPage] = useState(1)

  const fetchRankingsData = useCallback(() => {
    dispatch(fetchRankings({ sortField, sortOrder, page, pageSize: PAGE_SIZE }))
  }, [dispatch, sortField, sortOrder, page])

  useEffect(() => {
    fetchRankingsData()
  }, [fetchRankingsData])

  const loadMoreRankings = useCallback(() => {
    if (!isLoading && hasMore) {
      setPage((prevPage) => prevPage + 1)
    }
  }, [isLoading, hasMore])

  const handleSortFieldChange = (field: SortField) => {
    if (field === sortField) {
      setSortOrder(sortOrder === "asc" ? "desc" : "asc")
    } else {
      setSortField(field)
      setSortOrder("desc")
    }
    setPage(1)
    dispatch(clearRankings())
  }

  // Find current user's ranking (set to null when user is not authenticated)
  const currentUserRanking = null

  return (
    <div className="min-h-screen bg-[#161622] p-6 pb-24">
      <div className="flex justify-between items-center mb-8">
        <ThemeToggle />
        <UserMenu />
      </div>

      <div className="max-w-2xl mx-auto">
        {currentUserRanking && <UserRankingCard userRanking={currentUserRanking} />}

        <RankingHeader sortField={sortField} sortOrder={sortOrder} onSortFieldChange={handleSortFieldChange} />

        {error ? (
          <div className="text-center text-[#A2A2A7] py-8">{error}</div>
        ) : (
          <RankingList
            rankings={rankings}
            currentUserNickname={undefined} // Set to undefined when user is not authenticated
            hasMore={hasMore}
            isLoading={isLoading}
            onLoadMore={loadMoreRankings}
          />
        )}
      </div>

      <NavigationBar />
    </div>
  )
}

