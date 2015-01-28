# VDJviz: a lightweight immune repertoire browser

This is a [Play Framework](https://www.playframework.com/) version 2.2.4 application. To execute it type
```
play run
```
from the project directory. A local server could be accessed at `localhost:9000`. 

The application uses created using [VDJtools](https://github.com/mikessh/vdjtools) API to compute various immune repertoire statistics.

## Configuration

Before using this application you need to edit application.conf file in the `conf/` directory

- Specify the `uploadPath` variable that will be used to store user-uploaded files
- Add application secret key
- Set up the database configuration

