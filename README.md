webserver-report
================

Using our monitoringServlet, applications give some data with the format below

```json
{"timestamp": "2012-11-24 00:09:05.449", "type": "memory", "id": "total_memory", "status": "OK", "sizemb": "1024", "message": null}
{"timestamp": "2012-11-24 00:09:05.449", "type": "memory", "id": "free_memory", "status": "OK", "sizemb": "739", "message": null}
{"timestamp": "2012-11-24 00:09:05.449", "type": "memory", "id": "max_memory", "status": "OK", "sizemb": "2048", "message": null}
{"timestamp": "2012-11-24 00:09:05.449", "type": "memory", "id": "available_memory", "status": "OK", "sizemb": "1763", "message": null}
{"timestamp": "2012-11-24 00:09:05.449", "type": "thread", "id": "total_threads", "status": "OK", "count": "121", "message": null}
{"timestamp": "2012-11-24 00:09:05.552", "type": "was.pool.ds", "id": "DS_STU_CFM", "status": "OK", "jndi": "cfm", "used": "0", "available": "50", "min": "1", "max": "50", "message": null}
```

These application allows to store these data into a MongoDB server.
* One <em>application</em> per Mongo collection
* A An application is a set of Application Servers.
* Each application server (AS) is hosted on one or several physical servers.

We can extract data on
* Memory used
* Thread pools
* DataSource connection pools
* JMS queue connection pools


Run the project
===============
We use maven to build the project
* <em>mvn clean package<em>
```
run.bat
```
This cmd will load the MongoServer.main() class that start a tomcat instance


Technology used
===============
* Angular.js  / angular-ui
* moment.js
* Mongo
* jQuery
* bootstrap.js
* HighCharts.js
* Servlet 3.0
* JAX-RS
* Coffee
* Jasmine for test

Develop
=======
![basic uml diag](https://github.com/mlecoutre/webserver-report/blob/master/doc/img/webserver-report.png "Basic UML representation")



