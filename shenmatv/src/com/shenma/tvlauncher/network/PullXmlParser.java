package com.shenma.tvlauncher.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.shenma.tvlauncher.utils.Logger;

import android.util.Log;
import android.util.Xml;

/**
 * @class  PullXmlParser
 * @brief  XML解析类(Pull)。
 * @author joychang
 */
public class PullXmlParser {
    /**
     * @brief     构造函数。
     * @author    joychang
     * @param[in] callback 回调接口。
     */
    public PullXmlParser(PullXmlParserCallback callback) {
        Log.d(TAG, "PullXmlParser() start");

        mCallback = callback;
        
        Log.d(TAG, "PullXmlParser() end");
    }
    
    /**
     * @brief     构造函数。
     * @author    joychang
     * @param[in] callback 回调接口。
     */
    public PullXmlParser(PullXmlParserCallback callback , Boolean isaiqiyi) {
    	Log.d(TAG, "PullXmlParser() start");
    	
    	mCallback = callback;
    	this.isaiqiyi = isaiqiyi;
    	
    	Log.d(TAG, "PullXmlParser() end");
    }

    /**
     * @brief     XML解析函数。
     * @author    joychang
     * @param[in] url   XML网络地址。
     * @return    true  成功。
     * @return    false 失败。
     */
    public boolean parser(String url) {
        Log.d(TAG, "parser() start");

        boolean result = true;

        try {
            InputStream input = _load(url);
            if (input != null) {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(input, "UTF-8");
                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = parser.getName();
                    int attributeCount = parser.getAttributeCount();
                    String text = parser.getText();

                    Log.d(TAG, "------------------------------");

                    switch (parser.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.d(TAG, "START_DOCUMENT");

                        if (mCallback != null) {
                            mCallback.startDocument();
                        }
                        break;

                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "START_TAG");

                        if (mCallback != null) {
                            if ((nodeName != null) && (nodeName.length() > 0)) {
                                Map<String, String> attributes = new HashMap<String, String>();
                                for (int i=0; i<attributeCount; i++) {
                                    parser.getAttributeName(i);
                                    attributes.put(parser.getAttributeName(i), parser.getAttributeValue(i));
//                                	String label = parser.getAttributeValue(null, "label");
//                                	String list_src = parser.getAttributeValue(null, "list_src");
//                                	String date = parser.getAttributeValue(null, "date");
//                                	String src = parser.getAttributeValue(null, "src");
//                                	String name = parser.getAttributeValue(null, "name");
//                                	String link = parser.getAttributeValue(null, "link");
//                                	String duration = parser.getAttributeValue(null, "duration");
//                                	String version = parser.getAttributeValue(null, "version");
//                                	String description = parser.getAttributeValue(null, "description");
//                                  mCallback.startFlag(nodeName, label,list_src,date,src);
//                                  mCallback.startFlag(nodeName, name,link,duration,version,description);
                                }
                                mCallback.startFlag(nodeName, attributes);
                               
                            }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        Log.d(TAG, "TEXT");

                        if (mCallback != null) {
                            if ((text != null) && (text.length() > 0)) {
                                mCallback.text(text);
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "END_TAG");

                        if (mCallback != null) {
                            if ((nodeName != null) && (nodeName.length() > 0)) {
                                mCallback.endFlag(nodeName);
                            }
                        }
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        Log.d(TAG, "END_DOCUMENT");
                        break;

                    default:
                        break;
                    }

                  Log.d(TAG, "eventType: " + eventType);
                  Log.d(TAG, "nodeName: " + nodeName);
                  Log.d(TAG, "attributeCount: " + attributeCount);
                  Log.d(TAG, "text: " + text);
                  Log.d(TAG, "------------------------------");

                    eventType = parser.next();
                }

                if (mCallback != null) {
                    mCallback.endDocument();
                }

                input.close();
            }
            else {
                Log.e(TAG, "input stream is null!");
                
                if (mCallback != null) {
                    mCallback.haveError(PullXmlParserError.ERROR_URL);
                }

                result = false;
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
            result = false;
        }
        catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        Log.d(TAG, "parser() end");

        return result;
    }

    /**
     * @brief     加载网络文件函数。
     * @author    joychang
     * @param[in] url XML文件网络地址。
     * @return    XML文件数据流。
     */
    protected InputStream _load(String url) {
        Log.d(TAG, "_load() start");

        InputStream input = null;

        if (url != null) {
            try {
                URL connectUrl = new URL(url);
                if (connectUrl != null) {
            		Logger.d("joychang", "isaiqiyi=="+isaiqiyi);
            		//input = connectUrl.openStream();
                	if(isaiqiyi){
                		input = connectUrl.openStream();
                	}else{
                    	conn = (HttpURLConnection) connectUrl.openConnection();
                		conn.setRequestMethod("GET");
                		conn.setConnectTimeout(10 * 1000);
                		//input = conn.getInputStream();
                		conn.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko)");
                		if(302 == conn.getResponseCode()){
                			String uri = conn.getHeaderField("location");
                			URL mUrl = new URL(uri);
                          	conn = (HttpURLConnection) mUrl.openConnection();
                    		conn.setRequestMethod("GET");
                    		conn.setConnectTimeout(10 * 1000);
//                        	HttpURLConnection mconn = (HttpURLConnection) mUrl.openConnection();
//                        	mconn.setRequestMethod("GET");
//                        	mconn.setConnectTimeout(10 * 1000);
                			input = conn.getInputStream();
                		}else{
                			input = conn.getInputStream();
                		}
                	}
//                    //input = connectUrl.openStream();
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                input = null;
            }
            catch (IOException e) {
                e.printStackTrace();
                input = null;
            }
        }

        Log.d(TAG, "_load() end");

        return input;
    }

    /**
     * @brief 回调接口。
     */
    protected Boolean isaiqiyi = false;
    
    /**
     * @brief 回调接口。
     */
    protected PullXmlParserCallback mCallback = null;

    /**
     * @brief 日志标记。
     */
    protected static final String TAG = "PullXmlParser";

	private HttpURLConnection conn;
    
    
}
