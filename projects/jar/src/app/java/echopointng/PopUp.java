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

import java.io.Serializable;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import echopointng.able.Expandable;
import echopointng.model.DefaultExpansionModel;
import echopointng.model.ExpansionGroup;
import echopointng.model.ExpansionModel;
import echopointng.util.ColorKit;
import echopointng.util.ComponentTracker;

/**
 * <code>PopUp</code> is a component that 'pops up' a floating box containing
 * another component when its toggle image is clicked or moused over.
 * <p>
 * A <code>PopUp</code> has three main parts.
 * <ul>
 * <li>Target Component - this optional component sits beside the Toggle image
 * </li>
 * <li>Box Component - this is shown when the the Toggle image is clicked or
 * moused over</li>
 * <li>Toggle Image - this is an 'button like' image that can cause the popup
 * box to be shown</li>
 * </ul>
 */

public class PopUp extends AbleComponent implements Expandable {

	private class InternallListener implements ChangeListener, Serializable {
		/**
		 * @see nextapp.echo2.app.event.ChangeListener#stateChanged(nextapp.echo2.app.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			firePropertyChange(EXPANDED_CHANGED_PROPERTY, null, null);
		}
	}

	/** the default alignment is right/top */
	public static final Alignment DEFAULT_ALIGNMENT = new Alignment(Alignment.RIGHT, Alignment.TOP);

	/**
	 * This DEFAULT_STYLE is applied to the popup to give it a series of borders
	 * and background colors
	 */
	public static final Style DEFAULT_STYLE;

	/** The default image used as the toggle image */
	public static final ImageReference DEFAULT_TOGGLE_ICON = new ResourceImageReference("/echopointng/resource/images/popup_normal_icon.gif",
			new Extent(15), new Extent(16));

	public static final ImageReference DEFAULT_TOGGLE_ROLLOVER_ICON = new ResourceImageReference(
			"/echopointng/resource/images/popup_rollover_icon.gif", new Extent(15), new Extent(16));

	public static final ImageReference DEFAULT_TOGGLE_PRESSED_ICON = new ResourceImageReference(
			"/echopointng/resource/images/popup_pressed_icon.gif", new Extent(15), new Extent(16));

	public final static String PROPERTY_FOCUS_COMPONENT = "focusComponent";

	public final static String PROPERTY_FOCUS_ON_EXPAND = "focusOnExpand";

	public final static String PROPERTY_POPUP = "popUp";

	public final static String PROPERTY_POPUP_ALIGNMENT = "popUpAlignment";

	public final static String PROPERTY_POPUP_ALWAYS_ON_TOP = "popUpAlwaysOnTop";

	public final static String PROPERTY_POPUP_BACKGROUND = "popUpBackground";

	public final static String PROPERTY_POPUP_BORDER = "popUpBorder";

	public static final String PROPERTY_POPUP_INSETS = "popUpInsets";

	public static final String PROPERTY_POPUP_LEFT_OFFSET = "popUpLeftOffset";

	public final static String PROPERTY_POPUP_NEXT_TO_TOGGLE = "popUpNextToToggle";

	public final static String PROPERTY_POPUP_ON_ROLLOVER = "popUpOnRollover";

	public static final String PROPERTY_POPUP_OUTSETS = "popUpOutSets";

	public static final String PROPERTY_POPUP_TOP_OFFSET = "popUpTopOffset";

	public final static String PROPERTY_ROLLOVER_BACKGROUND = "rolloverBackground";

	public final static String PROPERTY_ROLLOVER_BORDER = "rolloverRolloverBorder";

	public final static String PROPERTY_ROLLOVER_ENABLED = "rolloverEnabled";

	public final static String PROPERTY_TARGET = "target";

	public final static String PROPERTY_TARGET_BACKGROUND = "targetBackground";

	public final static String PROPERTY_TARGET_BORDER = "targetBorder";

