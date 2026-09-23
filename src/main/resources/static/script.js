/*
 * Stores the slot currently being configured.
 */
let selectedSlot = null;


/*
 * Stores all installed applications.
 */
let installedApps = [];


/*
 * Load all tiles from the backend.
 */
async function loadTiles() {

    try {

        const response =
            await fetch("/api/tiles");

        const tiles =
            await response.json();

        displayTiles(tiles);

    } catch (error) {

        console.error(
            "Failed to load tiles:",
            error
        );

    }

}


/*
 * Display all 8 tiles.
 */
function displayTiles(tiles) {

    const container =
        document.getElementById("tile-container");

    container.innerHTML = "";


    for (let slot = 1; slot <= 8; slot++) {

        const tile =
            tiles.find(
                t => t.slotNumber === slot
            );


        const tileElement =
            document.createElement("div");

        tileElement.classList.add("tile");


        /*
         * Check whether this slot is configured.
         */
        const isConfigured =
            tile &&
            tile.name &&
            tile.type &&
            tile.target;


        if (isConfigured) {

            tileElement.innerHTML = `
                <div class="tile-name">
                    ${tile.name}
                </div>
            `;

        } else {

            tileElement.innerHTML = `
                <div class="empty-tile">
                    +
                </div>

                <div class="tile-name">
                    Slot ${slot}
                </div>
            `;

        }


        /*
         * Clicking a tile always opens
         * configuration.
         *
         * It does NOT launch the application.
         */
        tileElement.addEventListener(
            "click",
            () => openConfiguration(slot)
        );


        container.appendChild(tileElement);

    }

}


/*
 * Open configuration popup.
 */
function openConfiguration(slot) {

    selectedSlot = slot;


    const modal =
        document.getElementById("config-modal");

    const selectedSlotElement =
        document.getElementById("selected-slot");


    selectedSlotElement.textContent =
        `Configuring Slot ${slot}`;


    modal.classList.remove("hidden");

}


/*
 * Close configuration popup.
 */
function closeConfiguration() {

    const modal =
        document.getElementById("config-modal");

    modal.classList.add("hidden");

}


/*
 * Open installed application popup.
 */
async function openInstalledApps() {

    /*
     * Make sure a tile was selected.
     */
    if (selectedSlot === null) {

        console.error(
            "No tile selected."
        );

        return;

    }


    try {

        /*
         * Get installed applications.
         */
        const response =
            await fetch(
                "/api/installed-apps"
            );


        if (!response.ok) {

            throw new Error(
                "Failed to load installed applications"
            );

        }


        /*
         * Store applications.
         */
        installedApps =
            await response.json();


        /*
         * Close configuration popup.
         */
        closeConfiguration();


        /*
         * Open installed application popup.
         */
        const modal =
            document.getElementById(
                "installed-app-modal"
            );

        modal.classList.remove("hidden");


        /*
         * Clear previous search.
         */
        const searchInput =
            document.getElementById(
                "app-search"
            );

        searchInput.value = "";


        /*
         * Display all applications.
         */
        displayInstalledApps(
            installedApps
        );


        /*
         * Focus search box.
         */
        searchInput.focus();


    } catch (error) {

        console.error(
            "Failed to load installed applications:",
            error
        );

    }

}


/*
 * Display installed applications.
 */
function displayInstalledApps(apps) {

    const list =
        document.getElementById(
            "installed-app-list"
        );


    list.innerHTML = "";


    /*
     * No applications found.
     */
    if (apps.length === 0) {

        list.innerHTML = `
            <div class="no-apps">
                No applications found.
            </div>
        `;

        return;

    }


    /*
     * Create an item for every application.
     */
    apps.forEach(app => {

        const item =
            document.createElement("div");


        item.classList.add(
            "installed-app-item"
        );


        item.innerHTML = `
            <div class="installed-app-name">
                ${app.name}
            </div>
        `;


        /*
         * Selecting an application.
         */
        item.addEventListener(
            "click",
            () => selectInstalledApp(app)
        );


        list.appendChild(item);

    });

}


/*
 * Search installed applications.
 *
 * Search happens locally.
 */
function searchInstalledApps() {

    const searchInput =
        document.getElementById(
            "app-search"
        );


    const searchText =
        searchInput.value
            .trim()
            .toLowerCase();


    /*
     * Empty search:
     * show everything.
     */
    if (searchText === "") {

        displayInstalledApps(
            installedApps
        );

        return;

    }


    /*
     * Filter locally.
     */
    const filteredApps =
        installedApps.filter(app =>
            app.name
                .toLowerCase()
                .includes(searchText)
        );


    displayInstalledApps(
        filteredApps
    );

}


/*
 * Select installed application.
 */
async function selectInstalledApp(app) {

    /*
     * Make sure a tile was selected.
     */
    if (selectedSlot === null) {

        console.error(
            "No tile selected."
        );

        return;

    }


    try {

        /*
         * Send selected application
         * to Spring Boot.
         */
        const response =
            await fetch(
                "/api/installed-apps/select",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        slotNumber: selectedSlot,
                        name: app.name,
                        appId: app.appId
                    })
                }
            );


        if (!response.ok) {

            throw new Error(
                "Failed to save installed application"
            );

        }


        /*
         * Get updated tile.
         */
        const updatedTile =
            await response.json();


        console.log(
            "Installed application selected:",
            updatedTile
        );


        /*
         * Close popup.
         */
        closeInstalledApps();


        /*
         * Reload tiles.
         */
        await loadTiles();


    } catch (error) {

        console.error(
            "Failed to save installed application:",
            error
        );

    }

}


/*
 * Close installed application popup.
 */
function closeInstalledApps() {

    const modal =
        document.getElementById(
            "installed-app-modal"
        );

    modal.classList.add("hidden");

}


