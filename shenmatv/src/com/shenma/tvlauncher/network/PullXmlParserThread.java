package com.shenma.tvlauncher.network;

import android.util.Log;


/**
 * @class  PullXmlParserThread
 * @brief  XML异步线程解析类(Pull)。
 * @author joychang
 */
public class PullXmlParserThread extends BaseThread {
    /**
     * @brief     构造函数。
     * @author    joychang
     * @param[in] timeout  线程每次执行的间隔时间长度(ms)。
     * @param[in] callback 回调接口。
     * @param[in] url      XML的网络地址。
     */
    public PullXmlParserThread(long timeout,Boolean isaiqiyi,PullXmlParserCallback callback, String url) {
        super(timeout);

        Log.d(TAG, "PullXmlParserThread() start");

        mParser = new PullXmlParser(callback,isaiqiyi);
        mUrl = url;

        Log.d(TAG, "PullXmlParserThread() end");
    }
    /**
     * @brief     构造函数。
     * @author    joychang
     * @param[in] timeout  线程每次执行的间隔时间长度(ms)。
     * @param[in] callback 回调接口。
     * @param[in] url      XML的网络地址。
     */
    public PullXmlParserThread(long timeout,PullXmlParserCallback callback, String url) {
    	super(timeout);
    	
    	Log.d(TAG, "PullXmlParserThread() start");
    	
    	mParser = new PullXmlParser(callback);
    	mUrl = url;
    	
    	Log.d(TAG, "PullXmlParserThread() end");
    }

    /**
     * @brief  初始化函数。
     * @author joychang
     */
    @Override
    protected void _init() {
        Log.d(TAG, "_init() start");
        Log.d(TAG, "_init() end");
    }

    /**
     * @brief  完了化函数。
     * @author joychang
     */
    @Override
    protected void _finish() {
        Log.d(TAG, "_finish() start");
        Log.d(TAG, "_finish() end");
    }

    /**
     * @brief  执行函数。
     * @author joychang
     */
    @Override
    protected void _done() {
        Log.d(TAG, "_done() start");

        if (mUrl != null && mParser != null) {
            mParser.parser(mUrl);
        }

        this.stop();

        Log.d(TAG, "_done() end");
    }

    /**
     * @brief  线程中断处理函数。
     * @author joychang
     */
    @Override
    protected void _interrupted() {
        Log.d(TAG, "_interrupted() start");
        Log.d(TAG, "_interrupted() end");
    }

    /**
     * @brief XML解析器。
     */
    protected PullXmlParser mParser = null;

    /**
     * @brief XML文件的网络地址。
     */
    protected String mUrl = null;

    /**
     * @brief 日志标记。
     */
    protected static final String TAG = "PullXmlParserThread";
}
