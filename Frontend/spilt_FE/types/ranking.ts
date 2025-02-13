export interface RankingData {
  gameId: number
  userId: number
  nickname: string
  highlight: string
  totalGameCount: number
  gameDate: string
  poseHighscore: number
  poseLosescore: number
  poseAvgscore: number
  elbowAngleScore: number
  armStabilityScore: number
  armSpeed: number
}

export interface RankingResponse {
  code: string
  status: number
  message: string
  data?: RankingData[]
  timestamp: string
}

export type SortField = "poseHighscore" | "poseAvgscore" | "armSpeed"
export type SortOrder = "asc" | "desc"

