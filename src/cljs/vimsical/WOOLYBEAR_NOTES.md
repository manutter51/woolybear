# Notes

The re-frame-utils library provides functions for injecting a subscription into an
event handler via an interceptor (which is what woolybear uses it for), and also 
for dispatching events when a subscription changes (which woolybear does not use).

We're merging the files directly into our source repository, since it consists of
cljs files, which don't produce an artifact that you can check in to clojars or 
maven.

For the original source repository, go to:

  https://github.com/vimsical/re-frame-utils
  
Readers may also be interested in the technical discussion behind this library,
recorded here:

  https://github.com/Day8/re-frame/issues/255
  
Our thanks to @vimsical for putting this together and noting the caveats involved
with injecting subscriptions into event handlers.

