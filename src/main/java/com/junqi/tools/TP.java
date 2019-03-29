package com.junqi.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.ProgressListener;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.tools.texturepacker.TexturePackerFileProcessor;

public class TP {
	
	public void main (String[] args) throws Exception {

		Settings settings = new Settings();
		settings.maxWidth = 512;
		settings.maxHeight = 512;
//		settings.combineSubdirectories = true;
		
		String input = "F:\\GitHub\\moba-test\\laya\\assets\\comp";
		String output = "F:\\GitHub\\moba-test\\bin\\out";
		String packFileName = "comp";
		try {
			LayaTexturePackerFileProcessor processor = new LayaTexturePackerFileProcessor(settings, packFileName, null);
			FilenameFilter inputFilter = new FilenameFilter() {
				public boolean accept(File f, String fname){
                    return fname.toLowerCase().endsWith(".png") || fname.toLowerCase().endsWith(".jpg");
                }
			};
			processor.setInputFilter(inputFilter);
			processor.process(new File(input), new File(output));
		} catch (Exception ex) {
			throw new RuntimeException("Error packing images.", ex);
		}
		
		

//		new LwjglApplication(new Game(), "Game", 320, 480);
		
//		TextureAtlas atlas;
//		atlas = new TextureAtlas(Gdx.files.internal("packedimages/pack.atlas"));
//		AtlasRegion region = atlas.findRegion("imagename");
//		Sprite sprite = atlas.createSprite("otherimagename");
//		NinePatch patch = atlas.createPatch("patchimagename");
	}
}


class LayaTexturePackerFileProcessor extends TexturePackerFileProcessor {
	
	private ProgressListener progress;

	public LayaTexturePackerFileProcessor(Settings settings, String packFileName, ProgressListener progress) {
		super(settings, packFileName, progress);
	}
	
	protected TexturePacker newTexturePacker (File root, Settings settings) {
		TexturePacker packer = new LayaTexturePacker(root, settings);
		packer.setProgressListener(progress);
		return packer;
	}
	
	protected void processDir (Entry entryDir, ArrayList<Entry> files) throws Exception {
		super.processDir(entryDir, files);
	}

}



class LayaTexturePacker extends TexturePacker {
	
	public LayaTexturePacker(File rootDir, Settings settings) {
		super(rootDir, settings);
	}
	
	
	public void pack (File outputDir, String packFileName) {
		super.pack(outputDir, packFileName);
//		this.formateJSONFile(outputDir, packFileName);
	}
	
	
	public void formateJSONFile(File outputDir, String packFileName) {
		int n = 1/* settings.scale.length */;
		for (int i = 0; i < n; i++) {
//			String scaledPackFileName = settings.getScaledPackFileName(packFileName, i);
			File packFile = new File(outputDir, ".json");
			try {
				Writer writer = new OutputStreamWriter(new FileOutputStream(packFile, true), "UTF-8");
				System.out.println("打包:" + packFile);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	static public String getAtlasName (String name, boolean flattenPaths) {
		return flattenPaths ? new FileHandle(name).name() : name;
	}
	
}
