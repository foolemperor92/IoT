package com.example.yongha.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity{
    MqttAndroidClient client;
    String clientId = MqttClient.generateClientId();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int[] i = {0};
        final int[] check = {0};
        final int[] list = {0};
        i[0] = 0;
        check[0] = 0;
        list[0] = 0;

        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final Button mqttOn = (Button) findViewById(R.id.mqttOn);
        Button mqttOff = (Button) findViewById(R.id.mqttOff);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);


        mqttOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_LONG).show();

                try {
                    client = new MqttAndroidClient(getApplicationContext(), "tcp://218.150.182.15:1883", clientId);

                    final MqttConnectOptions options = new MqttConnectOptions();
                    options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            i[0] = Integer.parseInt(message.toString());

                            if(list[0] > i[0] && check[0] == 1){
                                list[0] = 0;
                                check[0] = 0;

                                imageView.setImageResource(R.drawable.aaaa);
                                vibe.vibrate(1200); // 1000에 1초
                                Toast.makeText(getApplicationContext(), "세탁이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                            else{
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
                                public void onClick(View v) {
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
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        String a = "abc";
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d(a, "onSuccess");
                            Toast.makeText(getApplicationContext(), "BrokerConnected", Toast.LENGTH_SHORT).show();
                            String topic = "test";
                            int qos = 1;
                            try {
                                IMqttToken subToken = client.subscribe(topic, qos);
                                subToken.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        // The message was published
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken,
                                                          Throwable exception) {
                                        // The subscription could not be performed, maybe the user was not
                                        // authorized to subscribe on the specified topic e.g. using wildcards
                                        Toast.makeText(getApplicationContext(), "Fail Subscribe", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("taga", e.toString());
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d(a, "onFailure");
                            Toast.makeText(getApplicationContext(), "Fail BrokerConnected", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("taga", e.toString());
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mqttOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "disConnected", Toast.LENGTH_LONG).show();
                imageView.setImageResource(R.drawable.aaaa);

                try {
                    client.disconnect();
                    client.close();
                } catch(Exception e){

                }
            }
        });
    }



}