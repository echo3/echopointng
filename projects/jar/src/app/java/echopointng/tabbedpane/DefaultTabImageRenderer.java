package echopointng.tabbedpane;
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

import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.AttributedString;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import echopointng.TabbedPane;
import echopointng.image.ImageKit;
import echopointng.image.TextImageReference;
import echopointng.ui.resource.Resources;
import echopointng.util.ColorKit;
import echopointng.util.reflect.ReflectionKit;

/**
 * <code>DefaultTabImageRenderer</code> is a default implementation
 * of <code>TabImageRenderer</code> that uses a "background template image"
 * and TextImageReference to return "dynamic" tab image representations.
 */
public class DefaultTabImageRenderer implements TabImageRenderer, Serializable {
	
	/** The default insets used by the DefaultTabImageRenderer */ 
	public static final Insets DEFAULT_INSETS = new Insets(5);
	
	private ImageReference leadInImage;
	private ImageReference leadOutImage;
	private transient Image activeTabImage;
	private transient Image inactiveTabImage;
	private Insets insets = DEFAULT_INSETS;
	private int borderWidth = 0;
	private Color borderColor = null;
	private Color activeColor = ColorKit.makeColor(0xC0,0xC0,0xC0);
	private Color inactiveColor = ColorKit.makeColor(0xE0,0xE0,0xE0);
	private int tabPlacement = Alignment.TOP;
	
	/**
	 * 
	 * Constructs a <code>DefaultTabImageRenderer</code> with default 
	 * background tab images.
	 *
	 */
	public DefaultTabImageRenderer() {
		// load defaults
		this.activeTabImage = _loadResImage("tabTemplateC0C0C0.gif");
		this.inactiveTabImage = _loadResImage("tabTemplateE0E0E0.gif");
	}

	/**
	 * 
	 * Constructs a <code>DefaultTabImageRenderer</code> with the specified
	 * background tab images.  The leadIn andleadOut ImageReferences will
	 * be null. 
	 * 
	 * @param activeTabImage - the java.awt.Image to be used for active tabs
	 * @param inactiveTabImage - the java.awt.Image to be used for inactive tabs
	 */
	public DefaultTabImageRenderer(Image activeTabImage, Image inactiveTabImage) {
		if (activeTabImage == null)
			throw new IllegalArgumentException("The active tab image must be non null!");
		if (inactiveTabImage == null)
			throw new IllegalArgumentException("The inactive tab image must be non null!");
		this.activeTabImage = activeTabImage;
		this.inactiveTabImage = inactiveTabImage;
	}

	/**
	 * @see echopointng.tabbedpane.TabImageRenderer#getLeadInImage(echopointng.TabbedPane)
	 */
	public ImageReference getLeadInImage(TabbedPane tp) {
		return leadInImage;
	}

	/**
	 * @see echopointng.tabbedpane.TabImageRenderer#getLeadOutImage(echopointng.TabbedPane)
	 */
	public ImageReference getLeadOutImage(TabbedPane tp) {
		return leadOutImage;
	}

	/**
	 * @see echopointng.tabbedpane.TabImageRenderer#getTabImage(echopointng.TabbedPane, int, nextapp.echo2.app.Component, boolean)
	 */
	public ImageReference getTabImage(TabbedPane tp, int tabIndex, Component tabComponent, boolean isSelected) {
		AttributedString as; 
		Image imagetoUse;
		String tabText = (String) ReflectionKit.invokeIfPresent("getText", new Class[0], String.class, tabComponent, new Object[0]);
		if (tabText == null) {
			tabText = "Tab " + tabIndex;
		}
		
		if (isSelected) {
			as = TextImageReference.getAttributedString(tabText, tabComponent.getForeground(), tp.getFont());
			imagetoUse = activeTabImage;			
		} else {
			as = TextImageReference.getAttributedString(tabText, tabComponent.getForeground(), tp.getFont());
			imagetoUse = inactiveTabImage;			
		}
		TextImageReference imageRef = new TextImageReference(as,imagetoUse, TextImageReference.SCALE_SPLICE_H_THEN_V);			
		if (this.insets != null)
			imageRef.setInsets(this.insets);
		return imageRef;
	}

	/**
	 * @see echopointng.tabbedpane.TabImageRenderer#getImageBorderWidth()
	 */
	public int getImageBorderWidth() {
		return borderWidth;
	}

