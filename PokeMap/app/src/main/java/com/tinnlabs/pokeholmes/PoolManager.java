package com.tinnlabs.pokeholmes;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by carlviar on 2016/07/29.
 */
public class PoolManager {

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 100;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 100;

    private static PoolManager instance;

    private final ThreadPoolExecutor mDownloadThreadPool;

    // A queue of Runnables for the image download pool
    private final BlockingQueue<Runnable> mDownloadWorkQueue;

    static {
        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        instance = new PoolManager();
    }

    private PoolManager(){

        /*
         * Creates a work queue for the pool of Thread objects used for downloading, using a linked
         * list queue that blocks when the queue is empty.
         */
        mDownloadWorkQueue = new LinkedBlockingQueue<>();

        /*
         * Creates a new pool of Thread objects for the download work queue
         */
        mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                                                     KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);

    }

    public static PoolManager getInstance(){

        if(instance != null){
            return  instance;
        }

        return new PoolManager();
    }

    public ThreadPoolExecutor getExecutor(){
        return mDownloadThreadPool;
    }

    public void add(Runnable runnable){
        mDownloadWorkQueue.add(runnable);
    }

    public void start(){

            Runnable runnable;

            while((runnable = instance.mDownloadWorkQueue.poll()) != null){

                instance.mDownloadThreadPool.execute(runnable);

            }

    }

}
