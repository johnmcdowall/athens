(ns athens.events
  (:require
   [athens.db :as db]
   [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-sub]]
   [re-posh.core :as rp :refer [reg-event-ds]]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [day8.re-frame.http-fx]
   [ajax.core :refer [json-request-format json-response-format]]
   [day8.re-frame.async-flow-fx]))

;; -- Initialization ------------------------------------------------

;; (defn boot-flow []
;;   {:first-dispatch
;;    [:load-dsdb]
;;    :rules [{:when :seen? :events :get-dsdb-success :halt? true}
;;            {:when :seen? :events :api-request-error :dispatch [:app-failed-state] :halt? true}]})

;; (reg-event-fx
;;  :load-dsdb
;;  (fn [{:keys [db]} [_ params]]
;;    {:http-xhrio {:method          :get
;;                  :uri             db/dsdb-help
;;                  :headers         {}
;;                  :response-format (json-response-format {:keywords? true})
;;                  :on-success      [:get-dsdb-success]
;;                  :on-failure      [:api-request-error :load-dsdb]}
;;     :db         (assoc-in db [:loading :dsdb] true)}))

(reg-event-ds
 :upload-dsdb
 (fn-traced [_ [event json-str]]
            (db/str-to-db-tx json-str)))

;; (reg-event-fx
;;  :boot-async
;;  (fn-traced [_ _]
;;             {:async-flow (boot-flow)}))

(reg-event-db
 :init-rfdb
 (fn [_ _]
   db/init-rfdb))

;; (reg-event-ds
;;  :init-dsdb
;;  (fn [_ _]
;;    db/init-dsdb))

;; -- Request Handlers -----------------------------------------------------------
(reg-event-ds
 :get-dsdb-success
 (fn-traced [_ [request-type response]]
            (db/str-to-db-tx response)
            ))

(reg-event-db
 :api-request-error
 (fn-traced [db [_ request-type response]]
   (-> db
       (assoc-in [:errors request-type] (get-in response [:response :errors]))
       (assoc-in [:loading request-type] false))))
