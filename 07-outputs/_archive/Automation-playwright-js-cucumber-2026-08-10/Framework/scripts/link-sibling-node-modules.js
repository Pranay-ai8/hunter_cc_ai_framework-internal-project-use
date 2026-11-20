// postinstall — makes bare requires (e.g. require("@cucumber/cucumber")) resolve
// correctly from the sibling ../PageObjects/ and ../Scripts/ folders.
//
// Node's module resolution walks up parent directories of the REQUIRING FILE,
// not the process cwd. Since PageObjects/ and Scripts/ are siblings of this
// Framework/ directory (not descendants of it — see readme.md "Folder Layout"),
// a bare `require("@cucumber/cucumber")` inside e.g. ../Scripts/steps/ui/*.js
// would never find Framework/node_modules by walking up its own ancestry.
//
// Symlinking Automation/node_modules -> Framework/node_modules gives every
// sibling folder a node_modules to find while walking up, without duplicating
// the actual install or requiring a second `npm install` anywhere else.
const fs = require("fs");
const path = require("path");

const target = path.resolve(__dirname, "..", "node_modules");
const linkPath = path.resolve(__dirname, "..", "..", "node_modules");

if (fs.existsSync(linkPath)) {
    console.log("Sibling node_modules link already exists — skipping.");
    process.exit(0);
}

try {
    fs.symlinkSync(target, linkPath, "dir");
    console.log(`Linked ${linkPath} -> ${target}`);
} catch (err) {
    console.error(`Failed to create sibling node_modules link: ${err.message}`);
    console.error("Automation running from ../Scripts or ../PageObjects may fail with MODULE_NOT_FOUND.");
    console.error(`Fix manually with: ln -s Framework/node_modules ${linkPath}`);
}
