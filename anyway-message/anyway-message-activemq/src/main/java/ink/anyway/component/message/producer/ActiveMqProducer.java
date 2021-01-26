package ink.anyway.component.message.producer;

import ink.anyway.component.message.exception.MethodNotSupportException;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ActiveMqProducer implements MessageProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostConstruct
    public void initMethod(){
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("msgClassType");
        jmsTemplate.setMessageConverter(messageConverter);
        // 设置JMS的持久性,持久化为文件
//        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    @Override
    public void convertAndSend(String destination, Object message) {
        ActiveMQQueue targetQueue = new ActiveMQQueue(destination);
        jmsTemplate.convertAndSend(targetQueue, message);
    }

    @Override
    public void convertAndSend(String exchange, String routingKey, Object message) {
        throw new MethodNotSupportException("Activemq not support exchange send! ");
    }
}
