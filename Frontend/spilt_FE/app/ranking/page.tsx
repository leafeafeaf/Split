"use client"

import { useState, useCallback, useEffect } from "react"
import { useAppDispatch, useAppSelector } from "@/app/store/hooks"
import { fetchRankings } from "@/app/features/rankingSlice"
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
  const { rankings, isLoading, error } = useAppSelector((state) => state.ranking)
  const [sortField, setSortField] = useState<SortField>("poseHighscore")
  const [sortOrder, setSortOrder] = useState<SortOrder>("desc")
  const [page, setPage] = useState(1)
  const [hasMore, setHasMore] = useState(true)

  useEffect(() => {
    dispatch(fetchRankings({ sortField, sortOrder, page, pageSize: PAGE_SIZE }))
  }, [dispatch, sortField, sortOrder, page])

  const loadMoreRankings = useCallback(() => {
    if (!isLoading && hasMore) {
      setPage((prev) => {
        const nextPage = prev + 1
        dispatch(fetchRankings({ sortField, sortOrder, page: nextPage, pageSize: PAGE_SIZE }))
        return nextPage
      })
    }
  }, [isLoading, hasMore, dispatch, sortField, sortOrder])

  const handleSortFieldChange = (field: SortField) => {
    if (field === sortField) {
      // If clicking the same field, toggle sort order
      setSortOrder(sortOrder === "asc" ? "desc" : "asc")
    } else {
      // If clicking a different field, set it as new sort field
      setSortField(field)
      setSortOrder("desc") // Default to descending order
    }
    // Reset pagination and fetch data again
    setPage(1)
    //setRankings([])  //No need to reset rankings as it's managed by Redux
    dispatch(fetchRankings({ sortField, sortOrder, page: 1, pageSize: PAGE_SIZE }))
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

