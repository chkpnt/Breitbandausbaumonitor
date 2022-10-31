<script lang="ts">
    import L from "leaflet";
    import Leaflet from "./Leaflet.svelte";
    import CoverageOverlay from "./CoverageOverlay.svelte";
    import Timeline from "./Timeline.svelte";
    import Legend from "./Legend.svelte";
    import GitHubCorner from "./GitHubCorner.svelte";
    import type { TimelineEntry } from "./Timeline.svelte";
    import log from "loglevel";

    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const region = urlParams.get("region") ?? "Tamm";

    let bbox: L.LatLngBounds | undefined = undefined;
    let coverageOverlayUrl: string | undefined = undefined;

    let timelineEntries: TimelineEntry<CoveragefileMetadata>[] = [];
    let selectedTimelineEntry: TimelineEntry<CoveragefileMetadata> | undefined;

    $: {
        log.debug("selectedTimelineEntry changed to:", selectedTimelineEntry);
        if (selectedTimelineEntry !== undefined) {
            showCoverageOverlayFor(selectedTimelineEntry.object);
        }
    }

    function setTimelineEntries(regionMetadata: RegionMetadata) {
        timelineEntries = regionMetadata.coverages.map((coverage) => ({
            timestamp: coverage.timestamp,
            comment: coverage.comment,
            object: coverage,
        }));
        selectedTimelineEntry = timelineEntries[0];
    }

    function showCoverageOverlayFor(metadata: CoveragefileMetadata) {
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
        comment: string | null;
    };

    type RegionMetadata = {
        coverages: CoveragefileMetadata[];
    };

    function reviver(key: string, value: unknown) {
        if (key == "timestamp") {
            return new Date(value as string);
        }

        return value;
    }

    fetch(`overlays/${region}/data.json`)
        .then((res) => res.text())
        .then((text) => JSON.parse(text, reviver))
        .then((data: RegionMetadata) => {
            console.log("Output: ", data);
            setTimelineEntries(data);
        })
        .catch((err) => log.error(err));
</script>

<main class="h-screen flex flex-col">
    <h1 class="text-xl sm:text-4xl m-2">
        Breitbandausbaumonitor für {region}
    </h1>

    <GitHubCorner />

    <!-- TODO: Prüfen, was genau passiert, wenn die Condition fehlt -->
    {#if bbox && coverageOverlayUrl}
        <div class="h-full flex-1">
            <div class="absolute w-full flex pl-16 pr-4">
                <Timeline
                    entries="{timelineEntries}"
                    bind:selectedEntry="{selectedTimelineEntry}"
                />
                <Legend />
            </div>
            <Leaflet bounds="{bbox}">
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
