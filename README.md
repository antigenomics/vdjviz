# VDJviz: a lightweight immune repertoire browser

This is a [Play Framework](https://www.playframework.com/) version 2.2.4 application. To run the server execute
```
play run
```
from the project directory. A local server could be accessed at `localhost:9000`. 

This application uses [VDJtools](https://github.com/mikessh/vdjtools) API to compute various immune repertoire statistics and [D3.js](http://d3js.org/) library for interactive graphs.

## Server configuration

Before using this application you will need to edit `application.conf` and `securesocial.conf` files in the `conf/` directory

*application.conf*
- `uploadPath`, points to the path that will be used to store user-uploaded files
- `maxFileSize`, max allowed file size (set it to `0` to remove the limit)
- `maxFilesCount`, max number of uploaded files per user (set it to `0` to remove the limit)
- `maxClonotypesCount`, max number of rows in clonotype table (set it to `0` to remove the limit)
- `deleteAfter`, time after the cache is deleted (disabled if set to `0`)
- `application.secret`, the secret key used in cryptographic hash functions
- `db.default.*`, database configuration
- `smtp.*`, SMTP server configuration

In fact the configuration file is used only when the application is creating user's account. After that server is guided by your account's limits for uploading files. This is done in order to be able to specify different limits for different accounts. So if you change the default configuration it will not affect already created accounts.

*securesocial.conf*
- `smtp`, set up the SMTP server to send registration confirmation e-mails

## Database configuration
  The application uses [MySQL DBMS](http://www.mysql.com/) for handling metadata by default, if you want to change MySQL to another DBMS please see the [corresponding Play documentation section](https://www.playframework.com/documentation/2.2.4/JavaDatabase)
  - `db.default.user`, MySQL server username
  - `db.default.password`, MySQL server password
  <br>

After setting your login and password, create **vdjviz** database by typing `CREATE DATABASE vdjviz;` in MySQL console

##Dependency

  - [milib](https://github.com/milaboratory/milib)
  - [vdjtools](https://github.com/mikessh/vdjtools)

