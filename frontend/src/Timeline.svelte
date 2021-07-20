<script context="module" lang="typescript">
    export type TimelineEntry = {
        timestamp: Date;
        object: any;
    };
</script>

<script lang="typescript">
    import log from "loglevel";

    export let entries: TimelineEntry[] = [];
    export let selectedEntry: TimelineEntry | undefined = undefined;

    type TimelineEntryWithPos = TimelineEntry & {
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
        extendedEntries = extendedEntries;
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
    <ol>
        <div class="timeline"></div>
        {#each extendedEntries as entry, index (entry.timestamp)}
            <li
                on:click="{() => select(entry)}"
                class="{entry.isSelected ? 'selected' : ''}"
                style="{cssLeftPosition(entry, index)}"
            >
                <div class="circle cursor-pointer"></div>
                <div class="badge rotate-around-circle">
                    {dateFormatter.format(entry.timestamp)}
                </div>
            </li>
        {/each}
    </ol>
</div>

<style lang="postcss">
    .timeline-container {
        @apply relative w-10/12 mx-auto h-4 mt-2 z-1000;
    }
    .timeline {
        @apply bg-red-600 h-0.5 inset-x-0 absolute bottom-2 transform translate-y-1/2;
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

    .badge {
        @apply absolute bottom-0 left-full h-5 leading-5 ml-1.5 pl-1 pr-2;
        @apply rounded-l rounded-r-full bg-red-50 bg-opacity-60;
        @apply shadow-md;
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
        @apply transform rotate-45;
    }
    @media (min-height: 600px) {
        .timeline-container {
            @apply mt-16;
        }
        li:nth-child(odd) > .badge.rotate-around-circle {
            @apply transform rotate-45;
        }
        li:nth-child(even) > .badge.rotate-around-circle {
            @apply transform -rotate-45;
        }
    }
</style>
