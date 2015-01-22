package com.shenma.tvlauncher.tvlive.parsexml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class PtopMainInterfaceParse {
	private PtoPMainInterface mPtoPMainInterface = null;
	private MainItem mMainItem = null;
	private InputStream inputStream = null;

	public PtoPMainInterface parseFileXml(Object xmlFile) {
		if (xmlFile == null)
			return null;

		if (xmlFile instanceof File) {
			File xmlFile2 = (File) xmlFile;

			if (xmlFile == null || !xmlFile2.exists()
					|| xmlFile2.getTotalSpace() <= 0) {
				return null;
			}

			try {
				inputStream = new FileInputStream(xmlFile2);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (xmlFile instanceof String) {
			String String1 = (String) xmlFile;
			inputStream = new ByteArrayInputStream(String1.getBytes());
		}

		try {

			XmlPullParser parser = Xml.newPullParser();

			mPtoPMainInterface = new PtoPMainInterface();

			parser.setInput(inputStream, "utf-8");

			int eventType = parser.getEventType();

			while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {

				switch (eventType) {
				case XmlPullParser.START_TAG:
					Log.e("handan", parser.getName());
					if ("m".equals(parser.getName())) {
						mMainItem = new MainItem();
					}
					if (mMainItem != null) {

						if ("list_name".equals(parser.getAttributeName(0))) {
							mMainItem.setList_name(parser.getAttributeValue(0));
						}
						if ("list_src".equals(parser.getAttributeName(1))) {
							mMainItem.setList_src(parser.getAttributeValue(1));
						}

					}

					break;
				case XmlPullParser.END_TAG:
					Log.e("handan11", parser.getName());
					if ("m".equals(parser.getName()) && mMainItem != null) {
						mPtoPMainInterface.getMainItems().add(mMainItem);
						mMainItem = null;
					}
					eventType = parser.next();
					break;
				default:
					break;
				}
			}
			inputStream.close();
			inputStream = null;

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return mPtoPMainInterface;
	}
}
