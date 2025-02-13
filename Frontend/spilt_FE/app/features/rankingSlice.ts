import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
import type { PayloadAction } from "@reduxjs/toolkit"
import api from "@/lib/api"
import type { RankingData } from "@/types/ranking"

interface RankingState {
  rankings: RankingData[]
  isLoading: boolean
  error: string | null
}

const initialState: RankingState = {
  rankings: [],
  isLoading: false,
  error: null,
}

export const fetchRankings = createAsyncThunk("ranking/fetchRankings", async (_, { rejectWithValue }) => {
  try {
    const response = await api.get("/rank")
    if (response.data.code === "SUCCESS") {
      return response.data.data
    }
    return rejectWithValue("Failed to fetch rankings")
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Failed to fetch rankings")
  }
})

export const rankingSlice = createSlice({
  name: "ranking",
  initialState,
  reducers: {
    clearRankings: (state) => {
      state.rankings = []
      state.error = null
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchRankings.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(fetchRankings.fulfilled, (state, action: PayloadAction<RankingData[]>) => {
        state.isLoading = false
        state.rankings = action.payload
        state.error = null
      })
      .addCase(fetchRankings.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
  },
})

export const { clearRankings } = rankingSlice.actions
export default rankingSlice.reducer

