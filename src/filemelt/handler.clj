(ns filemelt.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [filemelt.main-layout :refer :all]
            [filemelt.index :refer :all]))

(defn upload-handler
  [request]
  (loop [files (first (get-in request [:params :files]))]
    (when (< 0 (count files))
      (println (first files))
      ;(let [uploaded-file (:tempfile (first files)),
      ;      file-name (:filename (first files))]
      ;  (io/make-parents (str "resources/public/downloads/" file-name))
      ;  (io/copy (io/file uploaded-file) (io/file (str "resources/public/downloads/" file-name)))
      ;  (io/delete-file uploaded-file))
      (recur (rest files)))))

(defn do-nothing
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
  (GET "/" [] index-handler)
  (POST "/upload" [] upload-handler)
  (GET "/:file-id" [file-id & request] (download-handler request file-id))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
