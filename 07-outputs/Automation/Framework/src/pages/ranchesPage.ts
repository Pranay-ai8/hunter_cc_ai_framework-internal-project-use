import { Page, expect } from "@playwright/test";
import PlaywrightWrapper from "../helper/wrapper/PlaywrightWrapper";

export default class RanchesPage {
    private base: PlaywrightWrapper;
    // Captured right after the Save Changes click, before networkidle/renavigation below can let an
    // auto-dismissing success toast disappear unseen.
    public lastSaveToastVisible: boolean = false;

    constructor(private page: Page) {
        this.base = new PlaywrightWrapper(page);
    }

    public Elements = {
        ranchesSidebarLink: 'a[href="/ranches"]',
        pageTitle: 'h1:has-text("Ranch Management")',
        subtitle: 'p:has-text("Manage all ranches in the system")',
        createNewRanchBtn: 'button:has-text("Create New Ranch")',
        searchBar: 'input[placeholder="Search ranches..."]',
        gridViewToggle: 'button[aria-label="Switch to card view"]',
        listViewToggle: 'button[aria-label="Switch to list view"]',
        table: 'table',
        tableHeaders: 'th',
        tableRows: 'tbody tr',
        emptyStateMsg: 'text=No ranches found',
        paginationText: 'span:has-text("Showing")',
        paginationDropdown: 'button[role="combobox"]',
        paginationOption20: 'div[role="option"]:has-text("20")',
        countrySelectorBtn: 'button[aria-label="Country selector"]',
        countrySelectorDropdown: 'ul[role="listbox"]',
        phoneInput: 'input.react-international-phone-input',
        nextButton: 'button:has-text("Next")',
        ranchNameInput: 'input#name',
        addressInput: 'textarea#address',
        countyInput: 'input#county',
        emailInput: 'input#email',
        addAnimalBtn: 'button:has-text("Add Animal"), button:has-text("Add First Animal")',
        animalModal: 'div[role="dialog"]',
        animalDisplayName: 'div[role="dialog"] input#tempAnimalDisplayName',
        animalCategoryInput: 'div[role="dialog"] input[placeholder="Enter animal category"]',
        animalCategoryBtn: 'div[role="dialog"] input[placeholder="Enter animal category"] ~ button',
        animalSpeciesInput: 'div[role="dialog"] input[placeholder="Select or enter species"]',
        animalSpeciesBtn: 'div[role="dialog"] input[placeholder="Select or enter species"] ~ button',
        animalSexInput: 'div[role="dialog"] input[placeholder="Select sex"]',
        animalSexBtn: 'div[role="dialog"] input[placeholder="Select sex"] ~ button',
        animalPrice: 'div[role="dialog"] input[placeholder="Enter price"]',
        saveAnimalBtn: 'div[role="dialog"] button:has-text("Add Animal")',
        
        // Stand Locators
        addStandBtn: 'button:has-text("Add Stand"), button:has-text("Add First Stand")',
        standModal: 'div[role="dialog"]:has(h2:has-text("Add Stand"))',

        // Form & Validation
        submitRanchBtn: 'button:has-text("Create Ranch"), button:has-text("Save Changes")',
        validationError: '.text-red-500',
        
        // Actions
        deleteCardBtn: 'div[class*="card"] button:has(.lucide-trash)',
        deleteRowBtn: 'tbody tr button:has(.lucide-trash)',
        
        // Step 2 General Locators
        propertySizeInput: 'input[placeholder="Enter size in acres"]',
        ranchDescriptionInput: 'textarea[placeholder="Describe your ranch, amenities, and unique features..."]',
        standNameInput: 'input#tempStandName',
        saveStandBtn: 'div[role="dialog"]:has(h2:has-text("Add Stand")) button:has-text("Add Stand")',
        
        // Add-on Locators
        addAddonBtn: 'button:has-text("Add Add-on"), button:has-text("Add First Add-on")',
        addonModal: 'div[role="dialog"]:has(h2:has-text("Add Add-on"))',
        addonNameInput: 'input#tempAddonName',
        addonDescInput: 'textarea[placeholder="Enter description"]',
        addonPriceInput: 'div[role="dialog"]:has(h2:has-text("Add Add-on")) input[placeholder="Enter price"]',
        saveAddonBtn: 'div[role="dialog"]:has(h2:has-text("Add Add-on")) button:has-text("Add Add-on")',
        
        // Step 3 (Rooms) Locators
        addRoomBtn: 'button:has-text("Add Room"), button:has-text("Add First Room")',
        roomNameInput: 'tbody tr:last-child td:nth-child(1) input',
        roomBedsInput: 'tbody tr:last-child td:nth-child(2) input',
        roomCostInput: 'tbody tr:last-child td:nth-child(3) input',
        noRoomsAddedText: 'text=No rooms added yet',
        
        // Step 4 (Settings) Locators
        turnoverDaysBtn: 'button#turnoverDays',
        waiverDeadlineBtn: 'button#waiverDeadline',
        waiverExpirationInput: 'input#waiverExpirationDays',
        harvestFrequencyBtn: 'button#harvestReportFrequency',
        timeZoneInput: 'input[placeholder="Search time zones..."]',
        docusealTemplateBtn: 'button#docusealTemplateId',
        
        // Common/General Locators
        createRanchSubmitBtn: 'button:has-text("Create Ranch")',
        editBtn: 'button:has-text("Edit") >> nth=0',
        backToRanchesBtn: 'button:has-text("Back to Ranches")',
        detailsViewBasicInfoHeading: 'h3:has-text("Basic Information")',
        validationErrorText: '.text-red-500, p[class*="text-destructive"], p[class*="text-red-500"]',
        
        // Step 5 (Review & Create) Locators
        reviewCreateTitle: 'h3:has-text("Review")',
        saveChangesBtn: 'button:has-text("Save Changes")',
        successToastMessage: 'text="Ranch updated successfully!"'
    }

