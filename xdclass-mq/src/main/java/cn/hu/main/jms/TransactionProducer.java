package cn.hu.main.jms;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class TransactionProducer {
    private String producerGroup="trans_pay_producer_group";

    private TransactionMQProducer producer;

    private TransactionListener listener = new TransactionListenerImpl();

    private  ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("client-transaction-msg-check-thread");
            return thread;
        }
    });

    public TransactionProducer(){
        this.producer = new TransactionMQProducer(producerGroup);
        producer.setNamesrvAddr(JmsConfig.nameServerAddr);
        producer.setTransactionListener(listener);
        producer.setExecutorService(executorService);
        start();
    }

    public TransactionMQProducer getProducer(){
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
