package com.shenma.tvlauncher.tvlive.parsexml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.shenma.tvlauncher.tvlive.network.LiveConstant;

import android.util.Log;
import android.util.Xml;

public class NetMediaXmlParse {
	private NetMediaCollection netMediaCollection = null;
	private NetMedia netMedia = null;
	private InputStream inputStream = null;
	private int classPostion;
	private int chanelPostion;
	private int positionNum;

	public NetMediaCollection parseFileXml(File xmlFile) {
		classPostion = 0;
		chanelPostion = 0;
		if (xmlFile == null || !xmlFile.exists()
				|| xmlFile.getTotalSpace() <= 0) {
			return null;
		}
		try {

			XmlPullParser parser = Xml.newPullParser();

			inputStream = new FileInputStream(xmlFile);

			netMediaCollection = new NetMediaCollection();

			parser.setInput(inputStream, "utf-8");

			int evtType = parser.getEventType();

			while ((evtType = parser.next()) != XmlPullParser.END_DOCUMENT) {

				switch (evtType) {
				case XmlPullParser.START_TAG:
					
					String nameSpaceStart = parser.getName();

					if ("class".equals(nameSpaceStart)) { //
						classPostion++;
						chanelPostion = 0;
						netMedia = new NetMedia();
						netMedia.level = LiveConstant.CHANNEL_CLASS;
						if (parser.getAttributeValue(null, "classname") != null) {
							netMedia.setChannleClass(parser.getAttributeValue(
									null, "classname"));
						}
					}
					if ("channel".equals(nameSpaceStart)) { // 
						chanelPostion++;
						positionNum++;
						netMedia = new NetMedia();
						netMedia.level = LiveConstant.CHANNEL_TYPE;
						if (parser.getAttributeValue(null, "epg") != null) {
							netMedia.setEpg(parser.getAttributeValue(null,
									"epg"));
						}
						if (parser.getAttributeValue(null, "name") != null) {
							netMedia.setChannlename(parser.getAttributeValue(
									null, "name"));
						}
					}
					if ("tvlink".equals(nameSpaceStart)) { // 
						netMedia = new NetMedia();
						netMedia.level = LiveConstant.TVLINK_TYPE;
						if (parser.getAttributeValue(null, "link") != null) {
							netMedia.setLink(parser.getAttributeValue(null,
									"link"));
						}
						if (parser.getAttributeValue(null, "source") != null) {
							netMedia.setSource(parser.getAttributeValue(null,
									"source"));
						}
					}

					if (netMedia != null) {
						if (netMedia.level == LiveConstant.CHANNEL_CLASS) {
							netMediaCollection.getNetMediaList().add(netMedia);
						}
						if (netMedia.level == LiveConstant.CHANNEL_TYPE
								&& classPostion > 0) {
							netMedia.setTotlePosition(positionNum);
							netMediaCollection.getNetMediaList()
									.get(classPostion - 1)
									.getChannlesArrayList().add(netMedia);
						}
						if (netMedia.level == LiveConstant.TVLINK_TYPE
								&& chanelPostion > 0) {
							netMediaCollection.getNetMediaList()
									.get(classPostion - 1)
									.getChannlesArrayList()
									.get(chanelPostion - 1)
									.getChannlesArrayList().add(netMedia);
						}
						netMedia = null;
					}
					evtType = parser.next();
					break;
				case XmlPullParser.END_TAG:
					break;
				default:
					break;
				}
			}
			inputStream.close();
			inputStream = null;

		} catch (XmlPullParserException e) {
		} catch (IOException e1) {
		}
		return netMediaCollection;
	}
}
