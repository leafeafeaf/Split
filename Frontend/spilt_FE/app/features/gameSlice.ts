import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
import type { PayloadAction } from "@reduxjs/toolkit"
import api from "@/lib/api"
import type { GameData } from "@/types/game"
import type { FrameData } from "@/types/frame"

interface GameState {
  games: GameData[]
  isLoading: boolean
  error: string | null
  bowlingScore: number | null
  currentGameId: number | null
}

interface GameUploadData {
  isSkip: number
  poseHighscore: number
  poseLowscore: number
  poseAvgscore: number
  elbowAngleScore: number
  armStabilityScore: number
  armSpeed: number
  serialNum: number
  bowlingScore: number
}

const initialState: GameState = {
  games: [],
  isLoading: false,
  error: null,
  bowlingScore: null,
  currentGameId: null,
}

// Helper function to calculate game statistics from frames
const calculateGameStats = (frames: FrameData[]): Omit<GameUploadData, "serialNum" | "bowlingScore" | "isSkip"> => {
  const poseScores = frames.map((frame) => frame.poseScore)
  const elbowScores = frames.map((frame) => frame.elbowAngleScore)
  const stabilityScores = frames.map((frame) => frame.armStabilityScore)
  const speedScores = frames.map((frame) => frame.speed)

  return {
    poseHighscore: Math.max(...poseScores),
    poseLowscore: Math.min(...poseScores),
    poseAvgscore: poseScores.reduce((a, b) => a + b, 0) / frames.length,
    elbowAngleScore: elbowScores.reduce((a, b) => a + b, 0) / frames.length,
    armStabilityScore: stabilityScores.reduce((a, b) => a + b, 0) / frames.length,
    armSpeed: speedScores.reduce((a, b) => a + b, 0) / frames.length,
  }
}

export const uploadGame = createAsyncThunk(
  "game/uploadGame",
  async ({ frames, bowlingScore }: { frames: FrameData[]; bowlingScore: number }, { rejectWithValue }) => {
    try {
      const stats = calculateGameStats(frames)
      const gameData: GameUploadData = {
        ...stats,
        isSkip: frames.some((frame) => frame.isSkip) ? 1 : 0,
        serialNum: 1, // Fixed value as specified
        bowlingScore,
      }

      const response = await api.post("/game", gameData)

      if (response.data.code === "SUCCESS") {
        return response.data.data.id
      }
      return rejectWithValue("Failed to upload game")
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to upload game")
    }
  },
)

export const gameSlice = createSlice({
  name: "game",
  initialState,
  reducers: {
    setBowlingScore: (state, action: PayloadAction<number>) => {
      state.bowlingScore = action.payload
    },
    clearGameData: (state) => {
      state.bowlingScore = null
      state.currentGameId = null
      state.error = null
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(uploadGame.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(uploadGame.fulfilled, (state, action: PayloadAction<number>) => {
        state.isLoading = false
        state.currentGameId = action.payload
        state.error = null
      })
      .addCase(uploadGame.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
  },
})

export const { setBowlingScore, clearGameData } = gameSlice.actions
export default gameSlice.reducer

