import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
import type { PayloadAction } from "@reduxjs/toolkit"
import api from "@/lib/api"
import type { GameData } from "@/types/game"

interface GameState {
  games: GameData[]
  isLoading: boolean
  error: string | null
}

const initialState: GameState = {
  games: [],
  isLoading: false,
  error: null,
}

export const fetchGames = createAsyncThunk("game/fetchGames", async (count = 10, { rejectWithValue }) => {
  try {
    const response = await api.get("/game", { params: { count } })
    if (response.data.code === "SUCCESS") {
      return response.data.data.gameArr
    }
    return rejectWithValue("Failed to fetch games")
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Failed to fetch games")
  }
})

export const gameSlice = createSlice({
  name: "game",
  initialState,
  reducers: {
    clearGames: (state) => {
      state.games = []
      state.error = null
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchGames.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(fetchGames.fulfilled, (state, action: PayloadAction<GameData[]>) => {
        state.isLoading = false
        state.games = action.payload
        state.error = null
      })
      .addCase(fetchGames.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
  },
})

export const { clearGames } = gameSlice.actions
export default gameSlice.reducer

