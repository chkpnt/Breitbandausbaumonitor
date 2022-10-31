<script lang="ts">
    let collapsed = true;

    function toggle() {
        collapsed = !collapsed;
    }
</script>

<div class="legend-container">
    <div class="header {collapsed ? 'collapsed' : ''}" on:click="{toggle}">
        Legende
    </div>
    <div class="legend {collapsed ? 'collapsed' : ''}">
        <img
            src="images/legend.png"
            alt="Legend for the colors used by the coverage overlay."
        />
    </div>
</div>

<style lang="postcss">
    .legend-container {
        @apply flex-none;
        @apply hidden;
        @apply z-1000 mt-2 w-52 shadow-xl rounded-lg bg-white;
    }

    .header {
        @apply flex items-center justify-between;
        @apply px-3 pb-0.5 pt-1;
        @apply font-sans text-lg;
        @apply cursor-pointer;
    }
    .header::after {
        @apply transition-transform duration-300;
        @apply inline-block bg-center;
        height: 1.125rem; /* = font-size of text-lg */
        width: 1.125rem;
        /* Source MediaWiki Visual Editor Team, MIT, https://commons.wikimedia.org/wiki/File:VisualEditor_-_Icon_-_Collapse.svg */
        background-image: url(images/collapse.svg);
        content: "";
    }
    .header.collapsed::after {
        @apply transform rotate-180;
    }

    .legend {
        @apply px-2;
        @apply cursor-default;
        pointer-events: none;
    }
    .legend > img {
        transition: max-height 300ms linear;
        max-height: 375px;
        @apply mb-2;
    }
    .legend.collapsed > img {
        @apply max-h-0 mb-0;
    }

    @media (min-height: 580px) and (min-width: 1024px) {
        .legend-container {
            @apply block;
        }
    }

    @media (min-height: 600px) {
        .legend-container {
            @apply mt-16;
        }
    }
</style>
