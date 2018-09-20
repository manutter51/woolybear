# Best Practices

The following are my recommendations for maximizing code re-use, flexibility,
ease of refactoring, ease of testing, and ease of debuggin, while minimizing
the number of bugs.

* **Avoid the use of raw HTML outside of atomic components.** You may find cases
where you really do need raw HTML, but every time you encounter one, you should
think twice. Do you *really* need raw HTML, or would you be better off adding a
new, reusable atomic component?

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

* **Use semantic namespaces for your keywords** In other words, don't use
the `::my-keyword` shorthand. Keyword namespaces aren't tied to the filesystem
the way `ns` namespaces are, so you don't need or want to embed the original
file path in the namespace of your keyword. Instead, use namespaces that reflect
the semantic context. Use `:address/street`, `:address/city`, `:address/state`
and so on, or `:user/first-name` and `:user/last-name` etc. It's shorter, it's
easier to read, and you can have multiple different keyword namespaces in the same
file. Plus if you ever have to move a file, you won't need to refactor all your
keywords.

