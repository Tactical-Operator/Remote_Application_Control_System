/*
 * Stores the slot currently being configured.
 *
 * Example:
 * Click Slot 5
 * selectedSlot = 5
 */
let selectedSlot = null;


/*
 * Stores all installed applications.
 */
let installedApps = [];


/*
 * Stores the installed application currently selected.
 */
let selectedInstalledApp = null;


/*
 * =====================================================
 * Server Information
 * =====================================================
 */


/*
 * Load Spring Boot server information.
 */
async function loadServerInfo() {

    try {

        const response =
            await fetch("/api/server-info");

        const serverInfo =
            await response.json();

        document.getElementById(
            "server-address"
        ).textContent =
            `Server - ${serverInfo.address}`;

    } catch (error) {

        console.error(
            "Failed to load server information:",
            error
        );

        document.getElementById(
            "server-address"
        ).textContent =
            "Server - Unknown";
    }
}


/*
 * =====================================================
 * Tiles
 * =====================================================
 */


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
        document.getElementById(
            "tile-container"
        );

    container.innerHTML = "";


    /*
     * Sort tiles by slot number.
     */
    tiles.sort(
        (a, b) =>
            a.slotNumber - b.slotNumber
    );


    tiles.forEach(tile => {

        const tileElement =
            document.createElement("div");

        tileElement.className =
            "tile";


        /*
         * Clicking a Windows tile only opens
         * configuration.
         *
         * It does NOT launch the application.
         */
        tileElement.addEventListener(
            "click",
            () =>
                openConfiguration(
                    tile.slotNumber
                )
        );


        /*
         * Empty tile.
         */
        if (!tile.name) {

            tileElement.classList.add(
                "empty-tile"
            );

            const plus =
                document.createElement("div");

            plus.textContent = "+";


            const label =
                document.createElement("div");

            label.className =
                "empty-tile-label";

            label.textContent =
                `Slot ${tile.slotNumber}`;


            tileElement.appendChild(
                plus
            );

            tileElement.appendChild(
                label
            );

        } else {

            /*
             * Configured tile.
             *
             * Only display its icon.
             */

            const icon =
                document.createElement("img");

            icon.className =
                "tile-icon";


            /*
             * Timestamp prevents browser caching
             * when an icon has been replaced.
             */
            icon.src =
                `/icons/${tile.icon}?t=${Date.now()}`;

            icon.alt =
                tile.name;

            tileElement.appendChild(
                icon
            );
        }


        container.appendChild(
            tileElement
        );
    });
}


/*
 * =====================================================
 * Configuration Modal
 * =====================================================
 */


/*
 * Open configuration modal.
 */
function openConfiguration(slotNumber) {

    selectedSlot =
        slotNumber;

    document.getElementById(
        "config-slot"
    ).textContent =
        `Slot ${slotNumber}`;

    document.getElementById(
        "config-modal"
    ).classList.remove(
        "hidden"
    );
}


/*
 * Close configuration modal.
 */
function closeConfiguration() {

    document.getElementById(
        "config-modal"
    ).classList.add(
        "hidden"
    );

    selectedSlot =
        null;
}


/*
 * =====================================================
 * Installed Applications
 * =====================================================
 */


/*
 * Open installed applications modal.
 */
