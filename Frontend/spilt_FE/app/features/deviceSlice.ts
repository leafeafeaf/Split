import { createSlice, createAsyncThunk } from "@reduxjs/toolkit"
import api from "@/lib/api"

interface DeviceState {
  isLoading: boolean
  error: string | null
  currentSerial: string | null
}

const initialState: DeviceState = {
  isLoading: false,
  error: null,
  currentSerial: null,
}

export const startDeviceMeasurement = createAsyncThunk(
  "device/startMeasurement",
  async (serial: string, { rejectWithValue }) => {
    try {
      const response = await api.post(`device/${serial}`)
      return serial
    } catch (error: any) {
      if (error.response?.status === 409) {
        return rejectWithValue("Device is already in use")
      }
      return rejectWithValue(error.response?.data?.message || "Failed to start measurement")
    }
  },
)

export const deviceSlice = createSlice({
  name: "device",
  initialState,
  reducers: {
    clearDeviceState: (state) => {
      state.isLoading = false
      state.error = null
      state.currentSerial = null
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(startDeviceMeasurement.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(startDeviceMeasurement.fulfilled, (state, action) => {
        state.isLoading = false
        state.currentSerial = action.payload
        state.error = null
      })
      .addCase(startDeviceMeasurement.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
      })
  },
})

export const { clearDeviceState } = deviceSlice.actions
export default deviceSlice.reducer

