package cn.hu.main.jms;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

public class TransactionListenerImpl implements TransactionListener {
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        System.out.println("=============executeLocalTransaction================");
        String body  =new String(msg.getBody());
        String keys = msg.getKeys();
        String transactionId = msg.getTransactionId();
        System.out.println("executeLocalTransaction>>>>>>>"+transactionId+">>>>>>>"+keys+">>>>>>>"+body+">>>>>"+arg);

        int status = Integer.parseInt(arg.toString());
        if(status==1){
            return LocalTransactionState.COMMIT_MESSAGE;
        }else if(status==2){
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }else if(status==3){
            return LocalTransactionState.UNKNOW;
        }
        return null;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        System.out.println("=============checkLocalTransaction================");
        String body  =new String(msg.getBody());
        String keys = msg.getKeys();
        String transactionId = msg.getTransactionId();
        System.out.println("checkLocalTransaction>>>>>"+transactionId+">>>>>>>"+keys+">>>>>>>"+body);
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
