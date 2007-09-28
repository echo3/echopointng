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
package echopointng;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;
import echopointng.able.Expandable;
import echopointng.model.DefaultExpansionModel;
import echopointng.model.ExpansionGroup;
import echopointng.model.ExpansionModel;
import echopointng.util.ColorKit;

/** 
 * <code>TitleBar</code> is a component that usually acts as a "title" of
 * component, much like the titlebar area in a windowing environment like
 * Windows.
 * <p>
 * <code>TitleBar</code> has a concept of "expanding and contracing" other content and as such
 * implements <code>Expandable</code> and has a ExpansionModel to track whether
 * the titlebar is expanded or not.
 * <p>
 * <code>TitleBar</code> has a left and right icon that can be "expanded".  It can also
 * have an optional 'application', 'help' and 'close' component, which will allow
 * buttons, imageicons and other components to be used on the <code>TitleBar</code>.   
 * 
 * <p>
 * A TitleBar is always drawn as :
 * <pre>
 * |--------------------------------------------------------------------------|
 * |                                                                          |
 * | [application] [left icon] Title text           [right icon][help][close] |
 * |                                                                          |
 * |--------------------------------------------------------------------------|
 * </pre>
 */

public class TitleBar extends AbleComponent implements Expandable  {
	
	/** The default image used as the left expanded icon */
	public static final ImageReference DEFAULT_LEFT_EXPANDED_ICON = new ResourceImageReference("/echopointng/resource/images/horz_expand_on.gif",new Extent(17), new Extent(14)); 
	/** The default image used as the left icon */
	public static final ImageReference DEFAULT_LEFT_ICON = new ResourceImageReference("/echopointng/resource/images/horz_expand_off.gif",new Extent(17), new Extent(14)); 
	
	public static final Style DEFAULT_STYLE;

	/** the default alignment of the title text is left/center */
	public static final Alignment DEFAULT_TITLE_ALIGNMENT = new Alignment(Alignment.LEFT,Alignment.CENTER);
	
	public final static String PROPERTY_APPLICATION_COMPONENT = "applicationComponent";

	public final static String PROPERTY_BACKGROUND_IMAGE = "backgroundImage";
	public final static String PROPERTY_CLOSE_COMPONENT = "closeComponent";
	public final static String PROPERTY_EXPANDED_TITLE = "expandedTitle";
	public final static String PROPERTY_EXPANDED_TITLE_BACKGROUND_IMAGE = "titleBackgroundImage";
	public final static String PROPERTY_HELP_COMPONENT = "helpComponent";
	public final static String PROPERTY_LEFT_EXPANDED_ICON = "leftExpandedIcon";
	public final static String PROPERTY_LEFT_ICON = "leftIcon";
	public final static String PROPERTY_RIGHT_EXPANDED_ICON = "rightExpandedIcon";
	public final static String PROPERTY_RIGHT_ICON = "rightIcon";
    public static final String PROPERTY_ROLLOVER_BACKGROUND = "rolloverBackground";
    public static final String PROPERTY_ROLLOVER_BACKGROUND_IMAGE = "rolloverBackgroundImage";
    public static final String PROPERTY_ROLLOVER_BORDER = "rolloverBorder";
    public static final String PROPERTY_ROLLOVER_ENABLED = "rolloverEnabled";
    public static final String PROPERTY_ROLLOVER_FONT = "rolloverFont";
    public static final String PROPERTY_ROLLOVER_FOREGROUND = "rolloverForeground";
	public final static String PROPERTY_TITLE = "title";
	public final static String PROPERTY_TITLE_ALIGNMENT = "titleAlignment";
	public final static String PROPERTY_TITLE_BACKGROUND_IMAGE = "titleBackgroundImage";
	static {
		MutableStyleEx style = new MutableStyleEx();
		
		style.setProperty(PROPERTY_BACKGROUND, ColorKit.makeColor("#ACBCDC"));
		style.setProperty(PROPERTY_FOREGROUND, ColorKit.makeColor("#FFFFFF"));
		
		style.setProperty(PROPERTY_ROLLOVER_ENABLED,true);
		style.setProperty(PROPERTY_ROLLOVER_BACKGROUND,ColorKit.makeColor("#B8C9EB"));
		
		style.setProperty(PROPERTY_TITLE_ALIGNMENT, DEFAULT_TITLE_ALIGNMENT);
		style.setProperty(PROPERTY_LEFT_ICON, DEFAULT_LEFT_ICON);
		style.setProperty(PROPERTY_LEFT_EXPANDED_ICON, DEFAULT_LEFT_EXPANDED_ICON);
		
		DEFAULT_STYLE = style;
	}
	
