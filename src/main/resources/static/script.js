/* =========================================================
   Global Variables
   ========================================================= */

/*
 * Stores the slot currently being configured.
 *
 * Example:
 *
 * Click Slot 5
 * selectedSlot = 5
 */
let selectedSlot = null;


/*
 * Stores all installed applications retrieved
 * from the Windows computer.
 */
let installedApps = [];

/* =========================================================
   Server Information
   ========================================================= */

/*
 * Get the Windows server IP address and port
 * from Spring Boot.
 */
async function loadServerInfo() {

    try {

        const response =
            await fetch("/api/server-info");

        const serverInfo =
            await response.json();

        const serverAddress =
            document.getElementById(
                "server-address"
            );

        serverAddress.textContent =
            "Server - " + serverInfo.address;

    } catch (error) {

        console.error(
            "Failed to load server information:",
            error
        );

        const serverAddress =
            document.getElementById(
                "server-address"
            );

        serverAddress.textContent =
            "Server - Unavailable";
    }
}



/* =========================================================
   Load Tiles
   ========================================================= */

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


/* =========================================================
   Display Tiles
   ========================================================= */

/*
 * Display all 8 physical slots.
 *
 * Important:
 *
 * Clicking a tile NEVER launches the application
 * from this Windows configuration page.
 *
 * Clicking a tile always opens configuration.
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
         * Store slot number on the HTML element.
         */
        tileElement.dataset.slot =
            slot;


        /*
         * Empty tile
         */
        if (
            !tile ||
            !tile.name ||
            !tile.type ||
            !tile.target
        ) {

            tileElement.innerHTML = `

                <div class="empty-tile">
                    +
                </div>

                <div class="empty-tile-label">
                    Slot ${slot}
                </div>

            `;

        }


        /*
         * Configured tile
         */
        else {

            tileElement.innerHTML = `

                <div class="configured-tile-name">
                    ${tile.name}
                </div>

                <div class="configured-tile-type">
                    ${tile.type}
                </div>

            `;
        }


        /*
         * Clicking any tile opens configuration.
         *
         * It does NOT launch the application.
         */
        tileElement.addEventListener(
            "click",
            function () {

                openConfiguration(slot);

            }
        );


        container.appendChild(tileElement);
    }
}


/* =========================================================
   Configuration Modal
   ========================================================= */

/*
 * Open the main configuration popup.
 */
function openConfiguration(slot) {

    selectedSlot = slot;


    const configModal =
        document.getElementById("config-modal");


    const configSlot =
        document.getElementById("config-slot");


    configSlot.textContent =
        "Configuring Slot " + slot;


    configModal.classList.remove("hidden");
}


/*
 * Close the main configuration popup.
 */
function closeConfiguration() {

    const configModal =
        document.getElementById("config-modal");


    configModal.classList.add("hidden");
}


/* =========================================================
   Installed Applications
   ========================================================= */

/*
 * Open Installed Applications.
 */
async function openInstalledApps() {

    if (selectedSlot === null) {
        return;
    }


    try {

        /*
         * Get all installed applications
         * from Windows.
         */
        const response =
            await fetch("/api/installed-apps");


        installedApps =
            await response.json();


        /*
         * Close configuration popup.
         */
        closeConfiguration();


        /*
         * Update slot number.
         */
        const installedAppSlot =
            document.getElementById(
                "installed-app-slot"
            );


        installedAppSlot.textContent =
            "Configuring Slot " + selectedSlot;


        /*
         * Clear search box.
         */
        const searchInput =
            document.getElementById(
                "installed-app-search"
            );


        searchInput.value = "";


        /*
         * Display all applications.
         */
        displayInstalledApps(
            installedApps
        );


        /*
         * Show modal.
         */
        const modal =
            document.getElementById(
                "installed-app-modal"
            );


        modal.classList.remove("hidden");


        /*
         * Automatically place cursor
         * inside search box.
         */
        searchInput.focus();


    } catch (error) {

        console.error(
            "Failed to load installed applications:",
            error
        );

        alert(
            "Failed to load installed applications."
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
     * No results.
     */
    if (apps.length === 0) {

        list.innerHTML = `

            <div class="installed-app-item">

                <div class="installed-app-item-name">
                    No applications found.
                </div>

            </div>

        `;

        return;
    }


    /*
     * Create one item for every application.
     */
    apps.forEach(function (app) {

        const item =
            document.createElement("div");


        item.classList.add(
            "installed-app-item"
        );


        item.innerHTML = `

            <div class="installed-app-item-name">
                ${app.name}
            </div>

        `;


        /*
         * Selecting an application.
         */
        item.addEventListener(
            "click",
            function () {

                selectInstalledApp(app);

            }
        );


        list.appendChild(item);
    });
}


/*
 * Search installed applications locally.
 *
 * The backend is NOT contacted for every
 * character typed.
 *
 * We already downloaded the full list.
 */
function searchInstalledApps() {

    const searchInput =
        document.getElementById(
            "installed-app-search"
        );


    const searchText =
        searchInput.value
            .toLowerCase()
            .trim();


    const filteredApps =
        installedApps.filter(
            function (app) {

                return app.name
                    .toLowerCase()
                    .includes(searchText);

            }
        );


    displayInstalledApps(
        filteredApps
    );
}


/*
 * Save selected installed application.
 */
async function selectInstalledApp(app) {

    if (selectedSlot === null) {
        return;
    }


    try {

        const response =
            await fetch(
                "/api/installed-apps/select",
                {

                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        slotNumber:
                            selectedSlot,

                        name:
                            app.name,

                        appId:
                            app.appId
                    })
                }
            );


        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(errorText);
        }


        /*
         * Close installed app modal.
         */
        closeInstalledApps();


        /*
         * Reload tiles from database.
         */
        await loadTiles();


    } catch (error) {

        console.error(
            "Failed to save installed application:",
            error
        );

        alert(
            "Failed to save installed application."
        );
    }
}


