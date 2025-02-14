import axios from "axios"
import { store } from "@/app/store/store"
import { clearTokens, reissueTokens } from "@/app/features/authSlice"
import { clearUser } from "@/app/features/userSlice"

const BASE_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "https://i12b202.p.ssafy.io/api"

// Define endpoints that require authentication
const AUTH_REQUIRED_ENDPOINTS = ["logout", "user", "user/thema", "user/highlight", "device", "game"]

// Helper function to check if the endpoint needs authentication
const requiresAuth = (url: string) => {
  // Remove leading slash if present and get the first part of the path
  const trimmedUrl = url.startsWith("/") ? url.slice(1) : url
  const firstPathPart = trimmedUrl.split("/")[0]

  return AUTH_REQUIRED_ENDPOINTS.some((endpoint) => trimmedUrl.startsWith(endpoint) || firstPathPart === endpoint)
}

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
    // Remove leading slash if present
    const url = config.url?.startsWith("/") ? config.url.slice(1) : config.url

    // Add Authorization header for endpoints that require authentication
    if (requiresAuth(url || "")) {
      const state = store.getState()
      const accessToken = state.auth.accessToken

      if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`
      }
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

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const result = await store.dispatch(reissueTokens()).unwrap()
        originalRequest.headers.Authorization = `Bearer ${result.accessToken}`
        return api(originalRequest)
      } catch (refreshError) {
        store.dispatch(clearTokens())
        store.dispatch(clearUser())
        window.location.href = "/login"
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  },
)

export default api

