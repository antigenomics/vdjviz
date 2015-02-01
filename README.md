# VDJviz: a lightweight immune repertoire browser

This is a [Play Framework](https://www.playframework.com/) version 2.2.4 application. To execute it type
```
play run
```
from the project directory. A local server could be accessed at `localhost:9000`. 

The application uses [VDJtools](https://github.com/mikessh/vdjtools) API to compute various immune repertoire statistics and D3 JavaScript library for interactive graphs.

## Start configuration

Before using this application you need to edit application.conf and securesocial.conf files in the `conf/` directory

*application.conf*
- `uploadPath` variable that will be used to store user-uploaded files
- `maxFileSize`, set it to 0, if you don't want to limit max file size
- `maxFilesCount`, set it to 0, if you don't want to limit max files count
- `maxClonotypesCount`, set it to 0, if you don't want to limit max clonotypes count
- `deleteAfter`, set it to 0, if you don't want to delete cache files
- `application.secret`, the secret key is used to secure cryptographics functions
- `db.default.*`, set up the database configuration
- `smtp.*`, set up the smtp server

*securesocial.conf*
- `smtp`, set up the smtp server

## Database configuration
  The application uses [MySQL DBMS](http://www.mysql.com/) for handling meta data by default, if you want to change MySQL to another DBMS please see the [Play Documentation](https://www.playframework.com/documentation/2.2.4/JavaDatabase)
  - `db.default.user`, mysql server user
  - `db.default.password`, mysql server password
  <br>

After configuring your login and password you only need to create **vdjviz** database by typing `CREATE DATABASE vdjviz;` in mysql console

##Dependency

  - [milib](https://github.com/milaboratory/milib)
  - [vdjtools](https://github.com/mikessh/vdjtools)

