(ns queuing.web
  (:gen-class)
  (:require
    [langohr.core      :as rmq]
    [langohr.channel   :as lch]
    [langohr.queue     :as lq]
    [langohr.consumers :as lc]
    [langohr.basic     :as lb]
    [compojure.core    :refer [defroutes GET]]
    [compojure.handler :refer [site]]
    [compojure.route   :as route]
    [ring.adapter.jetty :as jetty]
    [environ.core      :refer [env]]))

(def ^{:const true} default-exchange-name "")

(def qname "langohr.examples.hello-world")

(def amqp-url (get (System/getenv) "CLOUDAMQP_URL" "amqp://guest:guest@localhost:5672"))

(defn message-handler
  [ch {:keys [content-type delivery-tag] :as meta} ^bytes payload]
  (println
    (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s"
    (String. payload "UTF-8") delivery-tag content-type)))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Published message!"})

(defroutes app
  (GET "*" []
    (let [conn  (rmq/connect {:uri amqp-url})
          ch    (lch/open conn)]
    (lb/publish ch default-exchange-name qname
      "Hello!" {:content-type "text/plain" :type "greetings.hi"})
    (rmq/close ch)
    (rmq/close conn)
    (splash))))

(defn -main [& [port]]
  (let [conn  (rmq/connect {:uri amqp-url})
        ch    (lch/open conn)
        port  (Integer. (or port (env :port) 5000))]
    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber ch)))
    (lq/declare ch qname {:exclusive false :auto-delete true})
    (lc/subscribe ch qname message-handler {:auto-ack true})
    (jetty/run-jetty (site #'app) {:port port :join? false})
    (.addShutdownHook (Runtime/getRuntime) (Thread. #(do (rmq/close ch) (rmq/close conn))))))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
