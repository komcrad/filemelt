(ns filemelt.main-layout
  (:require [hiccup.core :refer [html]]))

(defn layout
  [contenets]
  (html [:html
          [:head
		   (slurp (clojure.java.io/resource "public/favicon.html"))
           [:link {:rel "stylesheet" :type "text/css" :href "/styles/main.css"}]]
		   [:script {:src "/js/dropzone.js"}]
          [:body
           [:nav
            [:a {:href "/"} "Home"]]
		   [:div {:id "main"}
           contenets]]]))
