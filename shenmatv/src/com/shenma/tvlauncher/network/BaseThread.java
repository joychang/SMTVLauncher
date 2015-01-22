package com.shenma.tvlauncher.network;

import android.util.Log;

/**
 * @class  BaseThread
 * @brief  线程基类。
 * @author joychang
 * @note   线程处理的基础定义。
 * @remark 由于本类是抽象类，所以不能实例化，需要派生使用。
 */
public abstract class BaseThread {
    /**
     * @brief     构造函数。
     * @author    joychang
     * @param[in] timeout 线程每次执行的间隔时间长度(ms)。
     */
    public BaseThread(long timeout) {
        Log.d(TAG, "BaseThread() start");

        mTimeout = timeout;

        mMainThread = new Thread() {
            /**
             * @brief  运行函数。
             * @author joychang
             * @note   指定间隔时间内调用执行_done()函数。
             */
            public void run() {
                Log.d(TAG, "run() start");

                try {
                    while(mState == BaseThread.STATE.RUN) {
                        //执行处理
                        _done();
                        //sleep处理
                        if (mTimeout > 0) {
                            sleep(mTimeout);
                        }
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();

                    _interrupted();

                    //停止循环
                    mState = BaseThread.STATE.ERROR;
                }

                Log.d(TAG, "run() end");
            }
        };

        mState = BaseThread.STATE.INIT;

        _init();

        Log.d(TAG, "BaseThread() end");
    }

    /**
     * @brief  启动运行函数。
     * @author joychang
     * @return true  成功。
     * @return false 失败(线程状态并非初始化状态时，停止线程将失败)。
     * @note   该函数首先调用初始化_init()函数，然后启动实际线程。当指定0(ms)时代表没有时间间隔。
     */
    public boolean start() {
        Log.d(TAG, "start() start");

        boolean result =false;

        if (mState == BaseThread.STATE.INIT) {
            mState = BaseThread.STATE.RUN;
            mMainThread.start();

            result = true;
        }
        else {
            Log.e(TAG, "start(): state is not init.");
        }

        Log.d(TAG, "start() end");

        return result;
    }

    /**
     * @brief  设置线程优先级函数。
     * @author joychang
     * @param[in] priority 线程优先级。
     */
    public void setPriority(int priority) {
        Log.d(TAG, "setPriority() start");

        if (mMainThread != null) {
            mMainThread.setPriority(priority);
        }

        Log.d(TAG, "setPriority() end");
    }

    /**
     * @brief  停止运行函数。
     * @author joychang
     * @return true  成功。
     * @return false 失败(线程状态并非运行状态或者错误状态时，停止线程将失败)。
     * @note   通过设定标志位，使线程结束处理，并且调用完了化_finish()函数。
     */
    public boolean stop() {
        Log.d(TAG, "stop() start");

        boolean result =false;

        if ((mState == BaseThread.STATE.RUN) || (mState == BaseThread.STATE.ERROR)) {
            mState = BaseThread.STATE.FINISH;
            _finish();

            result = true;
        }
        else {
            Log.e(TAG, "start(): state is not run or error.");
        }

        Log.d(TAG, "stop() end");

        return result;
    }

    /**
     * @brief  中断运行函数。
     * @author joychang
     * @note   通过调用API强制线程停止。
     */
    public void interrupt() {
        Log.d(TAG, "interrupt() start");

        if ((mMainThread.isInterrupted() != true)
            && (mMainThread.interrupted() != true)) {
            mMainThread.interrupt();
        }

        Log.d(TAG, "interrupt() end");
    }

    /**
     * @brief  取得线程状态函数。
     * @author joychang
     * @return INIT   初始化状态。
     * @return RUN    运行中状态。
     * @return FINISH 完了化状态。
     * @return ERROR  错误状态。
     */
    public STATE getState() {
        Log.d(TAG, "stop() start");
        Log.d(TAG, "stop() end");
        return mState;
    }

    /**
     * @brief  初始化函数。
     * @author joychang
     * @note   派生类可以通过重载该函数完成自己的一些线程执行前的初始化处理。
     */
    protected abstract void _init();

    /**
     * @brief  完了化函数。
     * @author joychang
     * @note   派生类可以通过重载该函数完成自己的一些线程执行后的完了化处理。
     */
    protected abstract void _finish();

    /**
     * @brief  执行函数。
     * @author joychang
     * @note   派生类可以通过重载该函数完成自己的一些线程执行的处理。
     */
    protected abstract void _done();

    /**
     * @brief  线程中断处理函数。
     * @author joychang
     * @note   派生类可以通过重载该函数完成自己的一些线程中断的处理。
     */
    protected abstract void _interrupted();

    /**
     * @brief 系统线程句柄。
     */
    protected Thread mMainThread = null;

    /**
     * @brief 运行状态标志。
     */
    protected STATE mState = BaseThread.STATE.INIT;

    /**
     * @brief 每次执行时间间隔。
     */
    protected long mTimeout = 0;

    /**
     * @brief 日志标记。
     */
    protected static final String TAG = "BaseThread";

    /**
     * @enum   STATE
     * @brief  线程状态。
     * @author joychang
     */
    public enum STATE {
        /**
         * @breif 初始化。
         */
        INIT,

        /**
         * @breif 运行。
         */
        RUN,

        /**
         * @breif 完了化。
         */
        FINISH,

        /**
         * @breif 错误。
         */
        ERROR
    };
}