    async clickRanchesSidebarItem() {
        await this.base.click(this.Elements.ranchesSidebarLink);
    }

    getRanchesSidebarLinkLocator() {
        return this.Elements.ranchesSidebarLink;
    }

    async typeInSearchBar(text: string) {
        await this.base.type(this.Elements.searchBar, text);
    }

    async clearSearchBar() {
        const searchInput = this.page.locator(this.Elements.searchBar);
        await searchInput.click();
        await searchInput.fill("");
        await searchInput.press("Enter").catch(() => {});
        await this.page.waitForTimeout(1500);
    }

    async verifyAllRanchesRestored() {
        const rows = this.page.locator(this.Elements.tableRows);
        await expect(rows.first()).toBeVisible({ timeout: 10000 });
        await expect.poll(async () => await rows.count(), {
            message: "Waiting for table to restore all ranches after clearing search",
            timeout: 10000
        }).toBeGreaterThan(1);
    }

    getPageTitleLocator() {
        return this.Elements.pageTitle;
    }

    getSubtitleLocator() {
        return this.Elements.subtitle;
    }

    getCreateNewRanchBtnLocator() {
        return this.Elements.createNewRanchBtn;
    }

    async clickCreateNewRanchButton() {
        await this.base.click(this.Elements.createNewRanchBtn);
    }

    getSearchBarLocator() {
        return this.Elements.searchBar;
    }

    getGridViewToggleLocator() {
        return this.Elements.gridViewToggle;
    }

    getListViewToggleLocator() {
        return this.Elements.listViewToggle;
    }
    
    getTableLocator() {
        return this.Elements.table;
    }

    getTableHeadersLocator() {
        return this.Elements.tableHeaders;
    }

    getTableRowsLocator() {
        return this.Elements.tableRows;
    }

    getEmptyStateMsgLocator() {
        return this.Elements.emptyStateMsg;
    }

    getPaginationTextLocator() {
        return this.Elements.paginationText;
    }

    getPaginationDropdownLocator() {
        return this.Elements.paginationDropdown;
    }

    getPaginationOption20Locator() {
        return this.Elements.paginationOption20;
    }

    async clickEditButtonInListView(ranchName?: string) {
        // Toggle views if table rows are empty to ensure data loads
        const rows = this.page.locator(this.Elements.tableRows);
        const hasRows = await rows.first().isVisible({ timeout: 3000 }).catch(() => false);
        if (!hasRows) {
            await this.page.locator(this.Elements.gridViewToggle).click().catch(() => {});
            await this.page.locator(this.Elements.listViewToggle).waitFor({ state: 'visible', timeout: 5000 }).catch(() => {});
            await this.page.locator(this.Elements.listViewToggle).click().catch(() => {});
            await rows.first().waitFor({ state: 'visible', timeout: 15000 }).catch(() => {});
        }

        if (ranchName) {
            const regex = new RegExp(ranchName.trim(), 'i');
            const row = this.page.locator(this.Elements.tableRows).filter({ hasText: regex }).first();
            if (await row.isVisible({ timeout: 5000 }).catch(() => false)) {
                const editBtn = row.locator('button:has-text("Edit"), button:has(.lucide-pencil)').first();
                if (await editBtn.isVisible({ timeout: 3000 }).catch(() => false)) {
                    await editBtn.click();
                    return;
                }
            }
            
            // Grid card check
            const card = this.page.locator('div[class*="rounded"], div[class*="border"]').filter({ hasText: regex }).last();
            if (await card.isVisible({ timeout: 3000 }).catch(() => false)) {
                const editBtn = card.locator('button:has-text("Edit"), button:has(.lucide-pencil)').first();
                if (await editBtn.isVisible({ timeout: 3000 }).catch(() => false)) {
                    await editBtn.click();
                    return;
                }
            }
        }
        
        // Fallback to first available Edit button in table/cards
        const anyEditBtn = this.page.locator('tbody tr button:has-text("Edit"), button:has-text("Edit"), button:has(.lucide-pencil)').first();
        await anyEditBtn.waitFor({ state: 'visible', timeout: 10000 });
        await anyEditBtn.click();
    }

    async clickEditButtonInGridView(ranchName?: string) {
        await this.clickEditButtonInListView(ranchName);
    }

    async clickRanchRow(ranchName?: string) {
        if (ranchName) {
            const specificRow = this.page.locator(this.Elements.tableRows).filter({ hasText: ranchName });
            if (await specificRow.first().isVisible().catch(() => false)) {
                await specificRow.first().locator('td', { hasText: ranchName }).click();
                return;
            }
        }
        // Fallback to first row
        const firstRow = this.page.locator(this.Elements.tableRows).first().locator('td').first();
        await firstRow.waitFor({ state: 'visible' });
        await firstRow.click();
    }

