<script lang="typescript">
    import L from "leaflet";
    import CoverageOverlay from "./CoverageOverlay.svelte";
    import Leaflet from "./Leaflet.svelte";
    import Timeline from "./Timeline.svelte";
    import type { TimelineEntry } from "./Timeline.svelte";
    import log from "loglevel";

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const region = urlParams.get("region") ?? "Tamm";

    let bbox: L.LatLngBounds | undefined = undefined;
    let coverageOverlayUrl: string | undefined = undefined;

    let regionMetadata: RegionMetadata | undefined;
    let timelineEntries: TimelineEntry[] = [];
    let selectedTimelineEntry: TimelineEntry | undefined;
    $: timelineEntries =
        regionMetadata?.coverages.map((coverage) => ({
            timestamp: coverage.timestamp,
            object: coverage,
        })) ?? [];
    $: {
        log.debug("selectedTimelineEntry changed to:", selectedTimelineEntry);
        if (selectedTimelineEntry !== undefined) {
            initializeMap(selectedTimelineEntry.object);
        }
    }

    function initializeMap(metadata: CoveragefileMetadata) {
        let [east, south, west, north] = metadata.bbox.split(",").map(Number);

        let crs = L.CRS.EPSG3857; // coordinate reference system
        let southEastLatLng = crs.unproject(new L.Point(east, south));
        let northWestLatLng = crs.unproject(new L.Point(west, north));

        bbox = L.latLngBounds(southEastLatLng, northWestLatLng);
        coverageOverlayUrl = `overlays/${region}/${metadata.file}`;
    }

    type CoveragefileMetadata = {
        timestamp: Date;
        file: string;
        bbox: string;
    };

    type RegionMetadata = {
        coverages: CoveragefileMetadata[];
    };

    function reviver(key, value) {
        if (key == "timestamp") {
            return new Date(value);
        }

        return value;
    }

    fetch(`overlays/${region}/data.json`)
        .then((res) => res.text())
        .then((text) => JSON.parse(text, reviver))
        .then((data: RegionMetadata) => {
            console.log("Output: ", data);
            regionMetadata = data;
            initializeMap(data.coverages[0]);
            //initializeTimeline(data);
        })
        .catch((err) => log.error(err));
</script>

<main class="h-screen flex flex-col">
    <h1 class="text-4xl">Breitbandausbaumonitor für {region}</h1>

    <!-- TODO: Prüfen, was genau passiert, wenn die Condition fehlt -->
    {#if bbox && coverageOverlayUrl}
        <div class="h-full flex-1">
            <Leaflet bounds="{bbox}">
                <Timeline
                    entries="{timelineEntries}"
                    bind:selectedEntry="{selectedTimelineEntry}"
                />
                <CoverageOverlay url="{coverageOverlayUrl}" bbox="{bbox}" />
            </Leaflet>
        </div>
    {/if}
</main>

<style global lang="postcss">
    @tailwind base;
    @tailwind components;
    @tailwind utilities;
</style>
