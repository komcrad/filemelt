(ns filemelt.index
  (:require [filemelt.main-layout :refer :all]
			[ring.util.anti-forgery :refer [anti-forgery-field]]
			[hiccup.core :refer [html]]))

(defn generate-form 
  []
  (html [:form {:class "box", :method "post", :action "/upload", :enctype "multipart/form-data"} 
	[:div {:class "box__input"} 
	  [:input {:class "box__file", :type "file", :name "files[]", :id "file",
			   :data-multiple-caption "{count} files selected", :multiple ""}]
	  [:label {:for "file"} [:strong {} "Choose a file"]
		[:span {:class "box__dragndrop"} " or drag it here"] "."]
	  [:button {:class "box__button", :type "submit"} "Upload"]]
	[:div {:class "box__uploading"} "Uploading…"]
	[:div {:class "box__success"} "Done!"]
	[:div {:class "box__error"} "Error! " [:span] "."]
	(anti-forgery-field)]))

(defn index-handler
  [request]
  (layout (generate-form)))
