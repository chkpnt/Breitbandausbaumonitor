<script context="module" lang="typescript">
	export type TimelineEntry = {
		timestamp: Date;
		object: any;
	}
</script>

<script lang="typescript">
    export let entries: TimelineEntry[] = [];
    export let selectedEntry: TimelineEntry | undefined = undefined;

    function select(entry: TimelineEntry) {
        console.log(entry);
        selectedEntry = entry;
    }
</script>

<ol>
    {#each entries as entry}
        <li on:click="{() => select(entry)}">{entry.timestamp.toLocaleString()}</li>
    {/each}
</ol>
