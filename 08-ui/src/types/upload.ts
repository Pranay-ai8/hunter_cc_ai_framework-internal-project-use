export type DocumentCategory =
  | 'PDFs'
  | 'PRD'
  | 'BRD'
  | 'Screenshots'
  | 'Figma'
  | 'API Docs'
  | 'Other'

export interface UploadResult {
  success: boolean
  fileName: string
  category: DocumentCategory
  path: string
  message: string
  error?: string
}

export interface UploadResponse {
  success: boolean
  result?: UploadResult
  error?: string
}
