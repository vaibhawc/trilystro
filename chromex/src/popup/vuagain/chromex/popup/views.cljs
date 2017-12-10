(ns vuagain.chromex.popup.views
  (:require
   [chromex.ext.runtime :as runtime :refer-macros [connect]]
   [chromex.protocols :refer [post-message!]]
   [iron.re-utils :as re-utils :refer [sub2 <sub >evt]]
   [re-frame.core :as re-frame]
   [re-frame.loggers :refer [console]]
   [reagent.core :as reagent]
   [sodium.core :as na]
   [sodium.extensions :as nax]))

(defn display [display?]
  {:display (if display? "block" "none")})

(defn bg-msg [message]
  (post-message! (<sub [:background-port])
                 (clj->js (assoc message :app "VuAgain"))))


(defn top-bar []
  (let [user (<sub [:user])]
    [:div
     [:nav {:class "navbar navbar-default"}
      [:div {:class "container-fluid"}
       [:div {:class "navbar-header"}
        [:div {:class "navbar-brand"}]
        [:h3 "VuAgain"]]]]
     [:button.alignRight {:class "btn btn-social",
                          :type "button"
                          :id (if user "logout" "login")
                          :on-click #(bg-msg {:command (if user "sign-out" "sign-in")})}
      (if user (<sub [:user-name]) "login")]]))

(defn warning-bar []
  [:p {:class "warning",
       :id "warning",
       :style {:display "none"}}])


(defn TOS-page [display?]
  [:form {:class "spaPage", :id "TOSForm", :style (display display?)}
   [:h4 "VuAgain Terms of Service"]
   [:p "VuAgain is in alpha pre-release, and may not yet be fully reliable."]
   [:p "Do not yet use VuAgain to store any information that you cannot afford to\nlose, or that you must keep confidential."]
   [:p "We plan to upgrade rapidly. Please contact our"
    [:a {:href "mailto:info@vuagain.com"}
     "support desk"]" with your comments\nand suggestions."]
   [:h5 "How to Use"]
   [:p "VuAgain helps you save notes about web pages you want to remember.\nUnlike bookmarking services, VuAgain does not force\nyou to look for your saved notes. Instead, they appear in your regular\nGoogle search results."]
   [:p "You can save two kinds of notes:"]
   [:ul
    [:li "Private notes are seen only by you"]
    [:li "Public comments can be seen by everyone"]]
   [:button {:class "btn btn-default", :id "acceptTOSButton", :type "button"} "I agree"]])

(defn logged-out-page [display?]
  [:div
   [:form {:class "spaPage", :id "anonymousForm", :style (display display?)}
    [:p "You do not seem to be logged in with a chrome identity."]
    [:p "VuAgain depends on features supplied by Chrome and cannot start until you are\nlogged in with a Chrome identity."]
    [:p "VuAgain will not work if you are using a non-Chrome browser or are logged in\nanonymously."]
    [:p "If you are seeing this message in other circumstances, please\ncontact our "
     [:a {:href "mailto:info@vuagain.com"} "support desk"]"."]]
   [:form {:class "spaPage", :id "loginForm", :style (display display?)}
    [:p "VuAgain needs to know your Google or Facebook identity to let you share\ncomments publicly or with your friends."]]
   [TOS-page true]])


(defn logged-in-page [display?]
  [:form {:class "spaPage", :id "vaForm", :style (display display?)}
   [:div {:class "form-group"}
    [:div "Add Lystro for " [:b(<sub [:title])] ", from " [:em (<sub [:url])]]
    [:hr]
    [:label "Tags"]
    [:input {:dir "auto",
             :placeholder "(NYI)",
             :name "tags",
             :type "text",
             :maxLength "140",
             :id "tagsInput",
             :class "form-control",
             :autoFocus true,
             :autoComplete "off"}]]
   [nax/labelled-field
    :label "Text:"
    :content [na/text-area {:rows 3
                            :placeholder "Description..."
                            :default-value (<sub [:page-param :text])
                            :on-change (na/value->event-fn [:update-page-param-val :text])}]]
   [:hr]
   [:div {:class "form-group centered"}
    [:button {:class "btn", :id "cancelButton"} "Cancel"]
    [:button {:class "btn btn-primary",
              :type "button"
              :id "submitButton"
              :on-click #(>evt [:commit-lystro {:tags #{}
                                                :url (<sub [:url])
                                                :text (<sub [:page-param :text])
                                                :owner (<sub [:fb-uid])
                                                :public? false}])}
     "Save"]]])

(defn footer-bar [display?]
  [:div {:class "panel-footer", :style {:margin-top "15px"}}
   [:small
    [:p {:class "alignLeft", :id "versionString"}]
    [:p {:class "alignRight"}
     [:a {:id "showTOSButton", :href "#"} "Show Terms of Service"]]]])

(defn popup []
  (fn []
    [:div
     [top-bar]
     [:div {:class "container"}
      [warning-bar]
      (if (<sub [:user])
        [logged-in-page true]
        [logged-out-page true])]
     [footer-bar true]]))