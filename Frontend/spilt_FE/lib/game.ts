import { create } from "zustand"
import api from "./api"
import { toast } from "sonner"
import type { GameData } from "@/types/game"

interface GameStore {
  games: GameData[]
  isLoading: boolean
  fetchGames: (count?: number) => Promise<void>
}

export const useGameStore = create<GameStore>((set) => ({
  games: [],
  isLoading: false,
  fetchGames: async (count = 10) => {
    set({ isLoading: true })
    try {
      const response = await api.get("game", {
        // Add headers when token is implemented
        // headers: {
        //   Authorization: `Bearer ${token}`,
        // },
        params: { count },
      })

      if (response.data.code === "SUCCESS") {
        const sortedGames = response.data.data.gameArr.sort(
          (a: GameData, b: GameData) => new Date(b.gameDate).getTime() - new Date(a.gameDate).getTime(),
        )
        set({ games: sortedGames })
      }
    } catch (error: any) {
      if (error.response?.status === 404) {
        if (error.response.data.code === "GAME_ALREADY_DELETED") {
          toast.error("Game results have already been viewed and deleted")
        }
      } else if (error.response?.status === 401) {
        // Token related errors - commented out for now
        // if (error.response.data.code === "TOKEN_EXPIRED") {
        //   toast.error("Session expired. Please login again.")
        // } else if (error.response.data.code === "INVALID_TOKEN") {
        //   toast.error("Invalid session. Please login again.")
        // } else if (error.response.data.code === "TOKEN_MISSING") {
        //   toast.error("Authorization required. Please login.")
        // }
      } else {
        toast.error("Failed to fetch game data")
      }
    } finally {
      set({ isLoading: false })
    }
  },
}))

