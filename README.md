# BoBo AR API
---

### What to prepare : 
1. [Java SDK 1.7]( http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html )
2. Maven (for installing dependencies)
3. Redis (Can be remote) - cache
4. MySql (Can be remote) - database
5. Rabbitmq (Can be remote) - Message queue
6. Git - Code version control
7. Jetty - Inner

### How to setup : 
You can check the code out wherever you like, for example:
```
$ cd /home/painter/
$ git clone ssh://git@gitlab.ws.netease.com:16322/ar/ar-api.git
$ cd ar-api
$ mvn clean install -Dmaven.test.skip=true
```  
### How to develop.

**To build, package for deployment:** 
`mvn clean install`    

Optionally, to skip tests when building: `mvn clean install -Dmaven.test.skip=true`

**To deploy to dev:**
**To deploy to prod:**

### Code Layout
- **ar-api**  the shell project, contains all others
- **ar-common**  common classes and utilities that aren't specifically related to vshow.
- **ar-web**  http api and fe files,depends on ar-common

### Request Flow
 1. Requests come in to Controllers. These are in the `com.netease.ar-common` package.
 2. Controller -> Service -> DAO
 
### Coding Patterns
  - All Controllers and Services **must** be thread-safe.  They are Singletons, and shared by requests.
  - DB Performance - make sure queries are using indexes. Had better not join table in mysql, do it in Java.
  - Inter-Service Dependencies - No two Services should depend on one another.  Be mindful of the [Single Responsibility Principle](http://en.wikipedia.org/wiki/Single_responsibility_principle)
  - [Use Guava](https://code.google.com/p/guava-libraries/).  Guava is good.  Don't write code yourself that could be done more elegantly with this well-tested library.

### access the url : http://localhost:8081/api/v1/apidoc/index.html