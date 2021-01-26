package ink.anyway.component.common.engine;

import ink.anyway.component.common.engine.exception.ThreadPoolEngineExistsException;
import ink.anyway.component.common.lock.MethodLock;
import ink.anyway.component.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 通用服务器线程池引擎。
 *
 * @author Lhb
 */
public class ThreadPoolEngine implements DisposableBean {

    private static boolean ENGINE_EXISTS = false;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ThreadPoolEngine() {
        super();
    }

    public static synchronized ThreadPoolEngine createEngine(){
        if(ENGINE_EXISTS)
            throw new ThreadPoolEngineExistsException("application have a ThreadPoolEngine exists, please check...");
        ENGINE_EXISTS = true;
        return new ThreadPoolEngine();
    }

    /**
     * 保存所有线程池对象。
     */
    private Map<String, ExecutorService> threadPoolMap = new HashMap<>();

    private MethodLock lock = new MethodLock(5 * 60 * 1000L);

    /**
     * execute新任务时执行的策略
     * 1.线程数量未达到corePoolSize，则新建一个线程(核心线程)执行任务
     * 2.线程数量达到了corePoolSize，则将任务移入workQueue队列等待。workQueue队列的最大容量capacity，在传入workQueue参数时为其指定
     * 3.workQueue队列已满，新建线程(非核心线程)执行任务
     * 4.workQueue队列已满，总线程数(核心线程+非核心线程)又达到了maximumPoolSize，就会由(RejectedExecutionHandler)抛出异常
     */
    public ExecutorService startGeneralThreadPool(String poolId, int corePoolSize,
                                           int maximumPoolSize,
                                           long keepAliveTime,
                                           BlockingQueue<Runnable> workQueue,
                                           ThreadFactory threadFactory,
                                           RejectedExecutionHandler handler){
        lock.lockCurrentThread();
        if(this.threadPoolMap.get(poolId)!=null&&!this.threadPoolMap.get(poolId).isShutdown()){
            lock.unLockCurrentThread();
            return null;
        }
        this.threadPoolMap.put(poolId, new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory, handler));
        lock.unLockCurrentThread();
        return this.threadPoolMap.get(poolId);
    }

    public ExecutorService startCachedThreadPool(String poolId){
        lock.lockCurrentThread();
        if(this.threadPoolMap.get(poolId)!=null&&!this.threadPoolMap.get(poolId).isShutdown()){
            lock.unLockCurrentThread();
            return null;
        }
        this.threadPoolMap.put(poolId, Executors.newCachedThreadPool());
        lock.unLockCurrentThread();
        return this.threadPoolMap.get(poolId);
    }

    public ExecutorService startFixedThreadPool(String poolId, int corePoolSize){
        lock.lockCurrentThread();
        if(this.threadPoolMap.get(poolId)!=null&&!this.threadPoolMap.get(poolId).isShutdown()){
            lock.unLockCurrentThread();
            return null;
        }
        this.threadPoolMap.put(poolId, Executors.newFixedThreadPool(corePoolSize));
        lock.unLockCurrentThread();
        return this.threadPoolMap.get(poolId);
    }

    public ExecutorService startScheduledThreadPool(String poolId, int corePoolSize){
        lock.lockCurrentThread();
        if(this.threadPoolMap.get(poolId)!=null&&!this.threadPoolMap.get(poolId).isShutdown()){
            lock.unLockCurrentThread();
            return null;
        }
        this.threadPoolMap.put(poolId, Executors.newScheduledThreadPool(corePoolSize));
        lock.unLockCurrentThread();
        return this.threadPoolMap.get(poolId);
    }

    public ExecutorService startSingleThreadExecutor(String poolId){
        lock.lockCurrentThread();
        if(this.threadPoolMap.get(poolId)!=null&&!this.threadPoolMap.get(poolId).isShutdown()){
            lock.unLockCurrentThread();
            return null;
        }
        this.threadPoolMap.put(poolId, Executors.newSingleThreadExecutor());
        lock.unLockCurrentThread();
        return this.threadPoolMap.get(poolId);
    }

    public ExecutorService getThreadPool(String poolId){
        return this.threadPoolMap.get(poolId);
    }

    public synchronized boolean stopThreadPool(String poolId){
        lock.lockCurrentThread();
        if(this.threadPoolMap.get(poolId)!=null&&!this.threadPoolMap.get(poolId).isShutdown()){
            this.threadPoolMap.get(poolId).shutdown();
            this.threadPoolMap.remove(poolId);
            logger.info(StringUtil.compose("thread pool [", poolId, "] have stopped."));
            lock.unLockCurrentThread();
            return true;
        }
        lock.unLockCurrentThread();
        return false;
    }

    public synchronized void stopAllThreadPool(){
        lock.lockCurrentThread();
        List<String> poolIdL = new ArrayList<>();
        for(String poolId:this.threadPoolMap.keySet()){
            if(this.threadPoolMap.get(poolId)!=null&&!this.threadPoolMap.get(poolId).isShutdown()){
                this.threadPoolMap.get(poolId).shutdown();
                poolIdL.add("["+poolId+"]");
            }
        }
        this.threadPoolMap.clear();
        logger.info(StringUtil.compose("all thread pool {", StringUtil.composeWithRegex(poolIdL, ", "), "} in engine have stopped."));
        lock.unLockCurrentThread();
    }

    @Override
    public void destroy() throws Exception {
        this.stopAllThreadPool();
    }
}