async function openInstalledApps() {

    if (selectedSlot === null) {
        return;
    }

    try {

        const response =
            await fetch(
                "/api/installed-apps"
            );

        installedApps =
            await response.json();

        selectedInstalledApp =
            null;


        document.getElementById(
            "installed-app-slot"
        ).textContent =
            `Slot ${selectedSlot}`;


        document.getElementById(
            "installed-app-search"
        ).value = "";


        document.getElementById(
            "installed-app-selected"
        ).textContent =
            "Selected: None";


        document.getElementById(
            "installed-app-custom-icon"
        ).value = "";


        /*
         * Make sure the list is visible again
         * whenever this modal is opened.
         */
        document.getElementById(
            "installed-app-list"
        ).classList.remove(
            "hidden"
        );


        displayInstalledApps(
            installedApps
        );


        document.getElementById(
            "config-modal"
        ).classList.add(
            "hidden"
        );


        document.getElementById(
            "installed-app-modal"
        ).classList.remove(
            "hidden"
        );

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


    apps.forEach(app => {

        const item =
            document.createElement("div");

        item.className =
            "installed-app-item";


        const name =
            document.createElement("div");

        name.className =
            "installed-app-item-name";

        name.textContent =
            app.name;


        item.appendChild(
            name
        );


        /*
         * Selecting an application does NOT
         * save it.
         *
         * The user still has to press Save.
         */
        item.addEventListener(
            "click",
            function (event) {

                event.preventDefault();

                event.stopPropagation();

                selectInstalledApp(
                    app
                );
            }
        );


        list.appendChild(
            item
        );
    });
}


/*
 * Search installed applications locally.
 */
function searchInstalledApps() {

    const searchText =
        document.getElementById(
            "installed-app-search"
        )
            .value
            .toLowerCase()
            .trim();


    /*
     * If the user starts searching again,
     * show the result list again.
     */
    document.getElementById(
        "installed-app-list"
    ).classList.remove(
        "hidden"
    );


    const filteredApps =
        installedApps.filter(
            app =>
                app.name
                    .toLowerCase()
                    .includes(
                        searchText
                    )
        );


    displayInstalledApps(
        filteredApps
    );
}


/*
 * Select installed application.
 */
function selectInstalledApp(app) {

    selectedInstalledApp =
        app;


    document.getElementById(
        "installed-app-selected"
    ).textContent =
        `Selected: ${app.name}`;


    /*
     * Once an application has been selected,
     * hide the result list.
     */
    document.getElementById(
        "installed-app-list"
    ).classList.add(
        "hidden"
    );
}


/*
 * Save installed application.
 */
async function saveInstalledApp() {

    if (selectedSlot === null) {
        return;
    }


    if (!selectedInstalledApp) {

        alert(
            "Please select an application first."
        );

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
                            selectedInstalledApp.name,

                        appId:
                            selectedInstalledApp.appId
                    })
                }
            );


        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(
                errorText
            );
        }


        /*
         * Save optional custom icon.
         */
        const iconSaved =
            await saveCustomIconIfSelected();


        if (!iconSaved) {
            return;
        }


        /*
         * Refresh while selectedSlot
         * still contains the correct slot.
         */
        await loadTiles();


        closeInstalledApps();

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
 * Close installed applications modal.
 */
function closeInstalledApps() {

    document.getElementById(
        "installed-app-modal"
    ).classList.add(
        "hidden"
    );

    selectedInstalledApp =
        null;

    selectedSlot =
        null;
}


/*
 * =====================================================
 * Existing Optional Custom Icon System
 * =====================================================
 */


/*
 * Save a custom icon selected from one of the
 * Installed App / Custom App / Website forms.
 */
async function saveCustomIconIfSelected() {

    let fileInput;


    /*
     * Determine which configuration modal
     * is currently visible.
     */
    if (
        !document
            .getElementById(
                "installed-app-modal"
            )
            .classList.contains(
                "hidden"
            )
    ) {

        fileInput =
            document.getElementById(
                "installed-app-custom-icon"
            );

    } else if (
        !document
            .getElementById(
                "custom-app-modal"
            )
            .classList.contains(
                "hidden"
            )
    ) {

        fileInput =
            document.getElementById(
                "custom-app-custom-icon"
            );

    } else if (
        !document
            .getElementById(
                "website-modal"
            )
            .classList.contains(
                "hidden"
            )
    ) {

        fileInput =
            document.getElementById(
                "website-custom-icon"
            );
    }


    /*
     * No custom icon selected.
     *
     * This is not an error.
     */
    if (
        !fileInput ||
        fileInput.files.length === 0
    ) {

        return true;
    }


    const file =
        fileInput.files[0];


    return await uploadCustomIcon(
        file
    );
}


/*
 * =====================================================
 * Shared Custom Icon Upload
 * =====================================================
 */


/*
 * Upload one image to the Spring Boot
 * custom-icon endpoint.
 *
 * This function is shared by:
 *
 * 1. Installed App custom icon
 * 2. Custom App custom icon
 * 3. Website custom icon
 * 4. Independent Change Icon feature
 */
function uploadCustomIcon(file) {

    return new Promise(
        (resolve) => {

            const reader =
                new FileReader();


            reader.onload =
                async function () {

                    try {

                        const response =
                            await fetch(
                                "/api/custom-icon/save",
                                {
                                    method: "POST",

                                    headers: {
                                        "Content-Type":
                                            "application/json"
                                    },

                                    body:
                                        JSON.stringify({

                                            slotNumber:
                                                selectedSlot,

                                            imageBase64:
                                                reader.result
                                        })
                                }
                            );


                        if (!response.ok) {

                            const errorText =
                                await response.text();

                            throw new Error(
                                errorText
                            );
                        }


                        resolve(true);

                    } catch (error) {

                        console.error(
                            "Failed to save custom icon:",
                            error
                        );

                        alert(
                            "Failed to save custom icon."
                        );

                        resolve(false);
                    }
                };


            reader.onerror =
                function () {

                    console.error(
                        "Failed to read icon file."
                    );

                    alert(
                        "Failed to read icon file."
                    );

                    resolve(false);
                };


            reader.readAsDataURL(
                file
            );
        }
    );
}


