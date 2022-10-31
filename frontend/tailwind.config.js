/* eslint-env node */

module.exports = {
    content: ["./public/index.html", "./src/**/*.svelte"],
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
    plugins: [],
};
