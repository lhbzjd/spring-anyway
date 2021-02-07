package ink.anyway.component.tool.dcmqrscu.pojo;

import lombok.Data;

import java.io.File;

@Data
public class FileToSend {

    private String iuid;
    private String cuid;
    private String ts;
    private long dsPos;
    private File toSend;

}
