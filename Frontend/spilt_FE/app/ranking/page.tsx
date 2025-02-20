"use client"

import { useState, useCallback, useEffect } from "react"
import ThemeToggle from "@/components/ui/theme-toggle"
import { UserMenu } from "@/components/user-menu"
import { NavigationBar } from "@/components/navigation-bar"
import { RankingHeader } from "@/components/ranking/ranking-header"
import { RankingList } from "@/components/ranking/ranking-list"
import { UserRankingCard } from "@/components/ranking/user-ranking-card"
import type { UserRanking, SortField, SortOrder } from "@/types/ranking"

// Mock user data
const mockUserData = {
  nickname: "James",
  gender: "Male",
  height: 180,
}

// Mock current user ID
const CURRENT_USER_ID = "user-1"

// Mock rankings data generator
function generateMockRankings(count: number): UserRanking[] {
  const rankings = Array.from({ length: count }, (_, i) => ({
    id: `user-${i + 1}`,
    name: `User ${i + 1}`,
    email: `user${i + 1}@example.com`,
    averageScore: Math.floor(Math.random() * 300) + 100,
    highScore: Math.floor(Math.random() * 300) + 200,
  }))

  // Sort by high score and assign ranks
  return rankings.sort((a, b) => b.highScore - a.highScore).map((ranking, index) => ({ ...ranking, rank: index + 1 }))
}

export default function RankingPage() {
  const [sortField, setSortField] = useState<SortField>("highScore")
  const [sortOrder, setSortOrder] = useState<SortOrder>("desc")
  const [rankings, setRankings] = useState<UserRanking[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [hasMore, setHasMore] = useState(true)

  // Initialize rankings
  useEffect(() => {
    setRankings(generateMockRankings(20))
  }, [])

  // Find current user's ranking
  const currentUserRanking = rankings.find((r) => r.id === CURRENT_USER_ID)

  const handleLogout = () => {
    // Add your logout logic here
    console.log("Logging out...")
  }

  const loadMoreRankings = useCallback(() => {
    setIsLoading(true)
    // Simulate API call
    setTimeout(() => {
      const newRankings = generateMockRankings(10)
      setRankings((prev) => {
        const combined = [...prev, ...newRankings]
        return combined
          .sort((a, b) => b.highScore - a.highScore)
          .map((ranking, index) => ({ ...ranking, rank: index + 1 }))
      })
      setIsLoading(false)
      if (rankings.length > 100) setHasMore(false) // Limit for demo
    }, 1000)
  }, [rankings.length])

  const handleSortFieldChange = (field: SortField) => {
    if (field === sortField) {
      // If clicking the same field, toggle sort order
      setSortOrder(sortOrder === "asc" ? "desc" : "asc")
    } else {
      // If clicking a different field, set it as new sort field
      setSortField(field)
      setSortOrder("desc") // Default to descending order
    }

    setRankings((prev) =>
      [...prev].sort((a, b) => {
        const comparison = (sortOrder === "asc" ? 1 : -1) * (a[field] - b[field])
        return comparison
      }),
    )
  }

  return (
    <div className="min-h-screen bg-[#161622] p-6 pb-24">
      <div className="flex justify-between items-center mb-8">
        <ThemeToggle />
        <UserMenu userData={mockUserData} onLogout={handleLogout} />
      </div>

      <div className="max-w-2xl mx-auto">
        {currentUserRanking && <UserRankingCard userRanking={currentUserRanking} />}

        <RankingHeader sortField={sortField} sortOrder={sortOrder} onSortFieldChange={handleSortFieldChange} />

        <RankingList
          rankings={rankings}
          currentUserId={CURRENT_USER_ID}
          hasMore={hasMore}
          isLoading={isLoading}
          onLoadMore={loadMoreRankings}
        />
      </div>

      <NavigationBar />
    </div>
  )
}

