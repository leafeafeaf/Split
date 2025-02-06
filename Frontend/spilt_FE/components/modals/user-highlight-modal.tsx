import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"

interface UserHighlightModalProps {
  isOpen: boolean
  onClose: () => void
  userName: string
  videoUrl: string
}

export function UserHighlightModal({ isOpen, onClose, userName, videoUrl }: UserHighlightModalProps) {
  const [videoError, setVideoError] = useState(false)

  useEffect(() => {
    setVideoError(false)
  }, [])

  const handleVideoError = () => {
    setVideoError(true)
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[600px] bg-[#1E1E2D] border-[#2E2E3D]">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold text-white">{userName}'s Highlight</DialogTitle>
        </DialogHeader>
        <div className="mt-4">
          {!videoError ? (
            <video
              src={videoUrl}
              controls
              className="w-full rounded-lg"
              onError={handleVideoError}
              controlsList="nodownload"
            >
              Your browser does not support the video tag.
            </video>
          ) : (
            <div className="w-full h-[300px] flex items-center justify-center bg-[#161622] rounded-lg text-[#A2A2A7]">
              Failed to load video
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  )
}

