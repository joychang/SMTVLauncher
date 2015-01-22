package com.shenma.tvlauncher.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sunniwell.android.httpserver.fileupload.SWFileUpload;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class UploadFileHandler implements HttpRequestHandler {
	private Handler mHandler;
//	private String filePath = Constant.PUBLIC_DIR;
	private HttpResponse response;
	private String fileName = "";
	public static boolean isInstall;
	public int MaxWaitTime = 60;
	public Context mContext;
	
	public UploadFileHandler(Handler handler,Context context) {
		this.mHandler = handler;
		this.mContext = context;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		String target = request.getRequestLine().getUri();
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		this.response = response;
		if(!"GET".equals(method) && !"HEAD".equals(method) && !"POST".equals(method)) {
			throw new MethodNotSupportedException("this method name " + method + "not supported");
		}
		//处理文件上传
		if(request instanceof HttpEntityEnclosingRequest) {
			HttpEntityEnclosingRequest req = (HttpEntityEnclosingRequest)request;
			processHttpEntityEnclosingRequest(req);
		}
	}
	
	private void processHttpEntityEnclosingRequest(HttpEntityEnclosingRequest request) throws IOException{
		if(SWFileUpload.isMultipartContent(request)) {
			processMultipartContentRequest(request);
		}
	}
	
	private void processMultipartContentRequest(HttpEntityEnclosingRequest request) throws IOException{
		SWFileUpload upload = new SWFileUpload();
		try {
			FileItemIterator iter = upload.getItemIterator(request);
			while(iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream stream = item.openStream();
				if(item.isFormField()) {
					Logger.d("zhouchuan", "Form field " + name + " with value " + Streams.asString(stream) + " detected.");
				}else {
					Logger.d("zhouchuan", "File field " + name + " with file name " + item.getName() + " detected.");
					try {
						InputStream in = stream;
						
						File f = mContext.getCacheDir();
						fileName = item.getName();
						File file = new File(mContext.getFilesDir(), fileName);
						if(file.exists()) {
							file.delete();
						}
						JSONObject json = new JSONObject();
						Logger.d("zhouchuan", "-------processMultipartContentRequest------------------------f=" + f);
						if(f.isDirectory()) {
//							OutputStream out = new FileOutputStream(filePath + fileName);
							OutputStream out = mContext.openFileOutput(fileName, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
							long fileSize = Streams.copy(in, out, false);
							Logger.d("zhouchuan", "------------------fileSize=" + fileSize);
							if(fileSize > 0) {
								if (this.validateZipFile(file.getAbsolutePath())) {
									Logger.d("zhouchuan", "------processMultipartContentRequest--------1---------------------------------");
									//下载apk成功发消息安装apk
									Message msg = this.mHandler.obtainMessage();
									msg.what = RemoteServer.REMOTE_INSTALLATION_START_INSTALL;
									msg.obj = mContext.getFilesDir().getAbsolutePath()+File.separator+fileName;
									this.mHandler.sendMessageDelayed(msg, 1000);
									//让线程进入循环等待，等待安装结果 然后给用户响应，如果等待时间达到上限则停止等待
									isInstall = true;
									int waitTime = 0;
									while (isInstall && waitTime < MaxWaitTime) {
										Logger.d("zhouchuan", "----------正在安装稍等-----------");
										Thread.sleep(1000);
										waitTime++;
									}
									//达到等待最大时间则响应安装失败
									if(waitTime < MaxWaitTime) {
										return;
									}else {
										json.put("installation", false);
									}
								}else {
									Logger.d("zhouchuan", "------processMultipartContentRequest--------2---------------------------------");
									json.put("success", false);
								}
							}else {
								Logger.d("zhouchuan", "------processMultipartContentRequest--------3---------------------------------");
								json.put("success", false);
							}
						}else {
							Logger.d("zhouchuan", "------processMultipartContentRequest--------4---------------------------------");
							json.put("success", false);
						}
						writeToHTML(json.toString());
					} catch (Exception e) {
						JSONObject json = new JSONObject();
						try {
							Logger.d("zhouchuan", "------processMultipartContentRequest--------5---------------------------------");
							json.put("success", false);
						} catch (Exception e2) {
						}
						writeToHTML(json.toString());
					}
				}
			}
		} catch (Exception e) {
			JSONObject json = new JSONObject();
			try {
				Logger.d("zhouchuan", "------processMultipartContentRequest--------6---------------------------------");
				json.put("success", false);
			} catch (Exception e2) {
			}
			writeToHTML(json.toString());
		}
	}
	
	public void writeToHTML(final String returnStr) throws IOException {
		EntityTemplate body = new EntityTemplate(new ContentProducer() {
			public void writeTo(final OutputStream outstream) throws IOException {
				OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
				writer.write(returnStr);
				writer.flush();
			}
		});
		body.setContentType("text/html; charset=UTF-8");
		this.response.setEntity(body);
	}
	
	/**
	 * 校验ZIP文件的CRC
	 * 
	 * @param filename
	 */
	private boolean validateZipFile(String filename) {
		boolean isIntegrity = true;
		ZipFile zf = null;
		try {
			zf = new ZipFile(filename);
			byte[] buf = new byte[1024];
			Enumeration<ZipEntry> entryies = (Enumeration<ZipEntry>) zf.entries();
			while (entryies.hasMoreElements()) {
				ZipEntry entry = entryies.nextElement();
				if (!entry.isDirectory()) {
					CRC32 crc32 = new CRC32();
					InputStream is = zf.getInputStream(entry);
					int reads = 0;
					while ((reads = is.read(buf)) > 0) {
						crc32.update(buf, 0, reads);
					}
					is.close();
					if (crc32.getValue() != entry.getCrc())
						isIntegrity = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			isIntegrity = false;
		} finally {
			try {
				if (zf != null)
					zf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isIntegrity;
	}
	
}
