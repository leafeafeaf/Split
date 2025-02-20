export interface GameData {
  id: number
  userId: number
  gameDate: string
  isSkip: number
  poseHighscore: number
  poseLowscore: number
  poseAvgscore: number
  elbowAngleScore: number
  armStabilityScore: number
  armSpeed: number
  bowlingScore: number
}

export interface GameResponse {
  code: string
  status: number
  message: string
  timestamp: string
  data: {
    id: number
  }
}

