# CHANGELOG


## 0.2.1

- Introduced fixtures for selenium remote and JVM-timeout (before importing the namespace, make sure that selenium-server library in classpath, e.g. [org.seleniumhq.selenium/selenium-server "2.48.2" :scope "test"])
- Added url resolving for applications being tested in multi-machine environments
- Improved fill-form with waiting for fields to become enabled
- Improved fill-form with select, radio and checkbox controls support
- Improved fill-form with varargs support to reduce the number of parens

## 0.2.0

- Improved fail message for some waiters.
- Only assertion function from taxi-toolkit is accepted by `assert-ui`.
- Most (if not all) relevant UI checks from taxi now has a corresponding assertion in taxi-toolkit.
- `assert-ui` no longer asserts by itself, but simply runs assertion functions from taxi-toolkit. This enables possibility for more eloquent, relevant and verbose fail messages.
- `each` may now be called with either 1 argument or 2 arguments, depending on if the assertion is simple or requires an expected value.
- Assertions now have a uniform name convension.
  - ~~`text=`~~ `has-text?`
  - ~~`attr=`~~ `has-attr?` or `has-attribute?`
  - ~~`count=`~~ `is-count?` or `found-exact-nr?` `is-exactly-nr?`
  - ~~`visible?`~~ `is-visible?`
  - ~~`focused?`~~ `is-focused?`
  - ~~`missing?`~~ `is-missing?`
  - ~~`is-missing?`~~ `x-is-missing?` _(express function)_
  - ~~`selected?`~~ `is-selected?`
  - ~~`hidden?`~~ `is-hidden?`
  - ~~`enabled?`~~ `is-enabled?`
  - ~~`disabled?`~~ `is-disabled?`
- Added new assertion functions.
  - `has-value?`
  - `is-not-focused?`
  - `is-present?`
  - `is-existing?`
  - `is-not-selected?` or `is-deselected?`
  - `is-displayed?` _alias of `is-visible?`_
  - `allows-multiple?`
  - `is-not-multiple?`
  - `has-page-title?`
- Revised documentation (readme).

## 0.1.7

- New waiter function `wait-for-element-count`.

## 0.1.6

- Added: clear-with-backspace.
- Added: selected? and deselected? assertions for checkboxes.
- Fixed a bug in each= so it respects the assert function passed as an argument (previously it was ignored and text= was always used).

## 0.1.5

- New waiter function `wait-for-present`.

## 0.1.4

- New selectors: `by-id` and `by-name`.
- `$` and `$$` now support nested elements with no UI map lookup, i.e.:

```clojure
($ {:xpath "//div"} {:xpath "p"})
```

- Fixed `is-missing?` so it handles XPath selectors properly.
- Fixed `query-with-params` so it handles nested elements properly. Note that parameters will be replaced only in the
child selector, not in a parent selector.

## 0.1.3

- Syntax has changed for nesting elements in the UI map. The new syntax:
```clojure
{:some-el {:self ...
           :child1 ...
           :child2 ...}}
```

replaces the old one:

```clojure
{:some-el [...
           :child1 ...
           :child2 ...]}

```

- When nesting elements, both CSS and XPath selectors are now supported. It is even legal now to use XPath selector for
a parent element, and CSS one for the child (or vice versa).
- The new `is-missing` can now be used (along the old `missing?`) - it is faster, but cannot be currently used as an
assertion in `assert-ui`.
- `(by-xpath)` no longer responds with a string. It returns a proper `{:xpath ...}` map instead.

## <= 0.1.2

Initial releases.
