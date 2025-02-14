import axios from "axios"
import { store } from "@/app/store/store"
import { clearTokens, reissueTokens } from "@/app/features/authSlice"
import { clearUser } from "@/app/features/userSlice"

const BASE_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "https://i12b202.p.ssafy.io/api"

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Enable sending cookies
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const state = store.getState()
    const accessToken = state.auth.accessToken

    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }

    // Ensure the URL starts with the base URL
    if (!config.url?.startsWith("http")) {
      config.url = config.url?.startsWith("/") ? config.url.slice(1) : config.url
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (originalRequest._retry) {
      return Promise.reject(error)
    }

    if (error.response?.status === 401) {
      const errorCode = error.response.data.code

      switch (errorCode) {
        case "TOKEN_EXPIRED":
          try {
            originalRequest._retry = true
            const result = await store.dispatch(reissueTokens()).unwrap()
            originalRequest.headers.Authorization = `Bearer ${result.accessToken}`
            return api(originalRequest)
          } catch (refreshError) {
            store.dispatch(clearTokens())
            store.dispatch(clearUser())
            window.location.href = "/login"
            return Promise.reject(refreshError)
          }

        case "INVALID_TOKEN":
        case "TOKEN_MISSING":
          store.dispatch(clearTokens())
          store.dispatch(clearUser())
          window.location.href = "/login"
          return Promise.reject(error)

        default:
          return Promise.reject(error)
      }
    }

    return Promise.reject(error)
  },
)

export default api