/*
 * Close Installed Applications modal.
 */
function closeInstalledApps() {

    const modal =
        document.getElementById(
            "installed-app-modal"
        );


    modal.classList.add("hidden");
}


/* =========================================================
   Custom Application
   ========================================================= */

/*
 * Open Custom Application configuration.
 */
function openCustomApp() {

    if (selectedSlot === null) {
        return;
    }


    /*
     * Close main configuration popup.
     */
    closeConfiguration();


    const modal =
        document.getElementById(
            "custom-app-modal"
        );


    const slotText =
        document.getElementById(
            "custom-app-slot"
        );


    const pathInput =
        document.getElementById(
            "custom-app-path"
        );


    const nameText =
        document.getElementById(
            "custom-app-name"
        );


    /*
     * Show selected slot.
     */
    slotText.textContent =
        "Configuring Slot " + selectedSlot;


    /*
     * Clear previous values.
     */
    pathInput.value = "";


    nameText.textContent =
        "Application: -";


    /*
     * Show modal.
     */
    modal.classList.remove("hidden");


    /*
     * Focus path input.
     */
    pathInput.focus();
}


/*
 * Extract EXE filename from a path.
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
     * Remove trailing slashes.
     */
    path =
        path.replace(/[\\\/]+$/, "");


    /*
     * Find last Windows/Linux separator.
     */
    const lastBackslash =
        path.lastIndexOf("\\");


    const lastSlash =
        path.lastIndexOf("/");


    const lastSeparator =
        Math.max(
            lastBackslash,
            lastSlash
        );


    if (lastSeparator === -1) {

        return path;
    }


    return path.substring(
        lastSeparator + 1
    );
}


/*
 * Update displayed EXE name while typing.
 */
function updateCustomAppName() {

    const pathInput =
        document.getElementById(
            "custom-app-path"
        );


    const nameText =
        document.getElementById(
            "custom-app-name"
        );


    const path =
        pathInput.value.trim();


    if (path === "") {

        nameText.textContent =
            "Application: -";

        return;
    }


    const name =
        getExeName(path);


    nameText.textContent =
        "Application: " + name;
}


/*
 * Save Custom Application.
 */
async function saveCustomApp() {

    if (selectedSlot === null) {
        return;
    }


    const pathInput =
        document.getElementById(
            "custom-app-path"
        );


    const path =
        pathInput.value.trim();


    /*
     * Check empty path.
     */
    if (path === "") {

        alert(
            "Please enter the EXE path."
        );

        return;
    }


    /*
     * Check EXE extension.
     */
    if (
        !path
            .toLowerCase()
            .endsWith(".exe")
    ) {

        alert(
            "Only .exe files are allowed."
        );

        return;
    }


    const name =
        getExeName(path);


    if (name === "") {

        alert(
            "Could not determine application name."
        );

        return;
    }


    try {

        const response =
            await fetch(
                "/api/custom-app/save",
                {

                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        slotNumber:
                            selectedSlot,

                        name:
                            name,

                        path:
                            path
                    })
                }
            );


        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(errorText);
        }


        /*
         * Close modal.
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
 * Close Custom Application modal.
 */
function closeCustomApp() {

    const modal =
        document.getElementById(
            "custom-app-modal"
        );


    modal.classList.add("hidden");
}


/* =========================================================
   Website
   ========================================================= */

/*
 * Open Website configuration.
 */
function openWebsite() {

    if (selectedSlot === null) {
        return;
    }


    /*
     * Close main configuration popup.
     */
    closeConfiguration();


    const modal =
        document.getElementById(
            "website-modal"
        );


    const slotText =
        document.getElementById(
            "website-slot"
        );


    const urlInput =
        document.getElementById(
            "website-url"
        );


    const nameText =
        document.getElementById(
            "website-name"
        );


    /*
     * Display slot number.
     */
    slotText.textContent =
        "Configuring Slot " + selectedSlot;


    /*
     * Clear previous values.
     */
    urlInput.value = "";


    nameText.textContent =
        "Website: -";


    /*
     * Show modal.
     */
    modal.classList.remove("hidden");


    /*
     * Focus URL field.
     */
    urlInput.focus();
}


