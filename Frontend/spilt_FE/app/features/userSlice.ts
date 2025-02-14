import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
import type { PayloadAction } from "@reduxjs/toolkit"
import api from "@/lib/api"
import type { UserData } from "@/types/user"

interface UserState {
  user: UserData | null
  isLoading: boolean
  error: string | null
  highlightUpdateLoading: boolean
}

const initialState: UserState = {
  user: null,
  isLoading: false,
  error: null,
  highlightUpdateLoading: false,
}

export const fetchUser = createAsyncThunk("user/fetchUser", async (_, { rejectWithValue }) => {
  try {
    // This endpoint requires authentication, but our interceptor will handle it
    const response = await api.get("user")
    if (response.data.code === "SUCCESS") {
      return response.data.data
    }
    return rejectWithValue("Failed to fetch user data")
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Failed to fetch user data")
  }
})

export const createUser = createAsyncThunk("user/createUser", async (userData: FormData, { rejectWithValue }) => {
  try {
    // This is a public endpoint (signup), no auth needed
    const response = await api.post("user", userData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
    if (response.data.code === "SUCCESS") {
      return response.data.data
    }
    return rejectWithValue("Failed to create user")
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Failed to create user")
  }
})

export const checkNickname = createAsyncThunk("user/checkNickname", async (nickname: string, { rejectWithValue }) => {
  try {
    // This is a public endpoint, no auth needed
    await api.get(`user/check-nickname/${nickname}`)
    return true
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Failed to check nickname")
  }
})

export const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    clearUser: (state) => {
      state.user = null
      state.error = null
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUser.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(fetchUser.fulfilled, (state, action: PayloadAction<UserData>) => {
        state.isLoading = false
        state.user = action.payload
        state.error = null
      })
      .addCase(fetchUser.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
  },
})

export const { clearUser } = userSlice.actions
export default userSlice.reducer

