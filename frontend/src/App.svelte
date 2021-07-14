<script lang="typescript">
	import * as L from 'leaflet';
	import Leaflet from "./Leaflet.svelte";

	export let bbox: string;

	let [east, south, west, north] = bbox.split(",").map(Number);

	let crs = L.CRS.EPSG3857; // coordinate reference system
	let southEastLatLng = crs.unproject(new L.Point(east,south));
	let northWestLatLng = crs.unproject(new L.Point(west,north));

	const initialBounds = L.latLngBounds(southEastLatLng, northWestLatLng);

	let loaded = false;
</script>

<svelte:window on:load={() => (loaded = true)} />

<main class="h-screen flex flex-col">
	<div>
		<h1 class="text-4xl">Breitbandausbaumonitor</h1>
		<p>Visit the <a href="https://svelte.dev/tutorial">Svelte tutorial</a> to learn how to build Svelte apps.</p>
	</div>
	{#if loaded || document.readyState === 'complete'}
	<div class="h-full flex-1">
		<Leaflet bounds={initialBounds}></Leaflet>
	</div>
	{/if}
</main>

<style global lang="postcss">
	@tailwind base;
	@tailwind components;
	@tailwind utilities;
</style>
