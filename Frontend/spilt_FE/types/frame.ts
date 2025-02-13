export interface FrameData {
  id: number
  progressId: number
  serialNumber: number
  num: number
  video: string
  isSkip: number
  feedback: string
  poseScore: number
  elbowAngleScore: number
  armStabilityScore: number
  speed: number
}

export interface FrameResponse {
  code: string
  status: number
  message: string
  data: FrameData[]
  timestamp: string
}

export interface FrameUploadData {
  isSkip: boolean
  feedback?: string
  poseScore?: number
  elbowAngleScore?: number
  armStabilityScore?: number
  speed?: number
}

export interface FrameUploadResponse {
  code: string
  status: number
  message: string
  timestamp: string
  data?: {
    num: number
  }
}

export interface SingleFrameResponse {
  code: string
  status: number
  message: string
  data: FrameData
  timestamp: string
}