	public static final String PROPERTY_TARGET_INSETS = "targetInsets";

	public final static String PROPERTY_TARGET_ROLLOVER_BACKGROUND = "targetRolloverBackground";

	public final static String PROPERTY_TARGET_ROLLOVER_BORDER = "targetRolloverBorder";

	public final static String PROPERTY_TOGGLE_BACKGROUND = "toggleBackground";

	public final static String PROPERTY_TOGGLE_BORDER = "toggleBorder";

	public final static String PROPERTY_TOGGLE_ICON = "toggleIcon";

	public static final String PROPERTY_TOGGLE_INSETS = "toggleInsets";

	public final static String PROPERTY_TOGGLE_PRESSED_BACKGROUND = "togglePressedBackground";

	public final static String PROPERTY_TOGGLE_PRESSED_BORDER = "togglePressedBorder";

	public final static String PROPERTY_TOGGLE_PRESSED_ENABLED = "togglePressedEnabled";

	public final static String PROPERTY_TOGGLE_PRESSED_ICON = "togglePressedIcon";

	public final static String PROPERTY_TOGGLE_ROLLOVER_BACKGROUND = "toggleRolloverBackground";

	public final static String PROPERTY_TOGGLE_ROLLOVER_BORDER = "toggleRolloverBorder";

	public final static String PROPERTY_TOGGLE_ROLLOVER_ICON = "toggleRolloverIcon";

	static {
		MutableStyleEx style = new MutableStyleEx();

		Color GRAYISH = ColorKit.makeColor("#D6D3CE");
		Color BLUEISH = ColorKit.makeColor("#3169C6");

		style.setProperty(PROPERTY_INSETS, new Insets(0));
		style.setProperty(PROPERTY_BORDER, new BorderEx(GRAYISH));

		style.setProperty(PROPERTY_ROLLOVER_ENABLED, true);
		style.setProperty(PROPERTY_ROLLOVER_BORDER, new BorderEx(BLUEISH));

		style.setProperty(PROPERTY_POPUP_ALWAYS_ON_TOP, false);
		style.setProperty(PROPERTY_POPUP_BACKGROUND, Color.WHITE);
		style.setProperty(PROPERTY_POPUP_BORDER, new BorderEx(GRAYISH));
		style.setProperty(PROPERTY_POPUP_INSETS, new Insets(2));

		style.setProperty(PROPERTY_TOGGLE_INSETS, new Insets(1));
		style.setProperty(PROPERTY_TOGGLE_ICON, DEFAULT_TOGGLE_ICON);
		style.setProperty(PROPERTY_TOGGLE_ROLLOVER_ICON, DEFAULT_TOGGLE_ROLLOVER_ICON);
		style.setProperty(PROPERTY_TOGGLE_PRESSED_ICON, DEFAULT_TOGGLE_PRESSED_ICON);
		style.setProperty(PROPERTY_TOGGLE_PRESSED_ENABLED, true);

		DEFAULT_STYLE = style;
	}

	private InternallListener internalListener = new InternallListener();

	private ComponentTracker popUpTracker = new ComponentTracker(this);

	private ComponentTracker targetTracker = new ComponentTracker(this);

	/**
	 * Constructs a <code>PopUp</code> with no target and no popup component
	 * in place
	 */
	public PopUp() {
		this(null, null);
	}

