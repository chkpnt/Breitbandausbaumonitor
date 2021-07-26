<script lang="typescript">
    import { createEventDispatcher, setContext } from "svelte";
    import L from "leaflet";
    import "leaflet/dist/leaflet.css";

    export let height = "100%";
    export let width = "100%";
    export let bounds: L.LatLngBounds;
    let mapProp: L.Map | undefined = undefined;
    export { mapProp as map };

    const dispatch = createEventDispatcher();

    let map: L.Map | undefined;
    $: mapProp = map;

    export const getMap = (): L.Map | undefined => map;
    setContext("map", getMap);

    function createLeaflet(node: HTMLElement) {
        map = L.map(node, { scrollWheelZoom: false })
            .fitBounds(bounds)
            .on("zoom", (e) => dispatch("zoom", e));
        setTimeout(() => {
            if (map) {
                map.invalidateSize();
                map.fitBounds(bounds);
            }
        }, 250);

        L.tileLayer("https://tile.openstreetmap.de/{z}/{x}/{y}.png", {
            attribution:
                '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        }).addTo(map);

        return {
            destroy() {
                map?.remove();
                map = undefined;
            },
        };
    }

    $: map?.fitBounds(bounds);
</script>

<div style="height:{height};width:{width}" use:createLeaflet>
    {#if map}
        <slot map="{map}" />
    {/if}
</div>

<style>
    :global(.leaflet-control-container) {
        position: static;
    }
</style>
