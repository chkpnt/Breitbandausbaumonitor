/* eslint-env node */

module.exports = {
    purge: {
        enabled: !process.env.ROLLUP_WATCH,
        content: ["./public/index.html", "./src/**/*.svelte"],
        options: {
            defaultExtractor: (content) => [
                ...(content.match(/[^<>"'`\s]*[^<>"'`\s:]/g) || []),
                ...(content.match(/(?<=class:)[^=>/\s]*/g) || []),
            ],
        },
    },
    darkMode: false, // or 'media' or 'class'
    theme: {
        extend: {
            margin: {
                18: "4.5rem",
            },
            zIndex: {
                1000: 1000,
                1100: 1100,
            },
        },
    },
    variants: {
        extend: {},
    },
    plugins: [],
};
