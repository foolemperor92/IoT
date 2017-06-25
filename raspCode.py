import paho.mqtt.client as mqtt # mqtt client import
import RPi.GPIO as gpio # GPIO import
import time # time import

gpio.setmode(gpio.BCM) # GPIO API
gpio.setup(13, gpio.IN, pull_up_down = gpio.PUD_DOWN) # GPIO 13번 사용
gpio.setup(19, gpio.IN, pull_up_down = gpio.PUD_DOWN) # GPIO 19번 사용
time.sleep(1)
            
def on_connect(client, userdata, flags, rc): # on_connect
    print("Connected with result code " + str(rc))

def on_publish(client, userdata, mid): # on_publish
    msg_id = mid
            
mqttc = mqtt.Client()
mqttc.on_connect = on_connect
mqttc.on_publish = on_publish

mqttc.connect("218.150.182.15") # 서버 IP mqtt 연결
mqttc.loop_start()

try:
    check1 = 0  # 진동 감지 변수
    check2 = 0  # 진동이 감지가 안된 변수
    while True:
        result1 = gpio.input(13) # 진동이 감지되면 0 대입 그렇지 않으면 1
        result2 = gpio.input(19)
        
        if result1 == 0 or result2 == 0: # 2개의 센서중에 하나라도 진동이 감지되면
            check2 = 0 # not frequency 변수에 0 대입
            time.sleep(0.05)
            print "y" # 라즈베리파이 커널에 진동감지 출력
            check1 += 1 # 진동 감지 변수에 1 대입
            if check1 > 0: # 진동감지 변수가 0보다 크다면
                var = str(1)
                (result, m_id) = mqttc.publish("test", var) # 1로 mqtt pulish
                check1 = 0 # 그리고 초기화
            
        else: # 2개의 센서가 진동을 감지 못하면
            time.sleep(0.05)
            print "n"
            check2 += 1 # not frequency에 1씩 카운트
            if check2 > 50 : # not frequency가 50이 넘으면
                var = str(0)
                (result, m_id) = mqttc.publish("test", var) # 0으로 mqtt pulish
                check2 = 0
                
except KeyboardInterrupt:
    print("Finished!")
    mqttc.loop_stop()
    mqttc.disconnect()
