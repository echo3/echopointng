package echopointng.progressbar;

/* 
 * This file is part of the Echo Point Project.  This project is a collection
 * of Components that have extended the Echo Web Application Framework.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import nextapp.echo2.app.AwtImageReference;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import echopointng.EPNG;
import echopointng.ProgressBar;
import echopointng.util.ColorKit;
import echopointng.util.FontKit;

/**
 * The default ProgressBar renderer
 */
public class DefaultProgressBarRenderer implements ProgressBarRenderer, java.io.Serializable {

	int borderSize = 0;

	int height = 0;

	int hspacer = 2;

	private boolean isHorz;

	ProgressBar pb = null;

	int vspacer = 2;

	int width = 0;

	/**
	 * if we are HORZ then we draw from left to right and increase the x values
	 * 
	 * if we are VERT, we draw from bottom to top and decrease the y values
	 */
	private void _drawBlocks(Graphics2D g, int numBlocks, int blockWidth, int blockHeight, int partialDraw, java.awt.Color clr) {

		int x = 0 + hspacer + borderSize;
		int y = 0 + vspacer + borderSize;
		if (!_isHorz()) {
			y = height - borderSize - blockHeight;
		}
		for (int i = 0; i < numBlocks; i++) {
			g.setColor(clr);
			//
			// we adjust the paint block so that it leaves a gap on
			// one side. 1 blockWidth includes this spacer.
			int drawWidth = blockWidth;
			int drawHeight = blockHeight;
			int drawX = x;
			int drawY = y;

			if (_isHorz()) {
				drawWidth -= hspacer;
				//
				// check if we are on the last block
				if (i == numBlocks - 1 && partialDraw > 0) {
					drawWidth = partialDraw;
				}
				x += blockWidth;
			} else {

				drawHeight -= vspacer;
				//
				// check if we are on the last block
				if (i == numBlocks - 1 && partialDraw > 0) {
					drawY = y + (drawHeight - partialDraw);
					drawHeight = partialDraw;
				}
				y -= blockHeight;
			}
			g.fillRect(drawX, drawY, drawWidth, drawHeight);

		}

	}

	/**
	 * Draw text center around the given height and width
	 */
	private void _drawText(Graphics2D g, float w, float h, String s, Color foreGround) {
		if (s == null) {
			return;
		}
		FontRenderContext frc = g.getFontRenderContext();
		Font fnt = FontKit.makeAwtFont(_findFont(pb), new Font("Verdana", Font.BOLD, 12));

		g.setColor(foreGround);
		g.setFont(fnt);

		TextLayout tl = new TextLayout(s, fnt, frc);
		Rectangle rect = tl.getBounds().getBounds();

		//float x = (width - (float) rect.getWidth()) / 2;
		float y = (height - (float) rect.getHeight()) / 2;
		y = y + (float) rect.getHeight();

		int rotateDegress = 0;
		// draw it horizntally if we can cause its easier to read
		if (_isHorz() || rect.getWidth() < width) {
			rotateDegress = 0;
		} else {
			rotateDegress = 90;
		}

		float fx = w / 2; // << -- my points of interest
		float fy = h / 2; // << -- my points of interest

		// text is draw from the base line so adjust to its
		// logical bounding box, whcih is what we want centered
		float x1 = fx - (rect.width / 2);
		float y1 = fy + (rect.height / 2) - tl.getDescent();

		// save the current transform
		AffineTransform atPrev = g.getTransform();

		// and make a new one around of point of interest
		AffineTransform at = new AffineTransform();
		at.setToIdentity();
		at.rotate(Math.toRadians(rotateDegress), fx, fy);
		at.translate(x1, y1);

		g.setTransform(at);
		tl.draw(g, 0, 0);

		// debug - this draws a box around the text
		// helps with visualising centering
		//g.setColor(Color.black);
		//g.draw(rect);

		g.setTransform(atPrev);
	}

	/**
	 * Loops round and finds a font
	 */
	private nextapp.echo2.app.Font _findFont(Component c) {

		nextapp.echo2.app.Font fnt = c.getFont();
		while (c != null && fnt == null) {
			c = c.getParent();
			if (c != null)
				fnt = c.getFont();
		}
		return fnt;
	}

	/**
	 * returns true if we are a HORIZONTAL PB
	 */
	private boolean _isHorz() {
		return isHorz;
	}

