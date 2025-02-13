"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import ThemeToggle from "@/components/ui/theme-toggle"
import { UserMenu } from "@/components/user-menu"
import { NavigationBar } from "@/components/navigation-bar"
import { PostureCard } from "@/components/check/posture-card"
import { HandToggle } from "@/components/check/hand-toggle"
import { ButtonPrimary } from "@/components/ui/button-primary"
import { SerialNumberModal } from "@/components/modals/serial-number-modal"
import { useAppSelector } from "@/app/store/hooks"

// Mock posture data - replace image URLs with actual posture images
const postures = Array.from({ length: 9 }, (_, i) => ({
  id: i + 1,
  imageUrl: `/placeholder.svg?height=400&width=300&text=Posture${i + 1}`,
}))

export default function CheckPage() {
  const router = useRouter()
  const [selectedPosture, setSelectedPosture] = useState<number | null>(null)
  const [isRightHand, setIsRightHand] = useState(true)
  const [currentPage, setCurrentPage] = useState(0)
  const [isSerialModalOpen, setIsSerialModalOpen] = useState(false)
  const { currentSerial } = useAppSelector((state) => state.device)

  const handleStartMeasurement = () => {
    if (selectedPosture === null) {
      alert("Please select a posture first")
      return
    }
    setIsSerialModalOpen(true)
  }

  const handleSerialSubmit = (serialNumber: string) => {
    // After successful device connection, navigate to measure page
    router.push("/check/measure")
  }

  return (
    <div className="min-h-screen bg-[#161622] p-6 pb-24">
      <div className="flex justify-between items-center mb-8">
        <ThemeToggle />
        <UserMenu />
      </div>

      <div className="max-w-md mx-auto space-y-8">
        {/* Posture grid with horizontal scroll */}
        <div className="relative">
          <div className="overflow-x-auto scrollbar-hide">
            <div className="grid grid-cols-3 gap-4 min-w-full">
              {postures.map((posture) => (
                <div key={posture.id} className="w-[120px]">
                  <PostureCard
                    imageUrl={posture.imageUrl}
                    isSelected={selectedPosture === posture.id}
                    onClick={() => setSelectedPosture(posture.id)}
                  />
                </div>
              ))}
            </div>
          </div>
          <div className="absolute bottom-[-24px] right-0 flex items-center gap-2">
            <span className="text-[#A2A2A7] text-sm">Left</span>
            <HandToggle isRightHand={isRightHand} onToggle={() => setIsRightHand(!isRightHand)} />
            <span className="text-[#A2A2A7] text-sm">Right</span>
          </div>
        </div>

        {/* Page indicator */}
        <div className="flex justify-center gap-2 mt-8">
          {Array.from({ length: 3 }, (_, i) => (
            <div key={i} className={`w-2 h-2 rounded-full ${currentPage === i ? "bg-[#0066FF]" : "bg-[#2E2E3D]"}`} />
          ))}
        </div>

        {/* Start measurement button */}
        <ButtonPrimary onClick={handleStartMeasurement} className="w-full py-6 text-lg font-medium">
          측정 시작
        </ButtonPrimary>
      </div>

      <NavigationBar />

      <SerialNumberModal
        isOpen={isSerialModalOpen}
        onClose={() => setIsSerialModalOpen(false)}
        onSubmit={handleSerialSubmit}
      />
    </div>
  )
}

