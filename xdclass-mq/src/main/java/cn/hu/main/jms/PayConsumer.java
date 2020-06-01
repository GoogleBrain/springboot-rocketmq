package cn.hu.main.jms;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.rmi.server.ExportException;
import java.util.Date;
import java.util.List;

//@Component
public class PayConsumer {

    private String consumerGroup = "pay_consumer_group";

    public DefaultMQPushConsumer consumer;

    public PayConsumer() throws MQClientException {
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(JmsConfig.nameServerAddr);
//        consumer.setMessageModel();
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe(JmsConfig.topic2, "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt msg = msgs.get(0);
                int   times= msg.getReconsumeTimes();
                try {
                    System.out.println("****************************************************************************");
                    System.out.println("重试次数>>>>>>>>>"+times);
                    System.out.printf("CONSUMER>>>>>>   %s Receive New Messages: %s %n", Thread.currentThread().getName(), new String(msgs.get(0).getBody())+",当前时间>>>>"+new Date());
                    String topic = msg.getTopic();
                    String body = null;
                    body = new String(msg.getBody(), "utf-8");
                    String tags = msg.getTags();
                    String keys = msg.getKeys();
                    System.out.println("CONSUMER>>>>>>topic=" + topic + ", tags=" + tags + ",keys = " + keys + ", msg = " + body);
//                    if(keys.equalsIgnoreCase("777")){
//                        throw  new Exception();
//                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    if(times>2){
                        System.out.println("已经超过最大重试次数,需要人工干预.................");
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });
        consumer.start();

        System.out.println("CONSUMER>>>>>>consumer start success");
    }
}
