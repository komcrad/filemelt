(ns filemelt.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [hiccup.core :refer [html]]
            [clojure.string :as string]
            [clojure.java.io :as io]))

(defn layout
  [contenets]
  (html [:html
          [:head]
          [:body
           [:nav
            [:a {:href "/"} "Home"]]
           contenets]]))

(defn root-handler
  [request]
  (layout [:form {:action "/upload" :method "post" :enctype "multipart/form-data"}
           [:input {:type "file" :name "file"}]
           [:button {:type "submit"} "Send!"]
           (anti-forgery-field)]))

(defn upload-handler
  [request]
  (let [file (.getAbsolutePath (get-in request [:params :file :tempfile]))]
    (let [newfile (get-in request [:params :file :filename])]
      (io/copy (io/file file) (io/file (str "resources/public/downloads/" newfile)))
      (io/delete-file file)
      (future
        (. Thread sleep 300000)
        (io/delete-file (str "resources/public/downloads/" newfile)))
      (layout [:div [:h1 newfile]]))))

(defn download-handler
  [request file-id]
  (layout [:a {:href (str "/downloads/" file-id) :download file-id} file-id]))

(defroutes app-routes
  (GET "/" [] root-handler)
  (POST "/upload" [] upload-handler)
  (GET "/:file-id" [file-id & request] (download-handler request file-id))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
