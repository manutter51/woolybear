(defproject woolybear "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0-beta4"]
                 [org.clojure/clojurescript "1.10.339"]
                 [org.clojure/test.check "0.10.0-alpha3"]
                 [expound "0.7.1"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.5"]
                 [secretary "1.2.3"]
                 [re-frame-utils "0.1.0"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-sassc "0.10.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :resource-paths ["resources"]
  :test-paths ["test/cljs"]

  :sassc [{:src         "sass/woolybear.scss"
           :output-to   "resources/public/css/wb.css"
           :style       "nested"
           :import-path "sass"}]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]
             :server-logfile "logs/figwheel_server.log"}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]
                   [day8.re-frame/re-frame-10x "0.3.3"]
                   [day8.re-frame/tracing "0.5.1"]
                   [re-frisk "0.5.3"]
                   [figwheel-sidecar "0.5.15"]]

    :source-paths ["src/clj" "src/cljs" "scripts"]

    :plugins      [[lein-figwheel "0.5.16"]]}
   :prod {:dependencies [[day8.re-frame/tracing-stubs "0.5.1"]]
          :source-paths ["src/clj" "src/cljs"]}
   }

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "woolybear.core/mount-root"}
     :compiler     {:main                 woolybear.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload
                                           day8.re-frame-10x.preload
                                           re-frisk.preload]
                    :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true
                                           "day8.re_frame.tracing.trace_enabled_QMARK_" true}
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            woolybear.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}


    ]}
  )
