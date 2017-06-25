package com.example.yongha.myapplication;

import android.content.Context; // 기본 import
import android.os.Bundle; // 기본 import
import android.os.Vibrator; // 휴대폰 진동에 대한 import
import android.support.v7.app.AppCompatActivity; // 기본 import
import android.util.Log; // log import
import android.view.View; // View import
import android.widget.Button; // 버튼 import
import android.widget.ImageView; // 이미지 View import
import android.widget.Toast; // 휴대폰 화면 텍스트 출력 improt

import org.eclipse.paho.android.service.MqttAndroidClient; // mqtt 라이브러리
import org.eclipse.paho.client.mqttv3.IMqttActionListener; // mqtt 라이브러리
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken; // mqtt 라이브러리
import org.eclipse.paho.client.mqttv3.IMqttToken; // mqtt 라이브러리
import org.eclipse.paho.client.mqttv3.MqttCallback; // mqtt 라이브러리
import org.eclipse.paho.client.mqttv3.MqttClient; // mqtt 라이브러리
import org.eclipse.paho.client.mqttv3.MqttConnectOptions; // mqtt 라이브러리
import org.eclipse.paho.client.mqttv3.MqttMessage; // mqtt 라이브러리

public class MainActivity extends AppCompatActivity{
    MqttAndroidClient client; // client 변수 선언
    String clientId = MqttClient.generateClientId(); // client 주소에 대한 변수 선언
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int[] i = {0}; // mqtt publish 값 저장 배열
        final int[] check = {0}; // 사용자가 이미지를 눌렀을 때 이미지 전환을 위한 배열
        final int[] list = {0}; // 사용자가 이미지를 눌렀을 때 이미지 전환을 위한 배열
        i[0] = 0; // 0 으로 초기화
        check[0] = 0; // 0 으로 초기화
        list[0] = 0; // 0 으로 초기화

        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); // 진동을 울리기 위한 이벤트 처리
        final Button mqttOn = (Button) findViewById(R.id.mqttOn); // mqttOn 버튼 이벤트 처리
        Button mqttOff = (Button) findViewById(R.id.mqttOff); // mqttOff 버튼 이벤트 처리
        final ImageView imageView = (ImageView) findViewById(R.id.imageView); // 메인 세탁기 이미지 이벤트 처리


        mqttOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) { // mqttOn 버튼을 사용자가 눌렀을 때에 관한 이벤트 처리
                Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_LONG).show(); // 연결 시도중이라는 메세지 전달

                try {
                    client = new MqttAndroidClient(getApplicationContext(), "tcp://218.150.182.15:1883", clientId); // 서버 IP 연결 변수

                    final MqttConnectOptions options = new MqttConnectOptions(); // mqtt 변수
                    options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1); // mqtt 변수에 버전 처리
                    client.setCallback(new MqttCallback() { // client Callback
                        @Override
                        public void connectionLost(Throwable cause) {}

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            i[0] = Integer.parseInt(message.toString()); // 라즈베리파이에서 publish 한 값 저장

                            if(list[0] > i[0] && check[0] == 1){ // 세탁이 끝날 경우 사용자에게 메시지와 진동알림
                                list[0] = 0;
                                check[0] = 0;

                                imageView.setImageResource(R.drawable.aaaa);
                                vibe.vibrate(1200); // 진동 알림.
                                Toast.makeText(getApplicationContext(), "세탁이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                            else{ // 세탁이 진행되는 과정에서 세탁의 상태를 출력
                                list[0] = i[0];
                                if(check[0] == 0){
                                    if(i[0] > 0 )
                                        imageView.setImageResource(R.drawable.bbbb);
                                    else
                                        imageView.setImageResource(R.drawable.aaaa);
                                }

                                else if(check[0] == 1){
                                    if(i[0] > 0 )
                                        imageView.setImageResource(R.drawable.dddd);
                                    else {
                                        imageView.setImageResource(R.drawable.cccc);
                                    }
                                }
                            }

                            imageView.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) { // 사용자가 내 세탁기로 선택하거나 선택해제 할대 이벤트 처리
                                    if(i[0] == 0){
                                        if(check[0] == 0) {
                                            check[0] = 1;
                                            imageView.setImageResource(R.drawable.cccc);
                                        }

                                        else if(check[0] == 1) {
                                            check[0] = 0;
                                            imageView.setImageResource(R.drawable.aaaa);
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });

/********************************************************* MQTT API ******************************************************/
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        String a = "abc";
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d(a, "onSuccess");
                            Toast.makeText(getApplicationContext(), "BrokerConnected", Toast.LENGTH_SHORT).show();
                            String topic = "machine1"; // machine1 이라는 topic을 구독
                            int qos = 1;
                            try {
                                IMqttToken subToken = client.subscribe(topic, qos);
                                subToken.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        // The message was published
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken, // 구독에 실패할 경우 출력
                                                          Throwable exception) {
                                        // The subscription could not be performed, maybe the user was not
                                        // authorized to subscribe on the specified topic e.g. using wildcards
                                        Toast.makeText(getApplicationContext(), "Fail Subscribe", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } catch (Exception e) { // 예외사항 에러메시지
                                e.printStackTrace();
                                Log.e("taga", e.toString());
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) { // 브로커 연결에 실패할 경우 다음과 같은 텍스트 출력
                            Log.d(a, "onFailure");
                            Toast.makeText(getApplicationContext(), "Fail BrokerConnected", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) { // 예외사항 에러메시지
                    e.printStackTrace();
                    Log.e("taga", e.toString());
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mqttOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // mqttOff 버튼을 눌렀을 경우 mqtt 브로커 연결을 해제
                Toast.makeText(getApplicationContext(), "disConnected", Toast.LENGTH_LONG).show(); // 연결해제 출력
                imageView.setImageResource(R.drawable.aaaa); // 기본 세탁기 화면 출력

                try {
                    client.disconnect();
                    client.close();
                } catch(Exception e){

                }
            }
        });
    }
}