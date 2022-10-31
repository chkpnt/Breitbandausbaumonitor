# Breitbandausbaumonitor (front end)

This project provides a web app to visualize the archived coverage data for different landline data transmission technologies regarding the network of Deutsche Telekom.

## Development

This project is based on following technologies or projects:

-   [TypeScript](https://www.typescriptlang.org/) as programming language
-   [Svelte](https://svelte.dev/) as a front end compiler
-   [Leaflet](https://leafletjs.com/) to visualize the map
-   [Tailwind CSS](https://tailwindcss.com/) as a utility-first CSS framework
-   [OpenStreetMap (DE)](https://openstreetmap.de/), which provides German-style tiles for [OpenStreetMap](https://www.openstreetmap.org) data
-   [Node.js](https://nodejs.org/en/) as JavaScript runtime environment during development
-   [npm](https://www.npmjs.com/) as package manager
-   [Rollup](https://rollupjs.org/) as module bundler
-   [Prettier](https://prettier.io/) as code formatter
-   [ESLint](https://eslint.org/) as linter

### Recommendations

I recommend to use

-   [Volta](https://volta.sh/) to manage the used versions of node and npm, which are pinned in [package.json](package.json).
-   [Visual Studio Code](https://code.visualstudio.com/) with the extension [Svelte for VS Code](https://marketplace.visualstudio.com/items?itemName=svelte.svelte-vscode).

### Quickstart

```console
$ npm ci
$ npm run dev
```

The app is then available at [localhost:5000](http://localhost:5000).
By default, the map shows data for _Tamm_.
Other regions can be shown by using the GET-parameter `region`.

## Building and running in production mode

To create an optimised version of the app:

```console
$ npm run build
```

You can run the newly built app with `npm run start`, which uses [sirv](https://github.com/lukeed/sirv).

## Genesis of this project

1. Svelte-Template: https://github.com/sveltejs/template
1. converted to TypeScript
1. integration of Tailwind CSS
    - https://dev.to/matebek/simplest-way-to-set-up-svelte-with-tailwind-css-41bn
    - https://css-tricks.com/how-to-use-tailwind-on-a-svelte-site/
1. integration of Leaflet
    - inspired by https://github.com/dimfeld/svelte-leaflet-demo (@dimfeld, MIT)
