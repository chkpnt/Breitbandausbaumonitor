/* eslint-env node */
import commonjs from "@rollup/plugin-commonjs";
import resolve from "@rollup/plugin-node-resolve";
import replace from "@rollup/plugin-replace";
import terser from "@rollup/plugin-terser";
import typescript from "@rollup/plugin-typescript";
import child_process from "child_process";
import copy from "rollup-plugin-copy";
import css from "rollup-plugin-css-only";
import livereload from "rollup-plugin-livereload";
import svelte from "rollup-plugin-svelte";
import sveltePreprocess from "svelte-preprocess";

const production = !process.env.ROLLUP_WATCH;

function serve() {
    let server;

    function toExit() {
        if (server) server.kill(0);
    }

    return {
        writeBundle() {
            if (server) return;
            server = child_process.spawn(
                "npm",
                ["run", "start", "--", "--dev"],
                {
                    stdio: ["ignore", "inherit", "inherit"],
                    shell: true,
                },
            );

            process.on("SIGTERM", toExit);
            process.on("exit", toExit);
        },
    };
}

export default {
    input: "src/main.ts",
    output: {
        sourcemap: !production,
        format: "iife",
        name: "app",
        file: "dist/app.js",
    },
    plugins: [
        replace({
            values: {
                "env.DEBUG": !production,
            },
            preventAssignment: true,
        }),

        svelte({
            preprocess: sveltePreprocess({
                sourceMap: !production,
                postcss: {},
            }),
            compilerOptions: {
                // enable run-time checks when not in production
                dev: !production,
            },
        }),

        // we'll extract any component CSS out into
        // a separate file - better for performance
        css({ output: "styles.css" }),

        // If you have external dependencies installed from
        // npm, you'll most likely need these plugins. In
        // some cases you'll need additional configuration -
        // consult the documentation for details:
        // https://github.com/rollup/plugins/tree/master/packages/commonjs
        resolve({
            browser: true,
            dedupe: ["svelte"],
        }),
        commonjs(),
        typescript({
            sourceMap: !production,
            inlineSources: !production,
        }),

        copy({
            targets: [
                {
                    src: ["src/index.html", "src/styles.css"],
                    dest: "dist/",
                },
                { src: "src/images", dest: "dist/" },
                { src: "../overlays", dest: "dist/" },
            ],
        }),

        // In dev mode, call `npm run start` once
        // the bundle has been generated
        !production && serve(),

        // Watch the `public` directory and refresh the
        // browser on changes when not in production
        !production && livereload("dist"),

        // If we're building for production (npm run build
        // instead of npm run dev), minify
        production && terser(),
    ],
    watch: {
        clearScreen: false,
    },
    strictDeprecations: true,
};
