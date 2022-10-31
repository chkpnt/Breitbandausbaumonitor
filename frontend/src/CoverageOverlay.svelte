<script lang="ts">
    import L from "leaflet";
    import { getContext } from "svelte";
    import log from "loglevel";

    export let url: string;
    export let bbox: L.LatLngBounds;

    let imageOverlay: L.ImageOverlay;
    const map = getContext<() => L.Map>("map")();

    function replaceOverlay() {
        imageOverlay?.removeFrom(map);
        imageOverlay = L.imageOverlay(url, bbox, { opacity: 0.6 }).addTo(map);
    }

    $: {
        log.debug(`URL for coverageOverlay changed to ${url}`);
        replaceOverlay();
    }
</script>

<slot />
