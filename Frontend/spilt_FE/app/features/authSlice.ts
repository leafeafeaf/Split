import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
import type { PayloadAction } from "@reduxjs/toolkit"
import api from "@/lib/api"

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  isLoading: boolean
  error: string | null
}

const initialState: AuthState = {
  accessToken: null,
  refreshToken: null,
  isLoading: false,
  error: null,
}

export const reissueTokens = createAsyncThunk("auth/reissueTokens", async (_, { rejectWithValue }) => {
  try {
    // No need to manually add Authorization header here as it's a public endpoint
    const response = await api.post("reissue")
    if (response.data.code === "SUCCESS") {
      return {
        accessToken: response.data.accessToken,
        refreshToken: response.data.refreshToken,
      }
    }
    return rejectWithValue("Failed to reissue tokens")
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Failed to reissue tokens")
  }
})

export const logout = createAsyncThunk("auth/logout", async (_, { getState, rejectWithValue }) => {
  try {
    // This endpoint requires authentication, but our interceptor will handle it
    const response = await api.post("logout")
    if (response.data.code === "SUCCESS") {
      return true
    }
    return rejectWithValue("Failed to logout")
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Failed to logout")
  }
})

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setTokens: (state, action: PayloadAction<{ accessToken: string; refreshToken: string }>) => {
      state.accessToken = action.payload.accessToken
      state.refreshToken = action.payload.refreshToken
    },
    clearTokens: (state) => {
      state.accessToken = null
      state.refreshToken = null
      state.error = null
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(reissueTokens.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(reissueTokens.fulfilled, (state, action) => {
        state.isLoading = false
        state.accessToken = action.payload.accessToken
        state.refreshToken = action.payload.refreshToken
        state.error = null
      })
      .addCase(reissueTokens.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
      .addCase(logout.fulfilled, (state) => {
        state.accessToken = null
        state.refreshToken = null
        state.error = null
      })
  },
})

export const { setTokens, clearTokens } = authSlice.actions
export default authSlice.reducer

