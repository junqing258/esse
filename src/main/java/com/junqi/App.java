package com.junqi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;

public class App {

	static String INPUT;
	static String OUTPUT; 
	static String OUTCONF;
	static String INCLUDE;
	static String PUBDIR;
	

	static ArrayList<String> imageList = new ArrayList<String>();
	static int count = 0;

	public static void main(String[] args) {
		startCompress();
	}
	
	public static void startCompress() {
		try {
			Properties props = getProperty("resources/config.properties");
			INPUT = props.getProperty("input");
			OUTCONF = props.getProperty("outconf");
			INCLUDE = props.getProperty("include");
			PUBDIR = props.getProperty("pubdir");
			final int cupNum = Integer.parseInt(props.getProperty("cupNum"));

			InputStream is = new FileInputStream(OUTCONF);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			reader.close();
			is.close();

			final String OUTPUT = line.endsWith("/") ? line : line + "/";
			
//			traverseFolder(INPUT);

			final ArrayList<String> imageList = parseYaml(INCLUDE);

			for (int i = 0; i < cupNum; i++) {
				Runnable task = new Runnable() {
					public void run() {
						int c;
						while ((c = addCount()) < imageList.size()) {
							String name = imageList.get(c);//res.substring(INPUT.length());
							String res = INPUT + name;
							String target = OUTPUT + PUBDIR + name;
							try {
								System.out.println("compresse:" + res);
								compressedFile(res, target);
								System.out.println("compressed:" + target);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

				};
				new Thread(task).start();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	public static synchronized int addCount() {
		System.out.println("count:" + count);
		return count++;
	}

	public static Properties getProperty(String filePath) throws IOException {
		Properties prop = new Properties();
		InputStream in = new FileInputStream(new File(filePath));
		prop.load(in);
		in.close();
		return prop;
	}

	/**
	 * 遍历文件目录
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static void traverseFolder(String path) {
		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (null == files || files.length == 0) {
				System.out.println("文件夹是空的!");
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						System.out.println("============>>" + file2.getAbsolutePath());
						traverseFolder(file2.getAbsolutePath());
					} else {
						String fileStr = file2.getAbsolutePath();
						String pattern = ".*\\.png";
						if (Pattern.matches(pattern, fileStr)) {
							String str = fileStr.replaceAll("\\\\", "/").substring(INPUT.length());
							imageList.add(str);
						}
					}
				}
			}
		} else {
			System.out.println("文件不存在!");
		}
	}

	/**
	 * 解析yaml
	 * 
	 * @param path
	 * @return 
	 * @throws IOException
	 */
	public static ArrayList<String> parseYaml(String path) throws IOException {
		Yaml yaml = new Yaml();
		File dumpFile = new File(path);
		// Object load = yaml.load(new FileInputStream(dumpFile));
		// System.out.println(yaml.dump(load));
		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) yaml.load(new FileInputStream(dumpFile));
		for (String element : list) {
			System.out.println(element);
		}
		return list;
	}

	public static void compressedFile(String resourcesPath, String targetPath) throws IOException {
		File targetDir = new File(targetPath.replaceFirst("\\w+.png$", ""));
		if (!targetDir.exists()) {
			System.out.println("mkdir:" + targetDir);
			targetDir.mkdirs();
		}
		
		File targetFile = new File(targetPath);
		if (targetFile.exists()) {
			 if (targetFile.delete()) {
                System.out.println("删除文件" + targetPath);
            }
		}

		final PngOptimizer optimizer = new PngOptimizer();
//		optimizer.setCompressor("zopfli", null);

		final InputStream in = new BufferedInputStream(new FileInputStream(resourcesPath));
		final PngImage image = new PngImage(in);

		final PngImage optimizedImage = optimizer.optimize(image);
		final ByteArrayOutputStream optimizedBytes = new ByteArrayOutputStream();
		optimizedImage.writeDataOutputStream(optimizedBytes);
		optimizedImage.export(targetPath, optimizedBytes.toByteArray());

		in.close();
		optimizedBytes.close();
		
		File targetAltls = new File(resourcesPath.replaceFirst("\\.png$", ".json"));
//		System.out.println("复制文件:" + targetAltls + "::::" + targetDir);
		if (targetAltls.exists()) {
			 FileUtils.	copyFileToDirectory(targetAltls, targetDir);
			 System.out.println("复制文件:" + targetAltls);
		}
	}

}
