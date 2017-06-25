import paho.mqtt.client as mqtt # mqtt client import
import RPi.GPIO as gpio # GPIO import
import time # time import

gpio.setmode(gpio.BCM) # GPIO API
gpio.setup(13, gpio.IN, pull_up_down = gpio.PUD_DOWN) # GPIO 13�� ���
gpio.setup(19, gpio.IN, pull_up_down = gpio.PUD_DOWN) # GPIO 19�� ���
time.sleep(1)
            
def on_connect(client, userdata, flags, rc): # on_connect
    print("Connected with result code " + str(rc))

def on_publish(client, userdata, mid): # on_publish
    msg_id = mid
            
mqttc = mqtt.Client()
mqttc.on_connect = on_connect
mqttc.on_publish = on_publish

mqttc.connect("218.150.182.15") # ���� IP mqtt ����
mqttc.loop_start()

try:
    check1 = 0  # ���� ���� ����
    check2 = 0  # ������ ������ �ȵ� ����
    while True:
        result1 = gpio.input(13) # ������ �����Ǹ� 0 ���� �׷��� ������ 1
        result2 = gpio.input(19)
        
        if result1 == 0 or result2 == 0: # 2���� �����߿� �ϳ��� ������ �����Ǹ�
            check2 = 0 # not frequency ������ 0 ����
            time.sleep(0.05)
            print "y" # ��������� Ŀ�ο� �������� ���
            check1 += 1 # ���� ���� ������ 1 ����
            if check1 > 0: # �������� ������ 0���� ũ�ٸ�
                var = str(1)
                (result, m_id) = mqttc.publish("test", var) # 1�� mqtt pulish
                check1 = 0 # �׸��� �ʱ�ȭ
            
        else: # 2���� ������ ������ ���� ���ϸ�
            time.sleep(0.05)
            print "n"
            check2 += 1 # not frequency�� 1�� ī��Ʈ
            if check2 > 50 : # not frequency�� 50�� ������
                var = str(0)
                (result, m_id) = mqttc.publish("test", var) # 0���� mqtt pulish
                check2 = 0
                
except KeyboardInterrupt:
    print("Finished!")
    mqttc.loop_stop()
    mqttc.disconnect()
