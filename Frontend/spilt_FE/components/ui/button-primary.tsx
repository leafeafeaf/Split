import type React from "react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

interface ButtonPrimaryProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode
  className?: string
}

export function ButtonPrimary({ children, className, ...props }: ButtonPrimaryProps) {
  return (
    <Button className={cn("bg-[#0066FF] hover:bg-[#0066FF]/90 text-white", className)} {...props}>
      {children}
    </Button>
  )
}