/*
 * =====================================================
 * Independent Change Icon
 * =====================================================
 */


/*
 * Open independent Change Icon modal.
 */
function openChangeIcon() {

    if (selectedSlot === null) {
        return;
    }


    document.getElementById(
        "change-icon-slot"
    ).textContent =
        `Slot ${selectedSlot}`;


    /*
     * Clear previous file selection.
     */
    document.getElementById(
        "change-icon-file"
    ).value = "";


    /*
     * Hide configuration modal.
     */
    document.getElementById(
        "config-modal"
    ).classList.add(
        "hidden"
    );


    /*
     * Show Change Icon modal.
     */
    document.getElementById(
        "change-icon-modal"
    ).classList.remove(
        "hidden"
    );
}


/*
 * Save independent custom icon.
 *
 * IMPORTANT:
 * This does NOT modify:
 *
 * name
 * type
 * target
 *
 * It only replaces the icon for the slot.
 */
async function saveIndependentIcon() {

    if (selectedSlot === null) {
        return;
    }


    const fileInput =
        document.getElementById(
            "change-icon-file"
        );


    if (
        fileInput.files.length === 0
    ) {

        alert(
            "Please select an icon first."
        );

        return;
    }


    const file =
        fileInput.files[0];


    const iconSaved =
        await uploadCustomIcon(
            file
        );


    if (!iconSaved) {
        return;
    }


    /*
     * Reload the tiles immediately.
     *
     * displayTiles() adds a new timestamp
     * to the image URL, preventing an old
     * cached icon from being displayed.
     */
    await loadTiles();


    closeChangeIcon();
}


/*
 * Close Change Icon modal.
 */
function closeChangeIcon() {

    document.getElementById(
        "change-icon-modal"
    ).classList.add(
        "hidden"
    );

    selectedSlot =
        null;
}


/*
 * =====================================================
 * Custom Application
 * =====================================================
 */


/*
 * Open Custom Application modal.
 */
function openCustomApp() {

    if (selectedSlot === null) {
        return;
    }


    document.getElementById(
        "custom-app-slot"
    ).textContent =
        `Slot ${selectedSlot}`;


    document.getElementById(
        "custom-app-path"
    ).value = "";


    document.getElementById(
        "custom-app-name"
    ).textContent =
        "Application: -";


    document.getElementById(
        "custom-app-custom-icon"
    ).value = "";


    document.getElementById(
        "config-modal"
    ).classList.add(
        "hidden"
    );


    document.getElementById(
        "custom-app-modal"
    ).classList.remove(
        "hidden"
    );
}


/*
 * Update custom application name.
 */
function updateCustomAppName() {

    const path =
        document.getElementById(
            "custom-app-path"
        ).value.trim();


    if (!path) {

        document.getElementById(
            "custom-app-name"
        ).textContent =
            "Application: -";

        return;
    }


    const fileName =
        path
            .split("\\")
            .pop()
            .split("/")
            .pop();


    const appName =
        fileName.replace(
            /\.exe$/i,
            ""
        );


    document.getElementById(
        "custom-app-name"
    ).textContent =
        `Application: ${appName}`;
}


/*
 * Save Custom Application.
 */
async function saveCustomApp() {

    if (selectedSlot === null) {
        return;
    }


    const path =
        document.getElementById(
            "custom-app-path"
        ).value.trim();


    if (!path) {

        alert(
            "Please enter an EXE path."
        );

        return;
    }


    const fileName =
        path
            .split("\\")
            .pop()
            .split("/")
            .pop();


    const name =
        fileName.replace(
            /\.exe$/i,
            ""
        );


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

            throw new Error(
                errorText
            );
        }


        const iconSaved =
            await saveCustomIconIfSelected();


        if (!iconSaved) {
            return;
        }


        /*
         * Refresh tiles immediately.
         */
        await loadTiles();


        closeCustomApp();

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

    document.getElementById(
        "custom-app-modal"
    ).classList.add(
        "hidden"
    );

    selectedSlot =
        null;
}


/*
 * =====================================================
 * Website
 * =====================================================
 */


/*
 * Open Website modal.
 */
