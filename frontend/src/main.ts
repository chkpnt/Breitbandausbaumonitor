import App from "./App.svelte";
import log from "loglevel";

const env = {
    DEBUG: false,
};
// env.DEBUG is replaced during rollup by @rollup/plugin-replace
log.setLevel(env.DEBUG ? "debug" : "warn");

const app = new App({
    target: document.body,
});

export default app;
