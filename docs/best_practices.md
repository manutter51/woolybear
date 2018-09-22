# Best Practices

The following are my recommendations for maximizing code re-use, flexibility,
ease of refactoring, ease of testing, and ease of debugging, while minimizing
the number of bugs.

* **Avoid the use of raw HTML outside of atomic components.** You may find cases
where you really do need raw HTML, but every time you encounter one, you should
think twice. Do you *really* need a `div`, or would you be better off adding a
new, reusable atomic component? The very basic elements like `p`, `img`, and `a`,
are ok (and would probably be pointless to re-implement as custom one-off
components), but anything more complex than that should be formalized into a 
meaningful component. If it's hard to thing of a meaningful, useful component
that does what you want, it may be an indication that there's a flaw in your
UX design.

* **Don't set styles directly on DOM elements.** Instead of doing
`[:div {:style {:display "none"}} ...]`, use classes like `[:div.hidden ...]`.
It's easy to toggle classes on and off, and by having named CSS classes for
the style changes you want, you make your site more consistent and reduce
boilerplate.

* **Use semantic namespaces for your keywords** In other words, don't use
the `::my-keyword` shorthand. Keyword namespaces aren't tied to the filesystem
the way `ns` namespaces are, so you don't need or want to embed the original
file path in the namespace of your keyword. Instead, use namespaces that reflect
the semantic context. Use `:address/street`, `:address/city`, `:address/state`
and so on, or `:user/first-name` and `:user/last-name` etc. It's shorter, it's
easier to read, and you can have multiple different keyword namespaces in the same
file. Plus if you ever have to move a file, you won't need to refactor all your
keywords.

* **Write subscriptions that are only one layer deep.** In other words, instead
of writing this:
```$clojure
(re-frame/reg-sub
 :user-email
 (fn [db [_]]
   (get-in db [:user :email])))
```
write this:
```$clojure
(re-frame/reg-sub
 :db/user
 (fn [db [_]]
   (:user db)))

(re-frame/reg-sub
 :user/email
 :<- [:db/user]
 (fn [user]
   (:email user)))
```
Writing subscriptions that are only one layer deep maximizes the efficiency
gains you get from the fact that subscriptions trigger re-renders only when
the underlying data changes. When you `(get-in db [:user :email])`, you'll
re-render when *any* of the `:user` data changes, not just the `:email` part.


[Back](_start_here.md)
