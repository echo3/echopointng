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
import java.util.EventListener;

import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

/**
 * The <code>ImageIcon</code> class provides an component  
 * that displays an <code>ImageReference</code>.  A height and width
 * value can be specified to overide what may be defined in the 
 * <code>ImageReference</code> itself.  This allows images to be
 * "scaled" to different dimensions.
 * <p>
 * The advantage of <code>ImageIcon</code> over using a <code>nextapp.echo.LabelEx</code>
 * is that you can scale <code>ImageReference</code> objects that you may not
 * know the dimensions of, and it can be clicked on like a ButtonEx.
 */
public class ImageIcon extends AbleComponent  {
	
//	/** default height is 32 */
//	public static final Extent DEFAULT_HEIGHT = new Extent(32);
//
//	/** default width is 32 */
//	public static final Extent DEFAULT_WIDTH = new Extent(32);


	public static final String PROPERTY_ACTION_COMMAND = "actionCommand";
	public static final String PROPERTY_ICON = "icon";
	
	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
//		style.setProperty(PROPERTY_HEIGHT, DEFAULT_HEIGHT);
//		style.setProperty(PROPERTY_WIDTH, DEFAULT_WIDTH);
//		style.setProperty(MouseCursorable.PROPERTY_MOUSE_CURSOR, MouseCursorable.CURSOR_POINTER);
		DEFAULT_STYLE = style;
	}

	/**
	 * Constructs a <code>ImageIcon</code> with a null ImageReference.
	 * The width and height are set to DEFAULT_WIDTH and/or DEFAULT_HEIGHT.
	 */
	public ImageIcon() {
		this(null);
	}

	/**
	 * Constructs a <code>ImageIcon</code> and sets the
	 * width and height to be that of the ImageReference.  
	 *  
	 * @param imageRef
	 */
	public ImageIcon(ImageReference imageRef) {
		setFocusTraversalParticipant(true);
		setIcon(imageRef);


		if (imageRef != null) { 
			Extent width = imageRef.getWidth();
			Extent height = imageRef.getHeight();
			if (width != null)
				setWidth(width);	
			if (height != null)
				setHeight(height);
		}
	}

	/**
	 * Constructs a <code>ImageIcon</code> with the specified
	 * width and height
	 * 
	 * @param imageRef - the ImageReference to display
	 * @param width - the width to scale it to
	 * @param height - the height to scale it to
	 */
	public ImageIcon(ImageReference imageRef, Extent width, Extent height) {
		setIcon(imageRef);
		setWidth(width);
		setHeight(height);
	}

	/**
	 * Constructs a <code>ImageIcon</code> with the specified
	 * width and height
	 * 
	 * @param imageRef - the ImageReference to display
	 * @param width - the width in pixels to scale it to
	 * @param height - the height in pixels to scale it to
	 */
	public ImageIcon(ImageReference imageRef, int width, int height) {
		this(imageRef,new Extent(width), new Extent(height));
	}
	
	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String, java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		fireActionPerformed();
	}
	
	/**
	 * Adds an <code>ActionListener</code>.
	 *
	 * @param l The <code>ActionListener</code> to be added.
	 */
	public void addActionListener(ActionListener l) {
		getEventListenerList().addListener(ActionListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered for this event type.
	 */
	public void fireActionPerformed() {
		ActionEvent e = new ActionEvent(this,getActionCommand());
		EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			((ActionListener) listeners[index]).actionPerformed(e);
		}
	}
	/**
	 * Returns the action command for this ImageIcon.
	 *
	 * @return The action command for this button.
	 */
	public String getActionCommand() {
		return (String) getProperty(PROPERTY_ACTION_COMMAND);
	}
	
	/** 
	 * Returns the ImageReference within.
	 * 
	 * @return ImageReference - the ImageReference within.
	 */
	public ImageReference getIcon() {
		return (ImageReference) getProperty(PROPERTY_ICON);
	}

	/**
	 * Removes an <code>ActionListener</code>.
	 *
	 * @param l The <code>ActionListener</code> to be removed.
	 */
	public void removeActionListener(ActionListener l) {
		getEventListenerList().removeListener(ActionListener.class, l);
	}
	
	/**
     * Determines if the <code>ImageIcon</code> has any <code>ActionListener</code>s 
     * registered.
     * 
     * @return true if any action listeners are registered
     */
    public boolean hasActionListeners() {
        return hasEventListenerList() && getEventListenerList().getListenerCount(ActionListener.class) != 0;
    }
	
	
	/**
	 * Sets the ImageIcon's action command.
	 *
	 * @param newValue The new action command for this ImageIcon.
	 */
	public void setActionCommand(String newValue) {
		setProperty(PROPERTY_ACTION_COMMAND,newValue);
	}

	/** 
	 * Sets the ImageReference used by the ImageIcon.
	 * 
	 * @param newValue 
	 */
	public void setIcon(ImageReference newValue) {
		setProperty(PROPERTY_ICON,newValue);
	}

	/**
	 * @see echopointng.able.Heightable#getHeight()
	 */
	public Extent getHeight() {
		return (Extent) getProperty(PROPERTY_HEIGHT);
	}

	/**
	 * @see echopointng.able.Widthable#getWidth()
	 */
	public Extent getWidth() {
		return (Extent) getProperty(PROPERTY_WIDTH);
	}

	/**
	 * @see echopointng.able.Heightable#setHeight(nextapp.echo2.app.Extent)
	 */
	public void setHeight(Extent newValue) {
		setProperty(PROPERTY_HEIGHT,newValue);
	}

	/**
	 * @see echopointng.able.Widthable#setWidth(nextapp.echo2.app.Extent)
	 */
	public void setWidth(Extent newValue) {
		setProperty(PROPERTY_WIDTH,newValue);
	}
}
