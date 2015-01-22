package com.shenma.tvlauncher.network;

import java.util.Map;

/**
 * @class  PullXmlParser
 * @brief  XML解析类(Pull)回调接口。
 * @author joychang
 */
public interface PullXmlParserCallback {
    /**
     * @brief  文档解析开始函数。
     * @author joychang
     */
    public void startDocument();

    /**
     * @brief     节点解析开始函数。
     * @author    joychang
     * @param[in] nodeName  节点名称。
     * @param[in] attribute 节点属性。
     */
    public void startFlag(String nodeName, Map<String, String> attribute);
//    public void startFlag(String nodeName, String label,String list_src,String date,String src);
//    
//    
//    public void startFlag(String nodeName,String name,String link,String duration,String version,String description);

    /**
     * @brief     节点解析开始函数。
     * @author    joychang
     * @param[in] nodeName  节点名称。
     * @param[in] attribute 节点属性。
     */
    //public void startFlag(String nodeName, Map<String, String> attribute);
    /**
     * @brief     节点解析结束函数。
     * @author    joychang
     * @param[in] nodeName 节点名称。
     */
    public void endFlag(String nodeName);

    /**
     * @brief     节点文本函数。
     * @author    joychang
     * @param[in] text 节点文本。
     */
    public void text(String text);

    /**
     * @brief  文档解析结束函数。
     * @author joychang
     */
    public void endDocument();

    /**
     * @brief     文档解析结束函数。
     * @author    joychang
     * @param[in] error 错误值。
     */
    public void haveError(PullXmlParserError error);
}
