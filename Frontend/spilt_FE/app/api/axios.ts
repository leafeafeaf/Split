import axios from "axios"

const BASE_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "https://i12b202.p.ssafy.io/api"

const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
})

export default api

