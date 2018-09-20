# Architecture

At the top level, woolybear contains a `core` namespace and a `routes` namespace, and a set 
of namespaces that correspond to the basic building blocks of re-frame: `db`, `events`, 
`subs`, and `views`. These top-level domains contain the global parts of our application,
i.e. the `app-db`, the global event handlers, the global subscriptions, and the top-level
layout views. The rest of the application is divided up into `ad` (Atomic Design), `pack`,
and `page` namespaces.

## The `ad` namespace

The `woolybear.ad.*` namespaces are for designing the low-level, "atomic" components that
will be composed together to form larger components. Ideally, the `ad` components will be
the only place we use raw HTML elements like `[:div ...]` to form our views. If we're doing
it right, we should have `ad` components for *everything* we need to build larger units.
Because these components are small, we can put several related components in a single file.

## The `pack` namespace

The `woolybear.pack.*` namespaces are for larger components built up out of the atomic components
in the `ad` namespace. A "pack" is a file that has sections for each of the re-frame building
blocks: db, subscriptions, event handlers and views. Thus, each component gets its own `pack`
namespace that keeps everything you need to know about that component in one place. You can
also use a pack for functional systems like navigation that may not have explicit view components.

## The `page` namespace

As you might guess, the `woolybear.page.*` namespaces are where the top-level pages live. Each
`page` namespace defines the content area for a specific page, displayed between the global
header and footer provided by the top-level layout.

