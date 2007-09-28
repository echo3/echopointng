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
import java.util.ArrayList;
import java.util.List;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.event.ListDataEvent;
import nextapp.echo2.app.event.ListDataListener;
import nextapp.echo2.app.list.DefaultListModel;
import nextapp.echo2.app.list.ListModel;

/**
 * 
 * The <code>ListSection</code> class provides a <code>Component</code> that
 * can contain other <code>Component</code>s, <code>XhtmlFragment</code>s
 * or <code>Object</code>s in a list.
 * <p>
 * The list can be ordered or unordered. The default is unordered. <br>
 * The class uses the <code>ListModel</code> interface to provide its
 * contents.
 * <p>
 * If a <code>ListSection</code> is added as a child of another
 * <code>ListSection</code>, then a new level of list is rendered.
 * 
 * @author Brad Baker
 */
public class ListSection extends AbleComponent {

	public static final String PROPERTY_ORDERING = "ordering";
	public static final String PROPERTY_BULLETS = "bullets";
	public static final String PROPERTY_BULLETS_IMAGE = "bulletsImage";
	public static final String PROPERTY_MODEL = "model";

	/**
	 * Determines what type of bullet points are used
	 */
	public static final int BULLETS_NONE = 0;
	public static final int BULLETS_DISC = 1;
	public static final int BULLETS_CIRCLE = 2;
	public static final int BULLETS_SQUARE = 3;
	public static final int BULLETS_DECIMAL = 4;
	public static final int BULLETS_DECIMAL_LEADING_ZERO = 5;
	public static final int BULLETS_LOWER_ROMAN = 6;
	public static final int BULLETS_UPPER_ROMAN = 7;
	public static final int BULLETS_LOWER_ALPHA = 8;
	public static final int BULLETS_UPPER_ALPHA = 9;
	public static final int BULLETS_LOWER_GREEK = 10;
	public static final int BULLETS_LOWER_LATIN = 11;
	public static final int BULLETS_UPPER_LATIN = 12;

	/** default bullets */
	public static final int BULLETS_DEFAULT = BULLETS_DISC;

	/**
	 * Determines whether the list is ordered or not
	 */
	public static final int ORDERED = 0;
	public static final int UNORDERED = 1;
	
	
	/**
	 * Local handler for list data events.
	 */
	private class ListDataHandler implements ListDataListener, Serializable {

		public void contentsChanged(ListDataEvent e) {
			firePropertyChange("listDataChanged", null, null);
		}

		public void intervalAdded(ListDataEvent e) {
			firePropertyChange("listDataChanged", null, null);
		}

		public void intervalRemoved(ListDataEvent e) {
			firePropertyChange("listDataChanged", null, null);
		}
	}

	// our internal data listener
	private ListDataHandler dataListener;

	
	/**
	 * Constructs a <code>ListSection</code> with nothing in it
	 * 
	 */
	public ListSection() {
		this(UNORDERED, new ArrayList());
	}

	/**
	 * Constructs a <code>ListSection</code> with the list contents as
	 * arrList.
	 * 
	 */
	public ListSection(Object[] arrList) {
		this(UNORDERED, arrList);
	}

	/**
	 * Constructs a <code>ListSection</code>
	 * 
	 * Ordering can be one of the following values.
	 * 
	 * <ul>
	 * <li>ListSection.UNORDERED (the default)</li>
	 * <li>ListSection.ORDERED</li>
	 * </ul>
	 */
	public ListSection(int ordering) {
		this(ordering, new ArrayList());
	}

	/**
	 * Constructs a <code>ListSection</code> with the objects supplied in the
	 * provided array.
	 * 
	 * Ordering can be one of the following values.
	 * 
	 * <ul>
	 * <li>ListSection.UNORDERED (the default)</li>
	 * <li>ListSection.ORDERED</li>
	 * </ul>
	 */
	public ListSection(int ordering, Object[] arrList) {
		super();

		ArrayList newList = new ArrayList(arrList.length);
		for (int i = 0; i < arrList.length; i++) {
			newList.add(arrList[i]);
		}

		construct(ordering, newList);
	}

	/**
	 * Constructs a <code>ListSection</code> with the objects supplied in the
	 * provided list.
	 * 
	 * Ordering can be one of the following values.
	 * 
	 * <ul>
	 * <li>ListSection.UNORDERED (the default)</li>
	 * <li>ListSection.ORDERED</li>
	 * </ul>
	 */
	public ListSection(int ordering, List newList) {
		super();
		construct(ordering, newList);
	}