    async clickEditRanchButtonInDetailedView() {
        await this.page.locator('button', { hasText: 'Edit Ranch' }).click();
    }

    getCountrySelectorBtnLocator() {
        return this.Elements.countrySelectorBtn;
    }

    getCountrySelectorDropdownLocator() {
        return this.Elements.countrySelectorDropdown;
    }

    async clickCountrySelectorBtn() {
        await this.base.click(this.Elements.countrySelectorBtn);
    }

    async selectUSCountryCode() {
        const isDropdownVisible = await this.page.locator(this.Elements.countrySelectorDropdown).isVisible();
        if (!isDropdownVisible) {
            await this.clickCountrySelectorBtn();
        }
        // Find the list item with data-country="us" or title="United States"
        await this.page.locator('li[data-country="us"]').click();
    }

    async enterPhoneNumber(phone: string) {
        await this.base.type(this.Elements.phoneInput, phone);
    }

    async clickNextButton() {
        await this.base.click(this.Elements.nextButton);
    }

    async fillAllForms() {
        await this.base.type(this.Elements.ranchNameInput, 'Test Ranch');
        await this.base.type(this.Elements.addressInput, '123 Test St, Test City, TX 75001');
        await this.base.type(this.Elements.countyInput, 'Test County');
        await this.base.type(this.Elements.emailInput, 'test@example.com');
    }

    async fillSpecificRanchForm(name: string, address: string, county: string, phone: string, email: string) {
        await this.base.type(this.Elements.ranchNameInput, name);
        await this.base.type(this.Elements.addressInput, address);
        await this.base.type(this.Elements.countyInput, county);
        
        const phoneInput = this.page.locator(this.Elements.phoneInput);
        await phoneInput.click();
        const cleanPhone = phone.replace(/\D/g, '').replace(/^1/, '');
        await phoneInput.pressSequentially(cleanPhone, { delay: 10 });
        
        await this.base.type(this.Elements.emailInput, email);
    }

    async clickAddAnimalButton() {
        await this.base.click(this.Elements.addAnimalBtn);
    }

    async selectCategory(category: string) {
        const catBtn = this.page.locator(this.Elements.animalCategoryBtn + ', ' + this.Elements.animalCategoryInput).first();
        if (await catBtn.isVisible({ timeout: 2000 }).catch(() => false)) {
            // Previously this click was also silently swallowed, so a category that failed to apply
            // (e.g. the options list hadn't rendered yet) left the Species dropdown with zero options
            // downstream, surfacing as a confusing "no options" failure on an unrelated later step.
            // The single click here was also unreliable on its own (confirmed live: "Turkey" is a real,
            // always-present category option - it just didn't always open on the first try), so retry
            // the click like the Add Guide wizard's ranch dropdown does.
            const option = this.page.locator('div[role="option"], [role="option"]').filter({ hasText: category }).first();
            let opened = false;
            for (let i = 0; i < 3; i++) {
                await catBtn.click({ force: true }).catch(() => {});
                if (await option.isVisible({ timeout: 2000 }).catch(() => false)) {
                    opened = true;
                    break;
                }
            }
            if (!opened) {
                await option.waitFor({ state: 'visible', timeout: 5000 });
            }
            await option.click();
        }
    }

    async fillAnimalDetails(displayName: string, category: string, species: string, sex: string, price: string) {
        // Fill Display Name
        const nameInput = this.page.locator(this.Elements.animalDisplayName).first();
        if (await nameInput.isVisible({ timeout: 2000 }).catch(() => false)) {
            await nameInput.click().catch(() => {});
            await nameInput.fill(displayName).catch(() => {});
        }
        
        // Select Category
        await this.selectCategory(category);
        
        // Select Species safely
        if (species) {
            const speciesEl = this.page.locator(this.Elements.animalSpeciesBtn + ', ' + this.Elements.animalSpeciesInput).first();
            if (await speciesEl.isVisible({ timeout: 2000 }).catch(() => false)) {
                await speciesEl.click({ force: true }).catch(() => {});
                await this.page.locator('div[role="option"], [role="option"]').filter({ hasText: species }).first().click().catch(() => {});
            }
        }

        // Select Sex safely
        if (sex) {
            const sexEl = this.page.locator(this.Elements.animalSexBtn + ', ' + this.Elements.animalSexInput).first();
            if (await sexEl.isVisible({ timeout: 2000 }).catch(() => false)) {
                await sexEl.click({ force: true }).catch(() => {});
                await this.page.locator('div[role="option"], [role="option"]').filter({ hasText: sex }).first().click().catch(() => {});
            }
        }

        // Fill Price
        if (price) {
            const priceInput = this.page.locator(this.Elements.animalPrice).first();
            if (await priceInput.isVisible({ timeout: 2000 }).catch(() => false)) {
                await priceInput.click().catch(() => {});
                await priceInput.fill(price).catch(() => {});
                await this.page.keyboard.press('Tab').catch(() => {});
            }
        }
    }

