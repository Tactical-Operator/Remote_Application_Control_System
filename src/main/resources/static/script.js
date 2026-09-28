/*
 * =====================================================
 * Remote Pulse
 * script.js
 * =====================================================
 */


/*
 * Stores the slot currently being configured.
 */
let selectedSlot = null;


/*
 * Stores the latest Tile objects received
 * from Spring Boot.
 */
let currentTiles = [];


/*
 * Stores all installed applications.
 */
let installedApps = [];


/*
 * Stores the installed application
 * currently selected by the user.
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
            await fetch(
                "/api/server-info"
            );


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
 * Load all tiles from Spring Boot.
 */
async function loadTiles() {

    console.log(
        ">>> loadTiles() CALLED"
    );


    try {

        const response =
            await fetch(
                `/api/tiles?t=${Date.now()}`,
                {
                    cache: "no-store"
                }
            );


        const tiles =
            await response.json();


        /*
         * Store the complete tile list.
         *
         * This allows openConfiguration()
         * to inspect the clicked tile.
         */
        currentTiles =
            tiles;


        console.log(
            ">>> TILES RECEIVED:",
            tiles
        );


        displayTiles(
            tiles
        );

    } catch (error) {

        console.error(
            "Failed to load tiles:",
            error
        );
    }
}


/*
 * Display all 15 tiles.
 */
