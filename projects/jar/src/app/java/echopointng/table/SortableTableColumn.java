/* 
 * This file is part of the Echo2 Table Extension (hereinafter "ETE").
 * Copyright (C) 2002-2005 NextApp, Inc.
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
package echopointng.table;

import java.util.Comparator;

import nextapp.echo2.app.Extent;
import nextapp.echo2.app.table.TableCellRenderer;

/**
 * <code>TableColumn</code> class that supports a <code>Comparator</code>
 * property.  The default is a simple Object.toString() comparator.
 * 
 * @author Jason Dalton
 */
public class SortableTableColumn extends TableColumnEx {
    
    public static final Comparator DEFAULT_COMPARATOR = new StringComparator();
    private Comparator comparator;
    
    /**
     * @see TableColumnEx#TableColumnEx(int, Extent, Object, TableCellRenderer, TableCellRenderer)
	 */
	public SortableTableColumn(int modelIndex, Extent width, Object headerValue, TableCellRenderer cellRenderer, TableCellRenderer headerRenderer) {
		super(modelIndex, width, headerValue, cellRenderer, headerRenderer);
	}

    /**
     * @see TableColumnEx#TableColumnEx(int, Extent, TableCellRenderer, TableCellRenderer)
	 */
	public SortableTableColumn(int modelIndex, Extent width, TableCellRenderer cellRenderer, TableCellRenderer headerRenderer) {
		super(modelIndex, width, cellRenderer, headerRenderer);
	}

    /**
     * @see TableColumnEx#TableColumnEx(int, Extent) 
	 */
	public SortableTableColumn(int modelIndex, Extent width) {
		super(modelIndex, width);
	}

    /**
     * @see TableColumnEx#TableColumnEx(int, Object) 
	 */
	public SortableTableColumn(int modelIndex, Object headerValue) {
		super(modelIndex, headerValue);
	}

    /**
     * @see TableColumnEx#TableColumnEx(int) 
	 */
	public SortableTableColumn(int modelIndex) {
		super(modelIndex);
	}

	/**
     * @see nextapp.echo2.app.table.TableColumn#getHeaderRenderer()
     */
    public TableCellRenderer getHeaderRenderer() {
        if (super.getHeaderRenderer() == null) {
            return new SortableTableHeaderRenderer();
        } else {
            return super.getHeaderRenderer();
        }
    }
        
    public Comparator getComparator() {
        if (comparator == null){
            comparator = DEFAULT_COMPARATOR;
        }
        return comparator;
    }
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }
}

class StringComparator implements Comparator {
    
    public int compare(Object o1, Object o2) {
        if (o1 == null || o2 == null){
            return 1;
        } else {
            return o1.toString().compareTo(o2.toString());
        }
    }
}