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

const postures = [
  {
    id: 1,
    imageUrl: '/check_img/bowling1.webp'
  },
  {
    id: 2,
    imageUrl: '/check_img/bowling2.webp'
  },
  {
    id: 3,
    imageUrl: '/check_img/bowling3.webp'
  },
  {
    id: 4,
    imageUrl: '/check_img/bowling4.webp'
  }
]

export default function CheckPage() {
  const router = useRouter()
  const [selectedPosture, setSelectedPosture] = useState<number | null>(null)
  const [isRightHand, setIsRightHand] = useState(true)
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
    router.push("/check/measure")
  }

  return (
    <div className="min-h-screen bg-[#161622] p-4 pb-24">
      <div className="flex justify-between items-center mb-8">
        <ThemeToggle />
        <UserMenu />
      </div>

      <div className="max-w-sm mx-auto space-y-8">
        <div className="relative">
          <div className="space-y-4 max-h-[calc(100vh-300px)] overflow-y-auto pr-2">
            {postures.map((posture) => (
              <div
                key={posture.id}
                className={`w-full bg-gradient-to-r ${
                  selectedPosture === posture.id
                    ? 'from-[#1E1E2D] to-[#2E2E4D] border-2 border-[#0066FF]'
                    : 'from-[#1E1E2D] to-[#252535]'
                } rounded-2xl p-4 flex items-center gap-4 transition-all duration-300 hover:shadow-lg hover:from-[#1E1E2D] hover:to-[#2E2E4D] cursor-pointer`}
                onClick={() => setSelectedPosture(posture.id)}
              >
                <div className="w-20 h-20 overflow-hidden rounded-xl flex items-center justify-center">
                  <PostureCard
                    imageUrl={posture.imageUrl}
                    isSelected={selectedPosture === posture.id}
                    onClick={() => {}}
                  />
                </div>
                <div className="flex flex-col">
                  <span className="text-[#0066FF] text-2xl font-bold mb-1">
                    {posture.id === 1 ? "Stroke" : 
                     posture.id === 2 ? "Dumris" :
                     posture.id === 3 ? "Cranker" :
                     "Two handed"}
                  </span>
                  <span className="text-[#A2A2A7] text-sm">
                    {posture.id === 1 ? "정확성 중심의 안정적인 투구" : 
                     posture.id === 2 ? "엄지 없이 강한 회전과 스핀" :
                     posture.id === 3 ? "강한 손목 스냅과 폭발적인 파워" :
                     "양손을 활용한 회전 극대화"}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="flex justify-end items-center mt-8 mb-4">
          <span className="text-[#A2A2A7] text-sm mr-2">Left</span>
          <HandToggle isRightHand={isRightHand} onToggle={() => setIsRightHand(!isRightHand)} />
          <span className="text-[#A2A2A7] text-sm ml-2">Right</span>
        </div>

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
