import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
import type { PayloadAction } from "@reduxjs/toolkit"
import api from "@/lib/api"

interface ThemeState {
  theme: "light" | "dark"
  isLoading: boolean
  error: string | null
}

const initialState: ThemeState = {
  theme: "dark",
  isLoading: false,
  error: null,
}

export const updateTheme = createAsyncThunk(
  "theme/updateTheme",
  async (theme: "light" | "dark", { rejectWithValue }) => {
    try {
      const themeValue = theme === "light" ? 1 : 2
      const response = await api.patch("/user/thema", { thema: themeValue })
      if (response.data.code === "SUCCESS") {
        return theme
      }
      return rejectWithValue("Failed to update theme")
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to update theme")
    }
  },
)

export const themeSlice = createSlice({
  name: "theme",
  initialState,
  reducers: {
    setTheme: (state, action: PayloadAction<"light" | "dark">) => {
      state.theme = action.payload
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(updateTheme.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(updateTheme.fulfilled, (state, action) => {
        state.isLoading = false
        state.theme = action.payload
        state.error = null
      })
      .addCase(updateTheme.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
  },
})

export const { setTheme } = themeSlice.actions
export default themeSlice.reducer