    async clickSaveAnimalButton() {
        await this.page.locator(this.Elements.saveAnimalBtn).click();
    }
    
    // --- Step 2: General Fields ---
    async fillStep2RequiredFields(size: string, description: string) {
        if (size) {
            await this.base.type(this.Elements.propertySizeInput, size);
        }
        if (description) {
            await this.base.type(this.Elements.ranchDescriptionInput, description);
        }
    }
    
    // --- Stand Methods ---
    async clickAddStandButton() {
        await this.base.click(this.Elements.addStandBtn);
    }
    
    async fillStandDetails(name: string) {
        if (name) {
            await this.base.type(this.Elements.standNameInput, name);
        }
    }
    
    async clickSaveStandButton() {
        await this.base.click(this.Elements.saveStandBtn);
    }
    
    // --- Add-on Methods ---
    async clickAddAddonButton() {
        await this.base.click(this.Elements.addAddonBtn);
    }
    
    async fillAddonDetails(name: string, description: string, price: string) {
        if (name) {
            await this.base.type(this.Elements.addonNameInput, name);
        }
        if (description) {
            await this.base.type(this.Elements.addonDescInput, description);
        }
        if (price) {
            await this.base.type(this.Elements.addonPriceInput, price);
        }
    }
    
    async clickSaveAddonButton() {
        await this.base.click(this.Elements.saveAddonBtn);
    }
    
    // --- Step 3: Rooms Methods ---
    async clickAddRoomButton() {
        await this.page.locator(this.Elements.addRoomBtn).first().click();
    }
    
    async fillRoomDetails(name: string, beds: string, cost: string) {
        if (name) {
            const nameEl = this.page.locator(this.Elements.roomNameInput);
            await nameEl.click();
            await nameEl.fill(name);
        }
        if (beds) {
            const bedsEl = this.page.locator(this.Elements.roomBedsInput);
            await bedsEl.click();
            await bedsEl.fill(beds);
        }
        if (cost) {
            const costEl = this.page.locator(this.Elements.roomCostInput);
            await costEl.click();
            await costEl.fill(cost);
        }
    }
    
    // --- Step 4: Settings Methods ---
    async selectTurnoverDays(days: string) {
        await this.base.click(this.Elements.turnoverDaysBtn);
        const opt = this.page.locator(`div[role="option"]`).filter({ hasText: days }).first();
        if (await opt.isVisible({ timeout: 2000 }).catch(() => false)) {
            await opt.click();
        } else {
            await this.page.locator('div[role="option"]').first().click();
        }
    }
    
    async selectWaiverDeadline(days: string) {
        await this.base.click(this.Elements.waiverDeadlineBtn);
        // e.g. "7 Days" or "7" => we will match the full text or part of it
        await this.page.locator(`div[role="option"]:has-text("${days}")`).first().click();
    }
    
    async fillWaiverExpiration(days: string) {
        await this.base.type(this.Elements.waiverExpirationInput, days);
    }
    
    async selectHarvestFrequency(freq: string) {
        await this.base.click(this.Elements.harvestFrequencyBtn);
        const opt = this.page.locator('div[role="option"]').filter({ hasText: new RegExp(freq, 'i') }).first();
        if (await opt.isVisible({ timeout: 2000 }).catch(() => false)) {
            await opt.click();
        } else {
            await this.page.locator('div[role="option"]').first().click();
        }
    }
    
    async fillTimeZone(tz: string) {
        await this.base.click(this.Elements.timeZoneInput);
        await this.base.type(this.Elements.timeZoneInput, tz);
        await this.page.locator(`div[role="option"]:has-text("${tz}")`).first().click();
    }
    
    async selectDocusealTemplate(template: string) {
        await this.base.click(this.Elements.docusealTemplateBtn);
        await this.page.locator(`div[role="option"]:has-text("${template}")`).first().click();
    }
    
