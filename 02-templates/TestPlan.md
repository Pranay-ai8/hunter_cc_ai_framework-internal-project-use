**Revision History**

| **Version** | **Date** | **Author** | **Reviewed By** | **Authored By** | **Description** |
|:---|:---|:---|:---|:---|:---|
| **0.1** |    |    |    |    | Initial Draft |
| :--- | :--- | :--- | :--- | :--- | :--- |

**Table of Contents**

## Table of Contents

## Table of Contents


 1. [Introduction](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-introduction)
 2. [References](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-references)
 3. [Terms and Abbreviations](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-terms-and-abbreviations)
 4. [Testing Objectives](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-testing-objectives)
    * [Areas in Scope](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-areas-in-scope)
      * [Browser & Operating System Support](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-browser--operating-system-support)
      * [GUI](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-gui)
    * [Areas Out of Scope](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-areas-out-of-scope)
 5. [Testing Process](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-testing-process)
 6. [Planning, Implementation, & Release](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-planning-implementation--release)
 7. [Test Documentation](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-test-documentation)
 8. [Test Strategy](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-test-strategy)
    * [Overview](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-overview)
    * [Test Method](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-test-method)
    * [Testing Approach Per Feature](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-testing-approach-per-feature)
 9. [Test Tools](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-test-tools)
10. [Web Services Testing](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-web-services-testing)
11. [Build Process](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-build-process)
12. [Test Deliverables](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-test-deliverables)
13. [Exit/Entry Criteria](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-exitentry-criteria)
    * [Implementation Phase Exit](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-implementation-phase-exit)
    * [Beta Phase Exit](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-beta-phase-exit)
    * [Final Candidate Entry](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-final-candidate-entry)
14. [Risks and Assumptions](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-risks-and-assumptions)
    * [Assumptions](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-assumptions)
    * [Risks](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-risks)
15. [Resources](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-resources)
    * [QA Staffing & Responsibilities](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-qa-staffing--responsibilities)
    * [Test Environments](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-test-environments)
    * [OS Platforms](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-os-platforms)
    * [Version Requirements](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-version-requirements)
