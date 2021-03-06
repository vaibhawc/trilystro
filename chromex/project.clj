(defproject vuagain-chromex "0.6.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.async "0.4.474"]
                 [binaryage/chromex "0.5.15"]
                 [binaryage/oops "0.5.8"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [environ "1.1.0"]
                 [figwheel "0.5.14"]
                 [hipo "0.5.2"]
                 [prismatic/dommy "1.1.0"]
                 [re-frame "0.10.5"]
                 [re-frame-utils "0.1.0"]
                 [reagent "0.7.0"]
                 [com.degel/iron "0.2.0"]
                 [com.degel/re-frame-firebase "0.5.0"]
                 [com.degel/sodium "0.10.0"]
                 [trilib "0.4.1"]]

  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-figwheel "0.5.14"]
            [lein-shell "0.5.0"]
            [lein-environ "1.1.0"]
            [lein-cooper "1.2.2"]]

  :source-paths ["src/background"
                 "src/popup"
                 "src/content_script"
                 "checkouts/iron/src"
                 "checkouts/re-frame-firebase/src"
                 "checkouts/sodium/src"
                 "checkouts/trilib/src"]

  :clean-targets ^{:protect false} ["target"
                                    "resources/unpacked/compiled"
                                    "resources/release/compiled"]

  :cljsbuild {:builds {}} ; prevent https://github.com/emezeske/lein-cljsbuild/issues/413

  :profiles {:unpacked
             {:dependencies [[binaryage/devtools "0.9.9"]
                             [re-frisk "0.5.3"]]
              :cljsbuild {:builds
                          {:background
                           {:source-paths ["src/background"
                                           "checkouts/iron/src"
                                           "checkouts/re-frame-firebase/src"
                                           "checkouts/sodium/src"
                                           "checkouts/trilib/src"]
                            :figwheel     true
                            :compiler     {:output-to     "resources/unpacked/compiled/background/main.js"
                                           :output-dir    "resources/unpacked/compiled/background"
                                           :asset-path    "compiled/background"
                                           :preloads      [devtools.preload]
                                           :external-config      {:devtools/config {:features-to-install :all}}
                                           :main          vuagain.chromex.background
                                           :language-in :ecmascript5
                                           ;; [TODO] Eventually, turn on checked-arrays. But breaks too many libs now
                                           ;;        See: https://clojurescript.org/news/2017-07-14-checked-array-access
                                           ;; :checked-arrays :warn
                                           :optimizations :none
                                           :source-map    true}}
                           :popup
                           {:source-paths ["src/popup"
                                           "checkouts/iron/src"
                                           "checkouts/re-frame-firebase/src"
                                           "checkouts/sodium/src"
                                           "checkouts/trilib/src"]
                            :figwheel     true
                            :compiler     {:output-to     "resources/unpacked/compiled/popup/main.js"
                                           :output-dir    "resources/unpacked/compiled/popup"
                                           :asset-path    "compiled/popup"
                                           :preloads      [devtools.preload]
                                           :external-config {:devtools/config {:features-to-install :all}}
                                           :main          vuagain.chromex.popup
                                           :language-in :ecmascript5
                                           ;;:checked-arrays :warn
                                           :optimizations :none
                                           :source-map    true}}}}}
             :unpacked-content-script
             {:cljsbuild {:builds
                          {:content-script
                           {:source-paths ["src/content_script"
                                           "checkouts/iron/src"
                                           "checkouts/re-frame-firebase/src"
                                           "checkouts/sodium/src"
                                           "checkouts/trilib/src"]
                            :compiler     {:output-to     "resources/unpacked/compiled/content-script/main.js"
                                           :output-dir    "resources/unpacked/compiled/content-script"
                                           :asset-path    "compiled/content-script"
                                           :main          vuagain.chromex.content-script
                                           :language-in :ecmascript5
                                           ;;:checked-arrays :warn
                                           ;:optimizations :whitespace ; content scripts cannot do eval / load script dynamically
                                           :optimizations :advanced    ; let's use advanced build with pseudo-names for now, there seems to be a bug in deps ordering under :whitespace mode
                                           :closure-defines {goog.DEBUG false}
                                           :pseudo-names  true
                                           :pretty-print  true}}}}}
             :checkouts
             ; DON'T FORGET TO UPDATE scripts/ensure-checkouts.sh
             {:cljsbuild {:builds
                          {:background {:source-paths ["checkouts/cljs-devtools/src/lib"
                                                       "checkouts/chromex/src/lib"
                                                       "checkouts/chromex/src/exts"]}
                           :popup      {:source-paths ["checkouts/cljs-devtools/src/lib"
                                                       "checkouts/chromex/src/lib"
                                                       "checkouts/chromex/src/exts"]}}}}
             :checkouts-content-script
             ; DON'T FORGET TO UPDATE scripts/ensure-checkouts.sh
             {:cljsbuild {:builds
                          {:content-script {:source-paths ["checkouts/cljs-devtools/src/lib"
                                                           "checkouts/chromex/src/lib"
                                                           "checkouts/chromex/src/exts"]}}}}

             :figwheel
             {:figwheel {:server-port    6888
                         :server-logfile ".figwheel.log"
                         :repl           true}}

             :disable-figwheel-repl
             {:figwheel {:repl false}}

             :cooper
             {:cooper {"content-dev"     ["lein" "content-dev"]
                       "fig-dev-no-repl" ["lein" "fig-dev-no-repl"]
                       "browser"         ["scripts/launch-test-browser.sh"]}}

             :release
             {:env       {:chromex-elide-verbose-logging "true"}
              :dependencies [[re-frisk "0.5.3"]] ;;; [TODO] How can we get rid of this without breaking `lein release`?
              :cljsbuild {:builds
                          {:background
                           {:source-paths ["src/background"]
                            :compiler     {:output-to     "resources/release/compiled/background.js"
                                           :output-dir    "resources/release/compiled/background"
                                           :asset-path    "compiled/background"
                                           :main          vuagain.chromex.background
                                           :language-in   :ecmascript5
                                           ;;:checked-arrays :warn
                                           :optimizations :advanced
                                           :closure-defines {goog.DEBUG false}
                                           :elide-asserts true}}
                           :popup
                           {:source-paths ["src/popup"]
                            :compiler     {:output-to     "resources/release/compiled/popup.js"
                                           :output-dir    "resources/release/compiled/popup"
                                           :asset-path    "compiled/popup"
                                           :main          vuagain.chromex.popup
                                           :language-in   :ecmascript5
                                           ;;:checked-arrays :warn
                                           :optimizations :advanced
                                           :closure-defines {goog.DEBUG false}
                                           :elide-asserts true}}
                           :content-script
                           {:source-paths ["src/content_script"]
                            :compiler     {:output-to     "resources/release/compiled/content-script.js"
                                           :output-dir    "resources/release/compiled/content-script"
                                           :asset-path    "compiled/content-script"
                                           :main          vuagain.chromex.content-script
                                           :language-in :ecmascript5
                                           ;;:checked-arrays :warn
                                           :optimizations :advanced
                                           :closure-defines {goog.DEBUG false}
                                           :elide-asserts true}}}}}}

  :aliases {"dev-build"       ["with-profile" "+unpacked,+unpacked-content-script,+checkouts,+checkouts-content-script" "cljsbuild" "once"]
            "fig"             ["with-profile" "+unpacked,+figwheel" "figwheel" "background" "popup"]
            "content"         ["with-profile" "+unpacked-content-script" "cljsbuild" "auto" "content-script"]
            "fig-dev-no-repl" ["with-profile" "+unpacked,+figwheel,+disable-figwheel-repl,+checkouts" "figwheel" "background" "popup"]
            "content-dev"     ["with-profile" "+unpacked-content-script,+checkouts-content-script" "cljsbuild" "auto"]
            "devel"           ["with-profile" "+cooper" "do" ; for mac only
                               ["shell" "scripts/ensure-checkouts.sh"]
                               ["cooper"]]
            "release"         ["with-profile" "+release" "do"
                               ["clean"]
                               ["cljsbuild" "once" "background" "popup" "content-script"]]
            "package"         ["shell" "scripts/package.sh"]})
