# re-frame-utils

> Re-frame extensions

[![CircleCI](https://circleci.com/gh/vimsical/re-frame-utils.svg?style=svg)](https://circleci.com/gh/vimsical/re-frame-utils)

[![Clojars Project](https://img.shields.io/clojars/v/re-frame-utils.svg)](https://clojars.org/re-frame-utils)


## Releases and Dependency Information

* [All releases](https://clojars.org/vimsical/re-frame-utils)

[Leiningen] dependency information:

    [vimsical/re-frame-utils "0.1.0"]

[Maven] dependency information:

    <dependency>
      <groupId>vimsical</groupId>
      <artifactId>re-frame-utils</artifactId>
      <version>0.1.0</version>
    </dependency>

[Gradle] dependency information:

    compile "vimsical:re-frame-utils:0.1.0"

[Clojars]: http://clojars.org/
[Leiningen]: http://leiningen.org/
[Maven]: http://maven.apache.org/
[Gradle]: http://www.gradle.org/



## Dependencies and Compatibility

Tested against `re-frame 0.9.4` and `clojurescript 1.9.671`.



## Utils



### Cofx



#### [Inject](./src/vimsical/re_frame/cofx/inject.cljc)

Inject a subscription in an event handler.



### Fx



#### [Track](./src/vimsical/re_frame/fx/track.cljc)

Dynamically dispatch events when subscriptions update.



## License

Copyright © 2017 Vimsical

MIT License
