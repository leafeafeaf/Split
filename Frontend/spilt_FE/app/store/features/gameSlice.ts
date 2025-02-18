// import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
// import type { PayloadAction } from "@reduxjs/toolkit"
// import api from "@/lib/api"
// import type { GameData } from "@/types/game"

// interface GameState {
//   games: GameData[]
//   isLoading: boolean
//   error: string | null
// }

// const initialState: GameState = {
//   games: [],
//   isLoading: false,
//   error: null,
// }

// export const fetchGames = createAsyncThunk<GameData[], number, { rejectValue: string }>(
//   "game/fetchGames",
//   async (count = 10, { rejectWithValue }) => {
//     try {
//       const response = await api.get("/game", { params: { count } })
//       if (response.data.code === "SUCCESS") {
//         return response.data.data.gameArr
//       }
//       return rejectWithValue("Failed to fetch games")
//     } catch (error: any) {
//       return rejectWithValue(error.response?.data?.message || "Failed to fetch games")
//     }
//   },
// )

// export const gameSlice = createSlice({
//   name: "game",
//   initialState,
//   reducers: {
//     clearGames: (state) => {
//       state.games = []
//       state.error = null
//     },
//   },
//   extraReducers: (builder) => {
//     builder
//       .addCase(fetchGames.pending, (state) => {
//         state.isLoading = true
//         state.error = null
//       })
//       .addCase(fetchGames.fulfilled, (state, action: PayloadAction<GameData[]>) => {
//         state.isLoading = false
//         state.games = action.payload
//         state.error = null
//       })
//       .addCase(fetchGames.rejected, (state, action) => {
//         state.isLoading = false
//         state.error = action.payload as string
//       })
//   },
// })

// export const { clearGames } = gameSlice.actions
// export default gameSlice.reducer

// app/features/gameSlice.ts

import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
import type { PayloadAction } from "@reduxjs/toolkit"
import api from "@/lib/api"
import type { GameData } from "@/types/game"
import type { FrameData } from "@/types/frame"
import type { RootState } from "@/app/store/store"

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
  async ({ frames, bowlingScore }: { frames: FrameData[]; bowlingScore: number }, { rejectWithValue, getState }) => {
    try {
      console.log("Received frames:", frames)
      console.log("Received bowlingScore:", bowlingScore)

      // Get serialNumber from device state
      const state = getState() as RootState
      const serialNumber = state.device.currentSerial

      if (!serialNumber) {
        return rejectWithValue("No device connected")
      }

      const stats = calculateGameStats(frames)
      console.log("Calculated stats:", stats)

      const gameData: GameUploadData = {
        ...stats,
        isSkip: frames.some((frame) => frame.isSkip) ? 1 : 0,
        serialNum: Number.parseInt(serialNumber),
        bowlingScore,
      }
      console.log("Final gameData:", JSON.stringify(gameData, null, 2))

      const response = await api.post("game", gameData)
      console.log("API response:", response)

      if (response.data.code === "SUCCESS") {
        return response.data.data.id
      }
      return rejectWithValue("Failed to upload game")
    } catch (error: any) {
      console.error("Error in uploadGame:", error)
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