import paho.mqtt.client as mqtt
import RPi.GPIO as gpio
import time

gpio.setmode(gpio.BCM)
gpio.setup(13, gpio.IN, pull_up_down = gpio.PUD_DOWN)
gpio.setup(19, gpio.IN, pull_up_down = gpio.PUD_DOWN)
time.sleep(1)
            
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))

def on_publish(client, userdata, mid):
    msg_id = mid
            
mqttc = mqtt.Client()
mqttc.on_connect = on_connect
mqttc.on_publish = on_publish

mqttc.connect("218.150.182.15")
mqttc.loop_start()

try:
    check1 = 0
    check2 = 0
    while True:
        result1 = gpio.input(13)
        result2 = gpio.input(19)
        
        if result1 == 0 or result2 == 0:
            check2 = 0
            time.sleep(0.05)
            print "y"
            check1 += 1
            if check1 > 0:
                var = str(1)
                (result, m_id) = mqttc.publish("test", var)
                check1 = 0
            
        else:
            time.sleep(0.05)
            print "n"
            check2 += 1
            if check2 > 50 :
                var = str(0)
                (result, m_id) = mqttc.publish("test", var)
                check2 = 0
                
except KeyboardInterrupt:
    print("Finished!")
    mqttc.loop_stop()
    mqttc.disconnect()
