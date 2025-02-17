import axios from "axios"
import { store } from "@/app/store/store"
import { clearTokens, reissueTokens } from "@/app/features/authSlice"
import { clearUser } from "@/app/features/userSlice"

const BASE_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "https://i12b202.p.ssafy.io/api"

// Define endpoints that don't require authentication
const PUBLIC_ENDPOINTS = [
"login",
"user/check-nickname",
"user", // POST (signup) doesn't need auth
"reissue",
"rank",
]

// Helper function to check if the endpoint needs authentication
const requiresAuth = (url: string) => {
// Remove leading slash if present
const trimmedUrl = url.startsWith("/") ? url.slice(1) : url

console.log("URL 체크:", url +" : "+trimmedUrl)
// Check if the URL matches any of the public endpoints
return !PUBLIC_ENDPOINTS.some((endpoint) => {
// For exact matches

// For endpoints with parameters (e.g., user/check-nickname/{nickname})
if (endpoint.includes("check-nickname") && trimmedUrl.startsWith("user/check-nickname/")) return true
// Special case for /user endpoint - only POST (signup) is public
if (endpoint === "user") {
// Get the request method from the config when available
const method = axios.defaults.method || "get"
return method.toLowerCase() === "post"
}
if (trimmedUrl === endpoint) return true
return false
})
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
console.log("전체 요청 설정:", config)

// Remove leading slash if present
const url = config.url?.startsWith("/") ? config.url.slice(1) : config.url

// Only add Authorization header for endpoints that require authentication
if (requiresAuth(url || "")) {
console.log("인증이 필요한 엔드포인트:", url)

const state = store.getState()
const accessToken = state.auth.accessToken

console.log("현재 저장된 액세스 토큰:", accessToken)
console.log("요청 설정:", {
url: config.url,
method: config.method,
headers: config.headers
})

if (accessToken) {
config.headers.Authorization = accessToken
console.log("최종 요청 헤더:", config.headers)
} else {
console.log("액세스 토큰이 없음")
}
}

return config
},
(error) => {
console.log("요청 인터셉터 에러:", error)
return Promise.reject(error)
},
)

// Response interceptor
api.interceptors.response.use(
(response) => response,
async (error) => {
const originalRequest = error.config

// Prevent infinite loops
if (originalRequest._retry) {
return Promise.reject(error)
}

// 403 에러 처리 추가
if (error.response?.status === 403) {
console.log("403 Forbidden 에러 발생:", {
url: originalRequest.url,
method: originalRequest.method,
headers: originalRequest.headers,
errorData: error.response.data
})
}

if (error.response?.status === 401) {
console.log("401 에러 발생:", error.response.data)
const errorCode = error.response.data.code

switch (errorCode) {
case "TOKEN_EXPIRED":
try {
console.log("토큰 재발급 시도")
originalRequest._retry = true
const result = await store.dispatch(reissueTokens()).unwrap()
originalRequest.headers.Authorization = result.accessToken
console.log("토큰 재발급 성공:", result.accessToken)
return api(originalRequest)
} catch (refreshError) {
console.log("토큰 재발급 실패:", refreshError)
store.dispatch(clearTokens())
store.dispatch(clearUser())
window.location.href = "/login"
return Promise.reject(refreshError)
}

case "INVALID_TOKEN":
case "TOKEN_MISSING":
console.log("토큰 무효 또는 누락")
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