<script lang="typescript">
	import L from "leaflet";
	import CoverageOverlay from "./CoverageOverlay.svelte";
	import Leaflet from "./Leaflet.svelte";
	import log from "loglevel";

	const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);
	const region = urlParams.get("region") ?? "Tamm";
	let loaded = false;

	let bbox: L.LatLngBounds | undefined = undefined;
	let coverageOverlayUrl: string | undefined = undefined;

	function initializeMap(metadata: CoveragefileMetadata) {
		let [east, south, west, north] = metadata.bbox.split(",").map(Number);

		let crs = L.CRS.EPSG3857; // coordinate reference system
		let southEastLatLng = crs.unproject(new L.Point(east,south));
		let northWestLatLng = crs.unproject(new L.Point(west,north));

		bbox = L.latLngBounds(southEastLatLng, northWestLatLng);
		coverageOverlayUrl = `overlays/${region}/${metadata.file}`;
	}

	type CoveragefileMetadata = {
		timestamp: Date;
		file: string;
		bbox: string;
	}

	type RegionMetadata = {
		coverages: CoveragefileMetadata[];
	}

	fetch(`overlays/${region}/data.json`)
		.then(res => res.json())
		.then((data: RegionMetadata) => {
			console.log('Output: ', data);
			initializeMap(data.coverages[0]);
			//initializeTimeline(data);
		})
		.catch(err => log.error(err));

</script>

<svelte:window on:load={() => (loaded = true)} />

<main class="h-screen flex flex-col">
	<div>
		<h1 class="text-4xl">Breitbandausbaumonitor für {region}</h1>
		<p>Visit the <a href="https://svelte.dev/tutorial">Svelte tutorial</a> to learn how to build Svelte apps.</p>
	</div>
	<!-- TODO: Prüfen, was genau passiert, wenn die Condition fehlt -->
	{#if bbox && coverageOverlayUrl}
	<div class="h-full flex-1">
		<Leaflet bounds={bbox}>
			<CoverageOverlay url={coverageOverlayUrl} bbox={bbox} />
		</Leaflet>
	</div>
	{/if}
</main>

<style global lang="postcss">
	@tailwind base;
	@tailwind components;
	@tailwind utilities;
</style>
