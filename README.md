webserver-report
================

Using our monitoringServlet, applications give some data with the format below

```json
{"timestamp": "2012-11-24 00:09:05.449", "type": "memory", "id": "total_memory", "status": "OK", "sizemb": "1024", "message": null}
{"timestamp": "2012-11-24 00:09:05.449", "type": "memory", "id": "free_memory", "status": "OK", "sizemb": "739", "message": null}
{"timestamp": "2012-11-24 00:09:05.449", "type": "memory", "id": "max_memory", "status": "OK", "sizemb": "2048", "message": null}
{"timestamp": "2012-11-24 00:09:05.449", "type": "memory", "id": "available_memory", "status": "OK", "sizemb": "1763", "message": null}
{"timestamp": "2012-11-24 00:09:05.449", "type": "thread", "id": "total_threads", "status": "OK", "count": "121", "message": null}
{"timestamp": "2012-11-24 00:09:05.552", "type": "was.pool.ds", "id": "DS_STEELUSER_COLDFUSION", "status": "OK", "jndi": "coldfusion_db", "used": "0", "available": "50", "min": "1", "max": "50", "message": null}
```

These application allows to store these data into a MongoDB server.
* One <em>application</em> per Mongo collection
* A An application is a set of Application Servers.
* Each application server (AS) is hosted on one or several physical servers.

We can extract data on
* Memory used
* Thread pool
* Datasource connection pool
* JMS connection pool


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
* Mongo
* jquery.js
* bootstrap.js
* Mustache.js
* HighCharts.js
* Angular.js
* Servlet 3.0
* JAX-RS
