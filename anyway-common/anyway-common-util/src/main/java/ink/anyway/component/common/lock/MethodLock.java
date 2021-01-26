package ink.anyway.component.common.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 方法悲观锁，强制方法异步执行(Pessimistic Lock)
 * @version 1.0 2013-12-10 下午3:19:37
 * @author 李海博
 * @since v1.0
 */
public class MethodLock {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Object locker = new Object();

	private boolean flag = false;

	private boolean start = true;

	private long defaultTimeOut = 1000 * 60L;

	public MethodLock(long defaultTimeOut) {
		this.defaultTimeOut = defaultTimeOut;
	}

	/**
	 * 方法锁定时间
	 *
	 * @param timeout
	 */
	private void lock(long timeout) {
		try {
			synchronized (this.locker) {
				locker.wait(timeout);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void unLock() {
		locker.notify();
	}

	/** 解锁当前线程中执行的方法 */
	public void unLockCurrentThread() {
		this.flag = true;
		synchronized (this.locker) {
			this.unLock();
		}
		this.start = true;
	}

	/**
	 * 锁定当前线程中执行的方法
	 *
	 * @param timeout
	 *            :锁定超时时间
	 * @return -- true 正常返回 false 超时返回
	 */
	public boolean lockCurrentThread(long timeout) {
		this.flag = false;
		if (start) {
			this.start = false;
			logger.info("程序未处于锁定状态，开始执行...");
		} else {
			logger.info("程序处于锁定状态，等待锁定解除...");
			this.lock(timeout);
			this.start = false;
			if (flag) {
				logger.info("程序锁定已经被正常解除，开始执行...");
			} else {
				logger.warn("程序锁定时间超时，已解除锁定！");
			}
		}
		return flag;
	}

	/**
	 * 锁定当前线程中执行的方法
	 * @return boolean -- true 正常返回 false 超时返回
	 */
	public boolean lockCurrentThread() {
		this.flag = false;
		if (start) {
			this.start = false;
			logger.info("程序未处于锁定状态，开始执行...");
		} else {
			logger.info("程序处于锁定状态，等待锁定解除...");
			this.lock(this.defaultTimeOut);
			this.start = false;
			if (flag) {
				logger.info("程序锁定已经被正常解除，开始执行...");
			} else {
				logger.warn("程序锁定时间超时，已解除锁定！");
			}
		}
		return flag;
	}

	public void waitUnLock() {
		if (start) {
			logger.info("程序未处于锁定状态，开始执行...");
		} else {
			this.lock(this.defaultTimeOut);
			if (flag) {
				logger.info("程序锁定已经被正常解除，开始执行...");
			} else {
				logger.warn("程序锁定时间超时，已解除锁定！");
			}
		}
	}

}
