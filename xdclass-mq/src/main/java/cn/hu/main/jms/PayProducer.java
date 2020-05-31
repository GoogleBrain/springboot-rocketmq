package cn.hu.main.jms;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.stereotype.Component;

@Component
public class PayProducer {
    private String producerGroup="pay_producer_group";



    private DefaultMQProducer producer;

    public PayProducer(){
        this.producer = new DefaultMQProducer(producerGroup);
        producer.setRetryTimesWhenSendAsyncFailed(0);
        producer.setNamesrvAddr(JmsConfig.nameServerAddr);
        start();
    }

    public DefaultMQProducer getProducer(){
        return  this.producer;
    }

    public void start(){
        try {
            this.producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        this.producer.shutdown();
    }
}
