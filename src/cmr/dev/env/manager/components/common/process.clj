(ns cmr.dev.env.manager.components.common.process
  (:require
    [cmr.dev.env.manager.config :as config]
    [cmr.dev.env.manager.process.core :as process]
    [com.stuartsierra.component :as component]
    [taoensso.timbre :as log]))

(defrecord ProcessRunner [
  builder
  process-keyword
  process-data]
  component/Lifecycle

  (start [component]
    (let [cfg (builder (:process-keyword component))
          component (assoc component :config cfg)
          process-name (name (:process-keyword component))]
      (log/infof "Starting %s component ..." process-name)
      (log/debug "Component keys:" (keys component))
      (log/trace "Built configuration:" cfg)
      (log/debug "Config:" (:config component))
      (let [process-data (process/spawn! "lein" process-name)]
        (log/debugf "Started %s component." process-name)
        (assoc component :process-data process-data))))

  (stop [component]
    (let [process-name (name (:process-keyword component))]
      (log/debug "Process name:" process-name)
      (log/infof "Stopping %s component ..." process-name)
      (log/trace "process-data:" (:process-data component))
      (log/trace "process:" (get-in component [:process-data :process]))
      (if-let [exit-code (process/terminate! (:process-data component))]
        (do
          (log/debugf "Got exit code:" exit-code)
          (log/debugf "Got exit code:" (str exit-code))
          (log/debug "Exit code type:" (type exit-code)))
        (log/errorf (str "The process associated with %s doesn't seem to have "
                         "exitted properly.")))
      (log/debugf "Stopped %s component." process-name)
      component)))

(defn create-process-runner-component
  ""
  [config-builder-fn process-keyword]
  (map->ProcessRunner
    {:builder config-builder-fn
     :process-keyword process-keyword}))