	/**
	 * 
	 * @see echopointng.tabbedpane.TabImageRenderer#getImageBorderColor()
	 */
	public Color getImageBorderColor() {
		return borderColor;
	}

	/**
	 * Returns the current active tab java.awt.Image 
	 * @return the current active tab java.awt.Image
	 */
	public Image getActiveTabImage() {
		return activeTabImage;
	}

	/** 
	 * Returns the current inactive tab java.awt.Image 
	 * @return the current inactive tab java.awt.Image
	 */
	public Image getInactiveTabImage() {
		return inactiveTabImage;
	}

	/** 
	 * Returns the current lead in ImageReference 
	 * @return the current lead in ImageReference
	 */
	public ImageReference getLeadInImage() {
		return leadInImage;
	}

	/** 
	 * Returns the current lead out ImageReference 
	 * @return the current lead out ImageReference
	 */
	public ImageReference getLeadOutImage() {
		return leadOutImage;
	}

	/** 
	 * Sets the java.awt.Image to be used as a background for 
	 * active tabs.
	 * 
	 * @param image - the java.awt.Image to be used as a background for 
	 * active tabs 
	 */
	public void setActiveTabImage(Image image) {
		if (image == null)
			throw new IllegalArgumentException("The active tab image must be non null!");
		activeTabImage = image;
	}

	/** 
	 * Sets the java.awt.Image to be used as a background for 
	 * inactive tabs.
	 * 
	 * @param image - the java.awt.Image to be used as a background for 
	 * inactive tabs 
	 */
	public void setInactiveTabImage(Image image) {
		if (image == null)
			throw new IllegalArgumentException("The inactive tab image must be non null!");
		inactiveTabImage = image;
	}

	/** 
	 * Sets the ImageReference to be used for the lead in image
	 * @param reference the ImageReference to be used for the lead in image
	 */
	public void setLeadInImage(ImageReference reference) {
		leadInImage = reference;
	}

	/** 
	 * Sets the ImageReference to be used for the lead out image
	 * @param reference the ImageReference to be used for the lead out image
	 */
	public void setLeadOutImage(ImageReference reference) {
		leadOutImage = reference;
	}

	/** 
	 * Returns the image insets that will be appplied to any 
	 * TextImageReferences returned.
	 * @return the image insets 
	 */
	public Insets getInsets() {
		return insets;
	}

	/** 
	 * Sets the image insets that will be appplied to aby 
	 * TextImageReferences returned.
	 * @param insets
	 */
	public void setInsets(Insets insets) {
		this.insets = insets;
	}
	/** 
	 * Sets the border color that may be around any of the tab images
	 * 
	 * @param color - the border color that may be around any of the tab images
	 */
	public void setBorderColor(Color color) {
		borderColor = color;
	}

	/** 
	 * Sets the border width that may be around any of the tab images
	 * 
	 * @param i - the border width that may be around any of the tab images
	 */
	public void setBorderWidth(int i) {
		borderWidth = i;
	}
	
	/**
	 * This will set properties in the TabbedPane that are sutiable for this
	 * TabImageRenderer.  This can include things like border color,
	 * active background and inactive background colors, and tab placement.
	 * <p>
	 * This method is best used if you created the DefaultTabImageRenderer
	 * with the getInstance() methods.
	 *  
	 * @param tp the TabbedPane to be primed with values
	 */
	public void prime(TabbedPane tp) {
		if (tp == null)
			throw new IllegalArgumentException("The TabbedPane must be non null!");
			
		Border border = new Border(borderWidth,this.borderColor,Border.STYLE_SOLID);
		tp.setBorder(border);
		tp.setBackground(this.inactiveColor);
		tp.setTabPlacement(this.tabPlacement);
		int selectedIndex = tp.getSelectedIndex();
		if (selectedIndex >= 0) {
			Component content = tp.getModel().getTabContentAt(selectedIndex);
			if (content != null) {
				content.setBackground(this.activeColor);
			}
		}
	}

	/**
	 * This helper method will construct a DefaultTabImageRenderer with default
	 * colors of 0xC0C0C0 for the active tabs, 0xE0E0E0 for the inactive tabs
	 * and a border color of 0x000000 and a tab placement of Alignment.TOP
	 *  
	 * @return a primed DefaultTabImageRenderer 
	 */
	public static DefaultTabImageRenderer getInstance() {
		return getInstance(ColorKit.makeColor("#C0C0C0"),ColorKit.makeColor("#E0E0E0"),Color.BLACK,Alignment.TOP);
	}

