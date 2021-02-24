package com.example.kafkaproducer;

import com.example.kafkaproducer.model.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api")
public class DemoController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, Profile> profileKafkaTemplate;

    @PostMapping("/demo")
    public Boolean sendMessage(@RequestBody Map<String, String> body) {
        String topic = body.get("topic");
        String message = body.get("message");
        sendMessage(topic, message);
        return true;
    }

    @PostMapping("/profile")
    public int sendProfile(@RequestBody Profile profile) {
        Random rand = new Random();
        int num = rand.nextInt(1000);
        int loginCount = rand.nextInt(10);
        profile.setUserId(num);
        profile.getUserName().setLoginCount(loginCount);
        profileKafkaTemplate.send("original_user_topic", profile);
        return num;
    }


    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public void sendMessage(String topicName, String message) {

        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });
    }
}
