package com.androchef.cameraxfacedetection.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.androchef.cameraxfacedetection.MyApplication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class tools {

	private static final String TAG = tools.class.getSimpleName();
	/** 方便 tools 的使用 */
	@NonNull
	public static MyApplication getApplicationInstance() {
		return MyApplication.instance;
	}

	/** 获取当前屏幕 dpi */
	public static float getDensity(Activity a) {
		DisplayMetrics dm = new DisplayMetrics();
		a.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.density;
	}
	public static float getDensity(Context c) {
		DisplayMetrics dm = c.getResources().getDisplayMetrics();
		return dm.density;
	}

	/** 设置全屏显示 */
	public static void setFullScreenUI(Activity a) {
		// 全屏显示，隐藏状态栏和导航栏，拉出状态栏和导航栏显示一会儿后消失。

		a.getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	/** 保持屏幕常亮 */
	public static void setKeepScreenOn(Activity a) {
		//保持屏显常亮
		a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/** 数字转成中文年级 */
	public static String numberToZhGrade(int level) {
		String result;
		switch (level) {
			case 1:
				result = "一年级"; break;
			case 2:
				result = "二年级"; break;
			case 3:
				result = "三年级"; break;
			case 4:
				result = "四年级"; break;
			case 5:
				result = "五年级"; break;
			case 6:
				result = "六年级"; break;
			case 7:
				result = "初一"; break;
			case 8:
				result = "初二"; break;
			case 9:
				result = "初三"; break;
			case 10:
				result = "高一"; break;
			case 11:
				result = "高二"; break;
			case 12:
				result = "高三"; break;
			default:
				result = "未知"; break;
		}
		return result;
	}

	/**
	 * 获取assets目录下所有文件
	 *
	 * @param context 上下文
	 * @param path    文件地址
	 * @return files[] 文件列表
	 */
	public static String[] getFilesFromAssets(Context context,String path) {
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try {
			files = assetManager.list(path);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// for (String str : files) {
		//            LogUtils.logInfoStar(str);
		// Log.v(Constants.APP_TAG, "assets files -- " + str);
		// }

		return files;
	}

	/** 注册图所在的目录 */
	private static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "arcfacedemo";
	private static final String REGISTER_DIR = Environment.getExternalStorageDirectory().toString() + "/arcfacedemo/register";
	// private static final String REGISTER_DIR = Environment.getExternalFilesDir().getAbsolutePath(); + "/arcfacedemo/register";
	// private static final String REGISTER_DIR2 = ROOT_DIR + File.separator + "register";

	/** 把assets的大头照文件复制到内部储存的 人脸注册文件夹 */
	public static void copyAssetsFilesToRegisterFolder(Context context) {
		// 若目标文件夹不存在，则创建
		File dir = new File(REGISTER_DIR);
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				Log.d("FileUtils","mkdir error: ");
				return;
			}
		}
		// 获取assets student 所有的图片
		String[] fileList;
		try {
			fileList = context.getAssets().list("students");
		}
		catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		if (fileList == null) return;
		// 遍历这些图片
		for (String s : fileList) {
			String temp = "students/" + s;
			// 拷贝文件
			String filename = REGISTER_DIR + "/" + s;
			File file = new File(filename);
			if (!file.exists()) {
				try {
					InputStream inStream = context.getAssets().open(temp);
					FileOutputStream fileOutputStream = new FileOutputStream(filename);

					int byteread;
					byte[] buffer = new byte[1024];
					while ((byteread = inStream.read(buffer)) != -1) {
						fileOutputStream.write(buffer,0,byteread);
					}
					fileOutputStream.flush();
					inStream.close();
					fileOutputStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				Log.w("FileUtils","[copyFileFromAssets] copy asset file: " + temp + " to : " + filename);

			} else {
				Log.e("FileUtils","[copyFileFromAssets] file is exist: " + filename);
				Toast.makeText(context,"保存失败",Toast.LENGTH_SHORT).show();
			}
		}
		Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
	}

	/** 读取大头照时，不要全尺寸读取，使用 360小尺寸读取，避免oom */
	public static Bitmap revitionImageSize(Context c,String path,int size) throws IOException {
		// 取得图片
		InputStream temp = c.getAssets().open(path);
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 这个参数代表，不为bitmap分配内存空间，只记录一些该图片的信息（例如图片大小），说白了就是为了内存优化
		options.inJustDecodeBounds = true;
		// 通过创建图片的方式，取得options的内容（这里就是利用了java的地址传递来赋值）
		BitmapFactory.decodeStream(temp,null,options);
		// 关闭流
		temp.close();

		// 生成压缩的图片
		int i = 0;
		Bitmap bitmap;
		while (true) {
			// 这一步是根据要设置的大小，使宽和高都能满足
			if ((options.outWidth >> i <= size)
					&& (options.outHeight >> i <= size)) {
				// 重新取得流，注意：这里一定要再次加载，不能二次使用之前的流！
				temp = c.getAssets().open(path);
				// 这个参数表示 新生成的图片为原始图片的几分之一。
				options.inSampleSize = (int) Math.pow(2.0D,i);
				// 这里之前设置为了true，所以要改为false，否则就创建不出图片
				options.inJustDecodeBounds = false;

				bitmap = BitmapFactory.decodeStream(temp,null,options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	// 下面的方法好像没有用

	public static Bitmap decodeStreamBitmap2(InputStream open,int reqWidth,int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(open,null,options);
		// 计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
		// 还原inJustDecodeBounds为false
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(open,null,options);
	}
	public static Bitmap decodeBitmapFromResource(Resources res,int resId,
												  int reqWidth,int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res,resId,options);
		// 计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
		// 还原inJustDecodeBounds为false
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res,resId,options);
	}
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight) {
		final int width = options.outWidth;
		final int height = options.outHeight;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			//计算图片高度和我们需要高度的最接近比例值
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			//宽度比例值
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			//取比例值中的较大值作为inSampleSize
			inSampleSize = Math.max(heightRatio,widthRatio);
		}

		return inSampleSize;
	}

	/** 随机教师名字长度 */
	public static String getRandomChineseName() {
		int tempSize = new Random().nextInt(2) + 2;
		StringBuilder tempSchool = new StringBuilder();
		for (int ii = 0;ii < tempSize;ii++) {
			byte[] bytes = new byte[2];
			bytes[0] = (byte) (0xa0 + 16 + new Random().nextInt(38));
			bytes[1] = (byte) (0xa0 + 1 + new Random().nextInt(92));
			try {
				tempSchool.append(new String(bytes,"gb2312"));
			}
			catch (UnsupportedEncodingException ignored) {return "编码错误";}
		}
		return tempSchool.toString();
	}

	/**
	 * 获取assets的文本文件
	 *
	 * @param path e.g. work.txt
	 */
	public static String getAssetsTxtFile(Context c,String path) {
		AssetManager am = c.getAssets();
		StringBuilder novel = new StringBuilder();
		try {
			InputStream is = am.open(path);

			String code = getCode(is);
			is = am.open(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(is,code));

			String line = br.readLine();
			int i = 0;
			while (line != null) {
				novel.append(line).append("\n");
				line = br.readLine();
				i++;
				// if(i==200){
				// 	break;
				// }
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return novel.toString();
	}

	/** 获取文本编码 */
	public static String getCode(InputStream is) {
		try {
			BufferedInputStream bin = new BufferedInputStream(is);
			int p;

			p = (bin.read() << 8) + bin.read();

			String code;
			switch (p) {
				case 0xefbb:
					code = "UTF-8"; break;
				case 0xfffe:
					code = "Unicode"; break;
				case 0xfeff:
					code = "UTF-16BE"; break;
				default:
					code = "GBK"; break;
			}

			is.close();
			return code;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 把文本时间“08：40” 转成 具体多少分钟（int型） */
	public static int strTimeToIntMinute(String time) {
		int i = 0;
		try {
			i = (Integer.parseInt(time.substring(0,2)) * 60) + Integer.parseInt(time.substring(3));
		}
		catch (NumberFormatException ignored) {}
		return i;
	}

	/** 把文本时间“08：40:02” 转成 具体多少秒（int型） */
	public static int strTimeToIntSeconds(String time) {
		int i = 0;
		try {
			i = (Integer.parseInt(time.substring(0,2)) * 3600) + (Integer.parseInt(time.substring(3,5)) * 60) + Integer.parseInt(time.substring(6,8));
		}
		catch (NumberFormatException ignored) {}
		return i;
	}

	/** 把具体多少分钟（int型） 转成 文本时间“08：40” */
	public static String intMinuteToStrTime(int time) {
		int tempHours = time / 60;
		int tempMinutes = time % 60;
		return (tempHours < 10 ? "0:" : ":" + tempHours) + (tempMinutes < 10 ? "0" : "" + tempMinutes);
	}

	/** 获取今天过去了多少秒，把 Date 时间 转成 秒数 */
	@SuppressWarnings("deprecation")
	public static int getPassSecondsInADay(Date nowDate) {
		return nowDate.getHours() * 3600 + nowDate.getMinutes() * 60 + nowDate.getSeconds();
	}

	/** 获取指定数量的随机简繁中文字符 */
	public static String getRandomUTF8ZhString(int size) {
		StringBuilder tempSchool = new StringBuilder();
		for (int i = 0;i < size;i++) tempSchool.append((char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1))));
		return tempSchool.toString();
	}

	/** 获取指定数量的随机简体中文字符 */
	public static String getRandomSimpleZhString(int size) {
		StringBuilder tempSchool = new StringBuilder();
		for (int i = 0;i < size;i++) {
			byte[] bytes = new byte[2];
			bytes[0] = (byte) (0xa0 + 16 + new Random().nextInt(38));
			bytes[1] = (byte) (0xa0 + 1 + new Random().nextInt(92));
			try {
				tempSchool.append(new String(bytes,"gb2312"));
			}
			catch (UnsupportedEncodingException ignored) {}
		}
		return tempSchool.toString();
	}

	/** 复制assets文件到 本地路径 */
	public static boolean copyAssetFolder(Activity c,AssetManager assetManager,
										  String fromAssetPath,String toPath) {
		try {
			String[] files = assetManager.list(fromAssetPath);
			new File(toPath).mkdirs();
			boolean res = true;
			if (files == null) return false;
			for (String file : files)
				if (file.contains("."))
					res &= copyAsset(assetManager,
							fromAssetPath + "/" + file,
							toPath + "/" + file);
				else
					res &= copyAssetFolder(c,assetManager,
							fromAssetPath + "/" + file,
							toPath + "/" + file);
			c.finish();
			return res;
		}
		catch (Exception e) {
			e.printStackTrace();
			c.finish();
			return false;
		}

	}

	public static boolean copyAsset(AssetManager assetManager,
									String fromAssetPath,String toPath) {
		InputStream in;
		OutputStream out;
		try {
			in = assetManager.open(fromAssetPath);
			new File(toPath).createNewFile();
			out = new FileOutputStream(toPath);
			copyFile(in,out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void copyFile(InputStream in,OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer,0,read);
		}
	}

	public static final String[] SOME_FAMILY_NAME = new String[]{"太爷爷","太姥爷","太奶奶","太姥姥","爷爷","外公","奶奶","外婆","伯伯","舅舅","叔叔","姑姥","姑奶","姨姨","姑姑","舅姥爷","舅爷","舅姥姥","舅奶","舅妈","伯母","表哥","堂哥","表姐","婶婶","姨夫","堂哥","表哥","堂姐","表姐","姑父","表嫂","表哥","表侄子","表姐","表侄女","姑爷爷","表姐夫","表叔","表侄子","表姑","表侄女","表舅","表姨"};

	/** 获取随机亲戚称呼 */
	public static String getRandomFamilyName() {
		return SOME_FAMILY_NAME[new Random().nextInt(SOME_FAMILY_NAME.length)];
	}
	/** 获取随机电话号码 */
	public static String getRandomPhoneNumber() {
		StringBuilder number = new StringBuilder("13");
		for (int i = 0;i < 9;i++) {
			number.append(new Random().nextInt(10));
		}
		return number.toString();
	}

	public static final String[] ZH_PUNCTUATION = new String[]{"，","。"};
	/** 获取随机简体中文段落 */
	public static String getRandomZhSimpleText(int small,int big) {
		int tempRandom = new Random().nextInt(big - small) + small;
		StringBuilder text = new StringBuilder();
		for (int i = 0;i < tempRandom;i++) {
			text.append(getRandomSimpleZhString(new Random().nextInt(22) + 2)).append(ZH_PUNCTUATION[new Random().nextInt(2)]);
		}
		text.append(getRandomSimpleZhString(new Random().nextInt(22) + 2)).append("。");
		return text.toString();
	}

	/** 弹出软键盘 */
	public static void showIME(EditText edit) {
		Timer timer = new Timer();//等待150时间打开软键盘
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) edit.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(edit,0);
			}
		},200);
		edit.requestFocus();
	}

	/** ██ 强制退出程序 ██ */
	public static void exitApp(Context context) {
		// context.stopService(new Intent(context, MainService.class));

		ActivityManager actvityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo runningProInfo : procInfos) {
			if (runningProInfo.processName.contains(context.getPackageName()) && runningProInfo.pid != android.os.Process.myPid()) {
				android.os.Process.killProcess(runningProInfo.pid);
			}
		}
		System.exit(0);
	}

	private static String getTimeMillsMacId() {
		long timestamp = System.currentTimeMillis(); // 获取当前时间戳
		StringBuilder hexString = new StringBuilder(Long.toHexString(timestamp)); // 将时间戳转换为十六进制文本
		// 如果不够12个字符就手动添加1
		if (hexString.length() < 12) {
			int tempSize = 12 - hexString.length();
			for (int i = 0;i < tempSize;i++) {
				hexString.insert(0,"1");
			}
		}
		StringBuilder result = new StringBuilder();
		// 每隔两个字符加一个冒号
		for (int i = 0;i < hexString.length();i += 2) {
			result.append(hexString.substring(i,i + 2)).append(":");
		}
		// 删除字符串中的最后一个冒号
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	/**
	 * 实在不行，就通过这个方法获取mac地址，把当前转成mac地址
	 */
	public static String getIMEIMacId2(String imei) {
		if (TextUtils.isEmpty(imei)) {
			return null;
		}
		long timestamp = Long.parseLong(imei); // imei转long
		StringBuilder hexString = new StringBuilder(Long.toHexString(timestamp)); // 将时间戳转换为十六进制文本
		// 如果不够12个字符就手动添加1，多了就删除掉前面的
		if (hexString.length() < 12) {
			int tempSize = 12 - hexString.length();
			for (int i = 0;i < tempSize;i++) {
				hexString.insert(0,"1");
			}
		} else if (hexString.length() > 12) {
			hexString = new StringBuilder(hexString.substring(hexString.length() - 12));
		}
		StringBuilder result = new StringBuilder();
		// 每隔两个字符加一个冒号
		for (int i = 0;i < hexString.length();i += 2) {
			result.append(hexString.substring(i,i + 2)).append(":");
		}
		// 删除字符串中的最后一个冒号
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	public static String intToChineseNumeral(int num) {
		if (num == 0) {
			return "零";
		}

		String[] chineseNumeral = {"","一","二","三","四","五","六","七","八","九"};
		String[] unit = {"","十","百","千","万","亿"};

		StringBuilder result = new StringBuilder();
		int i = 0;
		while (num > 0) {
			int digit = num % 10;
			if (digit != 0 || i == 4 || i == 8) {
				result.insert(0,unit[i]);
			}
			if (!(i == 0 && digit == 0)) {
				result.insert(0,chineseNumeral[digit]);
			}
			num /= 10;
			i++;
		}
		return result.toString();
	}

	public static void addADTStoPacket(byte[] packet,int packetLen,int chancfg) {
		int profile = 2;  //AAC LC，MediaCodecInfo.CodecProfileLevel.AACObjectLC;
		int freqIdx = 4;  //见后面注释avpriv_mpeg4audio_sample_rates中441000对应的数组下标，来自ffmpeg源码
		//        int chanCfg = 1;  //见后面注释channel_configuration，AudioFormat.CHANNEL_IN_MONO 单声道(声道数量)
		int chanCfg = chancfg;  //见后面注释channel_configuration，AudioFormat.CHANNEL_IN_MONO 单声道(声道数量)

        /*int avpriv_mpeg4audio_sample_rates[] = {96000, 88200, 64000, 48000, 44100, 32000,24000, 22050, 16000, 12000, 11025, 8000, 7350};
        channel_configuration: 表示声道数chanCfg
        0: Defined in AOT Specifc Config
        1: 1 channel: front-center
        2: 2 channels: front-left, front-right
        3: 3 channels: front-center, front-left, front-right
        4: 4 channels: front-center, front-left, front-right, back-center
        5: 5 channels: front-center, front-left, front-right, back-left, back-right
        6: 6 channels: front-center, front-left, front-right, back-left, back-right, LFE-channel
        7: 8 channels: front-center, front-left, front-right, side-left, side-right, back-left, back-right, LFE-channel
        8-15: Reserved
        */

		// fill in ADTS data
		packet[0] = (byte) 0xFF;
		//        packet[1] = (byte)0xF9;
		packet[1] = (byte) 0xF1; //解决ios 手机不能播放问题
		packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
		packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
		packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
		packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
		packet[6] = (byte) 0xFC;

	}

	private final static int changeType = 1;//横屏的检测方式
	public static void setScreen(Context context,int index) {
		int screenOrientation = context.getResources().getConfiguration().orientation;
		if (index == 0) {//设置竖屏
			if (changeType == 0) {
				if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					Settings.System.putInt(context.getContentResolver(),Settings.System.USER_ROTATION,0);
				}
			} else {
				Settings.System.putInt(context.getContentResolver(),Settings.System.USER_ROTATION,0);
			}
		} else if (index == 1) { //设置横屏
			if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				Settings.System.putInt(context.getContentResolver(),Settings.System.USER_ROTATION,1);
			}
		} else if (index == 2) {// 设置反向横屏
			if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
				Settings.System.putInt(context.getContentResolver(),Settings.System.USER_ROTATION,3);
			}
		} else if (index == 3) {
			if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
				Settings.System.putInt(context.getContentResolver(),Settings.System.USER_ROTATION,2);
			}
		}
	}

	public static void setViewHeightWropContext(ViewGroup vg) {
		ViewGroup.LayoutParams parentParams = vg.getLayoutParams();
		parentParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		vg.setLayoutParams(parentParams);
	}

	public static void setViewHeightMatchParent(ViewGroup vg) {
		ViewGroup.LayoutParams parentParams = vg.getLayoutParams();
		parentParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
		vg.setLayoutParams(parentParams);
	}

	/** ██ 重启APP的最好方法 ██ */
	public static void restartMyAppBest(Activity a,Class<?> abc) {
		a.startActivity(new Intent(a,abc));
		a.overridePendingTransition(0,0);
		a.finish();
	}
	/** ██ 重启APP的最好方法 ██ */
	public static void restartMyAppBest(Activity a,Class<?> abc,String data) {
		Intent intent = new Intent(a,abc);
		intent.setData(Uri.parse(data));
		a.startActivity(intent);
		a.overridePendingTransition(0,0);
		a.finish();
	}

	/** 复制到剪贴板 */
	public static void setClipboard(Context c,String content) {
		//获取剪贴板管理器
		ClipboardManager cm = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
		// 创建普通字符型ClipData
		ClipData mClipData = ClipData.newPlainText("text",content);
		// 将ClipData内容放到系统剪贴板里。
		cm.setPrimaryClip(mClipData);
	}

	/** 检查字符串是否为合法链接 */
	public static boolean isValidUrl(String urlString) {
		try {
			URI uri = new URI(urlString);
			return uri.getScheme() != null && uri.getHost() != null;
		}
		catch (URISyntaxException e) {
			return false;
		}
	}
	/** 打开链接 */
	public static void openUri(Context c,String str) {
		Uri uri = Uri.parse(str);
		Intent intent = new Intent(Intent.ACTION_VIEW,uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(intent);
	}
	/** ██ 获取屏幕真实高度 ██ */
	public static int getScreenRealHeight(Activity a) {
		WindowManager windowManager = (WindowManager) (a.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
		Display defaultDisplay = windowManager.getDefaultDisplay();
		Point point = new Point();
		defaultDisplay.getRealSize(point);
		return point.y;
	}
	/** ██ 获取屏幕真实宽度 ██ */
	public static int getScreenRealWidth(Activity a) {
		WindowManager windowManager = (WindowManager) (a.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
		Display defaultDisplay = windowManager.getDefaultDisplay();
		Point point = new Point();
		defaultDisplay.getRealSize(point);
		return point.x;
	}
	/** ██ 获取屏幕真实 ██ */
	public static int[] getScreenRealResolution(Activity a) {
		WindowManager windowManager = (WindowManager) (a.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
		Display defaultDisplay = windowManager.getDefaultDisplay();
		Point point = new Point();
		defaultDisplay.getRealSize(point);
		return new int[]{point.x,point.y};
	}
	/** ██ 获取实际上可操作的屏幕高度 ██ */
	public static int getStatusBarHeight3(Activity a) {
		Rect outRect = new Rect();
		a.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
		return (int) Math.floor(outRect.bottom);
	}
	/** ██ 获取实际上可操作的屏幕 ██ */
	public static int[] getAvailableScreenResolution(Activity a) {
		Rect outRect = new Rect();
		a.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
		return new int[]{(int) Math.floor(outRect.right),(int) Math.floor(outRect.bottom)};
	}

	public static void setViewHeight(View view,int height) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.height = height;
		view.setLayoutParams(params);
	}

	public static int getPositionThroughXYCoordinates(int screenWidth,int screenHeight,int cols,int rows,int x,int y) {
		// 计算每个格子的宽度和高度
		int width = screenWidth / cols;
		int height = screenHeight / rows;

		// 将坐标转换为所在格子的行列数
		int row = y / height;
		int col = x / width;

		// 计算格子编号并返回
		return row * cols + col;
	}

	@SuppressLint("HardwareIds")
	public static String getIMEItoMacId(Activity activity) {
		TelephonyManager telephonyManager = (TelephonyManager) getApplicationInstance().getSystemService(Context.TELEPHONY_SERVICE);
		// 检查读取电话状态权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(activity,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				// 如果没有权限则进行权限请求
				ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_PHONE_STATE},1);
				Log.w(TAG,"getIMEItoMacId: " + "获取imei 没有权限");
				return null;
			}
		}
		// 获取IMEI码
		String imei;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			try {
				imei = telephonyManager.getImei();
			}
			catch (Exception e) {
				try {
					imei = telephonyManager.getDeviceId();
				}
				catch (Exception ex) {
					return null;
				}
			}
		} else {
			imei = telephonyManager.getDeviceId();
		}
		// 输出IMEI码
		// Log.d(TAG,"IMEI: " + imei);
		Log.w(TAG,"getIMEItoMacId: " + "获取imei成功（" + !TextUtils.isEmpty(imei) + "）");
		return getIMEIMacId2(imei);
	}

	@SuppressLint("HardwareIds")
	public static String getAndroidIdToMacId(Activity activity) {
		String imei = Settings.Secure.getString(activity.getContentResolver(),Settings.Secure.ANDROID_ID);
		// 输出IMEI码
		// Log.d(TAG,"IMEI: " + imei);
		Log.w(TAG,"getAndroidIdToMacId: " + "获取AndroidId成功为" + !TextUtils.isEmpty(imei) + "==" + imei);
		if (TextUtils.isEmpty(imei)) {
			return null;
		} else {
			return strToMacId(imei);
		}
	}

	@SuppressLint("HardwareIds")
	public static String getMacId2() {
		WifiManager wifiManager = (WifiManager) getApplicationInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getConnectionInfo().getMacAddress();
	}

	public static String getMacId(String netDevice) {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/" + netDevice + "/address");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			while (null != str) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		}
		catch (IOException ex) {
			// 赋予默认值
			// ex.printStackTrace();
			// Log.e(TAG,"getMacId2: 出错", ex);
		}
		return macSerial;

	}
	/** 实在不行，就通过这个方法获取mac地址，把当前转成mac地址 */
	public static String strToMacId(String hexString) {
		if (TextUtils.isEmpty(hexString)) {
			return null;
		}
		// String hexString = Long.toHexString(timestamp); // 将时间戳转换为十六进制文本
		// 如果不够12个字符就手动添加1，多了就删除掉前面的
		if (hexString.length() < 12) {
			int tempSize = 12 - hexString.length();
			StringBuilder hexStringBuilder = new StringBuilder(hexString);
			for (int i = 0;i < tempSize;i++) {
				hexStringBuilder.insert(0,"1");
			}
			hexString = hexStringBuilder.toString();
		} else if (hexString.length() > 12) {
			hexString = hexString.substring(hexString.length() - 12);
		}
		StringBuilder result = new StringBuilder();
		// 每隔两个字符加一个冒号
		for (int i = 0;i < hexString.length();i += 2) {
			result.append(hexString.substring(i,i + 2)).append(":");
		}
		// 删除字符串中的最后一个冒号
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	public static Bitmap getBitmapFromPath(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile(path,options);
	}

	/** 在安卓7以上 关闭 暴露 file uri检测 */
	public static void closeFileUriCheck() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
			StrictMode.setVmPolicy(builder.build());
			builder.detectFileUriExposure();
		}
	}

	/** 是否拥有权限 */
	public static boolean checkPermission(Context context,String[] permissions) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			for (String permission : permissions) {
				if (ContextCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED)
					return false;
			}
		}
		return true;
	}
	private static boolean checkPermissionsBest(Context context,String[] neededPermissions) {
		if (neededPermissions == null || neededPermissions.length == 0) {
			return true;
		}
		boolean allGranted = true;
		for (String neededPermission : neededPermissions) {
			allGranted &= ContextCompat.checkSelfPermission(context,neededPermission) == PackageManager.PERMISSION_GRANTED;
		}
		return allGranted;
	}

	/** 展示 导出文件夹，并允许复制，允许跳转文件管理 */
	public static void showFolderPathDialog(Context context,String title,String folderPath) {
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage("\n内部储存" + folderPath)
				.setPositiveButton("复制",(dialog,which) -> setClipboard(context,folderPath))
				.create().show();
	}

	/** 调用系统相机 */
	public static void openCamera(Context context) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// 确保有相机应用可以处理拍照意图
		if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
			// 启动相机
			context.startActivity(takePictureIntent);
		}
	}

	/** 调用系统相机拍照 */
	public static boolean openSystemCameraImage(Context context) {
		return openSystemCamera(context,MediaStore.ACTION_IMAGE_CAPTURE);
	}
	/** 调用系统相机摄影 */
	public static boolean openSystemCameraVideo(Context context) {
		return openSystemCamera(context,MediaStore.ACTION_VIDEO_CAPTURE);
	}

	/** 调用系统相机 */
	public static boolean openSystemCamera(Context context,String action) {
		// 查询所有能够处理相机Intent的应用信息
		Intent cameraIntent = new Intent(action);
		if (cameraIntent.resolveActivity(context.getPackageManager()) == null) return false;
		List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(cameraIntent,PackageManager.MATCH_DEFAULT_ONLY);
		// 如果相机应用只有一个，则直接启动
		if (resolveInfoList.size() == 1) {
			context.startActivity(cameraIntent);
			return true;
		}
		if (resolveInfoList.isEmpty()) {
			return false;
		}
		// 遍历相机应用列表，优先启动系统应用
		for (ResolveInfo resolveInfo : resolveInfoList) {
			if (isSystemApplication(context,resolveInfo.activityInfo.packageName)) {
				if (openApp(context,resolveInfo.activityInfo.packageName)) {
					return true;
				}
			}
		}
		// 不管了，直接启动 Intent
		context.startActivity(cameraIntent);
		return true;
	}

	/** 判断包名是否为系统应用 */
	public static boolean isSystemApplication(Context context,String pkgName) {
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo;
		try {
			applicationInfo = packageManager.getApplicationInfo(pkgName,0);
		}
		catch (PackageManager.NameNotFoundException e) {
			return false;
		}
		return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
	}

	/** 设置 ActionBar 菜单向上导航按钮，并更改 Drawable */
	public static void setActionBarHomeIndicatorClose(Activity a,int drawableRes) {
		ActionBar actionBar = a.getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeAsUpIndicator(drawableRes);
		}
	}
	/** 设置 ActionBar 菜单向上导航按钮 */
	public static void setActionBarHomeIndicator(Activity a) {
		if (a.getActionBar() != null) {
			a.getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	/** 设置 ActionBar 无阴影， */
	public static void setActionBarNoElevation(Activity a) {
		if (a.getActionBar() != null) {
			a.getActionBar().setElevation(0);
		}
	}

	/** 设置 ToolBar 菜单向上导航按钮，并更改 Drawable */
	public static void setToolBarHomeIndicatorClose(androidx.appcompat.app.AppCompatActivity a,int drawableRes) {
		if (a.getSupportActionBar() != null) {
			a.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			a.getSupportActionBar().setHomeAsUpIndicator(drawableRes);
		}
	}
	/** 设置 ToolBar 菜单向上导航按钮 */
	public static void setToolBarHomeIndicator(androidx.appcompat.app.AppCompatActivity a) {
		if (a.getSupportActionBar() != null) {
			a.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	/** 此方法无效，应在布局Appbar里设置，设置 ToolBar 无阴影 */
	@Deprecated
	public static void setToolBarNoElevation(androidx.appcompat.app.AppCompatActivity a) {
		if (a.getSupportActionBar() != null) {
			a.getSupportActionBar().setElevation(0);
		}
	}

	/**
	 * 使弹出菜单显示图标
	 * 4.0系统默认无效
	 */
	public static void setIconEnable(Menu menu) {
		if (menu != null) {
			if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
				try {
					Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);
					method.setAccessible(true);
					method.invoke(menu,true);
				}
				catch (Exception ignore) {}
			}
		}
	}

	/** 简单的 toast，无内存泄露风险 */
	public static void toast(Context c,String msg) {
		Toast.makeText(c.getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
	}

	/** 简单的 toast，无内存泄露风险 */
	public static void toast(String msg) {
		Toast.makeText(getApplicationInstance(),msg,Toast.LENGTH_SHORT).show();
	}

	private static void getIptest1(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

		WifiInfo connectionInfo = wm.getConnectionInfo();
		int ipAddress = connectionInfo.getIpAddress();
		String ipString = Formatter.formatIpAddress(ipAddress);
		Log.e(TAG,ipString);
		if (activeNetwork != null) {
			Log.e(TAG,activeNetwork.toString());
		}
	}

	//用这个去替换那个 原项目的 getSelfIp getIPAddress，getRouterIpAddress 看看好不好用
	/** 获得IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的 */
	@NonNull
	public static String getIPAddress(@NonNull Context context) {

		//try {
		NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
				try {
					//Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();enumIpAddr.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
								String ip = inetAddress.getHostAddress();
								return TextUtils.isEmpty(ip) ? "127.0.0.1" : ip;
							}
						}
					}
				}
				catch (Exception ignored) {}
			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
				WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				//调用方法将int转换为地址字符串
				String ip = intIP2StringIP(wifiInfo.getIpAddress());
				return TextUtils.isEmpty(ip) ? "127.0.0.1" : ip;
			}
		}
		// else {//当前无网络连接,请在设置中打开网络}
		return "127.0.0.1";
		//} catch (Exception e) {
		//	e.printStackTrace();
		//	return null;
		//}
		// return null;
		//return "0.0.0.0";
	}
	/** 将得到的int类型的IP转换为String类型 */
	public static String intIP2StringIP(int ip) {
		return (ip & 0xFF) + "." +
				((ip >> 8) & 0xFF) + "." +
				((ip >> 16) & 0xFF) + "." +
				(ip >> 24 & 0xFF);
	}

	/**
	 * 转到其他应用
	 *
	 * @param pkg 应用包名
	 * @return 是否打开成功
	 */
	public static boolean openApp(Context c,String pkg) {
		Intent intent = c.getPackageManager().getLaunchIntentForPackage(pkg);
		if (intent == null) {
			return false;
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {c.startActivity(intent);}
			catch (Exception e) {
				toast("启动失败！\n"+e);
			}
		}
		return true;
	}

	/**
	 * 获取蓝牙设备名称
	 * <p>
	 * <p>
	 * 要使用此功能，需要添加以下权限：
	 * <ul>
	 *     <li>{@link Manifest.permission#BLUETOOTH_CONNECT}</li>
	 *     <li>{@link Manifest.permission#BLUETOOTH_ADMIN}</li>
	 *     <li>{@link Manifest.permission#BLUETOOTH}</li>
	 * </ul>
	 */
	@SuppressLint("MissingPermission")
	@NonNull
	public static String getBluetoothDeviceName() {
		String deviceName = null;

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter != null) {
			try {
				deviceName = bluetoothAdapter.getName();
			}
			catch (Exception e) {
				Log.e(TAG,"getBluetoothDeviceName: ",e);
			}
		}

		if (isTextHasChar(deviceName)) return deviceName;

		deviceName = Build.BRAND + " " + Build.MODEL;
		return isTextHasChar(deviceName) ? deviceName : "Android Device";
	}

	/** 获取 WLAN 直连 SSID 名称 */
	@NonNull
	public static String getWlanSsidName(@NonNull Context context) {
		String ssidName = null;
		WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			ssidName = wifiInfo.getSSID(); // WLAN direct connect name (SSID)
		}
		return isTextHasChar(ssidName) ? ssidName : "Unknown SSID";
	}

	/** 判断字符串是否不为空，包含 Trim 方法 */
	public static boolean isTextHasChar(@Nullable String str) {
		return str != null && str.length() != 0 && !"".equals(str.trim());
	}
	/** 判断字符串是否为空，包含 Trim 方法 */
	public static boolean isEmpty(@Nullable String str) {
		return !isTextHasChar(str);
	}

	public static boolean isValidIPAddress(String ipAddress) {
		String ipAddressPattern =
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

		return Pattern.matches(ipAddressPattern,ipAddress);
	}

	/** 检查指定服务是否正在运行 */
	public static boolean isServiceRunning(Context context,Class<?> serviceClass) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean excuteSuCMD(String apkFilepath) {
		Process process;
		OutputStream out = null;
		InputStream in = null;
		try {
			// 请求root
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
			// 调用安装
			out.write(("pm install -r \"" + apkFilepath + "\"\n").getBytes());
			in = process.getInputStream();
			int len;
			byte[] bs = new byte[256];
			while (-1 != (len = in.read(bs))) {
				String state = new String(bs,0,len);
				if (state.equals("Success\n")) {
					//安装成功后的操作
					return true;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (in != null) {
					in.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void installApkFile(Context context,String apkFilePath) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(
				Uri.fromFile(new File(apkFilePath)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static void installAPK(Context context,File apkFile) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		if (Build.VERSION.SDK_INT >= 24) {
			Uri apkUri = FileProvider.getUriForFile(context,context.getPackageName() + ".provider",apkFile);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(apkUri,"application/vnd.android.package-archive");
			Log.e("TAG","installAPK: ................uri=" + apkUri);
		} else {
			intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
		}
		context.startActivity(intent);
		// if (closeApp) {
		// 	android.os.Process.killProcess(android.os.Process.myPid());// 如果不加上这句的话在apk安装完成之后点击单开会崩溃
		// }
	}

	// 赋予权限
	public static boolean givePermission(String path) {
		Log.e("sx","赋予权限 " + path);
		String[] commands = {"su","0","chmod","777",path};
		return execCommand(commands);
	}
	//静默安装并自动打开
	// public void silentInstall(String apkPath) {
	// 	givePermission(apkPath);
	// 	install(apkPath);
	// }
	//命令执行
	private static boolean execCommand(String[] commands) {
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		boolean result = false;
		try {
			process = processBuilder.start();
			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String s;
			while ((s = successResult.readLine()) != null) {
				successMsg.append(s);
			}
			while ((s = errorResult.readLine()) != null) {
				errorMsg.append(s);
			}
		}
		catch (Exception e) {
			Log.e("log","命令异常，" + e);
		}
		finally {
			try {
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			}
			catch (IOException e) {
				Log.e("log","IOException异常，" + e);
			}
			if (process != null) {
				process.destroy();
			}
		}

		Log.e("log","命令执行结果 = " + "\nsuccessMsg:" + successMsg + "\nErrorMsg:" + errorMsg);
		String success = successMsg.toString();
		String error = errorMsg.toString();
		//如果执行结果中，包含Success/success，或者无错误信息返回，则认为执行成功
		if (success.contains("Success") || success.contains("success") || TextUtils.isEmpty(error)) {
			result = true;
		}

		return result;
	}

	public static boolean install(Activity activity,String apkPath) {
		//cmd2 是执行安装apk, cmd1是安装后自动打开app
		// String cmd1= "am start -s 包名/"+ 启动Activity.class.getCanonicalName()+ " \n";//可能不行
		// String cmd1 = "am start -n com.ricecake.classboard/com.ricecake.classboard.activity.UebwActivity" + "\n";
		String cmd1 = "am start -a android.intent.action.MAIN -n com.ricecake.classboard/com.ricecake.classboard.activity.UebwActivity" + "\n";
		String cmd2 = "pm install -r \"" + apkPath + "\" && ping -c 6 www.baidu.com && ";
		String cmd = cmd2 + cmd1;

		// Runtime runtime = Runtime.getRuntime();
		try {
			Intent intent = new Intent();
			intent.setClassName("com.ricecake.sutool","com.blank.blankapplication.MainActivity");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("commond",cmd);
			activity.startActivity(intent);
			// Process localProcess = runtime.exec("su");
			// OutputStream localOutputStream = localProcess.getOutputStream();
			// DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
			// localDataOutputStream.writeBytes(cmd);
			// localDataOutputStream.flush();
			// Log.e("log","安装可能成功");
		}
		catch (Exception e) {
			Log.e("log","安装失败，" + e);
			toast(activity,"安装失败");
			return false;
		}
		return true;
	}

	public static boolean rkInstallApk(Activity activity,String apkPath) {
		Intent intent = new Intent();
		intent.setAction("com.fise.silence.install.auto.start");
		intent.putExtra("packageName","com.howfor.receiver");
		intent.putExtra("fisepath",apkPath);
		activity.sendBroadcast(intent);
		return true;
	}

	public static void suInstall(Context activity,String apkPath) {
		String cmd = "pm install -r \"" + apkPath + "\"\n";

		Runtime runtime = Runtime.getRuntime();
		try {
			Process localProcess = runtime.exec("su");
			OutputStream localOutputStream = localProcess.getOutputStream();
			DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
			localDataOutputStream.writeBytes(cmd);
			localDataOutputStream.flush();
			Log.e("log","安装可能成功");
		}
		catch (IOException e) {
			Log.e("log","安装失败，" + e);
			toast(activity,"安装失败");
		}
	}
	/** 复制assets的指定文件到，包名缓存目录 */
	public static void copyAssetFileToCache(Context context,String assetFileName) {
		File cacheDir = context.getCacheDir();
		File targetFile = new File(cacheDir,assetFileName);

		// 如果缓存文件已存在，则不复制
		if (targetFile.exists()) {
			return;
		}

		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			inputStream = assetManager.open(assetFileName);
			outputStream = new FileOutputStream(targetFile);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer,0,length);
			}

			outputStream.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** 检查是否拥有安装应用包的权限，如果没有就前往获取该权限的界面 */
	public static boolean checkInstallApkPermission(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			if (!activity.getPackageManager().canRequestPackageInstalls()) {
				// 弹出权限请求对话框
				Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:" + activity.getPackageName()));
				activity.startActivityForResult(intent,1);
			} else {
				return true;
				// 已经拥有权限，执行相应操作
				// ...
			}
		} else {
			return true;
		}

		return false;
	}
	public static void execCommand2(String cmd) {
		Runtime runtime = Runtime.getRuntime();
		OutputStream localOutputStream = null;
		DataOutputStream localDataOutputStream = null;
		try {
			Process localProcess = runtime.exec("su");
			localOutputStream = localProcess.getOutputStream();
			localDataOutputStream = new DataOutputStream(localOutputStream);
			localDataOutputStream.writeBytes(cmd);
		}
		catch (IOException ignored) {}
		finally {
			try {
				if (localDataOutputStream != null) {
					localDataOutputStream.flush();
					localDataOutputStream.close();
				}
				if (localOutputStream != null) {
					localOutputStream.flush();
					localOutputStream.close();
				}
			}
			catch (IOException ignored) {}
		}
	}

	public static void suKillMyApp() {
		//cmd2 是执行安装apk, cmd1是安装后自动打开app
		String cmd = "am force-stop com.ricecake.classboard" + "\n";
		Runtime runtime = Runtime.getRuntime();
		try {
			Process localProcess = runtime.exec("su");
			OutputStream localOutputStream = localProcess.getOutputStream();
			DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
			localDataOutputStream.writeBytes(cmd);
			localDataOutputStream.flush();
			Log.e("log","退出可能成功");
		}
		catch (IOException ignored) {}
	}

	private static final String[] chineseDigits = {"零","一","二","三","四","五","六","七","八","九"};
	/**
	 * 把阿拉伯数字转成中文数字<p>
	 * 10万次与方法二无差别
	 *
	 * @param number 待转换数字
	 */
	public static String simpleConvertToChinese(int number) {
		StringBuilder result = new StringBuilder();
		while (number > 0) {
			int digit = number % 10;
			number /= 10;
			result.append(chineseDigits[digit]);
		}
		return result.toString();
	}

	/** 把阿拉伯数字转成中文数字 */
	public static String simpleConvertToChinese2(int number) {
		String numberStr = Integer.toString(number);
		StringBuilder result = new StringBuilder();

		for (int i = 0;i < numberStr.length();i++) {
			char digitChar = numberStr.charAt(i);
			int digit = Character.getNumericValue(digitChar);
			result.append(chineseDigits[digit]);
		}

		return result.toString();
	}

	/** 以第一个空格为分隔，获取前面的字符串 */
	public static String getStrBeforeTheFirstSpace(String str) {
		return getStrBeforeTheFirstChar(str," ");
	}
	/** 以第一个给定字符为分隔，获取前面的字符串 */
	public static String getStrBeforeTheFirstChar(String str,String split) {
		int index = str.indexOf(split);
		return (index == -1) ? "" : str.substring(0,index);
	}

	/** 以第一个空格为分隔，获取后面的字符串 */
	public static String getStrBehindTheFirstSpace(String str) {
		return getStrBehindTheFirstChar(str," ");
	}
	/** 以第一个给定字符为分隔，获取前面的字符串 */
	public static String getStrBehindTheFirstChar(String str,String split) {
		int index = str.indexOf(split);
		return (index == -1) ? "" : str.substring(index + 1);
	}

	public static void startOtherAppActivity(Context context,String pkgName,String className) {
		Intent intent = new Intent();
		intent.setClassName(pkgName,className);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {context.startActivity(intent);}
		catch (Exception ignored) {}
	}

	public static boolean isAppInstalled(Context ctx,String PackageName) {
		try {
			PackageManager packageManager = ctx.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(PackageName,0);
			if (info != null) {
				return true;
			}
		}
		catch (Exception e) {
			/// loge(e);
		}
		return false;
	}

	/**
	 * app是否是前台(通过进程的级别判断)
	 *
	 * @param context 上下文
	 * @return true表示前台 false表示后台
	 */
	public static boolean isAppForeground(@Nullable Context context) {
		if (context != null) {
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			if (activityManager != null) {
				List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
				if (processes != null) {
					for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
						if (processInfo != null
								&& processInfo.processName != null
								&& (TextUtils.equals(processInfo.processName,context.getPackageName()))) {
							if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 这个方法不管用，方法检查应用程序当前是否设置为默认启动器
	 *
	 * @return boolean true表示当前设置为默认值，否则为false
	 */
	public static boolean isMyAppLauncherDefault(Context context) {
		final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
		filter.addCategory(Intent.CATEGORY_HOME);

		List<IntentFilter> filters = new ArrayList<>();
		filters.add(filter);

		final String myPackageName = context.getPackageName();
		List<ComponentName> activities = new ArrayList<>();
		final PackageManager packageManager = context.getPackageManager();

		packageManager.getPreferredActivities(filters,activities,null);

		for (ComponentName activity : activities) {
			if (myPackageName.equals(activity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/** 获取当前默认启动器的包名 */
	public static String getDefaultLauncherPackageName(Context context) {
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		final ResolveInfo res = context.getPackageManager().resolveActivity(intent,0);
		if (res == null)
			return "";
		if (res.activityInfo == null) {
			// 应对某些情况下没有默认启动器的情况
			return "";
		}
		if (res.activityInfo.packageName.equals("android")) {
			// 应对某些特殊情况
			return "";
		}
		return res.activityInfo.packageName;
	}

	/**
	 * 这个方法不管用，方法检查应用程序当前是否设置为默认启动器
	 *
	 * @return boolean true表示当前设置为默认值，否则为false
	 */
	public static boolean isMyAppLauncherDefaultBest(Context context) {
		return TextUtils.equals(context.getPackageName(),getDefaultLauncherPackageName(context));
	}

	/**
	 * bitmap 保存为图像文件
	 *
	 * @param filePath 输出图像文件的路径
	 * @param format   格式：JPG。。
	 * @param size     压缩比率 100
	 */
	public static boolean bitmapToFile(Bitmap destBitmap,String filePath,Bitmap.CompressFormat format,int size) {
		if (destBitmap == null) return false;
		try {
			File file = new File(filePath);
			if (file.getParentFile() == null) return false;
			file.getParentFile().mkdirs();
			if (file.exists()) file.delete();
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			destBitmap.compress(format,size,out);
			out.close();
			return true;
		}
		catch (IOException ignored) {}
		return false;
	}

	/**
	 * 获取当前连接的 Wifi 名称
	 * Android10 以下使用，需要有权限。以上需要有位置权限
	 */
	@NonNull
	public static String getCurrentWifiSsid(Context context) {
		WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo != null) {
			String ssid = wifiInfo.getSSID();
			if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
				ssid = ssid.substring(1,ssid.length() - 1);
			}
			Log.d("Current SSID: ",ssid); // 可以用sout替换hh
			return ssid;
		}
		return "未知";
	}

	/**
	 * 获取一些 Wifi 信息
	 * Android10 以下使用，需要有权限。以上需要有位置权限
	 */
	public static WifiInfo getWifiConnectionInfo(Context context) {
		WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		return wifimanager.getConnectionInfo();
	}
	/** 判断字符串是否为纯字母数字 */
	public static boolean isAlphanumeric(@NonNull String text) {
		Pattern p = Pattern.compile("[^a-zA-Z0-9]");
		return p.matcher(text).find();
	}

	public static void changeCurrentLanguage(Context context) {
		String languageCode = "fr"; // 目标语言的代码
		Locale locale = new Locale(languageCode);
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		Configuration config = resources.getConfiguration();
		config.locale = locale;
		resources.updateConfiguration(config,metrics);
	}

	/** 叠加两张透明图片，展台专用 */
	public static Bitmap toConformBitmap2(Bitmap bgBitmap,Bitmap fgBitmap) {
		// 创建一个新的Bitmap，大小与背景相同
		Bitmap resultBitmap = Bitmap.createBitmap(fgBitmap.getWidth(),fgBitmap.getHeight(),Bitmap.Config.ARGB_8888);
		float leftPosition = (fgBitmap.getWidth() - bgBitmap.getWidth()) / 2f;
		// 创建一个Canvas对象，将结果Bitmap绘制在上面
		Canvas canvas = new Canvas(resultBitmap);

		// 将背景Bitmap绘制到Canvas
		canvas.drawBitmap(fgBitmap,0,0,null);

		// 创建一个Paint对象，设置透明度和混合模式
		Paint paint = new Paint();
		paint.setAlpha(255); // 设置透明度，255为不透明
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER)); // 设置混合模式为DST_OVER，实现叠加效果

		// 将前景Bitmap绘制到Canvas，使用设置的Paint
		canvas.drawBitmap(bgBitmap,leftPosition,0,paint);
		return resultBitmap;
	}
}

