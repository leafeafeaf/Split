import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
import type { PayloadAction } from "@reduxjs/toolkit"
import api from "@/lib/api"
import type { FrameData, FrameUploadData } from "@/types/frame"

interface FrameState {
  frames: FrameData[]
  currentFrame: FrameData | null
  isLoading: boolean
  error: string | null
  progress: number
}

const initialState: FrameState = {
  frames: [],
  currentFrame: null,
  isLoading: false,
  error: null,
  progress: 0,
}

export const fetchFrames = createAsyncThunk("frame/fetchFrames", async (serial: string, { rejectWithValue }) => {
  try {
    const response = await api.get(`device/${serial}/frame`)
    if (response.data.code === "SUCCESS") {
      return response.data.data
    }
    return rejectWithValue("Failed to fetch frames")
  } catch (error: any) {
    if (error.response?.status === 404) {
      return rejectWithValue("Progress not found")
    }
    return rejectWithValue(error.response?.data?.message || "Failed to fetch frames")
  }
})

export const uploadFrame = createAsyncThunk(
  "frame/uploadFrame",
  async ({ serial, frameData }: { serial: string; frameData: FrameUploadData }, { dispatch, rejectWithValue }) => {
    try {
      const response = await api.post(`device/${serial}/frame`, frameData)

      if (response.data.code === "SUCCESS") {
        // After successful upload, fetch updated frame list
        await dispatch(fetchFrames(serial)).unwrap()
        return response.data
      }
      return rejectWithValue("Failed to upload frame")
    } catch (error: any) {
      if (error.response?.status === 400) {
        if (error.response.data.code === "NOT_IN_PROGRESS") {
          return rejectWithValue("No measurement in progress")
        } else if (error.response.data.code === "FRAME_LIMIT_REACHED") {
          return rejectWithValue("Maximum frame limit reached")
        }
      }
      return rejectWithValue(error.response?.data?.message || "Failed to upload frame")
    }
  },
)

export const skipFrame = createAsyncThunk("frame/skipFrame", async (serial: string, { dispatch, rejectWithValue }) => {
  try {
    // Upload empty frame with isSkip flag
    const skipData: FrameUploadData = {
      isSkip: true,
    }

    const response = await dispatch(uploadFrame({ serial, frameData: skipData })).unwrap()
    return response
  } catch (error) {
    return rejectWithValue(error)
  }
})

export const fetchSingleFrame = createAsyncThunk(
  "frame/fetchSingleFrame",
  async ({ serial, frameNum }: { serial: string; frameNum: number }, { rejectWithValue }) => {
    try {
      const response = await api.get(`device/${serial}/frame/${frameNum}`)
      if (response.data.code === "SUCCESS") {
        return response.data.data
      }
      return rejectWithValue("Failed to fetch frame")
    } catch (error: any) {
      if (error.response?.status === 404) {
        if (error.response.data.code === "PROGRESS_NOT_FOUND") {
          return rejectWithValue("Progress not found")
        } else if (error.response.data.code === "FRAME_NOT_FOUND") {
          return rejectWithValue("Frame not found")
        }
      }
      return rejectWithValue(error.response?.data?.message || "Failed to fetch frame")
    }
  },
)

export const frameSlice = createSlice({
  name: "frame",
  initialState,
  reducers: {
    clearFrames: (state) => {
      state.frames = []
      state.error = null
      state.progress = 0
      state.currentFrame = null
    },
    updateProgress: (state) => {
      // Calculate progress based on frame count (10 frames = 100%)
      state.progress = Math.min(state.frames.length * 10, 100)
    },
    clearCurrentFrame: (state) => {
      state.currentFrame = null
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchFrames.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(fetchFrames.fulfilled, (state, action: PayloadAction<FrameData[]>) => {
        state.isLoading = false
        state.frames = action.payload
        state.error = null
      })
      .addCase(fetchFrames.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
      .addCase(uploadFrame.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(uploadFrame.fulfilled, (state) => {
        state.isLoading = false
        state.error = null
      })
      .addCase(uploadFrame.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
      .addCase(fetchSingleFrame.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(fetchSingleFrame.fulfilled, (state, action: PayloadAction<FrameData>) => {
        state.isLoading = false
        state.currentFrame = action.payload
        state.error = null
      })
      .addCase(fetchSingleFrame.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
  },
})

export const { clearFrames, updateProgress, clearCurrentFrame } = frameSlice.actions
export default frameSlice.reducer

