package com.iteye.weimingtom.wce.util;

/**
 * jobQueueProcessorThread - 工作队列处理器线程 半自动单向的生产者-消费者模型 限制并行线程数为1的调度器（Scheduler）
 * 
 * @see QCServer
 * 
 */
public final class JobQueue {
	/**
	 * 单实例
	 */
	private static JobQueue instance;
	/**
	 * 工作队列处理器线程
	 */
	private Thread runner;

	// ------------------------------------------
	// 线程共享数据

	/**
	 * 动态增长的数组 TODO:可读写的线程共享数据
	 * 
	 * @see addJob
	 */
	private Runnable jobs[];
	/**
	 * TODO:只读的线程共享数据
	 */
	private int count;
	/**
	 * TODO:只读的线程共享数据
	 */
	private int processedJobCount;
	/**
	 * TODO:只读的线程共享数据
	 */
	private int waitedTimes;
	/**
	 * TODO:只读的线程共享数据
	 */
	private int state;
	/**
	 * TODO:只读的线程共享数据
	 */
	private Runnable currentJob;

	// ------------------------------------------

	/**
	 * 工作队列处理器线程
	 * 
	 * @author Administrator
	 * 
	 */
	public class Runner extends Thread {
		public Runner() {
			super("jobQueueProcessorThread");
		}

		@Override
		public void run() {
			while (runner == this) {
				state = 1;
				Runnable runnable = getNextJob();
				if (runnable == null) {
					continue;
				}
				if (runner != this)
					break;
				try {
					state = 2;
					currentJob = runnable;
					runnable.run();
					state = 0;
					currentJob = null;
					processedJobCount++;
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
			state = 0;
			runner = null;
		}
	}

	/**
	 * 获取下一个工作（消费者）
	 * 
	 * @return
	 */
	private Runnable getNextJob() {
		synchronized (this) {
			if (count <= 0)
				try {
					// 暂时归还锁，
					// 直至锁的新持有者使用notify把锁主动交还
					wait();
					waitedTimes++;
				} catch (InterruptedException interruptedexception) {
					//interruptedexception.printStackTrace();
				}
			if (count <= 0) {
				Runnable runnable1 = null;
				return runnable1;
			}
			Runnable runnable = jobs[0];
			for (int i = 1; i < count; i++) {
				jobs[i - 1] = jobs[i];
			}
			jobs[--count] = null;
			Runnable runnable2 = runnable;
			return runnable2;
		}
	}

	/**
	 * 当前工作
	 * 
	 * @return
	 */
	public Runnable getCurrentJob() {
		return currentJob;
	}

	/**
	 * 状态
	 * 
	 * @see Runner
	 * @return
	 */
	public int getState() {
		return state;
	}

	/**
	 * 工作数
	 * 
	 * @return
	 */
	public int getJobCount() {
		return count;
	}

	/**
	 * 等待时间
	 * 
	 * @return
	 */
	public int getWaitedTimes() {
		return waitedTimes;
	}

	/**
	 * 完成数
	 * 
	 * @return
	 */
	public int getCompletedJobCount() {
		return processedJobCount;
	}

	/**
	 * 添加工作（生产者） 然后用notify把控制权交还消费者
	 * 
	 * @param runnable
	 */
	public void addJob(Runnable runnable) {
		synchronized (this) {
			if (count >= jobs.length) {
				Runnable arunnable[] = new Runnable[count * 2];
				System.arraycopy(jobs, 0, arunnable, 0, count);
				jobs = arunnable;
			}
			jobs[count++] = runnable;
			notify();
		}
	}

	/**
	 * 启动线程
	 */
	public void start() {
		if (runner == null) {
			runner = new Runner();
			runner.setDaemon(true);
			runner.start();
		}
	}

	/**
	 * 向线程发送中断信号（生产者）， 然后用notify把控制权交还消费者
	 */
	public void stop() {
		if (runner != null) {
			runner.interrupt();
			runner = null;
		}
		synchronized (this) {
			for (int i = 0; i < count; i++) {
				jobs[i] = null;
			}
			count = 0;
			notify();
		}
	}

	/**
	 * 单实例
	 */
	private JobQueue() {
		jobs = new Runnable[1000];
	}

	/**
	 * 单实例
	 * 
	 * @return
	 */
	public static final JobQueue getInstance() {
		if (instance == null) {
			instance = new JobQueue();
			instance.start();
		}
		return instance;
	}

	/**
	 * 清除单实例
	 */
	public static final void stopInstance() {
		if (instance != null) {
			instance.stop();
		}
		instance = null;
	}
}
