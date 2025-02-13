import "./globals.css"
import { Providers } from "./providers"
import type { ReactNode } from "react"

export const metadata = {
  generator: "v0.dev",
}

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  )
}



import './globals.css'