/*
 * =========================
 * Custom Application
 * =========================
 */


/*
 * Open Custom App popup.
 */
function openCustomApp() {

    /*
     * Make sure a tile was selected.
     */
    if (selectedSlot === null) {

        console.error(
            "No tile selected."
        );

        return;

    }


    /*
     * Close configuration popup.
     */
    closeConfiguration();


    /*
     * Show which slot is being configured.
     */
    const slotElement =
        document.getElementById(
            "custom-app-slot"
        );

    slotElement.textContent =
        `Configuring Slot ${selectedSlot}`;


    /*
     * Clear previous path.
     */
    const pathInput =
        document.getElementById(
            "custom-app-path"
        );

    pathInput.value = "";


    /*
     * Reset application name.
     */
    const nameElement =
        document.getElementById(
            "custom-app-name"
        );

    nameElement.textContent =
        "Application: -";


    /*
     * Open Custom App popup.
     */
    const modal =
        document.getElementById(
            "custom-app-modal"
        );

    modal.classList.remove("hidden");


    /*
     * Focus path input.
     */
    pathInput.focus();

}


/*
 * Extract EXE filename from path.
 *
 * Example:
 *
 * D:\Projects\SCRIPTS\water_breaktest.exe
 *
 * becomes:
 *
 * water_breaktest.exe
 */
function getExeName(path) {

    /*
     * Remove any trailing slash.
     */
    const cleanedPath =
        path.replace(/[\\/]+$/, "");


    /*
     * Find the final slash.
     */
    const lastBackslash =
        cleanedPath.lastIndexOf("\\");

    const lastForwardSlash =
        cleanedPath.lastIndexOf("/");


    const lastSlash =
        Math.max(
            lastBackslash,
            lastForwardSlash
        );


    /*
     * Return everything after
     * the final slash.
     */
    return cleanedPath.substring(
        lastSlash + 1
    );

}


/*
 * Update application name
 * while user types/pastes path.
 */
function updateCustomAppName() {

    const pathInput =
        document.getElementById(
            "custom-app-path"
        );


    const path =
        pathInput.value.trim();


    const nameElement =
        document.getElementById(
            "custom-app-name"
        );


    /*
     * Nothing entered.
     */
    if (path === "") {

        nameElement.textContent =
            "Application: -";

        return;

    }


    /*
     * Extract filename.
     */
    const fileName =
        getExeName(path);


    nameElement.textContent =
        `Application: ${fileName}`;

}


/*
 * Save Custom App.
 */
async function saveCustomApp() {

    /*
     * Make sure a tile was selected.
     */
    if (selectedSlot === null) {

        console.error(
            "No tile selected."
        );

        return;

    }


    /*
     * Get path from input.
     */
    const pathInput =
        document.getElementById(
            "custom-app-path"
        );


    const path =
        pathInput.value.trim();


    /*
     * Make sure path exists.
     */
    if (path === "") {

        alert(
            "Please enter the EXE path."
        );

        return;

    }


    /*
     * Make sure it ends with .exe.
     */
    if (!path.toLowerCase().endsWith(".exe")) {

        alert(
            "Please enter a valid .exe path."
        );

        return;

    }


    /*
     * Extract filename.
     */
    const name =
        getExeName(path);


    /*
     * Make sure we actually got a filename.
     */
    if (name === "") {

        alert(
            "Could not determine application name."
        );

        return;

    }


    try {

        /*
         * Send Custom App to Spring Boot.
         */
        const response =
            await fetch(
                "/api/custom-app/save",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        slotNumber: selectedSlot,
                        name: name,
                        path: path
                    })
                }
            );


        if (!response.ok) {

            throw new Error(
                "Failed to save custom application"
            );

        }


        /*
         * Get updated tile.
         */
        const updatedTile =
            await response.json();


        console.log(
            "Custom application saved:",
            updatedTile
        );


        /*
         * Close Custom App popup.
         */
        closeCustomApp();


        /*
         * Reload tiles.
         */
        await loadTiles();


    } catch (error) {

        console.error(
            "Failed to save custom application:",
            error
        );

        alert(
            "Failed to save custom application."
        );

    }

}


/*
 * Close Custom App popup.
 */
function closeCustomApp() {

    const modal =
        document.getElementById(
            "custom-app-modal"
        );

    modal.classList.add("hidden");

}


/*
 * =========================
 * Event Listeners
 * =========================
 */


/*
 * Installed Application button.
 */
document
    .getElementById("installed-app-button")
    .addEventListener(
        "click",
        openInstalledApps
    );


/*
 * Custom App button.
 */
document
    .getElementById("custom-app-button")
    .addEventListener(
        "click",
        openCustomApp
    );


/*
 * Configuration Cancel button.
 */
document
    .getElementById("close-button")
    .addEventListener(
        "click",
        closeConfiguration
    );


/*
 * Installed Application search.
 */
document
    .getElementById("app-search")
    .addEventListener(
        "input",
        searchInstalledApps
    );


/*
 * Installed Application Cancel.
 */
document
    .getElementById(
        "installed-app-close-button"
    )
    .addEventListener(
        "click",
        closeInstalledApps
    );


/*
 * Custom App path input.
 *
 * Updates the displayed application
 * name while typing/pasting.
 */
document
    .getElementById("custom-app-path")
    .addEventListener(
        "input",
        updateCustomAppName
    );


/*
 * Custom App Save button.
 */
document
    .getElementById(
        "custom-app-save-button"
    )
    .addEventListener(
        "click",
        saveCustomApp
    );


/*
 * Custom App Cancel button.
 */
document
    .getElementById(
        "custom-app-close-button"
    )
    .addEventListener(
        "click",
        closeCustomApp
    );


/*
 * Load tiles when page opens.
 */
loadTiles();