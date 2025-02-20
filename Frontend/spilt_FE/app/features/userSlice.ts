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
      console.log(response)
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
      console.log(response)
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

// export const updateHighlight = createAsyncThunk(
//   "user/updateHighlight",
//   async ({ highlight, isUpdate }: { highlight: string; isUpdate: boolean }, { rejectWithValue }) => {
//     try {
//       // This endpoint requires authentication, interceptor will handle it
//       const response = await api({
//         method: isUpdate ? "patch" : "post",
//         url: "user/highlight",
//         data: { highlight },
//       })

//       if (response.data.code === "SUCCESS") {
//         console.log(response)
//         return highlight
//       }
//       return rejectWithValue("Failed to update highlight")
//     } catch (error: any) {
//       if (error.response?.status === 400) {
//         if (error.response.data.code === "INVALID_VIDEO_URL") {
//           return rejectWithValue("Invalid video URL format")
//         }
//       }
//       return rejectWithValue(error.response?.data?.message || "Failed to update highlight")
//     }
//   },
// )

export const updateHighlight = createAsyncThunk(
  'user/updateHighlight',
  async ({ highlight, isUpdate }: { highlight: string; isUpdate: boolean }, { rejectWithValue }) => {
    try {
      const response = await api({
        url: '/user/highlight',
        method: isUpdate ? 'PATCH' : 'POST',
        data: { highlight }
      });

      if (response.data.code === 'SUCCESS') {
        return highlight;
      }
      
      return rejectWithValue(response.data.message);
    } catch (error: any) {
      if (error.response?.data?.message) {
        return rejectWithValue(error.response.data.message);
      }
      return rejectWithValue('Failed to update highlight');
    }
  }
);

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
      .addCase(updateHighlight.pending, (state) => {
        state.highlightUpdateLoading = true
        state.error = null
      })
      .addCase(updateHighlight.fulfilled, (state, action: PayloadAction<string>) => {
        state.highlightUpdateLoading = false
        if (state.user) {
          state.user.highlight = action.payload
        }
        state.error = null
      })
      .addCase(updateHighlight.rejected, (state, action) => {
        state.highlightUpdateLoading = false
        state.error = action.payload as string
      })
  },
})

export const { clearUser } = userSlice.actions
export default userSlice.reducer

