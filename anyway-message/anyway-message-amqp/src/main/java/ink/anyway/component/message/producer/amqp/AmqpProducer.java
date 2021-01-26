package ink.anyway.component.message.producer.amqp;

import ink.anyway.component.message.producer.MessageProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AmqpProducer implements MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageConverter messageConverter;

    @PostConstruct
    public void initMethod(){
        rabbitTemplate.setMessageConverter(messageConverter);
    }

    @Override
    public void convertAndSend(String destination, Object message) {
        rabbitTemplate.convertAndSend(destination, message);
    }

    public void convertAndSend(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
