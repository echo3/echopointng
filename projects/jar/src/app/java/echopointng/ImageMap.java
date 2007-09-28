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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import echopointng.able.Borderable;
import echopointng.able.MouseCursorable;

import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

/**
 *
 * The <code>ImageMap</code> class provides a <code>Component</code>
 * that allows a user to click on region within a provided region.  
 * <br>
 * A series of <code>ImageMap.Coords</code> are provided that 
 * indicate what areas on the region should produce an
 * <code>ActionEvent</code>.
 * <br>
 * This <code>ImageMap.Coords</code> are stored in a map keyed by
 * by their ActionCommand String.  This means that their can be at 
 * most one set of coordinates for a given ActionCommand.
 * <br>
 * @author Brad Baker 
 */

public class ImageMap extends AbleComponent {

	/** Property Change Names */
	public static final String PROPERTY_IMAGE = "image";
	
	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(Borderable.PROPERTY_BORDER, new BorderEx(0,null,BorderEx.STYLE_NONE));
		style.setProperty(MouseCursorable.PROPERTY_MOUSE_CURSOR, MouseCursorable.CURSOR_POINTER);
		DEFAULT_STYLE = style;
	}
	
	// our list of co-ordinnates
	private Map coordMap = null;

	/**
	 * The Coords object is a set of co-ordinates and an ActionCommand
	 * String.
	 *
	 * The co-ordinates can be used to represent a circle, rectangle or 
	 * polygon.  The contructor used will determine type of Coords object
	 * is created
	 */
	public static class Coords implements Serializable {

		public static final int RECTANGLE = 0;
		public static final int CIRCLE = 1;
		public static final int POLYGON = 2;

		private int type;
		private int top;
		private int bottom;
		private int left;
		private int right;
		private int x;
		private int y;
		private int radius;
		private int[] polyCoords;

		private String actionCommand = null;
		private String altText = null;

		/**
		 * Creates a new Rectangular <code>Coords</code> object with the given dimensions
		 * with no ActionCommand string.
		 *
		 * @param left The value of the left co-ord.
		 * @param top The value of the top co-ord.
		 * @param right The value of the right co-ord.
		 * @param bottom The value of the bottom co-ord.
		 */
		public Coords(int left, int top, int right, int bottom) {
			this(left, top, right, bottom, null);
		}

		/**
		 * Creates a new Rectangular <code>Coords</code> object with the given dimensions
		 * with the given actionCommand String.
		 *
		 * @param left The value of the left co-ord.
		 * @param top The value of the top co-ord.
		 * @param right The value of the right co-ord.
		 * @param bottom The value of the bottom co-ord.
		 */
		public Coords(int left, int top, int right, int bottom, String actionCommand) {
			super();

			this.type = RECTANGLE;
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
			this.actionCommand = actionCommand;
		}

		/**
		 * Creates a new Circular <code>Coords</code> object with the given dimensions
		 * with no ActionCommand string.
		 *
		 * @param x The value of the x co-ord.
		 * @param y The value of the y co-ord.
		 * @param radius The value of the radius co-ord.
		 */
		public Coords(int x, int y, int radius) {
			this(x, y, radius, null);
		}

		/**
		 * Creates a new Circular <code>Coords</code> object with the given dimensions
		 * with the given ActionCommand string.
		 *
		 * @param x The value of the x co-ord.
		 * @param y The value of the y co-ord.
		 * @param radius The value of the radius co-ord.
		 * @param actionCommand The values of the ActionCommand
		 */
		public Coords(int x, int y, int radius, String actionCommand) {
			super();

			this.type = CIRCLE;
			this.x = x;
			this.y = y;
			this.radius = radius;

			this.actionCommand = actionCommand;
		}

		/**
		 * Creates a new Polygon <code>Coords</code> object with the given dimensions
		 * with no ActionCommand string.
		 *
		 * @param polyCoords An integer array of co-ordinates
		 */
		public Coords(int[] polyCoords) {
			this(polyCoords, null);
		}

		/**
		 * Creates a new Polygon <code>Coords</code> object with the given dimensions
		 * with the given ActionCommand string.
		 *
		 * @param polyCoords An integer array of co-ordinates
		 * @param actionCommand The values of the ActionCommand
		 */
		public Coords(int[] polyCoords, String actionCommand) {
			super();
			this.type = POLYGON;
			this.polyCoords = polyCoords;
			this.actionCommand = actionCommand;
		}

		/**
		 * Returns the value of the ActionCommand.
		 *
		 * @return The value of the ActionCommand.
		 */
		public String getActionCommand() {
			return actionCommand;
		}

		/**
		 * Returns the value of the bottom co-ord.
		 *
		 * @return The value of the bottom co-ord.
		 */
		public int getBottom() {
			return bottom;
		}

		/**
		 * Returns the value of the left co-ord.
		 *
		 * @return The value of the left co-ord.
		 */
		public int getLeft() {
			return left;
		}

		/**
		 * Returns the value of the right co-ord.
		 *
		 * @return The value of the right co-ord.
		 */
		public int getRight() {
			return right;
		}

		/**
		 * Returns the value of the top co-ord.
		 *
		 * @return The value of the top co-ord.
		 */
		public int getTop() {
			return top;
		}

		/**
		 * Returns the value of the x co-ord.
		 *
		 * @return The value of the x co-ord.
		 */
		public int getX() {
			return x;
		}

		/**
		 * Returns the value of the y co-ord.
		 *
		 * @return The value of the y co-ord.
		 */
		public int getY() {
			return y;
		}

		/**
		 * Returns the value of the radius co-ord.
		 *
		 * @return The value of the radius co-ord.
		 */
		public int getRadius() {
			return radius;
		}

		/**
		 * Returns the array of the polygon co-ords.
		 *
		 * @return The array of the polygon co-ords.
		 */
		public int[] getPolygonCoords() {
			return polyCoords;
		}

		/**
		 * Returns the type co-ords.
		 *
		 * This can be :
		 * <ul>
		 *	<li>RECTANGLE</li>
		 *	<li>CIRCLE</li>
		 *	<li>POLYGON</li>
		 * </ul>
		 *
		 * @return The type co-ords.
		 */
		public int getType() {
			return type;
		}

		/**
		 * Sets the action command of the co-ords
		 */
		public void setActionCommand(String newActionCommand) {
			actionCommand = newActionCommand;
		}
		
		/**
		 * @return Returns the alternate text for a given co-ord.
		 */
		public String getAltText() {
			return altText;
		}
		
		/**
		 * Sets the alternate text for a given co-ord
		 * @param altText - the alternate text for a given co-ord.
		 */
		public void setAltText(String altText) {
			this.altText = altText;
		}
	}
	/**
	 * Constructs a <code>ImageMap</code> with nothing in it
	 *
	 */
	public ImageMap() {
		this(null);
	}
	/**
	 * Constructs an <code>ImageMap</code> using the provided image.
	 *
	 */
	public ImageMap(ImageReference image) {
		super();
		this.coordMap = new HashMap();
		setImage(image);
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
	 * Adds a set of co-ordinate's to the <code>ImageMap</code>
	 */
	public void addCoord(ImageMap.Coords coord) {
		coordMap.put(coord.getActionCommand(), coord);
	}
	/**
	 * Adds a set of co-ordinate's to the <code>ImageMap</code> and 
	 * sets the co-ordinate's action command at the same time.
	 */
	public void addCoord(ImageMap.Coords coord, String actionCommand) {
		coord.setActionCommand(actionCommand);
		coordMap.put(coord.getActionCommand(), coord);
	}
	
	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String, java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		fireActionPerformed(new ActionEvent(this,inputName));
	}
	
	/**
	 * Notifies all listeners that have registered for this event type.
	 *
	 * @param e The <code>ActionEvent</code> to send.
	 */
	public void fireActionPerformed(ActionEvent e) {
		EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			((ActionListener) listeners[index]).actionPerformed(e);
		}
	}
	/**
	 * Returns the set of co-ordinates in the <code>ImageMap</code>
	 */
	public Set getCoords() {
		return coordMap.entrySet();
	}
	/**
	 * @return The <code>ImageReference</code> of the <code>ImageMap</code>.
	 */
	public ImageReference getImage() {
		return (ImageReference) getProperty(PROPERTY_IMAGE);
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
	 * Sets the <code>ImageReference</code> of the <code>ImageMap</code>.
	 */
	public void setImage(ImageReference newValue) {
		setProperty(PROPERTY_IMAGE,newValue);
	}
}
