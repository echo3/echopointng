package echopointng;

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

import java.io.Serializable;
import java.util.EventListener;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import echopointng.model.BoundedRangeModel;
import echopointng.model.DefaultBoundedRangeModel;
import echopointng.progressbar.DefaultProgressBarRenderer;
import echopointng.progressbar.ProgressBarRenderer;
import echopointng.util.ColorKit;

/**
 * The <code>ProgressBar</code>> component provides a visual display for an
 * integer value within a bounded interval.
 * <p>
 * A <code>ProgressBar</code> typically communicates the progress of an event
 * by displaying its percentage of completion and possibly a textual display of
 * this percentage.
 * <p>
 * The ProgressBar comes with a standard width and height that is optimised for
 * HORIZONATL orientation. If you change the orientation, be sure to flip the
 * height and width otherwise it will look strange. This is done by default on
 * the basic setOrientation() method.
 * <p>
 * The width and height of the ProgressBar can be adjusted slightly by the
 * ProgressBarRenderer, so it can fit the block size exactly. So dont assume its
 * size is always exact.
 *  
 */
public class ProgressBar extends AbleComponent {

	/*
	 * We pass each Change event to the listeners with the the progress bar as
	 * the event source. We also update our image reference to reflect the
	 * change.
	 */
	private class InternalModelListener implements ChangeListener, Serializable {
		public void stateChanged(ChangeEvent e) {

			//
			// set the flag that tells use to redraw our image reference
			// just before it gets rendered
			invalidate();

			//
			// then inform all listeners
			fireStateChanged();
		}
	}

	public static final String PROPERTY_COMPLETED_COLOR = "completedColor";

	public static final String PROPERTY_MODEL = "model";

	public static final String PROPERTY_NUMBER_OF_BLOCKS = "numberOfBlocks";

	public static final String PROPERTY_ORIENTATION = "orientation";

	public static final String PROPERTY_PROGRESS_STRING = "progressString";

	public static final String PROPERTY_PROGRESS_STRING_PAINTED = "progressStringPainted";

	public static final String PROPERTY_PROGRESSBAR_IMAGE = "progressBarImage";

	public static final String PROPERTY_RENDERER = "renderer";

	public static final String PROPERTY_UNCOMPLETED_COLOR = "uncompletedColor";

	public static final int ORIENTATION_HORIZONTAL = 0;

