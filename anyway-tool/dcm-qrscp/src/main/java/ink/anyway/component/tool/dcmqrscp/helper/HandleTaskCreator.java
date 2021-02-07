package ink.anyway.component.tool.dcmqrscp.helper;

public abstract class HandleTaskCreator {

    public abstract Runnable create(String taskId, Object info);
}
