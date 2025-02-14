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

export const login = createAsyncThunk(
  "auth/login",
  async (credentials: { email: string; password: string }, { rejectWithValue }) => {
    try {
      const response = await api.post("login", credentials)
      if (response.data.code === "SUCCESS") {
        return {
          accessToken: response.headers["authorization"],
          refreshToken: response.headers["refresh-token"],
        }
      }
      return rejectWithValue("Login failed")
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Login failed")
    }
  },
)

export const logout = createAsyncThunk("auth/logout", async (_, { rejectWithValue }) => {
  try {
    await api.post("logout")
    return true
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Logout failed")
  }
})

export const reissueTokens = createAsyncThunk("auth/reissueTokens", async (_, { getState, rejectWithValue }) => {
  try {
    const state = getState() as { auth: AuthState }
    const response = await api.post(
      "reissue",
      {},
      {
        headers: {
          "Refresh-Token": state.auth.refreshToken,
        },
      },
    )
    if (response.data.code === "SUCCESS") {
      return {
        accessToken: response.headers["authorization"],
        refreshToken: response.headers["refresh-token"],
      }
    }
    return rejectWithValue("Token reissue failed")
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Token reissue failed")
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
      .addCase(login.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false
        state.accessToken = action.payload.accessToken
        state.refreshToken = action.payload.refreshToken
        state.error = null
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
      .addCase(logout.fulfilled, (state) => {
        state.accessToken = null
        state.refreshToken = null
      })
      .addCase(reissueTokens.fulfilled, (state, action) => {
        state.accessToken = action.payload.accessToken
        state.refreshToken = action.payload.refreshToken
      })
  },
})

export const { setTokens, clearTokens } = authSlice.actions
export default authSlice.reducer

