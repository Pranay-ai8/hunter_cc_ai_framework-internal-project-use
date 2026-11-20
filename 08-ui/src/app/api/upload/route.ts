import { NextRequest, NextResponse } from 'next/server'
import path from 'path'
import fs from 'fs/promises'
import { DocumentCategory, UploadResult } from '@/types/upload'

function categorizeDocument(fileName: string): DocumentCategory {
  const lowerFileName = fileName.toLowerCase()
  const extension = path.extname(lowerFileName)

  // Check by extension first
  if (extension === '.pdf') {
    return 'PDFs'
  }

  if (['.png', '.jpg', '.jpeg', '.heic'].includes(extension)) {
    return 'Screenshots'
  }

  // Check by filename patterns
  if (lowerFileName.includes('prd')) {
    return 'PRD'
  }

  if (lowerFileName.includes('brd')) {
    return 'BRD'
  }

  if (lowerFileName.includes('figma') || extension === '.figma') {
    return 'Figma'
  }

  if (lowerFileName.includes('api')) {
    return 'API Docs'
  }

  return 'Other'
}

function getDestinationFolder(category: DocumentCategory): string {
  const baseDir = path.join(
    process.cwd(),
    '..',
    '01-docs'
  )

  const folders: Record<DocumentCategory, string> = {
    'PDFs': path.join(baseDir, 'PDFs'),
    'PRD': path.join(baseDir, 'PRD'),
    'BRD': path.join(baseDir, 'BRD'),
    'Screenshots': path.join(baseDir, 'Screenshots'),
    'Figma': path.join(baseDir, 'Figma', 'figma-exports'),
    'API Docs': path.join(baseDir, 'API Docs'),
    'Other': path.join(baseDir, 'Other Requirement Docs'),
  }

  return folders[category]
}

async function ensureDirectoryExists(dirPath: string): Promise<void> {
  try {
    await fs.access(dirPath)
  } catch {
    await fs.mkdir(dirPath, { recursive: true })
  }
}

export async function POST(request: NextRequest) {
  try {
    const formData = await request.formData()
    const file = formData.get('file') as File

    if (!file) {
      return NextResponse.json(
        { success: false, error: 'No file provided' },
        { status: 400 }
      )
    }

    // Get file info
    const fileName = file.name
    const buffer = await file.arrayBuffer()

    // Categorize the document
    const category = categorizeDocument(fileName)
    const destinationFolder = getDestinationFolder(category)

    // Ensure destination folder exists
    await ensureDirectoryExists(destinationFolder)

    // Save the file
    const filePath = path.join(destinationFolder, fileName)
    await fs.writeFile(filePath, Buffer.from(buffer))

    const result: UploadResult = {
      success: true,
      fileName,
      category,
      path: filePath,
      message: `File uploaded successfully to ${category} folder`,
    }

    return NextResponse.json({
      success: true,
      result,
    })
  } catch (error) {
    console.error('Upload error:', error)

    const errorMessage =
      error instanceof Error ? error.message : 'Upload failed'

    return NextResponse.json(
      {
        success: false,
        error: errorMessage,
      },
      { status: 500 }
    )
  }
}
