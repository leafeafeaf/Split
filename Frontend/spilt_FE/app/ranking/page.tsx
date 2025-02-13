"use client"

import { useState, useCallback, useEffect } from "react"
import ThemeToggle from "@/components/ui/theme-toggle"
import { UserMenu } from "@/components/user-menu"
import { NavigationBar } from "@/components/navigation-bar"
import { RankingHeader } from "@/components/ranking/ranking-header"
import { RankingList } from "@/components/ranking/ranking-list"
import { UserRankingCard } from "@/components/ranking/user-ranking-card"
import api from "@/lib/api"
import { toast } from "sonner"
import type { RankingData, SortField, SortOrder } from "@/types/ranking"

// Number of items to load per page
const PAGE_SIZE = 20

export default function RankingPage() {
  const [sortField, setSortField] = useState<SortField>("poseHighscore")
  const [sortOrder, setSortOrder] = useState<SortOrder>("desc")
  const [rankings, setRankings] = useState<RankingData[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [hasMore, setHasMore] = useState(true)
  const [page, setPage] = useState(1)
  const [error, setError] = useState<string | null>(null)

  // Fetch initial rankings
  const fetchRankings = useCallback(
    async (pageNum: number) => {
      setIsLoading(true)
      setError(null)

      try {
        const response = await api.get("rank")

        if (response.data.code === "SUCCESS") {
          const newData = response.data.data || []

          // Sort data based on current sort field and order
          const sortedData = [...newData].sort((a, b) => {
            const comparison = a[sortField] - b[sortField]
            return sortOrder === "asc" ? comparison : -comparison
          })

          // Handle pagination
          const start = (pageNum - 1) * PAGE_SIZE
          const paginatedData = sortedData.slice(start, start + PAGE_SIZE)

          if (pageNum === 1) {
            setRankings(paginatedData)
          } else {
            setRankings((prev) => [...prev, ...paginatedData])
          }

          // Check if we've reached the end of the data
          setHasMore(newData.length > start + PAGE_SIZE)
        }
      } catch (error: any) {
        if (error.response?.status === 404) {
          setError("No ranking data available")
          toast.error("No ranking data available")
        } else {
          setError("Failed to load rankings")
          toast.error("Failed to load rankings")
        }
        setHasMore(false)
      } finally {
        setIsLoading(false)
      }
    },
    [sortField, sortOrder],
  )

  // Initial load
  useEffect(() => {
    fetchRankings(1)
  }, [fetchRankings])

  const loadMoreRankings = useCallback(() => {
    if (!isLoading && hasMore) {
      setPage((prev) => {
        const nextPage = prev + 1
        fetchRankings(nextPage)
        return nextPage
      })
    }
  }, [isLoading, hasMore, fetchRankings])

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
    setRankings([])
    fetchRankings(1)
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