	public TitleBar() {
		this("TitleBar",false);
	}

	public TitleBar(String title) {
		this(title,false);
	}
	
	public TitleBar(String title, boolean isExpanded) {
		setFocusTraversalParticipant(true);
		setTitle(title);
		setExpansionModel(new DefaultExpansionModel(isExpanded));
	}
	public Component getApplicationComponent() {
		return (Component) getProperty(PROPERTY_APPLICATION_COMPONENT);
	}

	/**
	 * Returns the background image of the <code>TitleBar</code>.
	 * 
	 * @return the background image
	 */
	public FillImage getBackgroundImage() {
	    return (FillImage) getProperty(PROPERTY_BACKGROUND_IMAGE);
	}

	public Component getCloseComponent() {
		return (Component) getProperty(PROPERTY_CLOSE_COMPONENT);
	}
	/**
	 * @return the icon used on the left if the <code>TitleBar</code> is expanded
	 */
	public ImageReference getExpandedLeftIcon() {
		return (ImageReference) getProperty(PROPERTY_LEFT_EXPANDED_ICON);
	}
	/**
	 * @return the icon used on the right if the <code>TitleBar</code> is expanded
	 */
	public ImageReference getExpandedRightIcon() {
		return (ImageReference) getProperty(PROPERTY_RIGHT_EXPANDED_ICON);
	}
	/**
	 * @return the expanded title of the <code>TitleBar</code>
	 */
	public String getExpandedTitle() {
		return (String) getProperty(PROPERTY_EXPANDED_TITLE);
	}
	
	/**
	 * @return the background image of the <code>TitleBar</code> when it is in expanded mode.
	 */
	public FillImage getExpandedTitleBackgroundImage() {
		return (FillImage)getProperty(PROPERTY_EXPANDED_TITLE_BACKGROUND_IMAGE);
	}

	/**
	 * @see echopointng.able.Expandable#getExpansionGroup()
	 */
	public ExpansionGroup getExpansionGroup() {
		return (ExpansionGroup) getProperty(PROPERTY_EXPANSION_GROUP);
	}

	/**
	 * @see echopointng.able.Expandable#getExpansionModel()
	 */
	public ExpansionModel getExpansionModel() {
		return (ExpansionModel) getProperty(PROPERTY_EXPANSION_MODEL);
		
	}
	
	public Component getHelpComponent() {
		return (Component) getProperty(PROPERTY_HELP_COMPONENT);
	}
	/**
	 * @return the icon used on the left of the <code>TitleBar</code>
	 */
	public ImageReference getLeftIcon() {
		return (ImageReference) getProperty(PROPERTY_LEFT_ICON);
	}
	/**
	 * @return the icon used on the right of the <code>TitleBar</code>
	 */
	public ImageReference getRightIcon() {
		return (ImageReference) getProperty(PROPERTY_RIGHT_ICON);
	}

	/**
	 * Returns the background color of the <code>TitleBar</code> when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the color
	 */
	public Color getRolloverBackground() {
	    return (Color) getProperty(PROPERTY_ROLLOVER_BACKGROUND);
	}

	/**
	 * Returns the background image displayed when the mouse cursor is inside
	 * the <code>TitleBar</code>'s bounds. 
	 * 
	 * @return the background image
	 */
	public FillImage getRolloverBackgroundImage() {
	    return (FillImage) getProperty(PROPERTY_ROLLOVER_BACKGROUND_IMAGE);
	}

