;;; Author: David Goldfarb (deg@degel.com)
;;; Copyright (c) 2017, David Goldfarb

(ns trilystro.view-modal-confirm-delete
  (:require
   [re-frame.loggers :refer [console]]
   [sodium.core :as na]
   [iron.re-utils :refer [<sub >evt]]
   [trilib.firebase :as fb]
   [trilib.fsm :as fsm]
   [trilib.modal :as modal]))


(defn view-modal-confirm-delete []
  (let [lystro (<sub [::fsm/page-param])]
    [modal/modal {:page :modal-confirm-delete
                  :header "Really delete Lystro?"}
     [na/modal-content {}
      [na/container {}
       (str "Will delete \""
            (subs (or (:text lystro) (:url lystro) "") 0 20)
            "\"...")
       [na/divider {}]
       [na/button {:content "Delete"
                   :negative? true
                   :icon "delete"
                   :floated "right"
                   :on-click #(>evt [::fsm/goto :quit-modal
                                     {:dispatch [::fb/clear-lystro lystro]}])}]
       [na/button {:content "Cancel"
                   :icon "dont"
                   :secondary? true
                   :on-click #(modal/quit)}]]]]))