16. [Appendix](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-appendix)
    * [Test Cases](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-test-cases)
    * [Test Pass Results](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-test-pass-results)
    * [Issues](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-issues)
    * [Escalation](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-escalation)
    * [Triage](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-triage)
    * [Test Cycles](https://wiki.codecraft.in/doc/test_plan_template-mqz5Q1JriT#h-test-cycles)

## Introduction

This document prescribes the scope, approach, resources and schedule of the testing CodeCraft QA will perform on the **<Project Name>** project. This document will identify the items and features to be tested, any testing tasks that must be performed, who is responsible for these tasks and any risks that are associated with this plan.

## References

| **Document Title** | **Description** | **Version** |
|:---|:---|:---|
| **<Document Name/ Link>** |    | 0.01 |
|    |    |    |

*Table 1.1: References*

## Terms and Abbreviations

| **Term or Acronym** | **Description** |
|:---|:---|
| **<Project Name>** | Project name. |
|    |    |
| **Smoke Test** | Test of a product build to verify that core functionality is present and testing of the build can proceed. Designed to catch blocking issues and reject builds which are significantly untestable. |
|    |    |
| **Code Complete** | When the product or specific feature area meets all related functional requirements in the Design documents, and is ready for debugging. |
|    |    |
| **Sprint Complete** | When development is complete, and QA has tested all features of that sprint. |
|    |    |
| **Feature Complete (Beta)** | When the development of the product or specific feature area has implemented all functional requirements in the Design documents, no further functionality is yet to be implemented and there is no opened Priority 1 or Severity 1 issues. |
|    |    |
| **Test Area** | Functional groups of requirements related to one area of product functionality. E.g. “Login” would be a Test Area that tests all requirements and use case scenarios related to a user logging in to the system. |
|    |    |
| **Final Candidate (FC)** | A build of the application that QA has certified to meet the contractual obligations of the project. |
|    |    |
|    |    |
| **JIRA** | The task tracking, defect tracking and Test Case Management tool used by CodeCraft QA |
|    |    |

## Testing Objectives

The objective of Codecraft QA during the **<Project Name>** project is primarily to perform testing of the application flow, such that it can be determined that the requirements as outlined in the requirements(can be mentioned in detail as epics and stories in jira) have been met.

QA is also responsible for the following:

* Deployment of versions tested to UAT and Production environment
* Creating and maintaining test case suites and defect reports in **<Project Management Tool>**

### Areas in scope

All features and functionalities mentioned in the requirements in the form of epics and stories in jira of **<Project Name>** are considered IN scope for testing.

Proceeding from the requirements, the list of features deemed to be In Scope for verification in **<Project Name>** will include:

#### Browser & Operating System Support

The developed product will be tested in all browser/operating system environments as clearly specified in the requirements. Epics/stories will need to clearly call out the expected outcome of the story and QA must verify that accordingly.

#### GUI

We will verify that GUI matches the style guides and visual design document (ex: Figma design) supplied by Codecraft Graphics team. In case of backend APIs, detailed API level testing will be done. 

### Areas out of scope

Only the stories accepted in the sprint will be considered for testing. 


## Testing process

This section will describe how the testing portion of the project will be structured: what documents will be produced, what phases the testing will go through, how the test results will be recorded and issues logged.

## Planning, Implementation, & Release

**Planning:** During this phase, Codecraft QA is responsible for gathering the necessary details regarding the application requirements as well as any other details needed for the implementation phase. Details required during this phase include an assessment of the types of testing performed, the environment requirements, and necessary tools.

**Implementation** – Once planning is completed, Codecraft QA will proceed to setup the test environment, create the test data, and write the test cases. JIRA will be used by Codecraft QA to document all test cases and defects. Test execution and defect reporting will proceed per feature as they are considered code complete and become available from development. Regular test reports will be issued during this phase.

**Sprints** – Codecraft QA, will start testing as soon as development Sprint begins. At every Sprint end, QA will have verified all features of that sprint. Execute all test cases and report bugs found during that sprint.

**Release** – Once the application is declared *Feature Complete*, Codecraft QA will begin a full regression cycle, which executes the full test case suite. There may have multiple versions in each cycle, so that defects are verified as they are fixed, but the version that is sent to the customer must complete a full regression. This means all environments are tested.

## Test documentation

As well as this Test Plan, a Test Requirements document will be created. It is the cross-reference document that will link all Test Cases back to the Design document(s). Upon project completion, CodeCraft QA will deliver these documents along with all test suites and test results.

## Test Strategy

The strategy being employed during the testing of Project <Project Name> is as follows:

## Overview

This section will list out the detail necessary to describe the levels and types of testing that Codecraft QA will do during the course of the project.

New test cases will be written for **<Project Name>** specific features. All test cases and defects will be tracked in **<Codecraft’s/ Client’s>** Issue Tracking system – **<Project Management Tool> such as Jira.**

It is assumed that before any code is submitted to the Git for **<Project Name>**, that it has been unit tested by the submitting developer.

All issues or defects found by QA will be logged in the QA defect tracking system, **<Project Management Tool>**. **<Project Management Tool/ Google Sheet>** will also store the complete Test Case suite for the project and the test history data for the lifecycle of the project.

## Test method

Testing methods being employed in Project **<Project Name>** are to be determined based on the features being tested.


1. When validating UI, Codecraft QA will use the designs provided by the Codecraft’s Graphics team to ensure that the UI designed/envisioned is the UI built and delivered.
2. Codecraft QA works alongside of the Development Team and when needed will make requests for order of features to enable the quickest possible turn-around of issues as they arise.
3. Functional testing will be based on the requirements provided in the visual design document. Where there is any additional details required, Codecraft QA will work closely with the client to generate the necessary use cases or stories to ensure full functional coverage.
4. Regression testing will commence once the application is declared *Feature Complete*. At this point, an end-to-end execution approach will be adopted in order to ensure that the application integrity is maintained and bear out any possible risk areas.
5. Critical Path testing will target features and functionality that the user will see and use every day. Critical Path test cases must pass

By the end of every test cycles. Thus, the Critical Path test cases must all be executed at least once during each iteration cycle, and once during the final candidate cycle.

## Testing Approach Per Feature

The major features listed here will be subject to both GUI and Functional testing. The following features will have specific approaches for their testing:

### <Project Name> Flow

| **Feature** | **Testing Approach** |
|:---|:---|
| <Feature Name> | <describe Testing approach here> |
| :--- | :--- |


## Test Tools

Codecraft will use following third-party applications during the testing process:

* **Charles –** a web proxy ([HTTP Proxy](http://www.charlesproxy.com/documentation/additional/http-proxy/) / [HTTP Monitor](http://www.charlesproxy.com/documentation/additional/http-monitor/)) running on the tester computer. The web browser (or any other Internet application) is configured to access the Internet through Charles, and is then able to record and display data that is sent and received.
* **Postman** – Rest API Client
* **JIRA–** Project Management Tool, to track project progress, track test cases, bugs etc…

## Web services Testing

Codecraft QA’s **<Project Name>** testing will be limited to,

* Front end sends appropriate information or request to the <Project Name> backend.
* Verify the information that was sent by APIs, can be retrieved and information retrieved is correct.

## Build process

<Codecraft Developers will commit all their code to a Branch in Git.

QA will push this branch to “test” branch, to test the new features.

Once the features have been tested and all bugs fixed, the code from “test” branch will be pushed to production.>

## Test Deliverables

Codecraft QA will deliver the following materials upon Release acceptance:

* Current, updated version of this test plan document.
* Summary of results of testing covered by this test plan as well as all tests performed.
* All Object Code and files.
* Source code including Build Environment and any development tools written for this Product.
* All technical documentation and the final Product Specification.
* An export of all Test Cases developed and all Defects entered from **<Project Management Tool>.**

## Exit/Entry Criteria

For the **<Project Name>** project, the following acceptance criteria are outlined below and will be adhered to throughout.

## Implementation Phase Exit

Codecraft QA will test features at every sprint and validate them. QA will verify and validate all bugs fixed from the previous sprint. This phase is marked as complete, once QA has verified all features of the project in each consecutive sprint.

## Beta Phase Exit

A full regression sweep will be performed during the Beta 1 cycle. If no Severity 1 or 2 defects remain by the end of the Beta 1 milestone, the build is released to customer and the Beta 1 cycle is considered complete. If **new** Severity 1 or 2 defects are reported then they are marked as highest priority for immediate resolution and the new build is released for testing as Beta 2. These standards are to be maintained for each subsequent beta release cycle.

## Final Candidate Entry

At this point, when regression testing is complete there should be no more than 10 open Severity 3 defects left in **<Project Management Tool>** and no Severity 1 or 2 defects open. If this is the case then *Final Candidate* build can be declared (having met the contract quality requirements). If defects are discovered that keep the quality level from reaching *Final Candidate* standard (as defined by the contract with Customer) then more builds are made until enough defect fixes have been perform to Reach *Final Candidate* release quality.

For the **<Project Name>** project, the contract quality requirements are as follows:

* No known Severity 1 defects caused by Codecraft code
* No known Severity 2 defects caused by Codecraft code (Probability 2 or higher) and Severity definitions can be found in section 15.3 of this document

## Risks and Assumptions

## Assumptions

* Specific functional requirements are adequately defined so as to not hinder the Development and QA efforts.
* All builds delivered to QA will have been unit tested by Development and no Release build will be made for QA testing if unit tests are failing.

## Risks

|    | **Description of risk** | **Contingency Plan** | **Impact** | **Probability** |
|:---|:---|:---|:---|:---|
| **1** | Schedule slippage due to changing requirements after app features have been developed | Changing requirements would require a Change Order process to be followed that would result in (among other things) an updated schedule timeline after input from all stakeholders. | Medium | Medium |
| :--- | :--- | :--- | :--- | :--- |
| **2** | Issues in JIRA being ‘stalled’ due to non-responsiveness by the issue assignee. | Issues blocking delivery of a sprint will be brought to the attention of the team at daily scrum meeting and expected to be responded to within 24 hours. | High | Medium |
| :--- | :--- | :--- | :--- | :--- |

*Table 6.1: Risks and Contingencies*

## Resources

## QA Staffing & Responsibilities

The following staffing will be required for testing of Cox Homelife Care.

| **Position** | **Person** | **Responsibilities** |
|:---|:---|:---|
| **QA Lead** | <Resource Name> | -   Manage QA effort<br>-   Manage defect process<br>-   Plan QA for project<br>-   Monitor software quality and evaluate against milestone criteria<br>-   Create test cases<br>-   Execute tests<br>-   Write defect reports<br>-   Test defect fixes |
| :--- | :--- | :--- |
| **QA Engineer** | <Resource Name> | -   Act as a knowledge resource within areas of expertise for the QA department.<br>-   Create test cases<br>-   Execute tests<br>-   Write defect reports<br>-   Test defect fixes<br>-   Create test environment |
| :--- | :--- | :--- |

*Table 7.1: Staffing & Responsibilities*

## Test Environments

**<Project Name>** Application is developed only to support Mobile phone devices not for Tablets. Hence the testing will be performed only on Android and iOS Phones.

Below is a description of the supported platforms for **<Project Name>.** **<Project Name>** may perform additional platform-specific testing, but any issues arising from this testing are deemed to be outside the scope of this project.

**Note: Please update this section accordingly and delete this line**

## OS Platforms

The following Operating Systems are supported for <Project Name> (see also Section 4.1.1):

* Android
* iOS

**Note: Please update this section accordingly and delete this line**

## Version Requirements

Codecraft QA will test all Cox Homelife Care features on Android mobile devices supporting OS Version greater than 4.4 with Screen size : 5 inch \[540x960 pixels\],

5\.5 inch \[1080x1920 pixels\],

6\.0 inch \[720x1440 pixels\]

iPhone mobile devices supporting OS Version greater than 9.0 with screen size:4 inch \[1136x640 pixels\],

4\.7 inch \[1334x750 pixels\],

5\.5 inch \[1920x1080 pixels\]

**Note: Please update this section accordingly and delete this line**

## Appendix

## Test Cases

All test cases will be stored in **<Project Management tool/ Google Sheet>**. Test cases will be prioritized according to Functional Test Level as described by the table below. These levels do not mean that some tests will not be executed.

| **Level** | **Description** |
|:---|:---|
| **Smoke** | Subset of all tests used to determine if a build is suitable for testing |
| :--- | :--- |
| **1: Critical** | Tests main required functionality |
| :--- | :--- |
| **2: High** | Tests ancillary required functionality |
| :--- | :--- |
| **3: Medium** | Tests edge cases |
| :--- | :--- |
| **4: Low** | Tests usability requirements |
| :--- | :--- |

*Table 9.1: Functional test Level*

## Test Pass Results

The **<Codecraft QA Lead/ Codecraft QA>** will compile summarized results of test passes. Codecraft will only consider the product complete once QA gives final sign-off on the commercially ready state of the product and the number and quality of bug remaining meets contracted deliverable status.

## Issues

Any defect found in or other issue raised against **<Project Name>** will be logged and tracked in **<Project Management Tool>.** (*see figure 1.0 for* ***<Project Management>*** *workflow*)

Each issue will be assigned a Priority based on the following table. These criteria will be used to determine how or when the issue will be addressed, but does not mean that any issue will be ignored.

| **Priority** | **Description** |
|:---|:---|
| **1 (Immediate)** | **Crash/data loss and/or barrier to testing or any major issue that impedes the users’ ability to progress through the application** or greatly damages user experience/brand fidelity. For example: the application crashes when attempting to access an area |
| :--- | :--- |
| **2 (High)** | **A loss of functionality with no workaround** or any major issue that does not impede the users’ ability to progress through the application yet still damages user experience significantly. For example: the application frame rate drops for a brief period every time the user interacts with the device touch screen. |
| :--- | :--- |
| **3 (Normal)** | **Loss of functionality with workaround or any issue, which cannot be classed as major but still creates a negative user experience.** For example: there is an error in a non-high-traffic area of the app. |
| :--- | :--- |
| **4 (Low)** | Usability issues or any defect involving enhancements and design issues, or minor irregularities that do not affect the software’s functionality. |
| :--- | :--- |

*Table 9.2: Issue Priority Definitions*

**Figure 1.0**: **<Project Management Tool>** Workflow ![Jira WorkFlow](/api/attachments.redirect?id=c34ef3c5-84ad-419b-bef7-b3735fc869e9)

## Escalation

If a Blocking issue that significantly prevents testing is logged, email notification will be sent to **<Project Name>** Project Manager, Lead Developer, and Lead QA with a link to the issue in **<Project Management Tool>**. Likewise, as the product nears *Feature Complete* status, the logging of new Critical issues will be escalated to the project team.

## Triage

During the Beta phase daily triage meetings with the **<Project Name>** Project Manager, Lead Developer, and Lead QA will be held during this phase. Together they will decide the resolution schedule of the issue. Typically the priority of issues entered into **<Project Management Tool>** determines the order the issues are attended to; highest priority defects are tackled first. Priority is determined by taking an issue’s severity into consideration. High severity issues are considered high Priority, etc.

## Test Cycles

Each phase will consist of a series of test cycles. In general, a test cycle will consist of the following components:

* Perform smoke testing of representative subset of functionality.
* Test fixes of any defects that blocked testing in the previous cycles.
* Test all other defect fixes.

Perform regression tests (the full set of tests will be executed as often as possible).
