(ns filemelt.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [filemelt.main-layout :refer :all]))

(defn root-handler
  [request]
  (layout [:form {:id "file-upload" :action "/upload" :method "post" :enctype "multipart/form-data"}
             [:input {:type "file" :name "file"}]
             [:button {:type "submit"} "Upload"]
             (anti-forgery-field)]))

(defn upload-handler
  [request]
  (let [file (.getAbsolutePath (get-in request [:params :file :tempfile]))]
    (let [newfile (get-in request [:params :file :filename])]
      (io/make-parents (str "resources/public/downloads/" newfile))
      (io/copy (io/file file) (io/file (str "resources/public/downloads/" newfile)))
      (io/delete-file file)
      (future
        (. Thread sleep (* 1000 60 * 60 * 24))
        (io/delete-file (str "resources/public/downloads/" newfile)))
      (layout [:div [:h1 newfile]]))))

(defn download-handler
  [request file-id]
  {:headers {"Content-Disposition" "attachment" "filename" file-id}
   :body (io/file (str "resources/public/downloads/" file-id))})

(defroutes app-routes
  (GET "/" [] root-handler)
  (POST "/upload" [] upload-handler)
  (GET "/:file-id" [file-id & request] (download-handler request file-id))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
