<script lang="typescript">
  import * as L from "leaflet";
  import { getContext, setContext, onDestroy } from "svelte";

  export let url: string;
  export let bbox: L.LatLngBounds;

  const map = getContext<() => L.Map>('map')();

  let imageOverlay = L.imageOverlay(url, bbox, { opacity: 0.6 })
    .addTo(map);
</script>

<slot />
