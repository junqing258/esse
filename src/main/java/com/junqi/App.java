package com.junqi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;

public class App {

	static String INPUT;
	static String OUTPUT;

	static ArrayList<String> imageIn = new ArrayList<String>();

	static int count = 0;

	public static void main(String[] args) {
		App app = new App();

		Properties props;
		try {
			props = getProperty("resources/config.properties");

			INPUT = props.getProperty("input");
			OUTPUT = props.getProperty("output");

			app.traverseFolder(INPUT);

			for (int i = 0; i < 4; i++) {
				Runnable task = new Runnable() {
					@Override
					public void run() {
						int c;
						while ((c = addCount()) < imageIn.size() - 1) {
							String res = imageIn.get(c);
							String name = res.substring(INPUT.length());
							String target = OUTPUT + name;
							System.out.println("文件:" + res);
							try {
								compressedFile(res, target);
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
	public void traverseFolder(String path) {
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
							imageIn.add(fileStr);
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
	 * @throws IOException
	 */
	public void parseYaml(String path) throws IOException {
		Yaml yaml = new Yaml();
		File dumpFile = new File(path);
		// Object load = yaml.load(new FileInputStream(dumpFile));
		// System.out.println(yaml.dump(load));
		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) yaml.load(new FileInputStream(dumpFile));
		for (String element : list) {
			System.out.println(element);
		}
	}

	public static void compressedFile(String resourcesPath, String targetPath) throws IOException {
		File targetFile = new File(targetPath.replaceFirst("\\w+.png$", ""));
		if (!targetFile.exists()) {
			System.out.println("mkdir：" + targetFile);
			targetFile.mkdirs();
		}

		final PngOptimizer optimizer = new PngOptimizer();
		optimizer.setCompressor("zopfli", null);

		final InputStream in = new BufferedInputStream(new FileInputStream(resourcesPath));
		final PngImage image = new PngImage(in);

		final PngImage optimizedImage = optimizer.optimize(image);
		final ByteArrayOutputStream optimizedBytes = new ByteArrayOutputStream();
		optimizedImage.writeDataOutputStream(optimizedBytes);
		optimizedImage.export(targetPath, optimizedBytes.toByteArray());

		in.close();
		optimizedBytes.close();
	}

}
