package cn.hu.main.jms;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PayOrderConsumer {

    private String consumerGroup = "pay_order_consumer_group";

    public DefaultMQPushConsumer consumer;

    public PayOrderConsumer() throws MQClientException {
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(JmsConfig.nameServerAddr);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe(JmsConfig.topic_order, "*");
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext consumeConcurrentlyContext) {
                MessageExt msg = msgs.get(0);
                int   times= msg.getReconsumeTimes();
                try {
                    String topic = msg.getTopic();
                    String body = new String(msg.getBody(), "utf-8");
                    String tags = msg.getTags();
                    String keys = msg.getKeys();
                    System.out.println("顺序消费1>>>>>>>>>>>>>>>>>>>>>>>CONSUMER>>>>>>topic=" + topic + ", tags=" + tags + ",keys = " + keys + ", msg = " + body);
                    return ConsumeOrderlyStatus.SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    if(times>2){
                        System.out.println("顺序消费1>>>>>>>>>>>>>>>>>>>>>>>已经超过最大重试次数,需要人工干预.................");
                        return ConsumeOrderlyStatus.SUCCESS;
                    }
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            }
        });
        consumer.start();

        System.out.println("顺序消费1>>>>>>>>>>>>>>>>>>>>>>>CONSUMER>>>>>>consumer start success");
    }
}