	/**
	 * This helper method will construct a DefaultTabImageRenderer with default
	 * colors of 0xC0C0C0 for the active tabs, 0xE0E0E0 for the inactive tabs
	 * and a border color of 0x000000.  You need to specifiy the tab placement.
	 *  
	 * @param tabPlacement - can be EchoConstants.TOP or EchoConstants.BOTTOM
	 * @return a primed DefaultTabImageRenderer 
	 */
	public static DefaultTabImageRenderer getInstance(int tabPlacement) {
		return getInstance(ColorKit.makeColor("#C0C0C0"),ColorKit.makeColor("#E0E0E0"),Color.BLACK,tabPlacement);
	}

	/**
	 * This helper method will construct a DefaultTabImageRenderer using some template
	 * background images.  The template images have a 1 pixel border, rounded edges
	 * and a transparent side.  This method will "transform" the default
	 * template images to match the colors provided.
	 *  
	 * @param activeTabColor - the desired active tab color
	 * @param inactiveTabColor - the desired inactive tab color
	 * @param borderColor - the desired border color
	 * @param tabPlacement - can be EchoConstants.TOP or EchoConstants.BOTTOM
	 * @return a primed DefaultTabImageRenderer 
	 */
	public static DefaultTabImageRenderer getInstance(Color activeTabColor, Color inactiveTabColor, Color borderColor, int tabPlacement) {
		if (activeTabColor == null)
			throw new IllegalArgumentException("The activeTabColor must be non null");
		if (inactiveTabColor == null)
			throw new IllegalArgumentException("The inactiveTabColor must be non null");
		if (borderColor == null)
			throw new IllegalArgumentException("The borderColor must be non null");
			
		Image activeImage = _loadResImage("tabTemplateC0C0C0.gif");
		Image inactiveImage = _loadResImage("tabTemplateE0E0E0.gif");
		
		activeImage = _makeSmartTabImage(activeImage,ColorKit.makeColor("#C0C0C0"),		activeTabColor,		Color.BLACK,borderColor,tabPlacement);
		inactiveImage = _makeSmartTabImage(inactiveImage,ColorKit.makeColor("#E0E0E0"),	inactiveTabColor,	Color.BLACK,borderColor,tabPlacement);
		
		DefaultTabImageRenderer renderer = new DefaultTabImageRenderer(activeImage, inactiveImage);
		renderer.setBorderColor(borderColor);
		renderer.setBorderWidth(1);
		renderer.activeColor = activeTabColor;
		renderer.inactiveColor = inactiveTabColor;
		renderer.tabPlacement = tabPlacement;
		
		return renderer;	 	
	}
	
	/**
	 * Called to smart create and color the tab template images.
	 */
	private static Image _makeSmartTabImage(Image currentImage, Color currentColor, Color backgroundColor, Color currentBorderColor, Color borderColor, int tabPlacement) {
		if (! currentColor.equals(backgroundColor) || ! currentBorderColor.equals(borderColor)) {
			currentImage = ImageKit.copyImage(currentImage);
			currentImage = ImageKit.swapImageColors(currentImage,currentColor,backgroundColor);
			if (! Color.BLACK.equals(borderColor))
				currentImage = ImageKit.swapImageColors(currentImage,currentBorderColor,borderColor);
			if (tabPlacement == Alignment.BOTTOM)
				currentImage = ImageKit.flipImage(currentImage);
		}
		return currentImage;
	}
	
	/**
	 * Called to load a standrd imasge from class resources 
	 */
	private static Image _loadResImage(String imageFileName) {
		URL url = Resources.class.getResource("images/" + imageFileName);
		return ImageKit.loadCachedImage(url);
	}
	
	/**
	 * @see java.io.Serializable
	 */
	private void readObject(ObjectInputStream in)
	throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.activeTabImage = ImageKit.readSerializedImage(in);
		this.inactiveTabImage = ImageKit.readSerializedImage(in);
	}

	/**
	 * @see java.io.Serializable
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		ImageKit.writeSerializedImage(out,this.activeTabImage);
		ImageKit.writeSerializedImage(out,this.inactiveTabImage);
	}
	
}
