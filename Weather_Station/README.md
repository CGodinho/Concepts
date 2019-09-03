# Weather Station


## Introduction

Using a Raspberry Pi to build a basic weather station to control clima conditions in a cellar.

Data is retreived from the sensor and stored in a relational database for further processing.

## Material

* Raspbery Pi 3 B+
* Weather sensor BME 280
* Cables
* Breadboard for simple connections
* Wood panel
* Door locker
* 2 x Corner door hinge
* Wood glue
* Nails


## Box

 A wooden box is build with 6 panels:
 
 * 2 x **40x30 cm** for the background and door;
 * 2 x **40x20 cm** for the sides;
 * 2 x **30x20 cm** for the top and bottom. 

![box](box.png)

Cut the wood according to dimensions. Glue and nail the panels together to create the box.

Apply the locker and the hinges.

Make at least 2 shelfs to better use the box.


## I2C

BME 280 weather sensor is controlled over IC2 serial interface.

Install the Raspberry PI I2C tools and the Python moduke for access.

Install tools to control ic2

```sudo apt-get install -y python-smbus i2c-tools```

Check if ic2 is enable

```lsmod | grep i2c_```

Detect devices and presents the port where the device is connected.

```i2cdetect -y 1```



## Maria DB

Maria DB is selected as the realational database.


### Install ###

```sudo apt-get install mariadb-server```

### Setup ####

Run the DB hardening where root password may b e changed

```sudo mysql_secure_installation```

Access DB

```Sudo mysql -u root -p```

Create User, DB and grant previledges


```
CREATE USER raspberry IDENTIFIED BY 'pwd';
CREATE DATABASE raspberry;
grant all privileges on raspberry.* TO 'raspberry'@'%' WITH GRANT OPTION;
flush privileges;
show DATABASES;
SHOW GRANTS FOR raspberry;
```

### Allow remote access

Go to:

```cd /etc/mysql/mariadb.conf.d```

Edit file:

```50-server.cnf```

comment line:

```bind-address           = 127.0.0.1```

Restart the DB.


### Managenent ###

Stop

```sudo systemctl stop mariadb.service```

Restart

```sudo systemctl restart mariadb.service``` 

Disable at boot time

```sudo systemctl disable mariadb.service```

Check Status

```sudo systemctl is-active mariadb.service``` 














# Connecting ...
mysql --user=raspberry --password=pwd raspberry




#######################

sudo pip3 install mysqlclient












Managenent

Brew install mycli



Drop table ‘measure’;

CREATE TABLE `measure` (
  `timestamp` datetime DEFAULT NULL,
  `temperature` float DEFAULT NULL,
  `humidity` float DEFAULT NULL,
  `pressure` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




## Conclusion

Thanks for the opportunity!
Hope you enjoy it as I did :+1: 

