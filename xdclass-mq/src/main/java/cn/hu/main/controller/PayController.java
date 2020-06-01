package cn.hu.main.controller;

import cn.hu.main.domain.ProductOrder;
import cn.hu.main.jms.JmsConfig;
import cn.hu.main.jms.PayProducer;
import cn.hu.main.jms.TransactionProducer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class PayController {

    @Autowired
    private PayProducer payProducer;

    @Autowired
    private TransactionProducer transactionProducer;

    @RequestMapping("api/v1/pay_cb")
    public Object callback(String text) {
        Message message = new Message("hudaxian", "tag1", "777", ("hello " + text).getBytes());
        SendResult send = null;

        /**
         * 设置消息延迟发送级别。
         */
//        message.setDelayTimeLevel(3);
        try {
            /**
             * 同步方式发送
             */
            send= payProducer.getProducer().send(message);

            /**
             * 异步方式发送
             */
//            payProducer.getProducer().send(message, new SendCallback() {
//                @Override
//                public void onSuccess(SendResult sendResult) {
//                    System.out.println("异步发送请求结果>>>>>>>" + sendResult.toString());
//                }
//
//                @Override
//                public void onException(Throwable e) {
//                    e.printStackTrace();
//                }
//            });

            /**
             * oneWay方式发送消息
             */
//            payProducer.getProducer().sendOneway(message);


            /**
             * 消息发送到指定队列,同步方式
             */
//            SendResult send1 = payProducer.getProducer().send(message, new MessageQueueSelector() {
//                @Override
//                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
//                    int a = Integer.parseInt(arg.toString());
//                    return mqs.get(a);
//                }
//            }, 0);


            /**
             * 异步发送消息到指定队列
             */
//            payProducer.getProducer().send(message, new MessageQueueSelector() {
//                @Override
//                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
//                    int a = Integer.parseInt(arg.toString());
//                    return mqs.get(a);
//                }
//            }, 1, new SendCallback() {
//                @Override
//                public void onSuccess(SendResult sendResult) {
//                    System.out.println("异步发送到指定队列请求结果>>>>>>>" + sendResult.toString());
//                }
//
//                @Override
//                public void onException(Throwable e) {
//                    e.printStackTrace();
//                }
//            });
            System.out.println("PRODUCER>>>>>>>生产消息>>>>hello   " + text + ",当前时间>>>>" + new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return send;
    }


    @RequestMapping("/api/v2/pay_cb")
    public Object api2() {

        List list = ProductOrder.getProductOrderList();
        DefaultMQProducer producer = payProducer.getProducer();
        for (int i = 0; i < list.size(); i++) {
            ProductOrder po = (ProductOrder) list.get(i);

            Message message = new Message(JmsConfig.topic_order, "", po.getId() + "", po.toString().getBytes());
            try {
                SendResult send = producer.send(message, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        long a = (long) arg;
                        long b = a % mqs.size();
                        MessageQueue messageQueue = mqs.get((int) b);
                        return messageQueue;
                    }
                }, po.getId());
                System.out.println("PRODUCER>>>>>>>顺序生产消息>>>>hello   " + send.toString() + ">>>>>" + po.getId());
            } catch (MQClientException e) {
                e.printStackTrace();
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return new Date();
    }

    @RequestMapping("api/v3/pay_cb")
    public Object callback2(String tag) {
        Message message = new Message(JmsConfig.topic, tag, "777", ("hello " + tag).getBytes());
        SendResult send = null;
        try {
            send = payProducer.getProducer().send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("PRODUCER>>>>>>>生产消息>>>>hello   " + tag + ",当前时间>>>>" + new Date());
        return send;
    }


    /**
     * 分布式事务
     * @param tag
     * @return
     */
    @RequestMapping("api/v4/pay_cb")
    public Object callback3(String tag,int a) {
        Message message = new Message(JmsConfig.topic, tag, "777", ("hello " + tag).getBytes());
        SendResult send = null;
        try {
            send = transactionProducer.getProducer().sendMessageInTransaction(message,a);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PRODUCER>>>>>>>生产消息>>>>hello   " + tag + ",当前时间>>>>" + new Date());
        return send;
    }
}
