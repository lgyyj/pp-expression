(ns scs.lib
  (:gen-class))


(defn get_map_value [m key]
  (if (or (nil? m) (not (map? m)) (empty? m))
    nil
    (get m key)))


(defn filter-empty
  ""
  [data]
  (if data
    nil
    (if (coll? data)
      (filter (fn [x]
                (cond
                  coll? (empty? x)
                  :else (some? x))) data)
      data)))