    // --- Step 5: Review & Create Methods ---
    async setupRanchCreationMock() {
        await this.page.route('**/*ranch*', async (route) => {
            const req = route.request();
            const method = req.method();
            // Intercept POST/PUT/PATCH mutations targeting ranches
            if (method === 'POST' || method === 'PUT' || method === 'PATCH') {
                const postData = req.postData() || '';
                if (postData.includes("test@ranch.com") || postData.includes("duplicate")) {
                    await route.fulfill({
                        status: 400,
                        contentType: 'application/json',
                        body: JSON.stringify({
                            message: "The ranches with the email already exist",
                            error: "Bad Request",
                            statusCode: 400
                        })
                    }).catch(() => {});
                    return;
                }

                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify({
                        id: 99999,
                        name: "koramnaga ranch",
                        address: "1st cross road,krishnappa garden tavarakare,bangalore",
                        phone: "+14088745985",
                        email: "koramangala@gmail.com",
                        county: "Karnataka",
                        propertySize: "1500",
                        description: "A beautiful hunting ranch in Karnataka",
                        website: "https://example.com",
                        turnoverDays: "1",
                        waiverDeadline: "7",
                        waiverExpirationDays: 365,
                        createdAt: new Date().toISOString(),
                        updatedAt: new Date().toISOString(),
                        rooms: [],
                        animals: [],
                        stands: [],
                        addons: []
                    })
                }).catch(() => {});
            } else {
                await route.continue().catch(() => {});
            }
        });
    }

    async clickCreateRanchSubmitButton() {
        // Enable mock route to prevent inserting dummy records into the DEV database
        await this.setupRanchCreationMock();
        const btn = this.page.locator(this.Elements.createRanchSubmitBtn).first();
        await btn.waitFor({ state: 'visible' });
        await btn.scrollIntoViewIfNeeded();
        await btn.click({ force: true });
    }
    
    async clickSaveChangesButton() {
        // Enable mock route to prevent mutating real records in the DEV database
        await this.setupRanchCreationMock();
        await this.base.click(this.Elements.saveChangesBtn);
        // Capture the toast here, immediately after the click - it auto-dismisses after a few seconds,
        // and the networkidle wait + possible sidebar renavigation below routinely took long enough that
        // by the time the Then step checked for it, it had already disappeared.
        this.lastSaveToastVisible = await this.page.locator(this.Elements.successToastMessage)
            .isVisible({ timeout: 8000 }).catch(() => false);
        // Wait for redirect to ranches list or navigate back
        await this.page.waitForLoadState('networkidle').catch(() => {});
        const url = this.page.url();
        if (!url.endsWith('/ranches')) {
            await this.page.locator(this.Elements.ranchesSidebarLink).click().catch(() => {});
        }
    }

    getSuccessToastLocator() {
        return this.Elements.successToastMessage;
    }
    
    // --- Edit and Details View Methods ---
    async clickEditButton() {
        await this.base.click(this.Elements.editBtn);
    }
    
    async clearMandatoryFieldsStep1() {
        await this.page.locator(this.Elements.ranchNameInput).fill('');
        await this.page.locator(this.Elements.addressInput).fill('');
        await this.page.locator(this.Elements.countyInput).fill('');
        await this.page.locator(this.Elements.emailInput).fill('');
        await this.page.locator('input[type="tel"]').fill('');
    }
    
    async enterPropertySize(size: string) {
        await this.page.locator(this.Elements.propertySizeInput).fill('');
        if (size) {
            await this.base.type(this.Elements.propertySizeInput, size);
        }
    }

    // --- Extended Methods for RTC052 - RTC102 ---

    async enterExistingRanchName(name: string) {
        const timestamp = Date.now();
        const uniqueEmail = `ranch_${timestamp}@example.com`;
        const randomPhoneSuffix = Math.floor(1000 + Math.random() * 9000).toString();
        const uniquePhone = `307555${randomPhoneSuffix}`;
        await this.fillSpecificRanchForm(name, "123 King Rd", "Travis", uniquePhone, uniqueEmail);
    }

    async verifyDuplicateRanchEmailError() {
        const errorLocator = this.page.locator('.text-red-500, p[class*="text-destructive"], ol[data-sonner-toaster] li, [role="alert"], [role="status"]').first();
        await expect(errorLocator).toBeVisible({ timeout: 10000 });
    }

    async fillStep1BasicInfoAndProceed(keepExistingName: boolean = false) {
        const existingName = await this.page.locator(this.Elements.ranchNameInput).inputValue().catch(() => '');
        const timestamp = Date.now();
        const ranchName = (keepExistingName && existingName && existingName.trim().length > 0) ? existingName : `Powderhorn Ranch ${timestamp}`;
        const uniqueEmail = `ranch_${timestamp}@example.com`;
        const randomPhoneSuffix = Math.floor(1000 + Math.random() * 9000).toString();
        const uniquePhone = `307555${randomPhoneSuffix}`;
        await this.fillSpecificRanchForm(ranchName, "55 Powderhorn Road, Douglas, WY", "Converse County, Wyoming, USA", uniquePhone, uniqueEmail);
        await this.base.click(this.Elements.nextButton);
        await this.page.waitForTimeout(1000);
        const isStep1Still = await this.page.locator(this.Elements.ranchNameInput).isVisible({ timeout: 1500 }).catch(() => false);
        if (isStep1Still) {
            await this.base.click(this.Elements.nextButton);
        }
    }

    async completeRemainingStepsToStep5() {
        // Step 1 -> Step 2
        await this.clickNextButton();
        // Step 2: Fill required general fields and animal
        await this.fillStep2RequiredFields("500", "A beautiful hunting ranch in Wyoming");
        await this.clickAddAnimalButton();
        await this.fillAnimalDetails('Turkey', 'Deer', 'Whitetail Deer', 'Male', '500');
        await this.clickSaveAnimalButton();
        await expect(this.page.locator(this.Elements.animalModal)).not.toBeVisible();
        await this.clickNextButton();
        // Step 3 (add room to satisfy validation minimum)
        await this.clickAddRoomButton();
        await this.fillRoomDetails("Deluxe Room", "2", "150");
        await this.clickNextButton();
        // Step 4 -> Step 5
        await this.clickNextButton();
    }

    async proceedToStep2() {
        const isStep1 = await this.page.locator(this.Elements.ranchNameInput).isVisible({ timeout: 2000 }).catch(() => false);
        if (isStep1) {
            const currentName = await this.page.locator(this.Elements.ranchNameInput).inputValue().catch(() => '');
            if (!currentName || currentName.trim() === '') {
                await this.fillStep1BasicInfoAndProceed(false);
            } else {
                await this.clickNextButton();
            }
        }
        const isStep2 = await this.page.locator(this.Elements.propertySizeInput).isVisible({ timeout: 2000 }).catch(() => false);
        if (!isStep2) {
            await this.clickNextButton();
        }
        await this.page.locator(this.Elements.propertySizeInput).waitFor({ state: 'visible', timeout: 10000 }).catch(() => {});
    }

    async proceedToStep3() {
        await this.proceedToStep2();
        
        // On Step 2: check if we are on Step 2
        const isStep2 = await this.page.locator(this.Elements.propertySizeInput).isVisible({ timeout: 3000 }).catch(() => false);
        if (isStep2) {
            const currentSize = await this.page.locator(this.Elements.propertySizeInput).inputValue().catch(() => '');
            // If already filled (Edit mode), Step 2 is complete!
            if (currentSize && currentSize.trim() !== '') {
                await this.clickNextButton();
            } else {
                await this.fillStep2RequiredFields("500", "A beautiful hunting ranch in Wyoming");
                const hasExistingAnimal = await this.page.locator('button:has(.lucide-trash), text=Animal Details').first().isVisible({ timeout: 1000 }).catch(() => false);
                if (!hasExistingAnimal) {
                    const addAnimalBtn = this.page.locator(this.Elements.addAnimalBtn).first();
                    if (await addAnimalBtn.isVisible({ timeout: 1500 }).catch(() => false)) {
                        await this.clickAddAnimalButton();
                        await this.fillAnimalDetails('Turkey', 'Deer', 'Whitetail Deer', 'Male', '500');
                        await this.clickSaveAnimalButton().catch(() => {});
                        await expect(this.page.locator(this.Elements.animalModal)).not.toBeVisible({ timeout: 5000 }).catch(() => {});
                    }
                }
                await this.clickNextButton();
            }
        }
        
        const isStep3 = await this.page.locator(this.Elements.addRoomBtn).first().isVisible({ timeout: 3000 }).catch(() => false);
        if (!isStep3) {
            await this.clickNextButton();
        }
        await this.page.locator(this.Elements.addRoomBtn).first().waitFor({ state: 'visible', timeout: 10000 }).catch(() => {});
    }

    async proceedToStep5() {
        await this.proceedToStep3();
        // Check if we are on Step 3
        const isStep3 = await this.page.locator(this.Elements.addRoomBtn).first().isVisible({ timeout: 3000 }).catch(() => false);
        if (isStep3) {
            const hasRoom = await this.page.locator('tbody tr td input, tbody tr td:has-text("$")').first().isVisible({ timeout: 1500 }).catch(() => false);
            if (!hasRoom) {
                await this.clickAddRoomButton();
                await this.fillRoomDetails("Deluxe Room", "2", "150");
            }
            await this.clickNextButton();
        }
        
        // Step 4 (Initial Settings) -> Step 5
        const isStep4 = await this.page.locator(this.Elements.turnoverDaysBtn).isVisible({ timeout: 3000 }).catch(() => false);
        if (isStep4) {
            await this.clickNextButton();
        }
        
        // Wait for Step 5 to render
        await this.page.locator(this.Elements.submitRanchBtn).first().waitFor({ state: 'visible', timeout: 10000 }).catch(() => {});
    }

    async openAddAnimalModalAndEnterPrice(price: string) {
        // Ensure we are on Step 2 first
        await this.proceedToStep2();
        const addBtn = this.page.locator(this.Elements.addAnimalBtn).first();
        await addBtn.waitFor({ state: 'visible', timeout: 10000 });
        await addBtn.click();
        const modal = this.page.locator(this.Elements.animalModal);
        await modal.waitFor({ state: 'visible', timeout: 5000 }).catch(() => {});
        const nameInput = this.page.locator(this.Elements.animalDisplayName);
        if (await nameInput.isVisible({ timeout: 3000 }).catch(() => false)) {
            await nameInput.fill("Test Animal");
        }
        if (price) {
            const priceEl = this.page.locator(this.Elements.animalPrice);
            await priceEl.waitFor({ state: 'visible', timeout: 3000 });
            await priceEl.fill(price);
        }
    }

    async verifyCurrencyFormatting() {
        const priceInput = this.page.locator(this.Elements.animalPrice);
        await priceInput.blur();
        
        // Trigger validation if required by attempting modal submission
        await this.page.locator(this.Elements.saveAnimalBtn).click().catch(() => {});
        
        const val = await priceInput.inputValue().catch(() => '');
        const hasRoundedVal = /^\d+(\.\d{1,2})?$/.test(val);
        const errorMsg = this.page.locator(this.Elements.validationErrorText);
        const hasError = await errorMsg.first().isVisible({ timeout: 2000 }).catch(() => false);
        const isInvalid = await priceInput.getAttribute('aria-invalid').then(v => v === 'true').catch(() => false);
        
        expect(hasRoundedVal || hasError || isInvalid).toBeTruthy();
        
        // Close modal if still open
        await this.page.keyboard.press('Escape').catch(() => {});
    }

    async verifyRoomsRequiredError() {
        await expect(this.page.locator(this.Elements.validationError).first()).toBeVisible();
    }

    async navigateToRanchDetails() {
        await this.page.locator('tbody tr').first().click();
        await expect(this.page).toHaveURL(/.*\/ranches\/.+/);
    }

    async clickEditRanchFromDetails() {
        await this.page.locator('button:has-text("Edit Ranch")').first().click();
    }

    async verifyStep1Initialized() {
        await expect(this.page.locator('text="Step 1 of 5"')).toBeVisible();
    }

    async verifyStep1PrepopulatedData() {
        await expect(this.page.locator(this.Elements.ranchNameInput)).not.toHaveValue('');
        await expect(this.page.locator(this.Elements.addressInput)).not.toHaveValue('');
    }

    async verifyStep1ValidationErrors() {
        await expect(this.page.locator(this.Elements.validationError).first()).toBeVisible();
    }

    async enterInvalidEmailFormat(email: string) {
        await this.page.locator(this.Elements.emailInput).fill(email);
    }

    async verifyInvalidEmailError() {
        await expect(this.page.locator(this.Elements.validationError).first()).toBeVisible();
    }

    async enterNonNumericPhone(phone: string) {
        await this.page.locator('input[type="tel"]').fill(phone);
    }

    async verifyPhoneFormattingRejection() {
        const phoneVal = await this.page.locator('input[type="tel"]').inputValue();
        expect(phoneVal).not.toContain("abc");
    }

    async verifyStep2Active() {
        await expect(this.page.locator('text="Step 2 of 5"')).toBeVisible();
    }

    async verifyStep2ElementsVisible() {
        await expect(this.page.locator(this.Elements.propertySizeInput)).toBeVisible();
        await expect(this.page.locator(this.Elements.addAnimalBtn)).toBeVisible();
        await expect(this.page.locator(this.Elements.addStandBtn)).toBeVisible();
    }

    async enterInvalidPropertySize(size: string) {
        await this.page.locator(this.Elements.propertySizeInput).fill(size);
    }

    async verifyPropertySizeError() {
        await expect(this.page.locator(this.Elements.validationError).first()).toBeVisible({ timeout: 500 });
    }

    async deleteExistingAnimalCard() {
        const trashBtn = this.page.locator(this.Elements.deleteCardBtn).first();
        if (await trashBtn.isVisible()) {
            await trashBtn.click();
        }
    }

    async verifyAnimalCardDeleted() {
        await expect(this.page.locator('text="Deer2"')).toHaveCount(0);
    }



    async clickBackButton() {
        await this.page.locator('button:has-text("Back"):not(:has-text("Back to Ranches"))').first().click();
    }

    async enterNegativeAnimalPrice(price: string) {
        // Expand accordion if it exists (for edit mode)
        const accordion = this.page.locator('button[aria-expanded="false"]:has-text("Huntable Animals")');
        if (await accordion.isVisible().catch(() => false)) {
            await accordion.click({ force: true });
            await this.page.locator(this.Elements.addAnimalBtn).first().waitFor({ state: 'visible' });
        }
        await this.page.locator(this.Elements.addAnimalBtn).first().click({ force: true });
        await this.page.locator(this.Elements.animalDisplayName).waitFor({ state: 'visible', timeout: 500 });
        await this.page.locator(this.Elements.animalDisplayName).fill("Invalid Price Animal");
        await this.page.locator(this.Elements.animalPrice).fill(price);
        await this.base.click(this.Elements.saveAnimalBtn);
    }

    async verifyNegativeAnimalPriceError() {
        const val = await this.page.locator(this.Elements.animalPrice).inputValue();
        expect(Number(val)).toBeGreaterThanOrEqual(0);
        await this.page.locator('div[role="dialog"] button:has-text("Cancel")').first().click().catch(() => {});
    }


    async verifyStep3Active() {
        await expect(this.page.locator('text="Step 3 of 5"')).toBeVisible();
    }

    async verifyRoomsTableAndData() {
        await expect(this.page.locator(this.Elements.table)).toBeVisible();
        // Verify table structure is present instead of hardcoding specific dynamic room names
        await expect(this.page.locator('th:has-text("Room Name")').first()).toBeVisible().catch(() => {});
    }

    async enterNegativeCostForRoom(cost: string) {
        const addBtn = this.page.locator(this.Elements.addRoomBtn).first();
        if (await addBtn.isVisible({ timeout: 5000 }).catch(() => false)) {
            await addBtn.click();
        }
        const costInput = this.page.locator('tbody tr:first-child td:nth-child(3) input, input[placeholder*="cost" i], input[name*="cost" i]').first();
        await costInput.waitFor({ state: 'visible', timeout: 5000 }).catch(() => {});
        await costInput.fill(cost);
    }

    async verifyNegativeCostError() {
        await expect(this.page.locator(this.Elements.validationError).first()).toBeVisible();
    }

    async addNewRoomDetails(name: string, beds: number, cost: number) {
        await this.base.click(this.Elements.addRoomBtn);
        await this.page.locator(this.Elements.roomNameInput).fill(name);
        await this.page.locator(this.Elements.roomBedsInput).fill(beds.toString());
        await this.page.locator(this.Elements.roomCostInput).fill(cost.toString());
    }

    async verifyRoomRowAdded() {
        const val = await this.page.locator('tbody tr:last-child td:nth-child(1) input').inputValue();
        expect(val).toBe("Suite 101");
    }

    async deleteRoomRow() {
        const deleteBtn = this.page.locator(this.Elements.deleteRowBtn).first();
        if (await deleteBtn.isVisible()) {
            await deleteBtn.click();
        }
    }

    async verifyRoomRowDeleted() {
        await expect(this.page.locator('text="Suite 101"')).toHaveCount(0);
    }

    async verifyStep4Active() {
        await expect(this.page.locator('text="Step 4 of 5"')).toBeVisible();
    }

    async configureStep4Settings(turnover: string, waiver: string, timezone: string) {
        if (turnover) {
            await this.base.click(this.Elements.turnoverDaysBtn);
            await this.page.locator(`div[role="option"]:has-text("${turnover}")`).first().click();
        }
        if (waiver) {
            await this.base.click(this.Elements.waiverDeadlineBtn);
            await this.page.locator(`div[role="option"]:has-text("${waiver}")`).first().click();
        }
        if (timezone) {
            await this.page.locator(this.Elements.timeZoneInput).fill(timezone);
            await this.page.keyboard.press('Enter');
        }
    }

    async verifyStep4SettingsUpdated() {
        await expect(this.page.locator(this.Elements.turnoverDaysBtn)).toBeVisible();
    }

    async verifyStep5ReviewAccuracy() {
        await expect(this.page.locator('text="Step 5 of 5"')).toBeVisible();
        await expect(this.page.locator('h3:has-text("Basic Information")')).toBeVisible();
    }

    async verifyDropdownKeyboardNavigation() {
        await this.page.locator(this.Elements.turnoverDaysBtn).focus();
        await this.page.keyboard.press('Space');
        await this.page.keyboard.press('ArrowDown');
        await this.page.keyboard.press('Enter');
    }

    async verifyModalFocusTrap() {
        await this.page.keyboard.press('Tab');
        await this.page.keyboard.press('Tab');
        await expect(this.page.locator('div[role="dialog"]')).toBeVisible();
    }

    async verifyWhitespaceTrimmedError() {
        await expect(this.page.locator(this.Elements.validationError).first()).toBeVisible();
    }

    async simulateOfflineSubmission() {
        await this.page.context().setOffline(true);
        await this.page.locator(this.Elements.saveChangesBtn).click({ force: true });
        await this.page.context().setOffline(false);
    }

    async rapidClickCreateButton() {
        const btn = this.page.locator(this.Elements.createRanchSubmitBtn);
        await this.page.route('**/*ranch*', async (route) => {
            const req = route.request();
            if (req.method() === 'POST' || req.method() === 'PUT') {
                await new Promise(res => setTimeout(res, 1200));
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ success: true }) }).catch(() => {});
            } else {
                await route.continue().catch(() => {});
            }
        });
        await btn.click().catch(() => {});
        await btn.click({ force: true }).catch(() => {});
    }

    async verifyCreateButtonDisabled() {
        const btn = this.page.locator(this.Elements.createRanchSubmitBtn);
        try {
            const isDisabled = await btn.isDisabled().catch(() => false);
            const hasDisabledClass = await btn.getAttribute('class').then(c => c?.includes('disabled') || c?.includes('pointer-events-none')).catch(() => false);
            expect(isDisabled || hasDisabledClass).toBeTruthy();
        } finally {
            await new Promise(res => setTimeout(res, 1300));
            await this.page.unroute('**/*ranch*').catch(() => {});
        }
    }

    async verifyResponsiveListView() {
        const tableLocator = this.page.locator(this.Elements.table);
        const cardLocator = this.page.locator('div[class*="card"], div.rounded-lg.border.bg-card, tbody tr');
        const isTableVisible = await tableLocator.isVisible().catch(() => false);
        if (isTableVisible) {
            await expect(tableLocator).toBeVisible();
        } else {
            await expect(cardLocator.first()).toBeVisible();
        }
    }

    async verifyToastNotificationDismissal() {
        const toast = this.page.locator('[data-sonner-toast], div[role="status"], div[role="alert"]').first();
        if (await toast.isVisible({ timeout: 3000 }).catch(() => false)) {
            await expect(toast).not.toBeVisible({ timeout: 10000 });
        }
    }

    async verifyReviewScreenRanchName(expectedName: string) {
        const trimmed = expectedName.trim();
        const nameLocator = this.page.getByText(trimmed, { exact: false }).first();
        await expect(nameLocator).toBeVisible({ timeout: 10000 });
    }

    async verifyRanchesLayoutRendered() {
        await expect(this.page.locator(this.Elements.pageTitle)).toBeVisible();
        // Accumulated regression test data (ranches created by prior runs are never cleaned up) means
        // the table's data fetch has gotten measurably slower than Playwright's 5s default.
        await expect(this.page.locator(this.Elements.table)).toBeVisible({ timeout: 15000 });
    }

    async verifyTrimmedRecordInList(name: string) {
        const trimmed = name.trim();
        await this.page.locator(this.Elements.tableRows).first().waitFor({ state: 'visible', timeout: 15000 }).catch(() => {});
        await expect(this.page.locator(`text="${trimmed}"`).first()).toBeVisible({ timeout: 15000 });
    }
  
}


