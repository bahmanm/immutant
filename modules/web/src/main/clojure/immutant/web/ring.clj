;; Copyright 2008-2012 Red Hat, Inc, and individual contributors.
;; 
;; This is free software; you can redistribute it and/or modify it
;; under the terms of the GNU Lesser General Public License as
;; published by the Free Software Foundation; either version 2.1 of
;; the License, or (at your option) any later version.
;; 
;; This software is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
;; Lesser General Public License for more details.
;; 
;; You should have received a copy of the GNU Lesser General Public
;; License along with this software; if not, write to the Free
;; Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
;; 02110-1301 USA, or see the FSF site: http://www.fsf.org.

(ns ^{:no-doc true} immutant.web.ring
  (:use immutant.web.internal)
  (:require [clojure.string    :as string]
            [ring.util.servlet :as servlet])
  (:import (javax.servlet.http HttpServletRequest HttpServletResponse)))

(defn handle-request [servlet-name
                      ^HttpServletRequest request
                      ^HttpServletResponse response]
  (.setCharacterEncoding response "UTF-8")
  (let [{:keys [handler sub-context]} (get-servlet-info servlet-name)]
    (if handler
      (if-let [response-map (binding [current-servlet-request request]
                              (handler
                               (assoc (servlet/build-request-map request)
                                 :context (str (.getContextPath request)
                                               (.getServletPath request))
                                 :path-info (or (.getPathInfo request) "/"))))]
        (servlet/update-servlet-response response response-map)
        (throw (NullPointerException. "Handler returned nil.")))
      (throw (IllegalArgumentException. (str "No handler function found for " servlet-name))))))

