package ink.anyway.component.tool.dcmqrscp.starter;

import com.google.common.eventbus.EventBus;
import ink.anyway.component.tool.dcmqrscp.helper.ReceiveHandler;
import ink.anyway.component.tool.dcmqrscp.implement.DcmQRSCP;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@ConditionalOnProperty(prefix = "dicom.qr.scp", name = "bind")
public class QrScpStarter implements InitializingBean {

    @Value("${dicom.qr.scp.bind}")
    private String bind;

    @Value("${dicom.qr.scp.ae.config}")
    private String aeProPath;

    @Value("${dicom.qr.scp.file}")
    private String dicomDirFile;

    @Resource
    private EventBus eventBus;

    @Autowired
    private ReceiveHandler receiveHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            DcmQRSCP main = new DcmQRSCP(eventBus, receiveHandler, new String[]{"--ae-config", aeProPath, "--all-storage", "-b", bind, "--dicomdir", dicomDirFile});
            main.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
