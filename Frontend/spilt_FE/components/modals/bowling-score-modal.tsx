"use client"

import { useState } from "react"
import { motion, AnimatePresence } from "framer-motion"
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
    const numScore = Number.parseInt(score, 10)
    if (!isNaN(numScore) && numScore >= 0 && numScore <= 300) {
      onSubmit(numScore)
      onClose()
    } else {
      alert("Please enter a valid score between 0 and 300.")
    }
  }

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
          onClick={onClose}
        >
          <motion.div
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.9, opacity: 0 }}
            className="bg-gradient-to-br from-[#1E1E2D] to-[#161622] p-6 rounded-2xl shadow-xl w-full max-w-md"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-white">Bowling Score</h2>
            </div>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label htmlFor="score" className="block text-sm font-medium text-[#A2A2A7] mb-2">
                  Score (0-300)
                </label>
                <input
                  type="number"
                  id="score"
                  value={score}
                  onChange={(e) => setScore(e.target.value)}
                  className="w-full px-4 py-2 bg-[#2E2E3D] text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0066FF] transition-all"
                  min="0"
                  max="300"
                  required
                />
              </div>
              <ButtonPrimary type="submit" className="w-full py-3 text-lg font-medium">
                Submit Score
              </ButtonPrimary>
            </form>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  )
}

