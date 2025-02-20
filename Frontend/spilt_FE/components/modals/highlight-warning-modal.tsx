"use client"

import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { ButtonPrimary } from "@/components/ui/button-primary"
import { useAppSelector } from "@/app/store/hooks"

interface HighlightWarningModalProps {
  isOpen: boolean
  onClose: () => void
  onConfirm: () => void
}

export function HighlightWarningModal({ isOpen, onClose, onConfirm }: HighlightWarningModalProps) {
  const { user } = useAppSelector((state) => state.user)
  const isUpdate = !!user?.highlight

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="bg-[#1E1E2D] border-[#2E2E3D] p-6 max-w-md">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold text-white text-center">Warning</DialogTitle>
          <DialogDescription className="text-[#A2A2A7] text-center text-base mt-2">
            {isUpdate
              ? "하이라이트 영상을 수정하시겠습니까?"
              : "한 게임에 대한 하이라이트 영상을 더이상 변경할 수 없습니다."}
          </DialogDescription>
        </DialogHeader>
        <div className="flex justify-center mt-6">
          <ButtonPrimary onClick={onConfirm} className="w-32">
            확인
          </ButtonPrimary>
        </div>
      </DialogContent>
    </Dialog>
  )
}

