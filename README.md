# woolybear

A [re-frame](https://github.com/Day8/re-frame) application demonstrating some principles of Atomic Design
and reusable re-frame components.

## Setup

This repository was initially created using the following command:

    lein new re-frame woolybear +10x +re-frisk +routes

### Adding Bulma

Here's how I added the Bulma CSS framework as a git sub-module:

Under the main repository directory, create a `sass` subdirectory, then `cd` into that directory
and add Bulma as a git submodule so we can easily update it when/if needed. 

    $ mkdir sass
    $ cd sass
    $ git submodule add https://github.com/jgthms/bulma

Then add the `sass/woolybear.scss` file.

### Adding `lein sassc`

Here's how I added the `lein sassc` plugin:

In the `project.clj` file, add the following

    :plugins [...
              [lein-sassc "0.10.4"]]
    
    :sassc [{:src         "sass/woolybear.scss"
             :output-to   "resources/public/css/wb.css"
             :style       "nested"
             :import-path "sass"}]

Note: You will need to manually run `lein sassc once` to rebuild your CSS file
when you have changes. Your editor/IDE may have a way to set up a keyboard shortcut
to make this easier.

## Documentation

More detailed documentation is available [here](docs/).

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build

To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```

## References

* Re-frame: [https://github.com/Day8/re-frame](https://github.com/Day8/re-frame)
* Bulma: [https://bulma.io](https://bulma.io)
