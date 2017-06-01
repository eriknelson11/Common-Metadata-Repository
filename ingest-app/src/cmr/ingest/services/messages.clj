(ns cmr.ingest.services.messages
  (:require [cmr.common.util :as util]
            [cmr.common.validations.core :as vc]
            [clojure.string :as str]))

(defn parent-collection-does-not-exist
  [provider-id granule-ur collection-ref]
  (let [collection-ref-fields (util/remove-nil-keys (into {} collection-ref))
        coll-ref-humanized-fields (for [[field value] collection-ref-fields]
                                    (format "%s [%s]" (vc/humanize-field field) value))]
    (format "Collection with %s referenced in granule [%s] provider [%s] does not exist."
            (str/join ", " coll-ref-humanized-fields)
            granule-ur provider-id)))

(defn invalid-multipart-params
  [expected-params actual-params]
  (format "Unexpected multipart parameters: [%s]. Expected the multipart parameters [%s]."
          (str/join ", " actual-params)
          (str/join ", " expected-params)))

(defn invalid-revision-id
  [revision-id]
  (format "Invalid revision-id [%s]. Cmr-Revision-id in the header must be a positive integer." revision-id))

(defn invalid-parent-collection-for-validation
  [collection-validation-error]
  (str "The collection given for validating the granule was invalid: " collection-validation-error))

(defn platform-not-matches-kms-keywords
  [platform]
  (format "Platform short name [%s] and long name [%s] was not a valid keyword combination."
          (:ShortName platform) (:LongName platform)))

(defn instrument-not-matches-kms-keywords
  [instrument]
  (format "Instrument short name [%s] and long name [%s] was not a valid keyword combination."
          (:ShortName instrument) (:LongName instrument)))

(defn project-not-matches-kms-keywords
  [project-map]
  (format "Project short name [%s] and long name [%s] was not a valid keyword combination."
          (:ShortName project-map) (:LongName project-map)))

(defn datacenter-not-matches-kms-keywords
   "Error msg when DataCenter's ShortName, LongName are not in the KMS."
   [datacenter]
   (format "Data center short name [%s] and long name [%s] was not a valid keyword combination."
          (:ShortName datacenter) (:LongName datacenter)))

(defn directoryname-not-matches-kms-keywords
   "Error msg when DirectoryName's ShortName is not in the KMS."
   [directoryname]
   (format "Directory name short name [%s] was not a valid keyword."
          (:ShortName directoryname)))

(def science-keyword-attribute-order
  "The order of fields that should be displayed in the science keyword human readable list."
  [:Category :Topic :Term :VariableLevel1 :VariableLevel2 :VariableLevel3])

(def location-keyword-attribute-order
  "The order of fields that should be displayed in the spatial keyword human readable list."
  [:Category :Type :Subregion1 :Subregion2 :Subregion3])

(defn- keyword->human-attrib-list
  "Converts a keyword into a human readable list of attributes with their values."
  [k attribute-order]
  (let [human-id-values (keep (fn [field]
                                (when-let [value (get k field)]
                                  (str (vc/humanize-field field) " [" value "]")))
                              attribute-order)]
    (case (count human-id-values)
      1 (first human-id-values)
      2 (str/join " and " human-id-values)
      ;; else
      (str (str/join ", " (drop-last human-id-values)) ", and " (last human-id-values)))))

(defn science-keyword-not-matches-kms-keywords
  "Create the invalid science keyword message"
  [sk]
  (format "Science keyword %s was not a valid keyword combination."
          (keyword->human-attrib-list sk science-keyword-attribute-order)))

(defn location-keyword-not-matches-kms-keywords
  "Create the invalid location keyword message"
  [lk]
  (format "Location keyword %s was not a valid keyword combination."
          (keyword->human-attrib-list lk location-keyword-attribute-order)))

(defn variable-already-exists
  [variable concept-id]
  (format "A variable with native-id [%s] already exists with concept id [%s]."
          (:native-id variable) concept-id))

(def token-required-for-variable-modification
  "Variables cannot be modified without a valid user token.")

(defn variable-deleted
  [variable-key]
  (format "Variable with variable-key [%s] was deleted." variable-key))

(defn variable-does-not-exist
  [variable-key]
  (format "Variable could not be found with variable-key [%s]" variable-key))

(defn variable-already-exists
  [variable concept-id]
  (format "A variable with variable-key [%s] already exists with concept id [%s]."
          (:variable-key variable)
          concept-id))

(defn service-already-exists
  [service concept-id]
  (format "A service with native-id [%s] already exists with concept id [%s]."
          (:native-id service) concept-id))

(def token-required-for-service-modification
  "Services cannot be modified without a valid user token.")

(defn service-deleted
  [service-key]
  (format "Variable with service-key [%s] was deleted." service-key))

(defn service-does-not-exist
  [service-key]
  (format "Service could not be found with service-key [%s]" service-key))

(defn service-already-exists
  [service concept-id]
  (format "A service with service-key [%s] already exists with concept id [%s]."
          (:service-key service)
          concept-id))
