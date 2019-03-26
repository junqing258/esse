package com.junqi.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import com.badlogic.gdx.tools.texturepacker.ImageProcessor;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Alias;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Page;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Rect;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.tools.texturepacker.TexturePackerFileProcessor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TP {
	
	public static void main (String[] args) throws Exception {
//		TexturePacker.process(inputDir, outputDir, packFileName);
		Settings settings = new Settings();
		settings.maxWidth = 512;
		settings.maxHeight = 512;
		LayaTexturePacker.process(settings, "../images", "../game-android/assets", "game");
		
		

//		new LwjglApplication(new Game(), "Game", 320, 480);
		
//		TextureAtlas atlas;
//		atlas = new TextureAtlas(Gdx.files.internal("packedimages/pack.atlas"));
//		AtlasRegion region = atlas.findRegion("imagename");
//		Sprite sprite = atlas.createSprite("otherimagename");
//		NinePatch patch = atlas.createPatch("patchimagename");
	}
}



class LayaTexturePacker extends TexturePacker {
	
	public LayaTexturePacker(File rootDir, Settings settings) {
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
	
	public void pack (File outputDir, String packFileName) {
		super.pack(outputDir, packFileName);
		this.formateJSONFile(outputDir, packFileName);
		
	}
	
	public void formateJSONFile(File outputDir, String packFileName) {
		System.out.println("打包完成:" + outputDir + packFileName);
	}
	
	
}
