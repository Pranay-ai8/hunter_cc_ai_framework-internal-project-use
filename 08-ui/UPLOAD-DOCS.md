# Document Upload Feature

The UI now includes a powerful document upload feature that automatically categorizes and organizes your documents into the appropriate folders in `01-docs/`.

## Overview

Instead of manually organizing documents, you can now:
1. Click the "Upload Docs" tab
2. Drag and drop (or browse) your document
3. The system automatically detects the document type
4. Files are placed in the correct folder in `01-docs/`

## How It Works

### Step 1: Navigate to Upload Tab

Open the UI and click the **"📤 Upload Docs"** tab.

### Step 2: Upload Your Document

**Option A: Drag and Drop**
- Drag a file from your computer
- Drop it on the upload area

**Option B: Browse**
- Click "Browse Files" button
- Select file from your system
- File uploads automatically

### Step 3: Automatic Categorization

The system analyzes your file and determines the destination:

```
File Analysis
    ↓
Detect Type (extension, filename patterns)
    ↓
Determine Category
    ↓
Copy to Appropriate Folder in 01-docs/
    ↓
Display Success Message
    ↓
Show in "Recently Uploaded" list
```

## Categorization Rules

The upload system automatically categorizes files based on these rules (in priority order):

| File Type | Detection | Destination |
|-----------|-----------|-------------|
| **PDF** | Extension: `.pdf` | `01-docs/PDFs/` |
| **PRD** | Filename contains "prd" (case-insensitive) | `01-docs/PRD/` |
| **BRD** | Filename contains "brd" (case-insensitive) | `01-docs/BRD/` |
| **Screenshots** | Extension: `.png`, `.jpg`, `.jpeg`, `.heic` | `01-docs/Screenshots/` |
| **Figma Exports** | Extension: `.figma` or filename contains "figma" | `01-docs/Figma/figma-exports/` |
| **API Docs** | Filename contains "api" (case-insensitive) | `01-docs/API Docs/` |
| **Other Docs** | Everything else | `01-docs/Other Requirement Docs/` |

## Examples

### Example 1: PDF Document
```
File: Requirements.pdf
↓
Detected: .pdf extension
↓
Category: PDFs
↓
Saved to: 01-docs/PDFs/Requirements.pdf
```

### Example 2: PRD Document
```
File: Product_Requirements_Document.txt
↓
Detected: "prd" in filename
↓
Category: PRD
↓
Saved to: 01-docs/PRD/Product_Requirements_Document.txt
```

### Example 3: Figma Export
```
File: app_ui_designs.figma
↓
Detected: .figma extension
↓
Category: Figma
↓
Saved to: 01-docs/Figma/figma-exports/app_ui_designs.figma
```

### Example 4: Screenshot
```
File: login_screen.png
↓
Detected: .png extension
↓
Category: Screenshots
↓
Saved to: 01-docs/Screenshots/login_screen.png
```

### Example 5: API Documentation
```
File: swagger_API_specification.json
↓
Detected: "api" in filename
↓
Category: API Docs
↓
Saved to: 01-docs/API Docs/swagger_API_specification.json
```

## Supported Formats

### Documents
- `.pdf` - PDF files

### Images
- `.png` - PNG images
- `.jpg` - JPEG images
- `.jpeg` - JPEG images
- `.heic` - Apple HEIC images

### Design Files
- `.figma` - Figma export files

### Text Files
- `.txt`, `.md`, `.doc`, `.docx` - Any text format (categorized by rules)

## Features

### 1. Drag and Drop Interface
- Visual feedback when dragging files
- Color changes to indicate drop zone

### 2. Real-Time Feedback
- Upload status message
- Success notification with category
- Error messages if something goes wrong

### 3. Recent Uploads List
- Shows last 5 uploaded documents
- Displays filename and category
- Quick reference during session

### 4. Automatic Folder Creation
- Creates folders if they don't exist
- Handles Figma/figma-exports subfolder creation
- Maintains consistent structure

### 5. Smart Detection
- File extension analysis
- Filename pattern matching
- Case-insensitive matching

## Folder Structure Created

After uploads, your `01-docs/` folder looks like:

```
01-docs/
├── PDFs/
│   ├── Requirements.pdf
│   └── Guidelines.pdf
├── PRD/
│   ├── Product_Plan.txt
│   └── Features.md
├── BRD/
│   └── Business_Requirements.doc
├── Screenshots/
│   ├── login_screen.png
│   ├── dashboard.jpg
│   └── workflow.heic
├── Figma/
│   ├── figma-exports/
│   │   └── app_designs.figma
│   ├── figma-links.md
│   └── README.md
├── API Docs/
│   ├── swagger_API.json
│   └── endpoints.md
└── Other Requirement Docs/
    └── misc_doc.txt
```

## UI Components

### Upload Panel Header
- Shows "Document Upload" title
- Located in upload tab

### Drop Zone
- Drag files here to upload
- Click to browse files
- Shows hover effect

### Upload Status
- Success message: Green background
- Error message: Red background
- Auto-dismisses after 3 seconds (success)

### Recently Uploaded List
- Shows uploaded files in current session
- Displays filename and category
- Last 5 uploads visible

### Category Legend
- Shows auto-categorization rules
- Color-coded by file type
- Help reference

## API Endpoint

### POST /api/upload