	/**
	 * Constructs a <code>PopUp</code> with the specified target and popup
	 * component in place.
	 */
	public PopUp(Component targetComponent, Component popUpComponent) {
		setExpansionModel(new DefaultExpansionModel(false));

		setTarget(targetComponent);
		setPopUp(popUpComponent);
		setFocusComponent(popUpComponent);
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

	/**
	 * @return the Component that is shown in the popup box when the toggle is
	 *         pressed
	 */
	public Component getPopUp() {
		return (Component) getProperty(PROPERTY_POPUP);
	}

	/**
	 * @return the Alignment of the box around the popup component, with respect
	 *         to the target component or toggle
	 */
	public Alignment getPopUpAlignment() {
		return (Alignment) getProperty(PROPERTY_POPUP_ALIGNMENT);
	}

	/**
	 * @return whether the popup box is always on top
	 * @see PopUp#setPopUpAlwaysOnTop(boolean)
	 */
	public boolean getPopUpAlwaysOnTop() {
		return getProperty(PROPERTY_POPUP_ALWAYS_ON_TOP, false);
	}

	/**
	 * @return the background color of the box around the popup component
	 */
	public Color getPopUpBackground() {
		return (Color) getProperty(PROPERTY_POPUP_BACKGROUND);
	}

	/**
	 * @return the border of the box around the popup component
	 */
	public Border getPopUpBorder() {
		return (Border) getProperty(PROPERTY_POPUP_BORDER);
	}

	/**
	 * @return the inner padding of the box around the popup component
	 */
	public Insets getPopUpInsets() {
		return (Insets) getProperty(PROPERTY_POPUP_INSETS);
	}

	/**
	 * @return the left offset of the box around the popup component
	 */
	public int getPopUpLeftOffset() {
		return getProperty(PROPERTY_POPUP_LEFT_OFFSET, 0);
	}

	/**
	 * @return the outer padding of the box around the popup component
	 */
	public Insets getPopUpOutsets() {
		return (Insets) getProperty(PROPERTY_POPUP_OUTSETS);
	}

	/**
	 * @return the top offset of the box around the popup component
	 */
	public int getPopUpTopOffset() {
		return getProperty(PROPERTY_POPUP_TOP_OFFSET, 0);
	}

	/**
	 * Returns the background color of the popup when the mouse cursor is inside
	 * its bounds.
	 * 
	 * @return the color
	 */
	public Color getRolloverBackground() {
		return (Color) getProperty(PROPERTY_ROLLOVER_BACKGROUND);
	}

	/**
	 * Returns the border displayed around the popup when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the border
	 */
	public Border getRolloverBorder() {
		return (Border) getProperty(PROPERTY_ROLLOVER_BORDER);
	}

	/**
	 * @return the Component that is shown beside the toggle icon.
	 */
	public Component getTarget() {
		return (Component) getProperty(PROPERTY_TARGET);
	}

	/**
	 * Returns the background color of the target area.
	 * 
	 * @return the color
	 */
	public Color getTargetBackground() {
		return (Color) getProperty(PROPERTY_TARGET_BACKGROUND);
	}

	/**
	 * Returns the border displayed around the target area.
	 * 
	 * @return the border
	 */
	public Border getTargetBorder() {
		return (Border) getProperty(PROPERTY_TARGET_BORDER);
	}

	/**
	 * Returns the insets displayed around the target area.
	 * 
	 * @return the insets
	 */
	public Insets getTargetInsets() {
		return (Insets) getProperty(PROPERTY_TARGET_INSETS);
	}

	/**
	 * Returns the background color of the target area when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the color
	 */
	public Color getTargetRolloverBackground() {
		return (Color) getProperty(PROPERTY_TARGET_ROLLOVER_BACKGROUND);
	}

	/**
	 * Returns the border displayed around the target area when the mouse cursor
	 * is inside its bounds.
	 * 
	 * @return the border
	 */
	public Border getTargetRolloverBorder() {
		return (Border) getProperty(PROPERTY_TARGET_ROLLOVER_BORDER);
	}

	/**
	 * Returns the background color of the toggle area.
	 * 
	 * @return the color
	 */
	public Color getToggleBackground() {
		return (Color) getProperty(PROPERTY_TOGGLE_BACKGROUND);
	}

	/**
	 * Returns the border displayed around the toggle area.
	 * 
	 * @return the border
	 */
	public Border getToggleBorder() {
		return (Border) getProperty(PROPERTY_TOGGLE_BORDER);
	}

	/**
	 * Returns the icon of the toggle.
	 * 
	 * @return the icon
	 */
	public ImageReference getToggleIcon() {
		return (ImageReference) getProperty(PROPERTY_TOGGLE_ICON);
	}

	/**
	 * Returns the Insets of the toggle area.
	 * 
	 * @return the insets
	 */
	public Insets getToggleInsets() {
		return (Insets) getProperty(PROPERTY_TOGGLE_INSETS);
	}

	/**
	 * Returns the background color of the toggle when the toggle is pressed.
	 * 
	 * @return the color
	 */
	public Color getTogglePressedBackground() {
		return (Color) getProperty(PROPERTY_TOGGLE_PRESSED_BACKGROUND);
	}

	/**
	 * Returns the border displayed around the toggle when the toggle is
	 * pressed.
	 * 
	 * @return the border
	 */
	public Border getTogglePressedBorder() {
		return (Border) getProperty(PROPERTY_TOGGLE_PRESSED_BORDER);
	}

	/**
	 * Returns the icon of the toggle that is displayed when the toggle is
	 * pressed.
	 * 
	 * @return the icon
	 */
	public ImageReference getTogglePressedIcon() {
		return (ImageReference) getProperty(PROPERTY_TOGGLE_PRESSED_ICON);
	}

	/**
	 * Returns the background color of the toggle when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the color
	 */
	public Color getToggleRolloverBackground() {
		return (Color) getProperty(PROPERTY_TOGGLE_ROLLOVER_BACKGROUND);
	}

	/**
	 * Returns the border displayed around the toggle when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the border
	 */
	public Border getToggleRolloverBorder() {
		return (Border) getProperty(PROPERTY_TOGGLE_ROLLOVER_BORDER);
	}

	/**
	 * Returns the icon of the toggle that is displayed when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the icon
	 */
	public ImageReference getToggleRolloverIcon() {
		return (ImageReference) getProperty(PROPERTY_TOGGLE_ROLLOVER_ICON);
	}

	/**
	 * @see echopointng.able.Expandable#isExpanded()
	 */
	public boolean isExpanded() {
		return (getExpansionModel() == null ? false : getExpansionModel().isExpanded());
	}

	/**
	 * @return true if the popup box is aligned next to the toggle image or
	 *         false if its aligned next to the target component
	 */
	public boolean isPopUpNextToToggle() {
		return getProperty(PROPERTY_POPUP_NEXT_TO_TOGGLE, true);
	}

	/**
	 * @return true if the popup box should shown if the mouse rolls over the
	 *         toggle
	 */
	public boolean isPopUpOnRollover() {
		return getProperty(PROPERTY_POPUP_ON_ROLLOVER, true);
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
	 * Determines if pressed effects are enabled.
	 * 
	 * @return true if pressed effects are enabled
	 */
	public boolean isTogglePressedEnabled() {
		Boolean value = (Boolean) getProperty(PROPERTY_TOGGLE_PRESSED_ENABLED);
		return value == null ? false : value.booleanValue();
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		boolean expanded = ((Boolean) inputValue).booleanValue();
		setExpanded(expanded);
	}

	/**
	 * @see echopointng.able.Expandable#setExpanded(boolean)
	 */
	public void setExpanded(boolean isExpanded) {
		if (getExpansionModel() != null) {
			getExpansionModel().setExpanded(isExpanded);
		}
	}

	/**
	 * @see echopointng.able.Expandable#setExpansionGroup(echopointng.model.ExpansionGroup)
	 */
	public void setExpansionGroup(ExpansionGroup newExpansionGroup) {
		ExpansionGroup oldValue = getExpansionGroup();
		if (oldValue != null) {
			oldValue.removeExpandable(this);
		}
		if (newExpansionGroup != null) {
			newExpansionGroup.addExpandable(this);
		}
		setProperty(PROPERTY_EXPANSION_GROUP, newExpansionGroup);
	}

	/**
	 * @see echopointng.able.Expandable#setExpansionModel(echopointng.model.ExpansionModel)
	 */
	public void setExpansionModel(ExpansionModel newValue) {
		ExpansionModel oldValue = getExpansionModel();
		if (oldValue != null) {
			oldValue.removeChangeListener(internalListener);
		}
		if (newValue != null) {
			newValue.addChangeListener(internalListener);
		}
		setProperty(PROPERTY_EXPANSION_MODEL, newValue);
	}

	/**
	 * Sets the Component that is shown is the popup box when the toggle is
	 * pressed
	 */
	public void setPopUp(Component newValue) {
		setProperty(PROPERTY_POPUP, newValue);
		popUpTracker.removeAll();
		setFocusComponent(newValue);
		if (newValue != null)
			popUpTracker.add(newValue);
	}

	/**
	 * Sets the alignment of the box around the popup component, with respect to
	 * either the target component or the toggle image.
	 * 
	 * The horizontal alignment setting may be one of the following values:
	 * <ul>
	 * <li><code>LEFT</code>- the "left edge" of popup box is shown next to
	 * the "left edge" of the popup</li>
	 * <li><code>CENTER</code>- the popup box is centered horizontally over
	 * the center of the popup</li>
	 * <li><code>RIGHT</code>- the "left edge" of popup box is shown to next
	 * the "right edge" of the popup</li>
	 * </ul>
	 * The vertical alignment setting can be one of the following values:
	 * <ul>
	 * <li><code>TOP</code>- the "bottom edge" of the popup box is shown
	 * next to the "top edge" of the popup</li>
	 * <li><code>CENTER</code>- the popup box is centered vertically over
	 * the center of the popup</li>
	 * <li><code>BOTTOM</code>- the "top edge" of the popup box is shown
	 * next to the "bottom edge" of the popup</li>
	 * </ul>
	 * 
	 * @param newValue -
	 *            the new alignment for the popup. If this is null, then default
	 *            values will be used for alignment
	 */
	public void setPopUpAlignment(Alignment newValue) {
		if (newValue == null)
			newValue = DEFAULT_ALIGNMENT;
		switch (newValue.getHorizontal()) {
		case Alignment.LEFT:
		case Alignment.RIGHT:
		case Alignment.CENTER:
			break;
		default:
			throw new IllegalArgumentException("The horizontal alignment must be LEFT, CENTER or RIGHT");
		}
		switch (newValue.getVertical()) {
		case Alignment.TOP:
		case Alignment.CENTER:
		case Alignment.BOTTOM:
			break;
		default:
			throw new IllegalArgumentException("The vertical alignment must be TOP, CENTER or BOTTOM");
		}
		setProperty(PROPERTY_POPUP_ALIGNMENT, newValue);
	}

	/**
	 * The popUpAlwaysOnTop property can be used control how the popup box is
	 * drawn.
	 * <p>
	 * If this is true, then the popup box with always take the highest position
	 * on the screen and will disregard parent clipping and scrolling
	 * constraints.
	 * <p>
	 * If this is false, then the normal client clipping and scrollling
	 * constraints will apply. This can cause the popup box to be shown under
	 * other components according to the current hierarchy.
	 * 
	 * @param newValue -
	 *            the new popUpAlwaysOnTop value
	 */
	public void setPopUpAlwaysOnTop(boolean newValue) {
		setProperty(PROPERTY_POPUP_ALWAYS_ON_TOP, newValue);
	}

	/**
	 * Sets the background color of the box around the popup component.
	 * 
	 * @param newValue
	 */
	public void setPopUpBackground(Color newValue) {
		setProperty(PROPERTY_POPUP_BACKGROUND, newValue);
	}

	/**
	 * Sets the border of the box around the popup component.
	 * 
	 * @param newValue
	 */
	public void setPopUpBorder(Border newValue) {
		setProperty(PROPERTY_POPUP_BORDER, newValue);
	}

	/**
	 * Sets the insets inside the box around the popup component.
	 * 
	 * @param newValue
	 */
	public void setPopUpInsets(Insets newValue) {
		setProperty(PROPERTY_POPUP_INSETS, newValue);
	}

	/**
	 * Sets the left offset the box around the popup component.
	 * 
	 * @param newValue
	 */
	public void setPopUpLeftOffset(int newValue) {
		setProperty(PROPERTY_POPUP_LEFT_OFFSET, newValue);
	}

	/**
	 * Sets to true if the popup box is aligned next to the toggle image or
	 * false if its aligned next to the target component.
	 * <p>
	 * The popup alignment and offsets are related the area the poup box is next
	 * to.
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setPopUpNextToToggle(boolean newValue) {
		setProperty(PROPERTY_POPUP_NEXT_TO_TOGGLE, newValue);
	}

	/**
	 * Controls whether th popup box is shown when the mouse rolls over the
	 * toggle or false if a click is required to show the popup box.
	 * 
	 * @param newValue -
	 *            true if the mouse rollover will cause the popup box to be
	 *            shown
	 */
	public void setPopUpOnRollover(boolean newValue) {
		setProperty(PROPERTY_POPUP_ON_ROLLOVER, newValue);
	}

	/**
	 * Sets the outer spacing of the box around the popup component.
	 * 
	 * @param newValue
	 */
	public void setPopUpOutsets(Insets newValue) {
		setProperty(PROPERTY_POPUP_OUTSETS, newValue);
	}

	/**
	 * Sets the top offset of the box around the popup component.
	 * 
	 * @param newValue
	 */
	public void setPopUpTopOffset(int newValue) {
		setProperty(PROPERTY_POPUP_TOP_OFFSET, newValue);
	}

	/**
	 * Sets the background color of the popup when the mouse cursor is inside
	 * its bounds.
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setRolloverBackground(Color newValue) {
		setProperty(PROPERTY_ROLLOVER_BACKGROUND, newValue);
	}

	/**
	 * Sets the border displayed around the popup when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @param newValue
	 *            the new border
	 */
	public void setRolloverBorder(Border newValue) {
		setProperty(PROPERTY_ROLLOVER_BORDER, newValue);
	}

	/**
	 * Sets whether rollover effects are enabled when the mouse cursor is inside
	 * the popup's bounds. Rollover properties have no effect unless this
	 * property is set to true. The default value is false.
	 * 
	 * @param newValue
	 *            true if rollover effects should be enabled
	 */
	public void setRolloverEnabled(boolean newValue) {
		setProperty(PROPERTY_ROLLOVER_ENABLED, Boolean.valueOf(newValue));
	}

	/**
	 * Sets the component that is shown beside the toggle
	 */
	public void setTarget(Component newValue) {
		setProperty(PROPERTY_TARGET, newValue);
		targetTracker.removeAll();
		if (newValue != null)
			targetTracker.add(newValue);
	}

	/**
	 * Sets the background color of the target area
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setTargetBackground(Color newValue) {
		setProperty(PROPERTY_TARGET_BACKGROUND, newValue);
	}

	/**
	 * Sets the border displayed around the target area.
	 * 
	 * @param newValue
	 *            the new border
	 */
	public void setTargetBorder(Border newValue) {
		setProperty(PROPERTY_TARGET_BORDER, newValue);
	}

	/**
	 * Sets the Insets displayed around the target area.
	 * 
	 * @param newValue
	 *            the new Insets
	 */
	public void setTargetInsets(Insets newValue) {
		setProperty(PROPERTY_TARGET_INSETS, newValue);
	}

	/**
	 * Sets the background color of the target area when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setTargetRolloverBackground(Color newValue) {
		setProperty(PROPERTY_TARGET_ROLLOVER_BACKGROUND, newValue);
	}

	/**
	 * Sets the border displayed around the target area when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @param newValue
	 *            the new border
	 */
	public void setTargetRolloverBorder(Border newValue) {
		setProperty(PROPERTY_TARGET_ROLLOVER_BORDER, newValue);
	}

	/**
	 * Sets the background color of the toggle
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setToggleBackground(Color newValue) {
		setProperty(PROPERTY_TOGGLE_BACKGROUND, newValue);
	}

	/**
	 * Sets the border displayed around the toggle.
	 * 
	 * @param newValue
	 *            the new border
	 */
	public void setToggleBorder(Border newValue) {
		setProperty(PROPERTY_TOGGLE_BORDER, newValue);
	}

	/**
	 * Sets the icon of the toggle.
	 * 
	 * @param newValue
	 *            the new icon
	 */
	public void setToggleIcon(ImageReference newValue) {
		setProperty(PROPERTY_TOGGLE_ICON, newValue);
	}

	/**
	 * Sets the insets of the toggle area.
	 * 
	 * @param newValue
	 *            the new icon
	 */
	public void setToggleInsets(Insets newValue) {
		setProperty(PROPERTY_TOGGLE_INSETS, newValue);
	}

	/**
	 * Sets the background color of the toggle when the toggle is pressed.
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setTogglePressedBackground(Color newValue) {
		setProperty(PROPERTY_TOGGLE_PRESSED_BACKGROUND, newValue);
	}

	/**
	 * Sets the border displayed around the toggle when the toggle is pressed.
	 * 
	 * @param newValue
	 *            the new border
	 */
	public void setTogglePressedBorder(Border newValue) {
		setProperty(PROPERTY_TOGGLE_PRESSED_BORDER, newValue);
	}

	/**
	 * Sets whether pressed effects are enabled when the toggle is pressed.
	 * Pressed properties have no effect unless this property is set to true.
	 * The default value is false.
	 * 
	 * @param newValue
	 *            true if pressed effects should be enabled
	 */
	public void setTogglePressedEnabled(boolean newValue) {
		setProperty(PROPERTY_TOGGLE_PRESSED_ENABLED, Boolean.valueOf(newValue));
	}

	/**
	 * Sets the icon of the toggle that is displayed when the toggle is pressed.
	 * 
	 * @param newValue
	 *            the new icon
	 */
	public void setTogglePressedIcon(ImageReference newValue) {
		setProperty(PROPERTY_TOGGLE_PRESSED_ICON, newValue);
	}

	/**
	 * Sets the background color of the toggle when the mouse cursor is inside
	 * its bounds.
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setToggleRolloverBackground(Color newValue) {
		setProperty(PROPERTY_TOGGLE_ROLLOVER_BACKGROUND, newValue);
	}

	/**
	 * Sets the border displayed around the toggle when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @param newValue
	 *            the new border
	 */
	public void setToggleRolloverBorder(Border newValue) {
		setProperty(PROPERTY_TOGGLE_ROLLOVER_BORDER, newValue);
	}

	/**
	 * Sets the icon of the toggle that is displayed when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @param newValue
	 *            the new icon
	 */
	public void setToggleRolloverIcon(ImageReference newValue) {
		setProperty(PROPERTY_TOGGLE_ROLLOVER_ICON, newValue);
	}

	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
	}

	/**
	 * Sets the component that needs to get the focus when the popup is asked to
	 * focus
	 * 
	 * @param component
	 *            the component to focus (by default the popup itself)
	 */
	public void setFocusComponent(Component component) {
		if (component != null) {
			setProperty(PROPERTY_FOCUS_COMPONENT, component);
		}
	}

	/**
	 * Allows you to let the popup request focus when it is shown/expanded
	 * 
	 * @param showFocus
	 *            true when you want the popup to request focus when becoming
	 *            visible, false otherwise (false by default)
	 */
	public void setFocusOnExpand(boolean showFocus) {
		setProperty(PROPERTY_FOCUS_ON_EXPAND, showFocus);
	}
}
