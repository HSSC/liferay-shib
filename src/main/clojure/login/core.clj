(ns login.core
  (:import [com.liferay.portal.service UserLocalServiceUtil]
           [com.liferay.portal NoSuchUserException]
           [com.liferay.portal.util PortalUtil]
           [com.liferay.portal.security.auth AutoLogin AutoLoginException]
           [javax.servlet.http HttpServletRequest HttpServletResponse]
           [javax.servlet.jsp PageContext])
  (:gen-class
   :implements [com.liferay.portal.security.auth.AutoLogin]))

(def company-id 0)

(defn -init
  []
  [])

(defn get-credentials
  [email]
  (let [user (UserLocalServiceUtil/getUserByEmailAddress company-id email)
        credentials (make-array (.getClass String )  3)]
    (do
      (aset credentials 0 (str (.getUserId user)))
      (aset credentials 1 (str (.getPassword user)))
      (aset credentials 2 "true"))
    credentials))

(defn -login
  [_ ^HttpServletRequest request ^HttpServletResponse response]
  (println (enumeration-seq (.getHeaderNames request)))
  (println (enumeration-seq (.getAttributeNames request)))
  (println (.getRemoteUser request))
  (println (.getHeader request "Shib-EduPerson-Principal-Name"))
  (println "*****************************")
  (try
    (println "here")
    (let [user-id (.getHeader request "Shib-EduPerson-Principal-Name")
          mail (.getHeader request "Shib-InetOrgPersonMail")
          display-name (.getHeader request "Shib-Person-commonName")
          original-request (PortalUtil/getOriginalServletRequest request) ]
      (println (enumeration-seq (.getHeaderNames original-request)))
      (println (enumeration-seq (.getAttributeNames original-request)))
      (println (.getRemoteUser original-request))
      (println (.getHeader original-request "Shib-EduPerson-Principal-Name"))
      (println "*****************************")
      (if mail
        (get-credentials mail)))
    (catch NoSuchUserException nsue
      (println "no such user exception")
      (throw nsue))
    (catch Exception e
      (throw (AutoLoginException. e)))))


