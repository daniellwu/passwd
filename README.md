[![Build Status](https://travis-ci.org/daniellwu/passwd.svg?branch=master)](https://travis-ci.org/daniellwu/passwd)

Passwd as a Service
=============

### Introduction ###
This application is a result of an interview coding challenge that demonstrates a minimal web service that exposes the
content of /etc/passwd and /etc/group. It is obviously not meant to be used in the real world due to security concerns, but serves merely to highlight using spring boot to write a simple and clean web services. 

### Requirement ###

To aid testing and deployment, the paths to the passwd and groups file should be configurable, defaulting to the standard system path. If the input files are absent or malformed, your service must indicate an error in a manner you feel is appropriate.
This service is read-only but responses should reflect changes made to the underlying passwd and groups files while the service is running. The service should provide the following methods:

###### GET /users
Return a list of all users on the system, as defined in the /etc/passwd file.
Example Response:
```
[
{“name”: “root”, “uid”: 0, “gid”: 0, “comment”: “root”, “home”: “/root”,
“shell”: “/bin/bash”},
{“name”: “dwoodlins”, “uid”: 1001, “gid”: 1001, “comment”: “”, “home”:
“/home/dwoodlins”, “shell”: “/bin/false”}
]
```

###### GET /users/query[?name=<nq>][&uid=<uq>][&gid=<gq>][&comment=<cq>][&home=<hq>][&shell=<sq>]
Return a list of users matching all of the specified query fields. The bracket notation indicates that any of the
following query parameters may be supplied:
- name
- uid
- gid
- comment
- home
- shell

Only exact matches need to be supported.

Example Query: 
```
GET /users/query?shell=%2Fbin%2Ffalse
```
Example Response:
```
[
{“name”: “dwoodlins”, “uid”: 1001, “gid”: 1001, “comment”: “”, “home”:
“/home/dwoodlins”, “shell”: “/bin/false”}
]
```

###### GET /users/<uid>
Return a single user with <uid>. Return 404 if <uid> is not found.

Example Response:
```
{“name”: “dwoodlins”, “uid”: 1001, “gid”: 1001, “comment”: “”, “home”:
“/home/dwoodlins”, “shell”: “/bin/false”}
```

###### GET /users/<uid>/groups
Return all the groups for a given user.

Example Response:
```
[
{“name”: “docker”, “gid”: 1002, “members”: [“dwoodlins”]}
]
```

###### GET /groups
Return a list of all groups on the system, a defined by /etc/group.

Example Response:
```
[
{“name”: “_analyticsusers”, “gid”: 250, “members”:
[“_analyticsd’,”_networkd”,”_timed”]},
{“name”: “docker”, “gid”: 1002, “members”: []}
]
```

###### GET /groups/query[?name=<nq>][&gid=<gq>][&member=<mq1>[&member=<mq2>][&...]]
Return a list of groups matching all of the specified query fields. The bracket notation indicates that any of the
following query parameters may be supplied:
- name
- gid
- member (repeated)

Any group containing all the specified members should be returned, i.e. when query members are a subset of
group members.

Example Query: 
```
GET /groups/query?member=_analyticsd&member=_networkd
```
Example Response:
```
[
{“name”: “_analyticsusers”, “gid”: 250, “members”:
[“_analyticsd’,”_networkd”,”_timed”]}
]
```

###### GET /groups/<gid>
Return a single group with <gid>. Return 404 if <gid> is not found.

Example Response:
```
{“name”: “docker”, “gid”: 1002, “members”: [“dwoodlins”]}
```

### Usage ###

The parameters in [] are optional; if not specified will default to the location indicated in the example

- Option 1 (launch directly using gradle wrapper)

```
./gradlew bootRun [-Dgroup-path=/etc/group] [-Dpasswd-path=/etc/passwd]
```

- Option 2 (build an executable first, then run the executable)
```
./gradlew build
build/libs/passwd-1.0.0.jar [--group-path=/etc/group] [--passwd-path=/etc/passwd]
```

### Design considerations ###
* Each API call right now will scan the /etc/passwd and/or /etc/group file and parses them. This has the advantage of
fetching the newest data each time. However, depends on the use cases, if these calls are called with extremely high
frequency and the contents of /etc/passwd and /etc/group seldomly change, than it might be worthwhile to add caching
* Retrieving a single user/group right now will still scan and convert each line in the file into a java object. This
maximizes code-reuse and increases maintainability. However, if performance is highly critical, code can be 
micro-optimized to skip pojo creation unless necessary, and instead filter on the raw text directly using string
manipulation.
* Spring boot framework was chosen to provide an opinionated selection of libraries that work great out of the box. It 
is perfect for a CRUD like application such as this, and leverages well-tested libraries such as tomcat, spring, slf4j, 
etc.
* Error handling is done conservatively. That is, if any lines in /etc/passwd or /etc/group are invalid, rather than
skipping the line, an error is returned. Furthermore, on startup, reading and parsing of /etc/passwd and /etc/group are
attempted, so if it's unreachable or unparseable, it will fail at init time, rather than later (during run time)
