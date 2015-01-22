package com.shenma.tvlauncher.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * 接受连接盒子服务的请求
 * @author drowtram
 *
 */
public class HttpFileHandler implements HttpRequestHandler{
	private AssetManager assetManager;
	
	public HttpFileHandler(Context context) {
		super();
		this.assetManager = context.getAssets();
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		// 通过http访问才能调用此方法。
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
			throw new MethodNotSupportedException(method + " method not supported");
		}
		// 处理响应
		response(request, response);
	}
	
	private void response(HttpRequest request, HttpResponse response) throws UnsupportedEncodingException {
		String target = request.getRequestLine().getUri();
		try {
			String url = sanitizeUri(target);
			if (url.equals("/") || url.equals("/index.html")) {
				url = "/index.html";
			}
			if(url.contains("?"))
				url = url.substring(0,url.indexOf("?"));
			url = url.substring(1);
			InputStream in = assetManager.open(url);
			response.setStatusCode(HttpStatus.SC_OK);
			InputStreamEntity body = new InputStreamEntity(in, -1);
			response.setEntity(body);
		} catch (FileNotFoundException e) {
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			final String path = URLDecoder.decode(target, "UTF-8");
			EntityTemplate body = new EntityTemplate(new ContentProducer() {
				public void writeTo(final OutputStream outstream) throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
					writer.write("<html><body><h1>");
					writer.write("File ");
					writer.write(URLDecoder.decode(path, "UTF-8"));
					writer.write(" not found");
					writer.write("</h1></body></html>");
					writer.flush();
				}

			});
			body.setContentType("text/html; charset=UTF-8");
			response.setEntity(body);
		}catch (IOException e) {
			response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			EntityTemplate body = new EntityTemplate(new ContentProducer() {

				public void writeTo(final OutputStream outstream) throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
					writer.write("<html><body><h1>");
					writer.write("Access denied");
					writer.write("</h1></body></html>");
					writer.flush();
				}

			});
			body.setContentType("text/html; charset=UTF-8");
			response.setEntity(body);
		}
	}
	
	/**
	 * 解码
	 * 
	 * @param uri
	 * @return
	 */
	private String sanitizeUri(String uri) {
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			try {
				uri = URLDecoder.decode(uri, "ISO-8859-1");
			} catch (UnsupportedEncodingException e1) {
				throw new Error();
			}
		}
		return uri;
	}
}