	public static final int ORIENTATION_VERTICAL = 1;

	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_BORDER, new Border(1, ColorKit.makeColor("#D6D3CE"), Border.STYLE_SOLID));
		style.setProperty(PROPERTY_HEIGHT, new ExtentEx("30px"));
		style.setProperty(PROPERTY_WIDTH, new ExtentEx("146px"));
		style.setProperty(PROPERTY_COMPLETED_COLOR, Color.ORANGE);
		style.setProperty(PROPERTY_UNCOMPLETED_COLOR, Color.LIGHTGRAY);

		DEFAULT_STYLE = style;
	}

	/**
	 * Only one ChangeEvent is needed per instance since the event's only
	 * interesting property is the immutable source, which is the progress bar.
	 */
	protected transient ChangeEvent changeEvent = null;

	protected ChangeListener changeListener = null;

	private boolean invalid = true;

	/**
	 * Creates a horizontal progress bar. The default orientation for progress
	 * bars is <code>ProgressBar.HORIZONTAL</code>.
	 * <p>
	 * By default, the ProgressString is set to <code>null</code> and the
	 * ProgressStringPainted is not painted.
	 * <p>
	 * Uses the defaultMinimum (0) and defaultMaximum (100). Uses the
	 * defaultMinimum for the initial value of the progress bar.
	 */
	public ProgressBar() {
		this(new DefaultBoundedRangeModel());
	}

	/**
	 * Creates a horizontal progress bar, the default orientation.
	 * <p>
	 * By default, the ProgressString is set to <code>null</code> and the
	 * ProgressStringPainted is not painted.
	 * <p>
	 * Uses the specified BoundedRangeModel which holds the minimum, value, and
	 * maximum.
	 *  
	 */
	public ProgressBar(BoundedRangeModel newModel) {
		super();
		setModel(newModel);
		setRenderer(new DefaultProgressBarRenderer());
	}

	/**
	 * Creates a horizontal progress bar, which is the default.
	 * <p>
	 * By default, the ProgressString is set to <code>null</code> and the
	 * ProgressStringPainted is not painted.
	 * <p>
	 * Uses the specified minimum and maximum. Uses the specified minimum for
	 * the initial value of the progress bar.
	 */
	public ProgressBar(int min, int max) {
		this(new DefaultBoundedRangeModel(0, 0, min, max));
	}

	/**
	 * Adds a ChangeListener to the ProgressBar.
	 * 
	 * @param l
	 *            the ChangeListener to add
	 */
	public void addChangeListener(ChangeListener l) {
		getEventListenerList().addListener(ChangeListener.class, l);
	}

	/**
	 * Subclasses that want to handle ChangeEvents differently can override this
	 * to return a subclass of ModelListener or another ChangeListener
	 * implementation.
	 */
	protected ChangeListener createChangeListener() {
		return new InternalModelListener();
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type.
	 */
	protected void fireStateChanged() {
		EventListener[] listeners = getEventListenerList().getListeners(ChangeListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			if (changeEvent == null)
				changeEvent = new ChangeEvent(this);

			((ChangeListener) listeners[index]).stateChanged(changeEvent);
		}

	}

	/**
	 * @return The <code>Color</code> of the <code>ProgressBar's</code>
	 *         completed blocks.
	 */
	public Color getCompletedColor() {
		return (Color) getProperty(PROPERTY_COMPLETED_COLOR);
	}

	/**
	 * Returns the model's maximum value. By default, this is <code>100</code>.
	 * 
	 * @return an int -- the model's maximum
	 */
	public int getMaximum() {
		return getModel().getMaximum();
	}

	/**
	 * Returns the model's minimum value. By default, this is <code>0</code>.
	 *  
	 */
	public int getMinimum() {
		return getModel().getMinimum();
	}

	/**
	 * Returns the data model used by the <code>ProgressBar</code>.
	 *  
	 */
	public BoundedRangeModel getModel() {
		return (BoundedRangeModel) getProperty(PROPERTY_MODEL);
	}

	/**
	 * @return The number of blocks used when displaying the
	 *         <code>ProgressBar</code>.
	 */
	public int getNumberOfBlocks() {
		return getProperty(PROPERTY_NUMBER_OF_BLOCKS, 10);
	}

	/**
	 * Returns <code>ProgressBar.ORIENTATION_VERTICAL</code> or
	 * <code>ProgressBar.ORIENTATION_HORIZONTAL</code>, depending on the
	 * orientation of the progress bar. The default orientation is
	 * <code>ORIENTATION_HORIZONTAL</code>.
	 *  
	 */
	public int getOrientation() {
		return getProperty(PROPERTY_ORIENTATION, ORIENTATION_HORIZONTAL);
	}

	/**
	 * Returns the percentage/percent complete for the progress bar. Note that,
	 * as a double, this number is between 0.00 and 1.00.
	 * 
	 * @return the percent complete for this progress bar.
	 */
	public double getPercentComplete() {
		BoundedRangeModel model = getModel();
		double pc = 0;
		if (model != null) {
			long span = model.getMaximum() - model.getMinimum();
			double currentValue = model.getValue();
			pc = (currentValue - model.getMinimum()) / span;
		}
		return pc;
	}

	/**
	 * This returns the image used to represent the ProgressBar.
	 * <p>
	 * Notice that their is no setProgressBarImage() method. The internal image
	 * is updated whenever the model is changed.
	 * <p>
	 * This is done by making a call to the ProgressBarRenderer.drawProgressBar
	 * method, which subclasses can override to customise how the progress bar
	 * is drawn.
	 * <p>
	 *  
	 */
	public ImageReference getProgressBarImage() {
		return (ImageReference) getProperty(PROPERTY_PROGRESSBAR_IMAGE);
	}

	/**
	 * Returns the current value of the Progress String.
	 *  
	 */
	public String getProgressString() {
		String progressString = (String) getProperty(PROPERTY_PROGRESS_STRING);
		if (progressString != null) {
			return progressString;
		} else {
			int pc = (int) Math.round(100 * getPercentComplete());
			return new String(pc + "%");
		}
	}

	/**
	 * Returns the renderer used by the <code>ProgressBar</code>.
	 *  
	 */
	public ProgressBarRenderer getRenderer() {
		return (ProgressBarRenderer) getProperty(PROPERTY_RENDERER);
	}

	/**
	 * @return The <code>Color</code> of the <code>ProgressBar's</code>
	 *         uncompleted blocks.
	 */
	public Color getUncompletedColor() {
		return (Color) getProperty(PROPERTY_UNCOMPLETED_COLOR);
	}

	/**
	 * Returns the model's current value. The value is always between the
	 * model's minimum and maximum values, inclusive. By default, the value
	 * equals the minimum.
	 *  
	 */
	public int getValue() {
		return getModel().getValue();
	}

	/**
	 * Can be called to invalidate the ProgressBar so that it will redraw its
	 * internals when next rendered.
	 */
	public void invalidate() {
		invalid = true;
	}

	/**
	 * Returns true if the progress bar will render a string onto the
	 * representation of the progress bar. Returns false if it will not do this
	 * rendering. The default is false - the progress bar does not draw the
	 * string by default.
	 *  
	 */
	public boolean isProgressStringPainted() {
		return getProperty(PROPERTY_PROGRESS_STRING_PAINTED, false);
	}

	/**
	 * Removes a ChangeListener from the ProgressBar.
	 * 
	 * @param l
	 *            the ChangeListener to remove
	 */
	public void removeChangeListener(ChangeListener l) {
		getEventListenerList().removeListener(ChangeListener.class, l);
	}

	/**
	 * Sets the completed <code>Color</code> of the <code>ProgressBar</code>.
	 */
	public void setCompletedColor(Color newColor) {
		setProperty(PROPERTY_COMPLETED_COLOR, newColor);

	}

	/**
	 * Sets the model's maximum to <I>n </I>.
	 * 
	 * The underlying BoundedRangeModel will handle any mathematical issues
	 * arrising from assigning faulty values.
	 * <p>
	 * Notifies any listeners if the data changes.
	 *  
	 */
	public void setMaximum(int n) {
		getModel().setMaximum(n);
	}

	/**
	 * Sets the model's minimum to <I>n </I>.
	 * <p>
	 * The underlying BoundedRangeModel will handle any mathematical issues
	 * arrising from assigning faulty values.
	 * <p>
	 * Notifies any listeners if the data changes.
	 *  
	 */
	public void setMinimum(int n) {
		getModel().setMinimum(n);
	}

	/**
	 * Sets the data model used by the ProgressBar.
	 *  
	 */
	public void setModel(BoundedRangeModel newModel) {
		BoundedRangeModel oldModel = getModel();
		setProperty(PROPERTY_MODEL, newModel);
		if (newModel != oldModel) {
			if (oldModel != null) {
				oldModel.removeChangeListener(changeListener);
				changeListener = null;
			}

			if (newModel != null) {
				changeListener = createChangeListener();
				newModel.addChangeListener(changeListener);
			}
			newModel.setExtent(0);

		}
	}

	/**
	 * Sets the number of blocks used when displaying the
	 * <code>ProgressBar<</code>
	 *  
	 */
	public void setNumberOfBlocks(int newValue) {
		if (newValue < 0)
			newValue = 1;
		setProperty(PROPERTY_NUMBER_OF_BLOCKS, newValue);

	}

	/**
	 * Sets the progress bar's orientation to <I>newOrientation </I>, which must
	 * be <code>ProgressBar.ORIENTATION_VERTICAL</code> or
	 * <code>ProgressBar.ORIENTATION_HORIZONTAL</code>. The default
	 * orientation is <code>ORIENTATION_HORIZONTAL</code>.
	 * <p>
	 * 
	 *  
	 */
	public void setOrientation(int newOrientation) {
		setOrientation(newOrientation, true);
	}

	/**
	 * Sets the progress bar's orientation to <I>newOrientation </I>, which must
	 * be <code>ProgressBar.ORIENTATION_VERTICAL</code> or
	 * <code>ProgressBar.ORIENTATION_HORIZONTAL</code>. The default
	 * orientation is <code>ORIENTATION_HORIZONTAL</code>.
	 * <p>
	 * If the flipWidthAndHeight parameter is true, then the width and height
	 * properties will be flipped around.
	 *  
	 */
	public void setOrientation(int newOrientation, boolean flipWidthAndHeight) {
		int oldOrientation = getOrientation();
		if (oldOrientation != newOrientation) {
			switch (newOrientation) {
			case ORIENTATION_VERTICAL:
			case ORIENTATION_HORIZONTAL:
				if (flipWidthAndHeight) {
					Extent temp = getWidth();
					setWidth(getHeight());
					setHeight(temp);
				}
				setProperty(PROPERTY_ORIENTATION, newOrientation);

				break;
			default:
				throw new IllegalArgumentException(newOrientation + " is not a legal orientation");
			}
		}
	}

	/**
	 * Sets the value of the Progress String. By default, this String is set to
	 * <code>null</code>.
	 * <p>
	 *  
	 */
	public void setProgressString(String newValue) {
		setProperty(PROPERTY_PROGRESS_STRING, newValue);
	}

	/**
	 * Sets whether the progress bar will render a string.
	 *  
	 */
	public void setProgressStringPainted(boolean newValue) {
		setProperty(PROPERTY_PROGRESS_STRING_PAINTED, newValue);
	}

	/**
	 * Sets the renderer used by the ProgressBar.
	 *  
	 */
	public void setRenderer(ProgressBarRenderer newRenderer) {
		setProperty(PROPERTY_RENDERER, newRenderer);
	}

	/**
	 * Sets the uncompleted <code>Color</code> of the <code>ProgressBar</code>.
	 *  
	 */
	public void setUncompletedColor(Color newColor) {
		setProperty(PROPERTY_UNCOMPLETED_COLOR, newColor);
	}

	/**
	 * Sets the model's value to <I>n </I>.
	 * <p>
	 * The underlying BoundedRangeModel will handle any mathematical issues
	 * arrising from assigning faulty values.
	 * <p>
	 * Notifies any listeners if the data changes.
	 *  
	 */
	public void setValue(int n) {
		getModel().setValue(n);
	}

	/**
	 *  
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("w=");
		sb.append(getWidth());
		sb.append(" h=");
		sb.append(getHeight());
		sb.append(" orient=");
		sb.append((getOrientation() == ORIENTATION_HORIZONTAL ? "horz" : "vert"));
		sb.append(" val=");
		sb.append(getValue());
		return sb.toString();
	}

	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
		super.validate();
		if (invalid) {
			/**
			 * The image needs to be redraw when ever the model or properties
			 * change. We are listening for both.
			 */
			ImageReference newValue;
			ProgressBarRenderer renderer = getRenderer();
			if (renderer == null) {
				newValue = null;
			} else {
				newValue = renderer.drawProgressBar(this);
			}
			setProperty(PROPERTY_PROGRESSBAR_IMAGE, newValue);
			invalid = false;
		}
	}
}
