import pymysql
import psutil

db=pymysql.connect("raspberrypi", "raspberry", "pwd", "raspberry")
cursor=db.cursor()
sql = "select * from measure where timestamp = (select max(timestamp) from measure)"
cursor.execute(sql)
result = cursor.fetchone()

f = open("/var/www/html/weather/index.html", "w")

f.write("<HTML>")
f.write("    <H1>Weather Station</H1>")
f.write("    <H2>Last Weather Record</H2>")
f.write("Time:        {} \n".format(result[0]))
f.write("<BR>")
f.write("Temperature: {} C. \n".format(result[1]))
f.write("<BR>")
f.write("Humidity:    {} % \n".format(result[2]))
f.write("<BR>")
f.write("Pressure:    {} kPa".format(result[3]))
f.write("<HR>")

today = str(result[0])[:10]
sql_today = "select max(temperature), min(temperature) from measure where timestamp >= '{} 00:00:00'".format(today)

cursor.execute(sql_today)
result_today = cursor.fetchone()

f.write("    <H2>Today's range</H2>")
f.write("Max Temperature: {:10.4f} C. \n".format(result_today[0]))
f.write("<BR>")
f.write("Min Temperature: {:10.4f} C. \n".format(result_today[1]))
f.write("<BR>")
f.write("<HR>")

f.write("    <H2>System status</H2>\n")
f.write(str(psutil.disk_usage('/')))
f.write("\n")
f.write("<BR>")
cpu_temp = str(psutil.sensors_temperatures())
splitted_cpu_temp = cpu_temp.split(",")
f.write(splitted_cpu_temp[1].replace("current=", "CPU temperature: ") + " C.")

f.write("    </BODY>")
f.write("</HTML>")
f.close()

quit()
