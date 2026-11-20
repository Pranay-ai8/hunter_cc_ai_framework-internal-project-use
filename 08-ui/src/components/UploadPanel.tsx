'use client'

import { useState, useRef } from 'react'
import { UploadResult } from '@/types/upload'

interface UploadPanelProps {
  onUploadSuccess?: (result: UploadResult) => void
}

export default function UploadPanel({ onUploadSuccess }: UploadPanelProps) {
  const [isDragging, setIsDragging] = useState(false)
  const [isUploading, setIsUploading] = useState(false)
  const [uploadMessage, setUploadMessage] = useState<string>('')
  const [uploadError, setUploadError] = useState<string>('')
  const [recentUploads, setRecentUploads] = useState<UploadResult[]>([])
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(true)
  }

  const handleDragLeave = () => {
    setIsDragging(false)
  }

  const handleUploadFile = async (file: File) => {
    if (!file) return

    setIsUploading(true)
    setUploadError('')
    setUploadMessage('Uploading and categorizing...')

    const formData = new FormData()
    formData.append('file', file)

    try {
      const response = await fetch('/api/upload', {
        method: 'POST',
        body: formData,
      })

      const data = await response.json()

      if (data.success && data.result) {
        setUploadMessage(
          `✓ File uploaded successfully to ${data.result.category} folder`
        )
        setRecentUploads([data.result, ...recentUploads.slice(0, 4)])
        onUploadSuccess?.(data.result)

        setTimeout(() => setUploadMessage(''), 3000)
      } else {
        setUploadError(data.error || 'Upload failed')
      }
    } catch (error) {
      setUploadError(
        error instanceof Error ? error.message : 'Upload failed'
      )
    } finally {
      setIsUploading(false)
      setIsDragging(false)
    }
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)

    const files = e.dataTransfer.files
    if (files.length > 0) {
      handleUploadFile(files[0])
    }
  }

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.currentTarget.files
    if (files && files.length > 0) {
      handleUploadFile(files[0])
    }
    e.currentTarget.value = ''
  }

  return (
    <div className="bg-blue-50 rounded border border-blue-200 overflow-hidden">
      <div className="bg-blue-100 border-b border-blue-200 px-4 py-2.5">
        <h3 className="text-blue-900 text-sm font-semibold">Document Upload</h3>
      </div>

      <div className="p-4">
        {/* Drag and Drop Area */}
        <div
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          className={`border-2 border-dashed rounded p-6 text-center cursor-pointer transition ${
            isDragging
              ? 'border-blue-600 bg-blue-100'
              : 'border-blue-200 hover:border-blue-500'
          }`}
        >
          <input
            ref={fileInputRef}
            type="file"
            onChange={handleFileSelect}
            className="hidden"
            accept=".pdf,.png,.jpg,.jpeg,.heic,.prd,.brd,.figma,application/pdf,image/*"
          />

          <svg
            className="w-8 h-8 mx-auto text-blue-400 mb-2"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 4v16m8-8H4"
            />
          </svg>

          <p className="text-blue-800 text-sm font-medium mb-1">
            Drag and drop your document here
          </p>
          <p className="text-blue-600 text-xs mb-3">
            or click to browse
          </p>

          <button
            onClick={() => fileInputRef.current?.click()}
            disabled={isUploading}
            className="px-3 py-1.5 bg-blue-900 text-white text-xs rounded hover:bg-blue-950 transition disabled:bg-blue-300"
          >
            {isUploading ? 'Uploading...' : 'Browse Files'}
          </button>

          <div className="mt-3 text-xs text-blue-600">
            <p>Supported formats: PDF, PNG, JPG, HEIC</p>
            <p>Files are automatically categorized and placed in the appropriate folders</p>
          </div>
        </div>

        {/* Upload Status Messages */}
        {uploadMessage && (
          <div className="mt-3 p-2.5 bg-blue-100 border border-blue-200 rounded text-blue-800 text-xs">
            {uploadMessage}
          </div>
        )}

        {uploadError && (
          <div className="mt-3 p-2.5 bg-blue-100 border border-blue-200 rounded text-blue-900 text-xs">
            {uploadError}
          </div>
        )}

        {/* Recent Uploads */}
        {recentUploads.length > 0 && (
          <div className="mt-4 border-t pt-3">
            <h4 className="text-sm font-semibold text-blue-900 mb-2">Recent Uploads</h4>
            <div className="space-y-1.5">
              {recentUploads.map((upload, index) => (
                <div
                  key={index}
                  className="p-2 bg-blue-100 rounded border border-blue-100"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <p className="font-medium text-blue-900 text-xs">
                        {upload.fileName}
                      </p>
                      <p className="text-xs text-blue-600 mt-0.5">
                        → {upload.category}
                      </p>
                    </div>
                    <span className="px-2 py-0.5 bg-blue-200 text-blue-800 text-xs rounded font-medium">
                      Uploaded
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Category Legend */}
        <div className="mt-4 p-3 bg-blue-100 rounded border border-blue-100">
          <p className="font-semibold text-blue-800 mb-1.5 text-xs">
            Auto-Categorization Rules:
          </p>
          <ul className="text-xs text-blue-700 space-y-0.5">
            <li><strong>.pdf</strong> → PDFs folder</li>
            <li><strong>PRD</strong> in filename → PRD folder</li>
            <li><strong>BRD</strong> in filename → BRD folder</li>
            <li><strong>.png, .jpg, .jpeg, .heic</strong> → Screenshots folder</li>
            <li><strong>.figma</strong> or Figma in filename → Figma/figma-exports folder</li>
            <li><strong>API</strong> in filename → API Docs folder</li>
            <li><strong>Others</strong> → Other Requirement Docs folder</li>
          </ul>
        </div>
      </div>
    </div>
  )
}
