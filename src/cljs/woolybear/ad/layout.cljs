(ns woolybear.ad.layout
  "
  These components are the simplest AD components to write, since they do not
  require subscriptions* or event handlers. They are simple, basic wrappers to
  put things in--a tool for structuring pages and complex components. Because
  they are so simple, we're combining several of them in a single namespace
  instead of breaking them out into their own packs. Many of these components
  rely on the classes defined in the Bulma CSS library, which are documented
  at https://bulma.io/documentation/layout/.

  *NOTE: For consistency, each of these components supports our standard CSS
  options `:extra-classes` and `subscribe-to-classes`. These can be used to
  attach custom CSS classes to a component when used in the design of larger
  components.
  "
  (:require [woolybear.ad.utils :as adu]
            [cljs.spec.alpha :as s]))

(defn page
  "
  Very top-level block, designed to wrap all other elements on the page, with the
  exception of the page header and footer. Centers content horizontally (space
  permitting).
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :container
                                          :wb-page
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef page
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn page-header
  "
  Section that appears only in the upper portion of the page. Contains page
  title, nav items, etc.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :container
                                          :wb-page-header
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef page-header
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn page-title
  "
  Simple page title, designed to appear exactly once at the top of a page.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        [:div {:class (adu/css->str :container
                                    :wb-page-title
                                    :is-size-1
                                    extra-classes
                                    dynamic-classes)}
         children]))))

(s/fdef page-title
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn page-body
  "
  Main content area of page.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        ]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub
            ]
        (into [:div {:class (adu/css->str :content
                                          :wb-page-body
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef page-body
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn section
  "
  High-level block, designed to wrap and group a variety of other elements.
  Centers content horizontally (space permitting). Multiple sections on a
  page will have vertical separation between them.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :section
                                          :wb-section
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef section
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn section-heading
  "
  Simple heading, designed to appear exactly once at the top of a section.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :container
                                          :wb-section-heading
                                          :is-size-1
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef section-heading
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn text-block
  "
  Simple container for one or more paragraphs of text. Automatically adds a
  1.5rem margin between itself and the next child element, unless it is the
  last child element.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :content
                                          :wb-text-block
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef text-block
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn centered
  "
  Component that centers itself horizontally on the page. Accepts standard
  :extra-classes and :subscribe-to-classes options."
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :container :wb-centered
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef centered
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn caption
  "
  Component in a small, italicized fort, for captions. Accepts standard
  :extra-classes and :subscribe-to-classes options."
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :wb-caption
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef caption
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn frame
  "
  Encloses contents inside a frame with rounded corners and a slight drop
  shadow"
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        ]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :box
                                          :wb-frame
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef frame
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn columns
  "
  Parent container for a multi-column layout. Expects all children to be
  [layout/column] components. Supports standard :extra-classes and subscribe-to-classes
  options.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        ]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :columns
                                          :wb-columns
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef columns
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn column
  "
  Component for a single column inside a multi-column layout. Use inside a [layout/columns]
  component. Supports standard :extra-classes and subscribe-to-classes options. Additional
  column settings are available via CSS classes; see https://bulma.io/documentation/columns/sizes/
  for details.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        ]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :column
                                          :wb-column
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef column
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn padded
  "
  Component with padding around all four sides, for use where extra white space is needed.
  Supports standard :extra-classes and :subscribe-to-classes options.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        ]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub
            ]
        (into [:div {:class (adu/css->str :wb-padded
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef padded
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn zero-pad
  "
  A component with zero padding and zero margin. Used to group components without adding
  any additional width or height. Does not take any options.
  "
  [& children]
  (into [:div.wb-zero-pad] children))

(s/fdef zero-pad
  :args (s/cat :children (s/+ any?))
  :ret vector?)

(defn clearfix
  "Simple component to clear wrapping after an .is-pulled-left or .is-pulled-right element.
  Takes no arguments"
  []
  [:div.is-clearfix])