	/**
	 * Adds an <code>Object</code> to the <code>ListSection</code>
	 * <p>
	 * Note that this will only be performed if the underlying model is derived
	 * from <code>DefaultListModel</code>.
	 */
	public void add(Object object) {
		add(object, getModel().size());
	}

	/**
	 * Adds an <code>Object</code> to the <code>ListSection</code>at the
	 * specified index.
	 * <p>
	 * Note that this will only be performed if the underlying model is derived
	 * from <code>DefaultListModel</code>.
	 */
	public void add(Object object, int index) {
		if (object instanceof Component) {
			this.add((Component) object, index);
		} else {
			addImpl(object, index);
		}
	}

	/**
	 * Adds a <code>Component</code> to the <code>ListSection</code>
	 * <p>
	 * Note that this will only be performed if the underlying model is derived
	 * from <code>DefaultListModel</code>.
	 */
	public void add(Component c) {
		add(c, -1);
	}

	/**
	 * Adds a <code>Component</code> to the <code>ListSection</code>at the
	 * specified index. -1 indicates to add to the end of the list
	 * <p>
	 * Note that this will only be performed if the underlying model is derived
	 * from <code>DefaultListModel</code>.
	 */
	public void add(Component c, int index) {
		if (index == -1) {
			super.add(c, -1);
			index = getModel().size();
		} else if (index >= getComponentCount()) {
			super.add(c, -1);
		} else {
			super.add(c, index);
		}
		addImpl(c, index);
	}

	private void addImpl(Object object, int index) {
		if (getModel() instanceof DefaultListModel) {
			((DefaultListModel) getModel()).add(index, object);
		}
	}

	/**
	 * Adds a <code>ListDataListener</code> to the section
	 * 
	 * @param l
	 *            The <code>ListDataListener</code> to be added.
	 */
	public void addListDataListener(ListDataListener l) {
		getModel().addListDataListener(l);
	}

	/**
	 * The actual contructor
	 */
	private void construct(int ordering, List newList) {
		dataListener = new ListDataHandler();
		setOrdering(ordering);
		ListModel model = new DefaultListModel(newList.toArray());
		setModel(model);
		model.addListDataListener(dataListener);
	}

	/**
	 * @return The bullets in use by the <code>ListSection</code>
	 * 
	 * Can be one of the following values.
	 * 
	 * <ul>
	 * <li>ListSection.BULLETS_NONE</li>
	 * <li>ListSection.BULLETS_DISC</li>
	 * <li>ListSection.BULLETS_CIRCLE</li>
	 * <li>ListSection.BULLETS_SQUARE</li>
	 * <li>ListSection.BULLETS_DECIMAL</li>
	 * <li>ListSection.BULLETS_DECIMAL_LEADING_ZERO</li>
	 * <li>ListSection.BULLETS_LOWER_ROMAN</li>
	 * <li>ListSection.BULLETS_UPPER_ROMAN</li>
	 * <li>ListSection.BULLETS_LOWER_ALPHA</li>
	 * <li>ListSection.BULLETS_UPPER_ALPHA</li>
	 * <li>ListSection.BULLETS_LOWER_GREEK</li>
	 * <li>ListSection.BULLETS_LOWER_LATIN</li>
	 * <li>ListSection.BULLETS_UPPER_LATIN</li>
	 * </ul>
	 * 
	 */
	public int getBullets() {
		return getProperty(PROPERTY_BULLETS, BULLETS_DEFAULT);
	}

	/**
	 * @return The <code>ImageReference</code> of the
	 *         <code>ListSection's</code> bullets.
	 */
	public ImageReference getBulletsImage() {
		return (ImageReference) getProperty(PROPERTY_BULLETS_IMAGE);
	}

	/**
	 * @return The contents of the <code>ListModel</code> of the
	 *         <code>ListSection</code> as an array.
	 */
	public Object[] getList() {
		ListModel model = getModel();
		Object[] arr = new Object[model.size()];
		for (int i = 0; i < model.size(); i++) {
			arr[i] = model.get(i);
		}
		return arr;
	}

	/**
	 * @return The <code>ListModel</code> of the <code>ListSection</code>.
	 */
	public ListModel getModel() {
		return (ListModel) getProperty(PROPERTY_MODEL);
	}

