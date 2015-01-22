package com.shenma.tvlauncher.tvlive.network;

import java.io.File;

import com.wepower.live.parser.ILetv;
import com.wepower.live.parser.IPlay;

import dalvik.system.DexClassLoader;
import android.content.Context;

public class ClassManager {

	String LETV_IMP = "com.letv.pp.service.LeService";
	String PLAY_IMP = "com.wepower.live.tv.PlayImp";

	private Context context = null;
	private Class<?> letvClass, playClass = null;
	private IPlay iPlay = null;
	private ILetv iLetv = null;
	private File dexFile = null;
	private DexClassLoader dexCL = null;

	public ClassManager(Context context) {
		this.context = context;
	}

	public ILetv getLetvClass() {
		try {
			letvClass = dexCL.loadClass(LETV_IMP);
			iLetv = (ILetv) letvClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iLetv;

	}

	public IPlay getPlayClass() {
		try {
			playClass = dexCL.loadClass(PLAY_IMP);
			iPlay = (IPlay) playClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iPlay;
	}

	/**
	 * 初始化文件操作
	 */
	public void initFile() {
		dexFile = context.getDir("dex", Context.MODE_PRIVATE);
		dexCL = new DexClassLoader(findJarFile().getAbsolutePath(),
				dexFile.getAbsolutePath(), null, context.getClassLoader());
	}

	/**
	 * 找到jar包所在的位置
	 * 
	 * @return
	 */
	private File findJarFile() {
		File file=new File(context.getFilesDir()+File.separator,"play.jar");
		return file;
		
	}

}
