package ink.anyway.component.tool.dcmqrscp.subscribe;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import ink.anyway.component.common.engine.ThreadPoolEngine;
import ink.anyway.component.common.event.DcmQrReceiveEvent;
import ink.anyway.component.common.util.StringUtil;
import ink.anyway.component.tool.dcmqrscp.helper.HandleTaskCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@ConditionalOnProperty(prefix = "dicom.qr.scp.subscriber", name = "core-pool-size")
public class DcmQrReceiveSubscriber {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${dicom.qr.scp.subscriber.core-pool-size}")
    private int corePoolSize;

    @Value("${dicom.qr.scp.subscriber.maximum-pool-size}")
    private int maximumPoolSize;

    @Value("${dicom.qr.scp.subscriber.keep-alive-time}")
    private long keepAliveTime;

    @Value("${dicom.qr.scp.subscriber.work-queue-capacity}")
    private int workQueueCapacity;

    @Resource
    private EventBus eventBus;

    @Resource
    private ThreadPoolEngine threadPoolEngine;

    @Resource
    private HandleTaskCreator dcmReceiveTaskCreator;

    private String dcmReceivePoolId="pre.agent.dcm.scp.thread.pool";

    @PostConstruct
    public void init(){
        eventBus.register(this);
        if(this.threadPoolEngine.startGeneralThreadPool(dcmReceivePoolId, corePoolSize, maximumPoolSize, keepAliveTime,
                new ArrayBlockingQueue<Runnable>(workQueueCapacity), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy())==null){
            throw new BeanCreationException("重复启动线程池");
        }
    }

//    @AllowConcurrentEvents
    @Subscribe
    public void postTreatment(DcmQrReceiveEvent event) {
        logger.debug(StringUtil.compose("DCMQRSCP received dicom file, and post a event["+event.getEventId()+"] for InstanceUid["+event.getInfo().getInstanceUid()+"], at timestamp:["+event.getEventStartTimestamp()+"]"));
        /**
         *通俗的讲，任务进入线程池后，先往核心线程里放，装不下了就往队列里放，队列也放不下了，再往非核心线程里放，都放不下了就抛出异常。
         */
        this.threadPoolEngine.getThreadPool(dcmReceivePoolId).execute(dcmReceiveTaskCreator.create(event.getEventId(), event.getInfo()));
    }

}
