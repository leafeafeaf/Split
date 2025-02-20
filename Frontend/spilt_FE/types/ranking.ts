export interface UserRanking {
  id: string
  rank: number
  name: string
  email: string
  averageScore: number
  highScore: number
}

export type SortField = "averageScore" | "highScore"
export type SortOrder = "asc" | "desc"

