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
	
	private void writePackFile (File outputDir, String scaledPackFileName, Array<Page> pages) throws IOException {
		File packFile = new File(outputDir, scaledPackFileName + settings.atlasExtension);
		File packDir = packFile.getParentFile();
		packDir.mkdirs();
		
//		final TexturePacker.Rect Rect = TexturePacker.Rect;

		if (packFile.exists()) {
			// Make sure there aren't duplicate names.
			TextureAtlasData textureAtlasData = new TextureAtlasData(new FileHandle(packFile), new FileHandle(packFile), false);
			for (Page page : pages) {
				for (Rect rect : page.outputRects) {
					String rectName = Rect.getAtlasName(rect.name, settings.flattenPaths);
					for (Region region : textureAtlasData.getRegions()) {
						if (region.name.equals(rectName)) {
							throw new GdxRuntimeException(
								"A region with the name \"" + rectName + "\" has already been packed: " + rect.name);
						}
					}
				}
			}
		}

		Writer writer = new OutputStreamWriter(new FileOutputStream(packFile, true), "UTF-8");
		for (Page page : pages) {
			writer.write("\n" + page.imageName + "\n");
			writer.write("size: " + page.imageWidth + "," + page.imageHeight + "\n");
			writer.write("format: " + settings.format + "\n");
			writer.write("filter: " + settings.filterMin + "," + settings.filterMag + "\n");
			writer.write("repeat: " + getRepeatValue() + "\n");

			page.outputRects.sort();
			for (Rect rect : page.outputRects) {
				writeRect(writer, page, rect, rect.name);
				Array<Alias> aliases = new Array(rect.aliases.toArray());
				aliases.sort();
				for (Alias alias : aliases) {
					//	TODO: fix it
					Rect aliasRect = new Rect();
					aliasRect.set(rect);
					alias.apply(aliasRect);
					writeRect(writer, page, aliasRect, alias.name);
				}
			}
		}
		writer.close();
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
	
	private String getRepeatValue () {
		if (settings.wrapX == TextureWrap.Repeat && settings.wrapY == TextureWrap.Repeat) return "xy";
		if (settings.wrapX == TextureWrap.Repeat && settings.wrapY == TextureWrap.ClampToEdge) return "x";
		if (settings.wrapX == TextureWrap.ClampToEdge && settings.wrapY == TextureWrap.Repeat) return "y";
		return "none";
	}
	
	/*static public class Rect extends TexturePacker.Rect {

		public Rect() {
			super();
		}
	}*/
}