/*
 * Extract a basic website name.
 *
 * Example:
 *
 * https://www.youtube.com/
 *
 * becomes:
 *
 * Youtube
 */
function getWebsiteName(url) {

    try {

        const website =
            new URL(url);


        /*
         * Get hostname.
         *
         * www.youtube.com
         */
        let hostname =
            website.hostname;


        /*
         * Remove www.
         *
         * youtube.com
         */
        hostname =
            hostname.replace(
                /^www\./,
                ""
            );


        /*
         * Split:
         *
         * youtube.com
         *
         * into:
         *
         * ["youtube", "com"]
         */
        const parts =
            hostname.split(".");


        if (parts.length > 0) {

            const mainName =
                parts[0];


            /*
             * Capitalize first character.
             */
            return (
                mainName
                    .charAt(0)
                    .toUpperCase()
                +
                mainName.slice(1)
            );
        }


        return hostname;


    } catch (error) {

        return "";
    }
}


/*
 * Update website name while typing.
 */
function updateWebsiteName() {

    const urlInput =
        document.getElementById(
            "website-url"
        );


    const nameText =
        document.getElementById(
            "website-name"
        );


    const url =
        urlInput.value.trim();


    if (url === "") {

        nameText.textContent =
            "Website: -";

        return;
    }


    const name =
        getWebsiteName(url);


    if (name !== "") {

        nameText.textContent =
            "Website: " + name;

    } else {

        nameText.textContent =
            "Website: -";
    }
}


/*
 * Save Website.
 */
async function saveWebsite() {

    if (selectedSlot === null) {
        return;
    }


    const urlInput =
        document.getElementById(
            "website-url"
        );


    const url =
        urlInput.value.trim();


    /*
     * Check empty URL.
     */
    if (url === "") {

        alert(
            "Please enter a website URL."
        );

        return;
    }


    /*
     * Only HTTP/HTTPS websites.
     */
    if (
        !url.startsWith("http://") &&
        !url.startsWith("https://")
    ) {

        alert(
            "Website URL must start with http:// or https://"
        );

        return;
    }


    const name =
        getWebsiteName(url);


    if (name === "") {

        alert(
            "Could not determine website name."
        );

        return;
    }


    try {

        const response =
            await fetch(
                "/api/website/save",
                {

                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        slotNumber:
                            selectedSlot,

                        name:
                            name,

                        url:
                            url
                    })
                }
            );


        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(errorText);
        }


        /*
         * Close website modal.
         */
        closeWebsite();


        /*
         * Reload tiles from database.
         */
        await loadTiles();


    } catch (error) {

        console.error(
            "Failed to save website:",
            error
        );

        alert(
            "Failed to save website."
        );
    }
}


/*
 * Close Website modal.
 */
function closeWebsite() {

    const modal =
        document.getElementById(
            "website-modal"
        );


    modal.classList.add("hidden");
}


/* =========================================================
   Event Listeners
   ========================================================= */


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
 * Custom Application button.
 */
document
    .getElementById("custom-app-button")
    .addEventListener(
        "click",
        openCustomApp
    );


/*
 * Website button.
 */
document
    .getElementById("website-button")
    .addEventListener(
        "click",
        openWebsite
    );


/*
 * Configuration Cancel button.
 */
document
    .getElementById("config-cancel-button")
    .addEventListener(
        "click",
        closeConfiguration
    );


/*
 * Installed App search.
 */
document
    .getElementById("installed-app-search")
    .addEventListener(
        "input",
        searchInstalledApps
    );


/*
 * Installed App Cancel.
 */
document
    .getElementById("installed-app-close-button")
    .addEventListener(
        "click",
        closeInstalledApps
    );


/*
 * Custom App path input.
 *
 * Updates application name
 * while user types.
 */
document
    .getElementById("custom-app-path")
    .addEventListener(
        "input",
        updateCustomAppName
    );


/*
 * Custom App Save.
 */
document
    .getElementById("custom-app-save-button")
    .addEventListener(
        "click",
        saveCustomApp
    );


/*
 * Custom App Cancel.
 */
document
    .getElementById("custom-app-close-button")
    .addEventListener(
        "click",
        closeCustomApp
    );


/*
 * Website URL input.
 *
 * Updates website name
 * while user types.
 */
document
    .getElementById("website-url")
    .addEventListener(
        "input",
        updateWebsiteName
    );


/*
 * Website Save.
 */
document
    .getElementById("website-save-button")
    .addEventListener(
        "click",
        saveWebsite
    );


/*
 * Website Cancel.
 */
document
    .getElementById("website-close-button")
    .addEventListener(
        "click",
        closeWebsite
    );


/* =========================================================
   Initial Load
   ========================================================= */

/*
 * Load server information when page starts.
 */
loadServerInfo();

/*
 * Load tiles when page starts.
 */
loadTiles();