	/**
	 * This method can be used by subclasses to customise how the
	 * <code>ProgressBar</code> is to be displayed.
	 * <p>
	 * The subclass must take into account the properties of the ProgressBar at
	 * the time the image is to be drawn. For example the orientation, the
	 * progress string etc...
	 * <p>
	 * The default implementation create a AwtImageReference for display on the
	 * client via AWT graphics calls.
	 *  
	 */
	public ImageReference drawProgressBar(ProgressBar progressBar) {

		Style fallbackStyle = EPNG.getFallBackStyle(progressBar);

		pb = progressBar;

		Integer intObj = (Integer) getRP(pb, ProgressBar.PROPERTY_ORIENTATION, fallbackStyle);
		isHorz = ProgressBar.ORIENTATION_HORIZONTAL == (intObj == null ? ProgressBar.ORIENTATION_HORIZONTAL : intObj.intValue());

		intObj = (Integer) getRP(pb, ProgressBar.PROPERTY_NUMBER_OF_BLOCKS, fallbackStyle);
		int TOTAL_NUM_BLOCKS = (intObj == null ? 10 : intObj.intValue());

		Border border = (Border) getRP(pb, ProgressBar.PROPERTY_BORDER, fallbackStyle);
		nextapp.echo2.app.Color borderColor = (border == null ? null : border.getColor());

		java.awt.Color clrBorder = ColorKit.makeAwtColor(borderColor, Color.black);
		java.awt.Color clrBackground = ColorKit.makeAwtColor(getRPColor(pb, ProgressBar.PROPERTY_BACKGROUND, fallbackStyle), Color.white);
		java.awt.Color clrForeGround = ColorKit.makeAwtColor(getRPColor(pb, ProgressBar.PROPERTY_FOREGROUND, fallbackStyle), Color.black);
		java.awt.Color clrCompleted = ColorKit.makeAwtColor(getRPColor(pb, ProgressBar.PROPERTY_COMPLETED_COLOR, fallbackStyle), Color.orange);
		java.awt.Color clrUncompleted = ColorKit.makeAwtColor(getRPColor(pb, ProgressBar.PROPERTY_UNCOMPLETED_COLOR, fallbackStyle), Color.lightGray);

		hspacer = 2;
		vspacer = 2;
		if (border != null && border.getSize() != null) {
			borderSize = border.getSize().getValue();
		} else {
			borderSize = 0;
		}
		borderSize = 0;
		
		Extent widthExt = (Extent) getRP(pb, ProgressBar.PROPERTY_WIDTH, fallbackStyle);
		if (widthExt != null) {
			width = widthExt.getValue();
		} else {
			width = 146;
		}
		Extent heightExt = (Extent) getRP(pb, ProgressBar.PROPERTY_HEIGHT, fallbackStyle);
		if (heightExt != null) {
			height = heightExt.getValue();
		} else {
			height = 30;
		}

		//
		// the total width/height must divisible by TOTAL_NUM_BLOCKS
		// otherwize will have pixels at the end left over just hanging there
		//
		int blockHeight = 0;
		int blockWidth = 0;
		int leftOver = 0;

		if (_isHorz()) {
			int innerWidth = width - (hspacer * 1) - (borderSize * 2);
			int innerHeight = height - (vspacer * 2) - (borderSize * 2);
			blockHeight = innerHeight;
			blockWidth = innerWidth / TOTAL_NUM_BLOCKS;
			leftOver = innerWidth - (blockWidth * TOTAL_NUM_BLOCKS);
			if (leftOver > 0) {
				width -= leftOver;
				leftOver = 0;
			}

		} else {
			int innerWidth = width - (hspacer * 2) - (borderSize * 2);
			int innerHeight = height - (vspacer * 1) - (borderSize * 2);

			blockWidth = innerWidth;
			blockHeight = innerHeight / TOTAL_NUM_BLOCKS;
			leftOver = innerHeight - (blockHeight * TOTAL_NUM_BLOCKS);
			if (leftOver > 0) {
				height -= leftOver;
				leftOver = 0;
			}
		}

		//
		// create our Image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		if (borderSize > 0) {
			g.setColor(clrBorder);
			g.fillRect(0, 0, width, height);

			g.setColor(java.awt.Color.white);
			g.fillRect(borderSize, borderSize, width - (borderSize * 2), height - (borderSize * 2));
		} else {
			g.setColor(clrBackground);
			g.fillRect(0, 0, width, height);
		}

		////////////////////////////////
		// draw each inactive block
		_drawBlocks(g, TOTAL_NUM_BLOCKS, blockWidth, blockHeight, 0, clrUncompleted);

		////////////////////////////////
		// draw each active block up to the percent done
		// and no more. No need to draw if its is zero percent!
		//
		int partialDraw = 0;
		double pcDoneOrig = 100 * pb.getPercentComplete();
		double pcDone = Math.round(pcDoneOrig);
		pcDone = pcDone / 100;

		if (pcDone > 0) {
			double numBlocks = TOTAL_NUM_BLOCKS * pcDone;
			double rem = numBlocks - Math.floor(numBlocks);
			if (rem > 0) {
				numBlocks += 1;
				if (_isHorz())
					partialDraw = (int) (rem * (blockWidth - hspacer));
				else
					partialDraw = (int) (rem * (blockHeight - vspacer));
			}
			_drawBlocks(g, (int) Math.floor(numBlocks), blockWidth, blockHeight, partialDraw, clrCompleted);
		}

		boolean isPainted = Boolean.TRUE.equals(getRP(pb,ProgressBar.PROPERTY_PROGRESS_STRING_PAINTED, fallbackStyle));
		if (isPainted) {
			String text = (String) getRP(pb,ProgressBar.PROPERTY_PROGRESS_STRING, fallbackStyle);
			_drawText(g, width, height, text, clrForeGround);
		}

		g.dispose();
		return new AwtImageReference(image);
	}

	private Object getRP(Component c, String propertyName, Style fallbackStyle) {
		Object value = c.getRenderProperty(propertyName);
		if (value == null && fallbackStyle != null) {
			value = fallbackStyle.getProperty(propertyName);
		}
		return value;
	}

	private nextapp.echo2.app.Color getRPColor(Component c, String propertyName, Style fallbackStyle) {
		Object value = c.getRenderProperty(propertyName);
		if (value == null && fallbackStyle != null) {
			value = fallbackStyle.getProperty(propertyName);
		}
		return (nextapp.echo2.app.Color) value;
	}

	/**
	 * A toString method for debugging
	 */
	public String toString() {
		return "w=" + width + " h=" + height + " hs=" + hspacer + " vs=" + vspacer + " bs=" + borderSize;
	}
}