	/**
	 * Returns the border displayed around the <code>TitleBar</code> when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the border
	 */
	public Border getRolloverBorder() {
	    return (Border) getProperty(PROPERTY_ROLLOVER_BORDER);
	}

	/**
	 * Returns the font of the <code>TitleBar</code> when the mouse cursor is inside its
	 * bounds.
	 * 
	 * @return the font
	 */
	public Font getRolloverFont() {
	    return (Font) getProperty(PROPERTY_ROLLOVER_FONT);
	}

	/**
	 * Returns the foreground color of the <code>TitleBar</code> when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the color
	 */
	public Color getRolloverForeground() {
	    return (Color) getProperty(PROPERTY_ROLLOVER_FOREGROUND);
	}
	
	/**
	 * @return the title of the <code>TitleBar</code>
	 */
	public String getTitle() {
		return (String) getProperty(PROPERTY_TITLE);
	}
	/**
	 * @return the alignment of the title of the <code>TitleBar</code>
	 */
	public Alignment getTitleAlignment() {
		return (Alignment) getProperty(PROPERTY_TITLE_ALIGNMENT);
	}
	
	/**
	 * @return the background image of the <code>TitleBar</code>.
	 */
	public FillImage getTitleBackgroundImage() {
		return (FillImage)getProperty(PROPERTY_TITLE_BACKGROUND_IMAGE);
	}

	/**
	 * @see echopointng.able.Expandable#isExpanded()
	 */
	public boolean isExpanded() {
		return (getExpansionModel() == null ? false : getExpansionModel().isExpanded());
	}

