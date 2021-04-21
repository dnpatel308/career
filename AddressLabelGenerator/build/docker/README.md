# Isaac deployment
```
docker run --rm -it \
  -e CONSUL_URL=http://172.17.42.50:8503 \
  -e CONSUL_CONFIG_KEY=development \
  -e DATABASE_PASSWORD=<pwd> \
  -e DATABASE_USER=<user> \
  -e DATABASE_URL="jdbc:mysql://172.17.42.50:3308/bam?characterEncoding=UTF-8" \
  -p 8181:8181 docker.jfrog.io/brodos/${project.finalName}:1.0.0.SNAPSHOT-<buildnum>
```
## Variables
### CONSUL_URL
CONSUL_URL points to the consul server. If given, CONSUL_CONFIG_KEY has also to
be provided. the url is just the url to the hostname and port of the consul
http interface like `http://hostname:port`
### CONSUL_CONFIG_KEY
The value depends on how the layout of the structure has been created.
This installation assumes that the key `com/brodos/isaac` exists as a folder on
the addressed consul server. The startup script then appends the given
CONSUL_CONFIG_KEY as part of the url. Given `CONSUL_URL=http://hostname:port`
`CONSUL_CONFIG_KEY=development`the resulting url to read config files from
would be `http://hostname/port/v1/kv/com/brodos/isaac/development`. See below for
description of consul configuration structure for isaac.
### DATABASE_URL
The current installation is assuming a mariadb (10.x) installation. Using
DATABASE_URL you can point the configuration to a database server.
Syntax is: `jdbc:mysql://hostname:port/database-name?characterEncoding=UTF-8`
### DATABASE_USER
The user to connect to the database
### DATABASE_PASSWORD
Password of database user
## Consul config file structure
All keys under `com/brodos/isaac/<config_key>/` are considered to reflect the
karaf installation directory. So if you place a configuration key like
`com/brodos/isaac/<config_key>/etc/hystrix.properties.cfg` the whole value will
be written into a file named `hystrix.properties.cfg` in the `etc` directory
under the karaf installation dir. This way you can overwrite any config file
in etc directory.
*important*: only `etc` directory is supported currently.
