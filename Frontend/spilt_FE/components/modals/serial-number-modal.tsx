"use client"

import { useState } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { ButtonPrimary } from "@/components/ui/button-primary"

interface SerialNumberModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: (serialNumber: string) => void
}

export function SerialNumberModal({ isOpen, onClose, onSubmit }: SerialNumberModalProps) {
  const [serialNumber, setSerialNumber] = useState("")

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (serialNumber.trim()) {
      onSubmit(serialNumber.trim())
      onClose()
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
              <h2 className="text-2xl font-bold text-white">Serial Number</h2>
            </div>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label htmlFor="serialNumber" className="block text-sm font-medium text-[#A2A2A7] mb-2">
                  Enter Serial Number
                </label>
                <input
                  type="text"
                  id="serialNumber"
                  value={serialNumber}
                  onChange={(e) => setSerialNumber(e.target.value)}
                  className="w-full px-4 py-2 bg-[#2E2E3D] text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0066FF] transition-all"
                  required
                />
              </div>
              <ButtonPrimary type="submit" className="w-full py-3 text-lg font-medium">
                Submit
              </ButtonPrimary>
            </form>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  )
}

