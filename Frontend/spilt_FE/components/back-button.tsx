import { ChevronLeft } from "lucide-react"
import { useRouter } from "next/navigation"

export default function BackButton() {
  const router = useRouter()

  return (
    <button
      onClick={() => router.back()}
      className="w-10 h-10 rounded-full bg-[#1E1E2D] flex items-center justify-center text-[#A2A2A7] hover:text-white transition-colors"
    >
      <ChevronLeft className="h-6 w-6" />
    </button>
  )
}

