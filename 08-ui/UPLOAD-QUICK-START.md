# Document Upload - Quick Start

## 30-Second Overview

Your QA Framework UI now has a **document upload feature** that automatically organizes your files into the correct folders in `01-docs/`.

## ⚡ Quick Start

```bash
# 1. Make sure dev server is running
npm run dev

# 2. Open browser
http://localhost:3000

# 3. Click "📤 Upload Docs" tab

# 4. Drag and drop a file
# (or click "Browse Files")

# Done! File is automatically organized
```

## 📁 Where Files Go

| File Type | Where It Goes | How It's Detected |
|-----------|---------------|-------------------|
| 📄 PDF | `01-docs/PDFs/` | `.pdf` extension |
| 📋 PRD | `01-docs/PRD/` | "prd" in filename |
| 📋 BRD | `01-docs/BRD/` | "brd" in filename |
| 🖼️ Screenshot | `01-docs/Screenshots/` | `.png`, `.jpg`, `.jpeg`, `.heic` |
| 🎨 Figma | `01-docs/Figma/figma-exports/` | `.figma` or "figma" in name |
| 📚 API Docs | `01-docs/API Docs/` | "api" in filename |
| ❓ Other | `01-docs/Other Requirement Docs/` | Anything else |

## 📝 Good Filenames

```
PRD Documents:
✓ Product_Requirements.pdf
✓ PRD_Document.txt
✓ my_prd_file.doc

BRD Documents:
✓ Business_Requirements.pdf
✓ BRD_Document.txt
✓ our_brd_file.doc

Screenshots:
✓ login_screen.png
✓ dashboard.jpg
✓ workflow.heic

Figma:
✓ app_designs.figma
✓ figma_export.figma
✓ ui_components.figma

API Docs:
✓ swagger_API.json
✓ api_specification.txt
✓ REST_API.md
```

## 🎯 5-Minute Test

```
1. Create a text file: "My_PRD_Document.txt"
2. Go to Upload Docs tab
3. Drag file onto drop zone
4. You should see: "✓ File uploaded successfully to PRD folder"
5. Check: 01-docs/PRD/My_PRD_Document.txt should exist
```

## 🔧 Files Changed

- ✅ Added `src/components/UploadPanel.tsx`
- ✅ Added `src/app/api/upload/route.ts`
- ✅ Added `src/types/upload.ts`
- ✅ Updated `src/app/page.tsx`
- ✅ Updated `README.md`
- ✅ Added `UPLOAD-DOCS.md` (comprehensive guide)

## 🚀 All Set!

Your document upload feature is ready to use. For detailed documentation, see [UPLOAD-DOCS.md](UPLOAD-DOCS.md).

## ❓ What If?

**What if I upload a file with wrong name?**
- Manually move it to correct folder, or
- Re-upload with corrected filename

**What if upload fails?**
- Check browser console (F12)
- Make sure folder has write permissions
- Try uploading a smaller file first

**Where are the files saved?**
- In your project folder: `CC-QA-AI-Framework/01-docs/[Category]/`

---

**Start uploading!** Click "📤 Upload Docs" tab and drag a file. ✨
