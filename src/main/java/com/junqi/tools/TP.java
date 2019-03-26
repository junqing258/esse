package com.junqi.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Page;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Rect;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.tools.texturepacker.TexturePackerFileProcessor;

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
	private Settings settings;
	
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
	
	private void writeRect (Writer writer, Page page, Rect rect, String name) throws IOException {
		writer.write(Rect.getAtlasName(name, settings.flattenPaths) + "\n");
		writer.write("  rotate: " + rect.rotated + "\n");
		writer
			.write("  xy: " + (page.x + rect.x) + ", " + (page.y + page.height - rect.y - (rect.height - settings.paddingY)) + "\n");

		writer.write("  size: " + rect.regionWidth + ", " + rect.regionHeight + "\n");
		if (rect.splits != null) {
			writer.write("  split: " //
				+ rect.splits[0] + ", " + rect.splits[1] + ", " + rect.splits[2] + ", " + rect.splits[3] + "\n");
		}
		if (rect.pads != null) {
			if (rect.splits == null) writer.write("  split: 0, 0, 0, 0\n");
			writer.write("  pad: " + rect.pads[0] + ", " + rect.pads[1] + ", " + rect.pads[2] + ", " + rect.pads[3] + "\n");
		}
		writer.write("  orig: " + rect.originalWidth + ", " + rect.originalHeight + "\n");
		writer.write("  offset: " + rect.offsetX + ", " + (rect.originalHeight - rect.regionHeight - rect.offsetY) + "\n");
		writer.write("  index: " + rect.index + "\n");
	}
}
