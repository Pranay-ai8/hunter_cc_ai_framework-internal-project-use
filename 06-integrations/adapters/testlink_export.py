#!/usr/bin/env python3
"""
Export framework-generated Functional Test Cases (.xlsx) into TestLink's
native XML import format (Test Suite / Test Case / Steps schema).

Usage:
    python3 testlink_export.py \
        --input  ../../07-outputs/TestCases/FunctionalTestCases.xlsx \
        --output ../../07-outputs/TestCases/FunctionalTestCases_TestLink.xml

Import in TestLink: Test Specification -> a Test Suite node -> Operations ->
"Import Test Cases" -> choose the generated XML file.

No TestLink API/credentials are required for this path since it produces a
file for TestLink's built-in XML importer. See testlink_export_api.md for the
alternative (API-based) push if you'd rather automate the upload itself.
"""

import argparse
import sys
import xml.etree.ElementTree as ET
from xml.dom import minidom

try:
    import openpyxl
except ImportError:
    sys.exit("Missing dependency: pip install openpyxl")

# TestLink importance levels
IMPORTANCE = {"P1": "3", "P2": "2", "P3": "1", "P4": "1"}
# TestLink execution_type: 1 = Manual, 2 = Automated
EXEC_TYPE_AUTO = "2"
EXEC_TYPE_MANUAL = "1"

# FunctionalTestCases.xlsx has no "Module" column -- module was only a
# markdown section heading. Derive it from the Test Case ID prefix instead
# (TC_<PREFIX>_<SEQ>), matching FunctionalTestCases.md's module structure.
MODULE_BY_PREFIX = {
    "REG": "User Registration",
    "LOGIN": "Login",
    "SEARCH": "Product Search",
    "PDP": "Product Details",
    "CART": "Cart Management",
    "CHK": "Checkout",
    "COUP": "Coupons and Offers",
    "PAY": "Payment",
    "ORD": "Order Confirmation",
    "LOGOUT": "Logout",
}


def load_rows(xlsx_path):
    wb = openpyxl.load_workbook(xlsx_path, data_only=True)
    ws = wb.active
    header = [c.value.strip() if c.value else "" for c in ws[1]]
    rows = []
    for r in ws.iter_rows(min_row=2, values_only=True):
        if not any(r):
            continue
        row = dict(zip(header, r))
        rows.append(row)
    return rows


def module_for(row):
    tc_id = row.get("Test Case ID", "") or ""
    parts = tc_id.split("_")
    if len(parts) >= 2:
        prefix = parts[1]
        if prefix in MODULE_BY_PREFIX:
            return MODULE_BY_PREFIX[prefix]
    return "Uncategorized"


def group_by_module(rows):
    modules = {}
    for row in rows:
        module = module_for(row)
        modules.setdefault(module, []).append(row)
    return modules


def exec_type_for(row):
    candidate = (row.get("Automation Candidate") or "").strip().lower()
    return EXEC_TYPE_AUTO if candidate == "yes" else EXEC_TYPE_MANUAL


def importance_for(row):
    priority = (row.get("Priority") or "").strip().upper()
    for key, val in IMPORTANCE.items():
        if priority.startswith(key):
            return val
    return "2"  # default Medium


def add_cdata(parent, tag, text):
    el = ET.SubElement(parent, tag)
    el.text = text or ""
    return el


def build_xml(modules):
    root = ET.Element("testsuite", {"name": "Amazon E-Commerce Functional Test Cases"})

    for module, rows in modules.items():
        suite = ET.SubElement(root, "testsuite", {"name": module})
        details = ET.SubElement(suite, "details")
        details.text = f"Test cases for the {module} module, exported from FunctionalTestCases.xlsx"

        for row in rows:
            tc_id = row.get("Test Case ID", "")
            scenario = row.get("Test Scenario", "") or tc_id
            objective = row.get("Objective", "")
            preconditions = row.get("Preconditions", "")
            steps_text = row.get("Test Steps", "")
            expected = row.get("Expected Result", "")
            req_id = row.get("Requirement ID", "")

            testcase = ET.SubElement(
                suite,
                "testcase",
                {"name": f"{tc_id} - {scenario}" if tc_id else scenario},
            )

            add_cdata(testcase, "node_order", "0")
            add_cdata(
                testcase,
                "externalid",
                tc_id.replace("TC_", "").replace("_", "-") if tc_id else "",
            )
            add_cdata(testcase, "version", "1")
            add_cdata(
                testcase,
                "summary",
                f"<![CDATA[{objective} (Requirement: {req_id})]]>",
            )
            add_cdata(testcase, "preconditions", f"<![CDATA[{preconditions}]]>")
            add_cdata(testcase, "importance", importance_for(row))
            add_cdata(testcase, "execution_type", exec_type_for(row))
            add_cdata(testcase, "is_open", "1")
            add_cdata(testcase, "active", "1")

            steps = ET.SubElement(testcase, "steps")
            step = ET.SubElement(steps, "step")
            add_cdata(step, "step_number", "1")
            add_cdata(step, "actions", f"<![CDATA[{steps_text}]]>")
            add_cdata(step, "expectedresults", f"<![CDATA[{expected}]]>")
            add_cdata(step, "execution_type", exec_type_for(row))

    return root


def prettify(elem):
    rough = ET.tostring(elem, encoding="utf-8")
    reparsed = minidom.parseString(rough)
    pretty = reparsed.toprettyxml(indent="  ")
    # minidom escapes our literal CDATA markers; restore them so TestLink
    # treats the field bodies as CDATA instead of escaped HTML entities.
    pretty = pretty.replace("&lt;![CDATA[", "<![CDATA[").replace("]]&gt;", "]]>")
    pretty = pretty.replace("&amp;lt;", "&lt;").replace("&amp;gt;", "&gt;")
    return pretty


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--input",
        default="../../07-outputs/TestCases/FunctionalTestCases.xlsx",
        help="Path to the generated FunctionalTestCases.xlsx",
    )
    parser.add_argument(
        "--output",
        default="../../07-outputs/TestCases/FunctionalTestCases_TestLink.xml",
        help="Path to write the TestLink-importable XML file",
    )
    args = parser.parse_args()

    rows = load_rows(args.input)
    if not rows:
        sys.exit(f"No test case rows found in {args.input}")

    modules = group_by_module(rows)
    root = build_xml(modules)

    with open(args.output, "w", encoding="utf-8") as f:
        f.write(prettify(root))

    total_cases = sum(len(v) for v in modules.values())
    print(f"Exported {total_cases} test cases across {len(modules)} modules -> {args.output}")


if __name__ == "__main__":
    main()