	/**
	 * Determines if rollover effects are enabled.
	 * 
	 * @return true if rollover effects are enabled
	 * @see #setRolloverEnabled(boolean)
	 */
	public boolean isRolloverEnabled() {
	    Boolean value = (Boolean) getProperty(PROPERTY_ROLLOVER_ENABLED);
	    return value == null ? false : value.booleanValue();
	}
	
	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String, java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		boolean expanded = ((Boolean) inputValue).booleanValue();
		setExpanded(expanded);
	}
	
	public void setApplicationComponent(Component newValue) {
		setProperty(PROPERTY_APPLICATION_COMPONENT,newValue);
	}

	/**
	 * Sets the background image of the <code>TitleBar</code>.
	 * 
	 * @param newValue the new background image
	 */
	public void setBackgroundImage(FillImage newValue) {
	    setProperty(PROPERTY_BACKGROUND_IMAGE, newValue);
	}
	
	public void setCloseComponent(Component newValue) {
		setProperty(PROPERTY_CLOSE_COMPONENT,newValue);
	}

	/**
	 * @see echopointng.able.Expandable#setExpanded(boolean)
	 */
	public void setExpanded(boolean isExpanded) {
		if(getExpansionModel() != null) {
			getExpansionModel().setExpanded(isExpanded);
		}
	}
	
	/**
	 * Sets the expanded title of the <code>TitleBar</code>.  If this
	 * is null, then the general title will be used 
	 */
	public void setExpandedTitle(String newValue) {
		setProperty(PROPERTY_EXPANDED_TITLE,newValue);
	}
		
	/**
	 * Sets the background of the <code>TitleBar</code> when it is in expanded mode.
	 * @param fillImage The <code>FillImage</code> to use as background.
	 */
	public void setExpandedTitleBackgroundImage(FillImage fillImage) {
		setProperty(PROPERTY_EXPANDED_TITLE_BACKGROUND_IMAGE, fillImage);
	}
	
	/**
	 * This setExpansionGroup method is not supported on TitleBars.
	 * 
	 * @see echopointng.able.Expandable#setExpansionGroup(echopointng.model.ExpansionGroup)
	 */
	public void setExpansionGroup(ExpansionGroup newExpansionGroup) {
		throw new UnsupportedOperationException("The setExpansionGroup method is not supported on TitleBars");
	}

	/**
	 * @see echopointng.able.Expandable#setExpansionModel(echopointng.model.ExpansionModel)
	 */
	public void setExpansionModel(ExpansionModel newExpansionModel) {
		setProperty(PROPERTY_EXPANSION_MODEL,newExpansionModel);
	}
	
	public void setHelpComponent(Component newValue) {
		setProperty(PROPERTY_HELP_COMPONENT,newValue);
	}
	
	public void setLeftExpandedIcon(ImageReference newValue) {
		setProperty(PROPERTY_LEFT_EXPANDED_ICON,newValue);
	}
	public void setLeftIcon(ImageReference newValue) {
		setProperty(PROPERTY_LEFT_ICON,newValue);
	}
	public void setRightExpandedIcon(ImageReference newValue) {
		setProperty(PROPERTY_RIGHT_EXPANDED_ICON,newValue);
	}
	public void setRightIcon(ImageReference newValue) {
		setProperty(PROPERTY_RIGHT_ICON,newValue);
	}

	/**
	 * Sets the background color of the <code>TitleBar</code> when the mouse cursor is inside
	 * its bounds.
	 * 
	 * @param newValue the new <code>Color</code>
	 */
	public void setRolloverBackground(Color newValue) {
	    setProperty(PROPERTY_ROLLOVER_BACKGROUND, newValue);
	}

	/**
	 * Sets the background image displayed when the mouse cursor is inside the
	 * <code>TitleBar</code>'s bounds
	 * 
	 * @param newValue the new background image
	 */
	public void setRolloverBackgroundImage(FillImage newValue) {
	    setProperty(PROPERTY_ROLLOVER_BACKGROUND_IMAGE, newValue);
	}

	/**
	 * Sets the border displayed around the <code>TitleBar</code> when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @param newValue the new border
	 */
	public void setRolloverBorder(Border newValue) {
	    setProperty(PROPERTY_ROLLOVER_BORDER, newValue);
	}

	/**
	 * Sets whether rollover effects are enabled when the mouse cursor is inside
	 * the <code>TitleBar</code>'s bounds. Rollover properties have no effect unless this
	 * property is set to true. The default value is false.
	 * 
	 * @param newValue true if rollover effects should be enabled
	 */
	public void setRolloverEnabled(boolean newValue) {
	    setProperty(PROPERTY_ROLLOVER_ENABLED, Boolean.valueOf(newValue));
	}

	/**
	 * Sets the font of the <code>TitleBar</code> when the mouse cursor is inside its bounds.
	 * 
	 * @param newValue the new <code>Font</code>
	 */
	public void setRolloverFont(Font newValue) {
	    setProperty(PROPERTY_ROLLOVER_FONT, newValue);
	}

	/**
	 * Sets the foreground color of the <code>TitleBar</code> when the mouse cursor is inside
	 * its bounds.
	 * 
	 * @param newValue the new <code>Color</code>
	 */
	public void setRolloverForeground(Color newValue) {
	    setProperty(PROPERTY_ROLLOVER_FOREGROUND, newValue);
	}
	
	/**
	 * Sets the title of the <code>TitleBar</code>
	 */
	public void setTitle(String newValue) {
		setProperty(PROPERTY_TITLE,newValue);
	}
	
	/**
	 * Sets the alignment of the title of the <code>TitleBar</code>
	 */
	public void setTitleAlignment(Alignment newValue) {
		setProperty(PROPERTY_TITLE_ALIGNMENT,newValue);
	}
	
	/**
	 * Sets the background of the <code>TitleBar</code>.
	 * @param fillImage The <code>FillImage</code> to use as background.
	 */
	public void setTitleBackgroundImage(FillImage fillImage) {
		setProperty(PROPERTY_TITLE_BACKGROUND_IMAGE, fillImage);
	}
	
	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
		if (getApplicationComponent() != null && ! isAncestorOf(getApplicationComponent())) {
			add(getApplicationComponent());
		}
		if (getHelpComponent() != null && ! isAncestorOf(getHelpComponent())) {
			add(getHelpComponent());
		}
		if (getCloseComponent() != null && ! isAncestorOf(getCloseComponent())) {
			add(getCloseComponent());
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getTitle());
		sb.append(" - ");
		sb.append(super.toString());
		return sb.toString();
	}

}
