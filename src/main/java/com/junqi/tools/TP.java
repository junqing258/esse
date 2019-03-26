package com.junqi.tools;

import java.io.File;
import java.io.FilenameFilter;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.tools.texturepacker.TexturePackerFileProcessor;

public class TP {
	
	public static void main (String[] args) throws Exception {
//		TexturePacker.process(inputDir, outputDir, packFileName);
		Settings settings = new Settings();
		settings.maxWidth = 512;
		settings.maxHeight = 512;
		MyTexturePacker.process(settings, "../images", "../game-android/assets", "game");

//		new LwjglApplication(new Game(), "Game", 320, 480);
		
//		TextureAtlas atlas;
//		atlas = new TextureAtlas(Gdx.files.internal("packedimages/pack.atlas"));
//		AtlasRegion region = atlas.findRegion("imagename");
//		Sprite sprite = atlas.createSprite("otherimagename");
//		NinePatch patch = atlas.createPatch("patchimagename");
	}
}



class MyTexturePacker extends TexturePacker {
	
	public MyTexturePacker(File rootDir, Settings settings) {
		super(rootDir, settings);
	}

	static public void process (Settings settings, String input, String output, String packFileName,
		final ProgressListener progress) {
		
		try {
			TexturePackerFileProcessor processor = new TexturePackerFileProcessor(settings, packFileName, progress);
			FilenameFilter inputFilter = new FilenameFilter() {
				public boolean accept(File f, String fname){
                    return fname.toLowerCase().endsWith(".jpg") || fname.toLowerCase().endsWith(".jpeg") || fname.toLowerCase().endsWith(".webp");
                }
			};
			processor.setInputFilter(inputFilter);
			processor.process(new File(input), new File(output));
		} catch (Exception ex) {
			throw new RuntimeException("Error packing images.", ex);
		}
	}
}
