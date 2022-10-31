<script context="module" lang="ts">
    export type TimelineEntry<T> = {
        timestamp: Date;
        comment: string | null;
        object: T;
    };
</script>

<script lang="ts">
    import log from "loglevel";

    export let entries: TimelineEntry<unknown>[] = [];
    export let selectedEntry: TimelineEntry<unknown> | undefined = undefined;

    type TimelineEntryWithPos = TimelineEntry<unknown> & {
        position: number; // 0..100, percentage
        isSelected: boolean;
    };

    // sorted descending (the latest coverage overlay shall be the first in DOM)
    let extendedEntries: TimelineEntryWithPos[] = [];

    function initExtendedEntries() {
        let timestamps = entries.map((entry) => entry.timestamp.valueOf());
        let firstDate = Math.min(...timestamps);
        let lastDate = Math.max(...timestamps);
        extendedEntries = entries
            .map((entry) => {
                let position = toRelativePositionOf(
                    entry.timestamp.valueOf(),
                    firstDate,
                    lastDate,
                );
                log.debug(
                    `Position of ${entry.timestamp} between ${firstDate} and ${lastDate}: ${position}`,
                );
                return {
                    timestamp: entry.timestamp,
                    comment: entry.comment,
                    position: position,
                    isSelected: entry.timestamp == selectedEntry?.timestamp,
                    object: entry.object,
                };
            })
            .sort((a, b) => b.timestamp.valueOf() - a.timestamp.valueOf());
        log.debug("extendedEntries", extendedEntries);
    }

    function toRelativePositionOf(
        value: number,
        from: number,
        until: number,
    ): number {
        let interval = until - from;
        if (interval == 0) {
            return 0;
        }
        let relativePosition = (value - from) / interval;
        return Math.round(relativePosition * 10_000) / 100;
    }

    function select(entry: TimelineEntryWithPos) {
        console.log(entry);
        extendedEntries.forEach((it) => (it.isSelected = false));
        entry.isSelected = true;
        selectedEntry = entries.find((it) => it.timestamp == entry.timestamp);
        extendedEntries = extendedEntries; // eslint-disable-line no-self-assign
    }

    const dateFormatter = new Intl.DateTimeFormat([], {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
    });

    function cssLeftPosition(
        entry: TimelineEntryWithPos,
        index: number,
    ): string {
        if (entry.position == 0) {
            return "left: 0%";
        }

        // this should prevent most cases of overlapping timeline entries:

        let predecessor = extendedEntries[index + 1]; // index 0 is the latest entry
        let prepredecessor = extendedEntries[index + 2]; // index 0 is the latest entry

        // Tailwind's spacing 4 = 1rem
        if (prepredecessor === undefined) {
            return `left: calc(max(${entry.position}%, ${predecessor.position}% + 1rem))`;
        } else {
            return `left: calc(max(${entry.position}%, ${predecessor.position}% + 1rem, ${prepredecessor.position}% + 2rem))`;
        }
    }

    initExtendedEntries();
</script>

<div class="timeline-container">
    {#if extendedEntries.length > 1}
        <ol>
            <div class="timeline"></div>
            {#each extendedEntries as entry, index (entry.timestamp)}
                <li
                    class="{entry.isSelected ? 'selected' : ''}"
                    style="{cssLeftPosition(entry, index)}"
                >
                    <!-- TODO: lang (needed for hyphenation) should come from comment -->
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <div
                        class="circle cursor-pointer"
                        on:click="{() => select(entry)}"
                        lang="de"
                        data-tooltip="{entry.comment}"
                    ></div>
                    <div class="badge rotate-around-circle">
                        {dateFormatter.format(entry.timestamp)}
                    </div>
                </li>
            {/each}
        </ol>
    {:else}
        <div class="single-badge">
            {dateFormatter.format(selectedEntry?.timestamp)}
        </div>
    {/if}
</div>

<style lang="postcss">
    :root {
        --tooltip-width: 10rem;
    }

    .timeline-container {
        /*@apply absolute mx-auto h-4 mt-4;*/
        @apply grow;
        @apply relative;
        @apply w-3/4 h-4 mt-4 mr-16;
        @apply z-1100;
        @apply font-sans text-xs;
        font-family: "Helvetica Neue", Arial, Helvetica, sans-serif;
        /* so the tooltip keeps in the viewport */
        /* left: calc(max(4%, calc(var(--tooltip-width) / 2))); */
        /* right: calc(max(250px, calc(var(--tooltip-width) / 2))); */
    }

    .timeline {
        @apply absolute bg-red-600 h-0.5 inset-x-0 top-1/2 transform -translate-y-1/2;
        @apply shadow-md;
        @apply z-auto;
    }

    li {
        @apply absolute bottom-0 transform -translate-x-1/2;
        @apply z-10;
    }
    li.selected {
        @apply z-20;
    }
    .circle {
        @apply rounded-full h-4 w-4 bg-red-200 border-red-600 border-2;
        @apply shadow-md;
    }
    .selected > .circle {
        @apply bg-red-500;
    }
    .circle:hover {
        @apply bg-red-600;
    }
    [data-tooltip] {
        @apply relative;
        @apply table; /* so that top:100% in [data-tooltip]:after is relative to the border-box */
    }
    [data-tooltip]::after {
        @apply invisible transition-all ease-out delay-200 duration-200;
        @apply absolute w-40 mt-2 top-full px-2 py-1.5;
        @apply left-1/2 transform -translate-x-1/2;
        @apply bg-gray-50/90;
        @apply shadow-lg rounded-lg;
        @apply z-50;
        width: var(--tooltip-width);
        content: attr(data-tooltip);
        hyphens: auto;
    }
    [data-tooltip]:hover::after {
        @apply visible;
    }

    .badge {
        @apply absolute bottom-0 left-full h-5 ml-1.5 pl-2 pr-2;
        @apply rounded-l-lg rounded-r-lg bg-gray-50/50;
        @apply shadow-md;
        @apply leading-5;
        @apply z-0;
    }
    .selected > .badge {
        @apply font-semibold;
    }
    .badge.rotate-around-circle {
        /* origin shall be the center of the circle */
        transform-origin: calc(
                -1 * (theme("spacing[1.5]") + theme("spacing[4]") / 2)
            )
            center;
    }
    .badge.rotate-around-circle {
        @apply rotate-45;
    }

    .single-badge {
        @apply absolute right-0;
        @apply px-2;
        @apply rounded-l-lg rounded-r-lg bg-gray-50/50;
        @apply shadow-md;
        @apply z-0;
    }

    @media (min-height: 600px) {
        .timeline-container {
            @apply mt-18;
        }
        li:nth-child(odd) > .badge.rotate-around-circle {
            @apply rotate-45;
        }
        li:nth-child(even) > .badge.rotate-around-circle {
            @apply -rotate-45;
        }
    }
</style>
