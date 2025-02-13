export interface UserData {
  id: number
  email: string
  gender: number
  height: number
  nickname: string
  totalGameCount: number
  highlight: string
  totalPoseHighscore: number
  totalPoseAvgscore: number
  elbowAngleScore: number
  armStabilityScore: number
  armSpeedScore: number
  thema: number
  currBowlingScore: number
  avgBowlingScore: number
}

export interface UserResponse {
  code: string
  status: number
  message: string
  timestamp: string
  data: UserData
}

