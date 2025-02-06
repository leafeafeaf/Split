"use client"

import { Download } from "lucide-react"
import { ButtonPrimary } from "@/components/ui/button-primary"
import { Card } from "@/components/ui/card"

interface HighlightPlayerProps {
  videoUrl: string
  onDownload: () => void
}

export function HighlightPlayer({ videoUrl, onDownload }: HighlightPlayerProps) {
  return (
    <div className="space-y-4">
      <h2 className="text-2xl text-white text-center font-medium">Highlight</h2>
      <Card className="bg-[#1E1E2D]">
        <div className="relative aspect-video rounded-lg overflow-hidden">
          <video src={videoUrl} controls className="w-full h-full object-cover" />
        </div>
        <div className="mt-4 flex justify-end p-4">
          <ButtonPrimary onClick={onDownload}>
            <Download className="w-4 h-4 mr-2" />
            Download
          </ButtonPrimary>
        </div>
      </Card>
    </div>
  )
}

