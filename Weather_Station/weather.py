import smbus2
import pymysql
import bme280

port = 1
address = 0x76
bus = smbus2.SMBus(port)

calibration_params = bme280.load_calibration_params(bus, address)

data = bme280.sample(bus, address=0x76)

print(data)

db=pymysql.connect("localhost", "raspberry", "pwd", "raspberry")
cursor=db.cursor()

sql = """INSERT INTO measure(timestamp, temperature, humidity, pressure) VALUES (%s, %s, %s, %s)"""
recordTuple = (data.timestamp, data.temperature, data.humidity, data.pressure)
cursor.execute(sql, recordTuple)

db.commit()
db.close()

quit()