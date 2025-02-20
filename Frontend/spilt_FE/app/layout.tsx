import localFont from 'next/font/local'
import "./globals.css"
import { Providers } from "./providers"
import type { ReactNode } from "react"
import type { Metadata } from 'next'

const moneygraphy = localFont({
  src: '../public/fonts/Moneygraphy-Rounded.ttf',
  variable: '--font-moneygraphy',
})

export const metadata: Metadata = {
  title: 'SPLIT',
  description: 'SPLIT application',
  icons: {
    icon: '/check_img/favicon.ico',
  },
}

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en" className={moneygraphy.variable} suppressHydrationWarning>
      <head>
        <link rel="icon" href="/img.ico" sizes="any" />
      </head>
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  )
}
