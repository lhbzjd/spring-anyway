package ink.anyway.component.tool.dcmqrscp.helper;

import ink.anyway.component.common.pojo.DicomInfo;

public interface ReceiveHandler {

    public void doReceive(String taskId, DicomInfo info);

}