	/**
	 * @return The ordering in use by the <code>ListSection</code>
	 * 
	 * Can be one of the following values.
	 * 
	 * <ul>
	 * <li>ListSection.UNORDERED (the default)</li>
	 * <li>ListSection.ORDERED</li>
	 * </ul>
	 * 
	 */
	public int getOrdering() {
		return getProperty(PROPERTY_ORDERING, UNORDERED);
	}

	/**
	 * Removes a <code>String</code> from the <code>ListSection</code>
	 * <p>
	 * Note that this will only be performed if the underlying model is derived
	 * from <code>DefaultListModel</code>.
	 */
	public void remove(Object object) {
		ListModel model = getModel();
		if (model instanceof DefaultListModel) {
			for (int i = 0; i < model.size(); i++) {
				Object o = model.get(i);
				if (o == object) {
					((DefaultListModel) model).remove(o);
					break;
				}
			}
		}
	}

	/**
	 * Removes a <code>Component</code> from the <code>ListSection</code>
	 * <p>
	 * Note that this will only be performed if the underlying model is derived
	 * from <code>DefaultListModel</code>.
	 */
	public void remove(Component c) {
		super.remove(c);
		if (getModel() instanceof DefaultListModel) {
			((DefaultListModel) getModel()).remove(c);
		}
	}

	/**
	 * Removes a ListDataListener from this component.
	 * 
	 * @param l
	 *            the ListDataListener to remove
	 * 
	 */
	public void removeListDataListener(ListDataListener l) {
		getModel().removeListDataListener(l);
	}

	/**
	 * Sets the bullets in use for the <code>ListSection</code>. <br>
	 * Can be one of the following values.
	 * <ul>
	 * <li>ListSection.BULLETS_NONE</li>
	 * <li>ListSection.BULLETS_DISC</li>
	 * <li>ListSection.BULLETS_CIRCLE</li>
	 * <li>ListSection.BULLETS_SQUARE</li>
	 * <li>ListSection.BULLETS_DECIMAL</li>
	 * <li>ListSection.BULLETS_DECIMAL_LEADING_ZERO</li>
	 * <li>ListSection.BULLETS_LOWER_ROMAN</li>
	 * <li>ListSection.BULLETS_UPPER_ROMAN</li>
	 * <li>ListSection.BULLETS_LOWER_ALPHA</li>
	 * <li>ListSection.BULLETS_UPPER_ALPHA</li>
	 * <li>ListSection.BULLETS_LOWER_GREEK</li>
	 * <li>ListSection.BULLETS_LOWER_LATIN</li>
	 * <li>ListSection.BULLETS_UPPER_LATIN</li>
	 * </ul>
	 */
	public void setBullets(int newBullets) {
		setProperty(PROPERTY_BULLETS, newBullets);
	}

	/**
	 * Sets the <code>ImageReference</code> of the <code>ListSection's</code>
	 * bullets. <br>
	 */
	public void setBulletsImage(ImageReference newBulletsImage) {
		setProperty(PROPERTY_BULLETS_IMAGE, newBulletsImage);
	}

	/**
	 * Sets the contents of the <code>ListModel</code> of the
	 * <code>ListSection</code>.
	 * 
	 */
	public void setList(Object[] newList) {
		setProperty(PROPERTY_MODEL, new DefaultListModel(newList));
	}

	/**
	 * Sets the contents of the <code>ListModel</code> of the
	 * <code>ListSection</code>. <br>
	 */
	public void setList(List newList) {
		setProperty(PROPERTY_MODEL, new DefaultListModel(newList.toArray(new Object[newList.size()])));
	}

	/**
	 * Sets the <code>ListModel</code> of the <code>ListSection</code>.
	 * <br>
	 */
	public void setModel(ListModel newModel) {
		if (newModel == null) {
			throw new IllegalStateException("The ListSection must have a non null ListModel");
		}
		ListModel oldValue = getModel();
		if (oldValue != null)
			oldValue.removeListDataListener(dataListener);
		if (newModel != null)
			newModel.addListDataListener(dataListener);

		setProperty(PROPERTY_MODEL, newModel);
	}

	/**
	 * Sets the ordering in use for the <code>ListSection</code>. <br>
	 * Can be one of the following values.
	 * 
	 * <ul>
	 * <li>ListSection.UNORDERED (the default)</li>
	 * <li>ListSection.ORDERED</li>
	 * </ul>
	 */
	public void setOrdering(int newOrdering) {
		setProperty(PROPERTY_ORDERING, newOrdering);
	}

}