**Request:**
```
Content-Type: multipart/form-data
Body: 
  - file: [File object]
```

**Response (Success):**
```json
{
  "success": true,
  "result": {
    "success": true,
    "fileName": "Requirements.pdf",
    "category": "PDFs",
    "path": "/path/to/01-docs/PDFs/Requirements.pdf",
    "message": "File uploaded successfully to PDFs folder"
  }
}
```

**Response (Error):**
```json
{
  "success": false,
  "error": "Error message describing what went wrong"
}
```

## How Files Are Saved

1. **File Received** - Browser sends file to `/api/upload`
2. **Analysis** - Server analyzes filename and extension
3. **Categorization** - System determines destination folder
4. **Folder Check** - Creates folder if it doesn't exist
5. **File Save** - Writes file to destination
6. **Response** - Returns success with file info
7. **UI Update** - Shows success message and adds to recent list

## Error Handling

| Error | Cause | Solution |
|-------|-------|----------|
| "No file provided" | No file selected | Select a file and try again |
| "Upload failed" | Server error | Check browser console for details |
| Path permission error | Can't write to folder | Check folder permissions |
| Disk full error | No space available | Free up disk space |

## Tips & Best Practices

### Naming Conventions

**For PRD Documents:**
```
✓ Good:  Product_Requirements_Document.pdf
✓ Good:  PRD_v2.docx
✓ Good:  prd-features.txt
✗ Bad:   Document.pdf  (not detected as PRD)
```

**For BRD Documents:**
```
✓ Good:  Business_Requirements.pdf
✓ Good:  BRD_2024.docx
✓ Good:  brd-scope.txt
✗ Bad:   Requirements.pdf  (not detected as BRD)
```

**For Screenshots:**
```
✓ Good:  login_screen.png
✓ Good:  dashboard_v2.jpg
✓ Good:  workflow_diagram.heic
✓ Good:  screen-capture.png
```

**For Figma Exports:**
```
✓ Good:  app_designs.figma
✓ Good:  ui_components.figma
✓ Good:  figma-export.figma
✗ Bad:   export.txt  (not detected as Figma)
```

### Upload Order

Typically upload in this order:
1. **Requirements** (BRD, PRD, requirements documents)
2. **Design Files** (Figma exports, mockups)
3. **Screenshots** (UI screenshots, flow diagrams)
4. **API Docs** (Swagger, API specifications)
5. **Supporting Docs** (guidelines, other docs)

This ensures all foundational documents are in place first.

## Troubleshooting

### File Not Uploading

**Check:**
1. Is the file format supported?
2. Is the file size reasonable (< 100MB)?
3. Are there permission issues on the destination folder?
4. Check browser console (F12) for error messages

### File Uploaded to Wrong Category

**Fix:**
1. Move file manually to correct folder
2. Or re-upload with corrected filename
3. Example: Rename "doc.txt" to "PRD_doc.txt" if it should be in PRD folder

### Can't Find Uploaded File

**Check:**
1. Look in the folder indicated in the success message
2. Check the "Recently Uploaded" list in the Upload tab
3. Check `01-docs/` folder structure
4. Verify file wasn't uploaded with different name

### Permission Denied Error

**Fix:**
1. Check folder permissions: `chmod 755 01-docs/`
2. Check if running server has write access
3. Try uploading to a different category folder
4. Restart the server

## Advanced Usage

### Bulk Categorization

If you have many documents to organize:

1. **Rename files first** - Use correct naming patterns
2. **Upload in batches** - Category by category
3. **Verify structure** - Check 01-docs/ folders
4. **Document notes** - Record any manual adjustments

### Custom Rules

To add custom categorization rules, edit:
```
08-ui/src/app/api/upload/route.ts
```

Function to modify:
```typescript
function categorizeDocument(fileName: string): DocumentCategory {
  // Add custom rules here
}
```

### Integration with Agent

The uploaded documents are now available to your master agent:
- Agent can read from `01-docs/` folders
- Use uploaded requirements for test case generation
- Use screenshots for UI automation
- Reference API docs for API testing

## Future Enhancements

Potential improvements:
- [ ] Multiple file uploads at once
- [ ] Upload progress bar
- [ ] File preview before categorization
- [ ] Custom category mapping per project
- [ ] Automatic backup of uploaded files
- [ ] Integration with Google Drive / OneDrive
- [ ] Metadata extraction (author, date, etc.)
- [ ] Upload scheduling / automation
- [ ] File version control
- [ ] Integration with CI/CD pipelines

## FAQ

**Q: Can I upload multiple files at once?** A: Currently one at a time. Upload one, then upload the next.

**Q: What happens if I upload a file with the same name?** A: It overwrites the existing file (you should see a success message).

**Q: Can I undo an upload?** A: No, but you can delete the file from the folder manually.

**Q: Where do files actually get saved?** A: In your project folder: `/CC-QA-AI-Framework/01-docs/[Category]/`

**Q: How large can files be?** A: Node.js by default allows up to 1MB per upload. For larger files, modify Next.js config.

**Q: Can I customize the categories?** A: Yes, edit `/08-ui/src/app/api/upload/route.ts` and add custom categorization rules.

**Q: Do uploaded files get processed by the master agent?** A: Yes! Your master agent can read from `01-docs/` and use the uploaded documents.

---

**Start uploading your documents!**

Click the "📤 Upload Docs" tab and drag a file to get started.
