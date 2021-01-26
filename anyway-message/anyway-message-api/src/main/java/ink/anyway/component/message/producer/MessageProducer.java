package ink.anyway.component.message.producer;

public interface MessageProducer {

    public void convertAndSend(String destination, Object message);

    public void convertAndSend(String exchange, String routingKey, Object message);

}