function openWebsite() {

    if (selectedSlot === null) {
        return;
    }


    document.getElementById(
        "website-slot"
    ).textContent =
        `Slot ${selectedSlot}`;


    document.getElementById(
        "website-url"
    ).value = "";


    document.getElementById(
        "website-name"
    ).textContent =
        "Website: -";


    document.getElementById(
        "website-custom-icon"
    ).value = "";


    document.getElementById(
        "config-modal"
    ).classList.add(
        "hidden"
    );


    document.getElementById(
        "website-modal"
    ).classList.remove(
        "hidden"
    );
}


/*
 * Update website name.
 */
function updateWebsiteName() {

    const url =
        document.getElementById(
            "website-url"
        ).value.trim();


    if (!url) {

        document.getElementById(
            "website-name"
        ).textContent =
            "Website: -";

        return;
    }


    try {

        const parsedUrl =
            new URL(url);


        document.getElementById(
            "website-name"
        ).textContent =
            `Website: ${parsedUrl.hostname}`;

    } catch {

        document.getElementById(
            "website-name"
        ).textContent =
            "Website: Invalid URL";
    }
}


/*
 * Save Website.
 */
async function saveWebsite() {

    if (selectedSlot === null) {
        return;
    }


    let url =
        document.getElementById(
            "website-url"
        ).value.trim();


    if (!url) {

        alert(
            "Please enter a website URL."
        );

        return;
    }


    /*
     * Automatically add HTTPS when
     * the user did not type a protocol.
     */
    if (
        !url.startsWith("http://") &&
        !url.startsWith("https://")
    ) {

        url =
            "https://" + url;
    }


    try {

        const parsedUrl =
            new URL(url);


        const name =
            parsedUrl.hostname;


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

            throw new Error(
                errorText
            );
        }


        const iconSaved =
            await saveCustomIconIfSelected();


        if (!iconSaved) {
            return;
        }


        /*
         * Refresh tiles immediately.
         */
        await loadTiles();


        closeWebsite();

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

    document.getElementById(
        "website-modal"
    ).classList.add(
        "hidden"
    );

    selectedSlot =
        null;
}


/*
 * =====================================================
 * Event Listeners
 * =====================================================
 */


/*
 * Configuration modal.
 */

document
    .getElementById(
        "installed-app-button"
    )
    .addEventListener(
        "click",
        openInstalledApps
    );


document
    .getElementById(
        "custom-app-button"
    )
    .addEventListener(
        "click",
        openCustomApp
    );


document
    .getElementById(
        "website-button"
    )
    .addEventListener(
        "click",
        openWebsite
    );


/*
 * NEW:
 * Independent Change Icon button.
 */
document
    .getElementById(
        "change-icon-button"
    )
    .addEventListener(
        "click",
        openChangeIcon
    );


document
    .getElementById(
        "config-cancel-button"
    )
    .addEventListener(
        "click",
        closeConfiguration
    );


/*
 * =====================================================
 * Change Icon
 * =====================================================
 */

document
    .getElementById(
        "change-icon-save-button"
    )
    .addEventListener(
        "click",
        saveIndependentIcon
    );


document
    .getElementById(
        "change-icon-close-button"
    )
    .addEventListener(
        "click",
        closeChangeIcon
    );


/*
 * =====================================================
 * Installed Applications
 * =====================================================
 */

document
    .getElementById(
        "installed-app-search"
    )
    .addEventListener(
        "input",
        searchInstalledApps
    );


document
    .getElementById(
        "installed-app-save-button"
    )
    .addEventListener(
        "click",
        saveInstalledApp
    );


document
    .getElementById(
        "installed-app-close-button"
    )
    .addEventListener(
        "click",
        closeInstalledApps
    );


/*
 * =====================================================
 * Custom Application
 * =====================================================
 */

document
    .getElementById(
        "custom-app-path"
    )
    .addEventListener(
        "input",
        updateCustomAppName
    );


document
    .getElementById(
        "custom-app-save-button"
    )
    .addEventListener(
        "click",
        saveCustomApp
    );


document
    .getElementById(
        "custom-app-close-button"
    )
    .addEventListener(
        "click",
        closeCustomApp
    );


/*
 * =====================================================
 * Website
 * =====================================================
 */

document
    .getElementById(
        "website-url"
    )
    .addEventListener(
        "input",
        updateWebsiteName
    );


document
    .getElementById(
        "website-save-button"
    )
    .addEventListener(
        "click",
        saveWebsite
    );


document
    .getElementById(
        "website-close-button"
    )
    .addEventListener(
        "click",
        closeWebsite
    );


/*
 * =====================================================
 * Initial Page Load
 * =====================================================
 */

loadServerInfo();

loadTiles();