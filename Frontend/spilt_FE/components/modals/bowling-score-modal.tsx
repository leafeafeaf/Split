"use client"

import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { ButtonPrimary } from "@/components/ui/button-primary"

interface BowlingScoreModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: (score: number) => void
}

export function BowlingScoreModal({ isOpen, onClose, onSubmit }: BowlingScoreModalProps) {
  const [score, setScore] = useState("")

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const numScore = Number(score)
    if (numScore >= 0 && numScore <= 300) {
      onSubmit(numScore)
    } else {
      alert("Please enter a valid score between 0 and 300.")
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[425px] bg-[#1E1E2D] border-[#2E2E3D]">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold text-white">Enter Bowling Score</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Input
              type="number"
              value={score}
              onChange={(e) => setScore(e.target.value)}
              placeholder="Enter score (0-300)"
              className="bg-[#2E2E3D] text-white border-[#4E4E5D]"
              min={0}
              max={300}
            />
          </div>
          <ButtonPrimary type="submit" className="w-full">
            Submit Score
          </ButtonPrimary>
        </form>
      </DialogContent>
    </Dialog>
  )
}

