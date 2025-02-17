// import { configureStore } from "@reduxjs/toolkit"
// import userReducer from "../features/userSlice"
// import themeReducer from "../features/themeSlice"
// import gameReducer from "../features/gameSlice"
// import rankingReducer from "../features/rankingSlice"
// import authReducer from "../features/authSlice"
// import deviceReducer from "../features/deviceSlice"
// import frameReducer from "../features/frameSlice"

// export const store = configureStore({
//   reducer: {
//     user: userReducer,
//     theme: themeReducer,
//     game: gameReducer,
//     ranking: rankingReducer,
//     auth: authReducer,
//     device: deviceReducer,
//     frame: frameReducer,
//   },
// })

// export type RootState = ReturnType<typeof store.getState>
// export type AppDispatch = typeof store.dispatch

import { configureStore } from "@reduxjs/toolkit"
import userReducer from "../features/userSlice"
import themeReducer from "../features/themeSlice"
import gameReducer from "../features/gameSlice"
import rankingReducer from "../features/rankingSlice"
import authReducer from "../features/authSlice"
import deviceReducer from "../features/deviceSlice"
import frameReducer from "../features/frameSlice"

export const store = configureStore({
  reducer: {
    user: userReducer,
    theme: themeReducer,
    game: gameReducer,
    ranking: rankingReducer,
    auth: authReducer,
    device: deviceReducer,
    frame: frameReducer,
  },
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch

