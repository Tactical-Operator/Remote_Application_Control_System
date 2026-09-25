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
 * Load Spring Boot server information.
 */
async function loadServerInfo() {

    try {

        const response =
            await fetch("/api/server-info");

        const serverInfo =
            await response.json();

        document.getElementById("server-address").textContent =
            `Server - ${serverInfo.address}`;

    } catch (error) {

        console.error(
            "Failed to load server information:",
            error
        );

        document.getElementById("server-address").textContent =
            "Server - Unknown";
    }
}


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

        tileElement.className = "tile";


        /*
         * Clicking a tile opens configuration.
         *
         * The Windows frontend never launches
         * the application.
         */
        tileElement.addEventListener(
            "click",
            () => openConfiguration(tile.slotNumber)
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


            tileElement.appendChild(plus);
            tileElement.appendChild(label);

        }


        /*
         * Configured tile.
         *
         * Only the icon is displayed.
         *
         * No application name.
         * No application type.
         */
        else {

            const icon =
                document.createElement("img");

            icon.className =
                "tile-icon";


            /*
             * Add timestamp to prevent the browser
             * from displaying an old cached icon
             * after changing an application's icon.
             */
            icon.src =
                `/icons/${tile.icon}?t=${Date.now()}`;


            icon.alt =
                tile.name;


            tileElement.appendChild(icon);
        }


        container.appendChild(
            tileElement
        );
    });
}


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
    ).classList.remove("hidden");
}


/*
 * Close configuration modal.
 */
function closeConfiguration() {

    document.getElementById(
        "config-modal"
    ).classList.add("hidden");

    selectedSlot = null;
}


/*
 * =========================
 * Installed Applications
 * =========================
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
            await fetch("/api/installed-apps");

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


        displayInstalledApps(
            installedApps
        );


        document.getElementById(
            "config-modal"
        ).classList.add("hidden");


        document.getElementById(
            "installed-app-modal"
        ).classList.remove("hidden");


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


        item.appendChild(name);


        /*
         * Selecting an application does NOT save it.
         */
        item.addEventListener(
            "click",
            function (event) {

                event.preventDefault();
                event.stopPropagation();

                selectInstalledApp(app);
            }
        );


        list.appendChild(item);
    });
}


/*
 * Search installed applications locally.
 */
function searchInstalledApps() {

    const searchText =
        document.getElementById(
            "installed-app-search"
        ).value
        .toLowerCase()
        .trim();


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
 * Select an installed application.
 *
 * IMPORTANT:
 * This function does NOT save.
 * The user must press Save.
 */
function selectInstalledApp(app) {

    selectedInstalledApp =
        app;


    document.getElementById(
        "installed-app-selected"
    ).textContent =
        `Selected: ${app.name}`;
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

        /*
         * Save installed application.
         */
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
         * Save custom icon if selected.
         */
        const iconSaved =
            await saveCustomIconIfSelected();


        if (!iconSaved) {
            return;
        }


        /*
         * Close modal only after saving.
         */
        closeInstalledApps();


        /*
         * Refresh tiles.
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
 * Close installed applications modal.
 */
function closeInstalledApps() {

    document.getElementById(
        "installed-app-modal"
    ).classList.add("hidden");

    selectedInstalledApp =
        null;

    selectedSlot =
        null;
}


/*
 * =========================
 * Custom Icon
 * =========================
 */


/*
 * Save custom icon if the user selected one.
 */
async function saveCustomIconIfSelected() {

    let fileInput;


    /*
     * Determine which custom icon input
     * is currently being used.
     */
    if (
        !document
            .getElementById(
                "installed-app-modal"
            )
            .classList.contains("hidden")
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
            .classList.contains("hidden")
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
            .classList.contains("hidden")
    ) {

        fileInput =
            document.getElementById(
                "website-custom-icon"
            );
    }


    /*
     * No custom icon selected.
     */
    if (
        !fileInput ||
        fileInput.files.length === 0
    ) {

        return true;
    }


    const file =
        fileInput.files[0];


    return new Promise(
        (resolve, reject) => {

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

                                    body: JSON.stringify({

                                        slotNumber:
                                            selectedSlot,

                                        imageBase64:
                                            reader.result
                                    })
                                }
                            );


                        if (!response.ok) {

                            throw new Error(
                                "Failed to save custom icon."
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


            reader.readAsDataURL(file);
        }
    );
}


/*
 * =========================
 * Custom Application
 * =========================
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
    ).classList.add("hidden");


    document.getElementById(
        "custom-app-modal"
    ).classList.remove("hidden");
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
        fileName
            .replace(
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
        fileName
            .replace(
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


        closeCustomApp();

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

    document.getElementById(
        "custom-app-modal"
    ).classList.add("hidden");

    selectedSlot =
        null;
}


/*
 * =========================
 * Website
 * =========================
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
    ).classList.add("hidden");


    document.getElementById(
        "website-modal"
    ).classList.remove("hidden");
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


        closeWebsite();

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

    document.getElementById(
        "website-modal"
    ).classList.add("hidden");

    selectedSlot =
        null;
}


/*
 * =========================
 * Event Listeners
 * =========================
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


document
    .getElementById(
        "config-cancel-button"
    )
    .addEventListener(
        "click",
        closeConfiguration
    );


/*
 * Installed applications.
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
 * Custom application.
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
 * Website.
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
 * =========================
 * Initial Page Load
 * =========================
 */

loadServerInfo();

loadTiles();