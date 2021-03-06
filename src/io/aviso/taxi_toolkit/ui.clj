(ns io.aviso.taxi-toolkit.ui
  "Set of helper functions for interacting with DOM elements."
  (:require [clj-webdriver.taxi :refer :all]
            [clj-webdriver.core :refer [->actions move-to-element click-and-hold release]]
            [clojure.string :as s]
            [clojure.test :refer [is]]
            [io.aviso.taxi-toolkit
             [utils :refer :all]
             [ui-map :refer :all]])
  (:import org.openqa.selenium.Keys))

(def webdriver-timeout (* 15 1000))
(def ^:private ui-maps (atom {}))


(defn set-ui-spec!
  [& xs]
  (reset! ui-maps (apply merge xs)))

(defn $
  "Find one element"
  [& el-path]
  (apply find-one @ui-maps el-path))

(defn $$
  "Find all elements"
  [& el-path]
  (apply find-all @ui-maps el-path))

(defn query-with-params
  "Retrieves element spec and replaces all params needles
  with the proper values."
  [params & el-path]
  (let [nested? (not= 1 (count el-path))
        el-spec (resolve-element-path @ui-maps el-path)
        el-spec-resolved (into {} (map (fn [[k v]] [k (replace-all v params)]) el-spec))]
    (if nested?
      (let [parent-el-spec (resolve-element-path @ui-maps (first el-path))]
        [parent-el-spec el-spec-resolved])
      el-spec-resolved)))

(defn click-non-clickable
  "Similar to (taxi/click), but works with non-clickable elements such as <div>
   or <li>."
  [el]
  (->actions *driver*
           (move-to-element el)
           (click-and-hold el)
           (release el)))

(defn a-click
  "Element-agnostic. Runs either (taxi/click) or (click-anything)."
  [& el-spec]
  (let [el (apply $ el-spec)]
    (case (.getTagName (:webelement el))
      ("a" "button") (retry #(click el))
      (retry #(click-non-clickable el)))))

(defn a-text
  "For non-form elements such as <div> works like (taxi/text).
  For <input> works like (taxi/value)."
  [el]
  (case (.getTagName (:webelement el))
    ("input") (retry #(value el))
    (retry #(text el))))

(defn classes
  "Return list of CSS classes element has applied directly (via attribute)."
  [el]
  (s/split (attribute el :class) #"\s+"))

(defn fill-form
  "Fill a form. Accepts a map of 'element - value' pairs.

  Also, waits until the element is enabled using clj-webdriver.taxi/wait-until.

  Will invoke an appropriate function depending on the element charactericts:
   - input[type=checkbox] or input[type=radio]- select/deselect, otherwise
   - input/textarea - input-text, otherwise
   - select - select-option

  Takes either a vector [el value] pairs (or a map which would behave as such collection when applied to doseq)
  or an even number of key-value pairs if we want to preserve the order."

  [& el-val-or-entries]
  (let [el-val (if (= (count el-val-or-entries) 1)
                 el-val-or-entries
                 (partition 2 el-val-or-entries))]

    (doseq [[el-spec value] el-val]
      (let [q (apply $ (as-vector el-spec))]
        (wait-until #(enabled? q) webdriver-timeout)
        (let [tag-name (s/lower-case (tag q))
              type-attr (s/lower-case (or (attribute q "type") ""))]
          (case tag-name
            "select" (select-option q value)
            ("textarea" "input") (case type-attr
                                   ("radio" "checkbox") (if value (select q) (deselect q))
                                   (input-text q value)))))))
  el-val-or-entries)

(defn clear-with-backspace
  "Clears the input by pressing the backspace key until it's empty."
  [& el-spec]
  (let [el (apply $ el-spec)
        n-of-strokes (count (a-text el))]
    (doall (repeatedly n-of-strokes #(send-keys el org.openqa.selenium.Keys/BACK_SPACE)))))
