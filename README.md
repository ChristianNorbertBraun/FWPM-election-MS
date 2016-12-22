Student Election Microservice
====================================

This microservice is part of our project FWPM Election and it enables students to elect FWPMs.
The project is developed for a docker-environment.

You can find our Dockerfiles under:

* [Dockerfiles OSX][osx]

System Requirements
-------------------

* Running docker-environment described in the Dockerfile-Repos 
* [Maven][mvn]

Running
-------

* Add a Server 'tomcat-localhost' with the same User and Password you configured in your Tomcat
* $ mvn clean install
* $ mvn tomcat:redeploy

Testing
-------
You can run the tests to test your database but you will need an mysql host entry in your hosts file.
[osx]: https://github.com/marcelgross90/Tomcat-MYSQL-Docker
[mvn]: https://maven.apache.org/
