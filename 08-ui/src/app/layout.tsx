import type { Metadata } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: 'QA Framework Agent UI',
  description: 'Master Agent Control Panel for QA Testing Framework',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className="bg-[#050a14] text-sm">
        <div className="min-h-screen">
          {children}
        </div>
      </body>
    </html>
  )
}