function displayTiles(tiles) {

    const container =
        document.getElementById(
            "tile-container"
        );


    container.innerHTML =
        "";


    /*
     * Sort:
     *
     * 1
     * 2
     * 3
     * ...
     * 15
     */
    tiles.sort(
        (a, b) =>
            a.slotNumber -
            b.slotNumber
    );


    tiles.forEach(tile => {

        const tileElement =
            document.createElement(
                "div"
            );


        tileElement.className =
            "tile";


        /*
         * Clicking a tile on the Windows
         * configuration page DOES NOT
         * launch the application.
         *
         * It only opens configuration.
         */
        tileElement.addEventListener(
            "click",
            () => {
                openConfiguration(
                    tile.slotNumber
                );
            }
        );


        /*
         * Tile is empty only when
         * absolutely nothing is configured.
         */
        const isEmpty =
            !tile.name &&
            !tile.type &&
            !tile.target &&
            !tile.icon;


        /*
         * Completely empty slot.
         */
        if (isEmpty) {

            tileElement.classList.add(
                "empty-tile"
            );


            const questionMark =
                document.createElement(
                    "div"
                );


            questionMark.className =
                "empty-tile-question";


            questionMark.textContent =
                "?";


            tileElement.appendChild(
                questionMark
            );
        }


        /*
         * Configured slot having an icon.
         */
        else if (tile.icon) {

            const icon =
                document.createElement(
                    "img"
                );


            icon.className =
                "tile-icon";


            /*
             * Add a unique value to prevent
             * Chrome from displaying an old
             * cached slot-X.png.
             */
            const iconUrl =
                `/icons/${tile.icon}` +
                `?v=${Date.now()}-${Math.random()}`;


            icon.onload =
                function () {

                    console.log(
                        `ICON LOADED FOR SLOT ${tile.slotNumber}:`,
                        tile.name
                    );
                };


            icon.onerror =
                function () {

                    console.error(
                        `ICON FAILED FOR SLOT ${tile.slotNumber}:`,
                        iconUrl
                    );
                };


            icon.src =
                iconUrl;


            icon.alt =
                tile.name ||
                `Slot ${tile.slotNumber}`;


            tileElement.appendChild(
                icon
            );
        }


        /*
         * Configuration exists but there
         * is currently no icon.
         */
        else {

            const questionMark =
                document.createElement(
                    "div"
                );


            questionMark.className =
                "empty-tile-question";


            questionMark.textContent =
                "?";


            tileElement.appendChild(
                questionMark
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
 * Open Configure Tile modal.
 */
function openConfiguration(slotNumber) {

    selectedSlot =
        slotNumber;


    /*
     * Find the complete Tile object.
     */
    const tile =
        currentTiles.find(
            tile =>
                tile.slotNumber ===
                slotNumber
        );


    document.getElementById(
        "config-slot"
    ).textContent =
        `Slot ${slotNumber}`;


    const deleteButton =
        document.getElementById(
            "delete-configuration-button"
        );


    /*
     * A slot counts as configured if
     * ANYTHING exists:
     *
     * name
     * type
     * target
     * icon
     *
     * Therefore an icon-only tile also
     * counts as configured.
     */
    const isConfigured =
        tile &&
        (
            tile.name ||
            tile.type ||
            tile.target ||
            tile.icon
        );


    /*
     * Only configured tiles should
     * display Delete Configuration.
     */
    if (isConfigured) {

        deleteButton.classList.remove(
            "hidden"
        );

    } else {

        deleteButton.classList.add(
            "hidden"
        );
    }


    document.getElementById(
        "config-modal"
    ).classList.remove(
        "hidden"
    );
}


/*
 * Close Configure Tile modal.
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
 * Delete Configuration
 * =====================================================
 */


/*
 * Open Delete Configuration confirmation.
 */
function openDeleteConfiguration() {

    if (selectedSlot === null) {
        return;
    }


    const tile =
        currentTiles.find(
            tile =>
                tile.slotNumber ===
                selectedSlot
        );


    if (!tile) {

        console.error(
            "Selected tile could not be found."
        );

        return;
    }


    let displayName;


    /*
     * Website:
     *
     * Show the actual website URL.
     *
     * Example:
     * https://github.com
     */
    if (
        tile.type === "WEBSITE" &&
        tile.target
    ) {

        displayName =
            tile.target;
    }


    /*
     * Installed application:
     *
     * Example:
     * Visual Studio Code
     */
    else if (
        tile.type === "INSTALLED_APP" &&
        tile.name
    ) {

        displayName =
            tile.name;
    }


    /*
     * Custom application:
     *
     * Example:
     * water_breaktest
     */
    else if (
        tile.type === "CUSTOM_APP" &&
        tile.name
    ) {

        displayName =
            tile.name;
    }


    /*
     * Generic name fallback.
     */
    else if (tile.name) {

        displayName =
            tile.name;
    }


    /*
     * Icon-only slot.
     */
    else if (tile.icon) {

        displayName =
            `Icon for Slot ${tile.slotNumber}`;
    }


    /*
     * Final fallback.
     */
    else {

        displayName =
            `Slot ${tile.slotNumber}`;
    }


    document.getElementById(
        "delete-configuration-name"
    ).textContent =
        displayName;


    /*
     * Hide configuration modal.
     */
    document.getElementById(
        "config-modal"
    ).classList.add(
        "hidden"
    );


    /*
     * Show delete confirmation.
     */
    document.getElementById(
        "delete-configuration-modal"
    ).classList.remove(
        "hidden"
    );
}


/*
 * User clicked No.
 */
function cancelDeleteConfiguration() {

    /*
     * Hide delete confirmation.
     */
    document.getElementById(
        "delete-configuration-modal"
    ).classList.add(
        "hidden"
    );


    /*
     * Return to Configure Tile.
     */
    document.getElementById(
        "config-modal"
    ).classList.remove(
        "hidden"
    );
}


/*
 * User clicked Yes.
 */
async function deleteConfiguration() {

    if (selectedSlot === null) {
        return;
    }


    /*
     * Remember the slot before
     * selectedSlot is cleared.
     */
    const slotToDelete =
        selectedSlot;


    try {

        const response =
            await fetch(
                `/api/tiles/slot/${slotToDelete}/configuration`,
                {
                    method: "DELETE"
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
         * Give the backend a small moment
         * after deleting the icon file.
         */
        await new Promise(
            resolve =>
                setTimeout(
                    resolve,
                    300
                )
        );


        /*
         * Reload fresh tile information.
         */
        await loadTiles();


        /*
         * Hide confirmation modal.
         */
        document.getElementById(
            "delete-configuration-modal"
        ).classList.add(
            "hidden"
        );


        /*
         * Make sure Configure Tile is
         * also closed.
         */
        document.getElementById(
            "config-modal"
        ).classList.add(
            "hidden"
        );


        selectedSlot =
            null;


    } catch (error) {

        console.error(
            "Failed to delete configuration:",
            error
        );


        alert(
            "Failed to delete configuration."
        );
    }
}


/*
 * =====================================================
 * Installed Applications
 * =====================================================
 */


/*
 * Open Installed Applications modal.
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
        ).value =
            "";


        document.getElementById(
            "installed-app-selected"
        ).textContent =
            "Selected: None";


        document.getElementById(
            "installed-app-custom-icon"
        ).value =
            "";


        /*
         * Make sure the result list
         * is visible when modal opens.
         */
        document.getElementById(
            "installed-app-list"
        ).classList.remove(
            "hidden"
        );


        displayInstalledApps(
            installedApps
        );


        /*
         * Hide main configuration modal.
         */
        document.getElementById(
            "config-modal"
        ).classList.add(
            "hidden"
        );


        /*
         * Show Installed Apps modal.
         */
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


    list.innerHTML =
        "";


    apps.forEach(app => {

        const item =
            document.createElement(
                "div"
            );


        item.className =
            "installed-app-item";


        const name =
            document.createElement(
                "div"
            );


        name.className =
            "installed-app-item-name";


        name.textContent =
            app.name;


        item.appendChild(
            name
        );


        /*
         * Selecting an application does
         * NOT save it yet.
         *
         * User must still press Save.
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
 *
 * The complete app list is already loaded
 * from Spring Boot, so typing does not
 * make another backend request.
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
     * User started typing again,
     * so show results again.
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
 * Select one installed application.
 */
function selectInstalledApp(app) {

    selectedInstalledApp =
        app;


    document.getElementById(
        "installed-app-selected"
    ).textContent =
        `Selected: ${app.name}`;


    /*
     * Hide result list once an
     * application has been selected.
     */
    document.getElementById(
        "installed-app-list"
    ).classList.add(
        "hidden"
    );
}


/*
 * Save selected installed application.
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
         * If the user selected a custom
         * icon, upload it now.
         */
        const iconSaved =
            await saveCustomIconIfSelected();


        if (!iconSaved) {
            return;
        }


        /*
         * IMPORTANT:
         *
         * We previously found that the
         * generated icon sometimes wasn't
         * immediately available when the
         * frontend refreshed.
         *
         * Keep this 500 ms delay.
         */
        await new Promise(
            resolve =>
                setTimeout(
                    resolve,
                    500
                )
        );


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
 * Close Installed Applications modal.
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
 * Optional Custom Icon System
 * =====================================================
 */


/*
 * Check whether the user selected a
 * custom icon from:
 *
 * Installed App
 * Custom App
 * Website
 *
 * If no icon was selected, return true
 * because that is perfectly valid.
 */
async function saveCustomIconIfSelected() {

    let fileInput;


    /*
     * Installed Application modal.
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
    }


    /*
     * Custom Application modal.
     */
    else if (
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
    }


    /*
     * Website modal.
     */
    else if (
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
     * This is NOT an error.
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
 * Upload an image to Spring Boot.
 *
 * Used by:
 *
 * Installed App custom icon
 * Custom App custom icon
 * Website custom icon
 * Change Icon
 */
function uploadCustomIcon(file) {

    return new Promise(
        resolve => {

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


                        resolve(
                            true
                        );


                    } catch (error) {

                        console.error(
                            "Failed to save custom icon:",
                            error
                        );


                        alert(
                            "Failed to save custom icon."
                        );


                        resolve(
                            false
                        );
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


                    resolve(
                        false
                    );
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
 * Open Change Icon modal.
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
    ).value =
        "";


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
 * This DOES NOT change:
 *
 * name
 * type
 * target
 *
 * Only icon is changed.
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
     * Small delay helps when replacing
     * the physical slot-X.png file.
     */
    await new Promise(
        resolve =>
            setTimeout(
                resolve,
                300
            )
    );


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
    ).value =
        "";


    document.getElementById(
        "custom-app-name"
    ).textContent =
        "Application: -";


    document.getElementById(
        "custom-app-custom-icon"
    ).value =
        "";


    /*
     * Hide Configure Tile.
     */
    document.getElementById(
        "config-modal"
    ).classList.add(
        "hidden"
    );


    /*
     * Show Custom App modal.
     */
    document.getElementById(
        "custom-app-modal"
    ).classList.remove(
        "hidden"
    );
}


/*
 * Automatically determine the app name
 * from the entered EXE path.
 *
 * Example:
 *
 * D:\Apps\Spotify.exe
 *
 * becomes:
 *
 * Spotify
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


        /*
         * Optional custom icon.
         */
        const iconSaved =
            await saveCustomIconIfSelected();


        if (!iconSaved) {
            return;
        }


        /*
         * Give icon generation a moment.
         */
        await new Promise(
            resolve =>
                setTimeout(
                    resolve,
                    300
                )
        );


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
    ).value =
        "";


    document.getElementById(
        "website-name"
    ).textContent =
        "Website: -";


    document.getElementById(
        "website-custom-icon"
    ).value =
        "";


    /*
     * Hide Configure Tile.
     */
    document.getElementById(
        "config-modal"
    ).classList.add(
        "hidden"
    );


    /*
     * Show Website modal.
     */
    document.getElementById(
        "website-modal"
    ).classList.remove(
        "hidden"
    );
}


/*
 * Update website display name
 * while the user types.
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


    /*
     * Allow the preview to work even
     * if user has not typed https:// yet.
     */
    let previewUrl =
        url;


    if (
        !previewUrl.startsWith(
            "http://"
        ) &&
        !previewUrl.startsWith(
            "https://"
        )
    ) {

        previewUrl =
            "https://" +
            previewUrl;
    }


    try {

        const parsedUrl =
            new URL(
                previewUrl
            );


        document.getElementById(
            "website-name"
        ).textContent =
            `Website: ${parsedUrl.hostname}`;


    } catch (error) {

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
     * protocol was not entered.
     */
    if (
        !url.startsWith(
            "http://"
        ) &&
        !url.startsWith(
            "https://"
        )
    ) {

        url =
            "https://" +
            url;
    }


    try {

        const parsedUrl =
            new URL(
                url
            );


        const name =
            parsedUrl.hostname;


        /*
         * Save website configuration.
         *
         * WebsiteSaveService waits for the
         * PowerShell icon extraction process
         * to finish before this request returns.
         */
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


        /*
         * Save optional custom icon.
         *
         * If the user selected a custom icon,
         * it replaces the automatically
         * extracted website icon.
         */
        const iconSaved =
            await saveCustomIconIfSelected();


        if (!iconSaved) {
            return;
        }


        /*
         * Reload the tile configuration.
         */
        await loadTiles();


        /*
         * Close Website modal.
         */
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
 * =====================================================
 * Configure Tile
 * =====================================================
 */


/*
 * Installed Application.
 */
document
    .getElementById(
        "installed-app-button"
    )
    .addEventListener(
        "click",
        openInstalledApps
    );


/*
 * Custom Application.
 */
document
    .getElementById(
        "custom-app-button"
    )
    .addEventListener(
        "click",
        openCustomApp
    );


/*
 * Website.
 */
document
    .getElementById(
        "website-button"
    )
    .addEventListener(
        "click",
        openWebsite
    );


/*
 * Change Icon.
 */
document
    .getElementById(
        "change-icon-button"
    )
    .addEventListener(
        "click",
        openChangeIcon
    );


/*
 * Delete Configuration.
 */
document
    .getElementById(
        "delete-configuration-button"
    )
    .addEventListener(
        "click",
        openDeleteConfiguration
    );


/*
 * Configure Tile Cancel.
 */
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
 * Delete Configuration
 * =====================================================
 */


/*
 * YES.
 */
document
    .getElementById(
        "delete-configuration-yes-button"
    )
    .addEventListener(
        "click",
        deleteConfiguration
    );


/*
 * NO.
 */
document
    .getElementById(
        "delete-configuration-no-button"
    )
    .addEventListener(
        "click",
        cancelDeleteConfiguration
    );


/*
 * =====================================================
 * Change Icon
 * =====================================================
 */


/*
 * Save changed icon.
 */
document
    .getElementById(
        "change-icon-save-button"
    )
    .addEventListener(
        "click",
        saveIndependentIcon
    );


/*
 * Cancel Change Icon.
 */
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


/*
 * Search while typing.
 */
document
    .getElementById(
        "installed-app-search"
    )
    .addEventListener(
        "input",
        searchInstalledApps
    );


/*
 * Save installed application.
 */
document
    .getElementById(
        "installed-app-save-button"
    )
    .addEventListener(
        "click",
        saveInstalledApp
    );


/*
 * Cancel Installed Applications.
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
 * =====================================================
 * Custom Application
 * =====================================================
 */


/*
 * Update app name while EXE
 * path is being entered.
 */
document
    .getElementById(
        "custom-app-path"
    )
    .addEventListener(
        "input",
        updateCustomAppName
    );


/*
 * Save Custom Application.
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
 * Cancel Custom Application.
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
 * =====================================================
 * Website
 * =====================================================
 */


/*
 * Update website name while typing.
 */
document
    .getElementById(
        "website-url"
    )
    .addEventListener(
        "input",
        updateWebsiteName
    );


/*
 * Save Website.
 */
document
    .getElementById(
        "website-save-button"
    )
    .addEventListener(
        "click",
        saveWebsite
    );


/*
 * Cancel Website.
 */
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


/*
 * Display Spring Boot server IP.
 */
loadServerInfo();


/*
 * Load all 15 Remote Pulse tiles.
 */
loadTiles();