package echopointng.treetable;

import echopointng.tree.TreeModel;

/*
 * This file is part of the Echo Point Project. This project is a collection of
 * Components that have extended the Echo Web Application Framework.
 * 
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or the
 * GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in which
 * case the provisions of the GPL or the LGPL are applicable instead of those
 * above. If you wish to allow use of your version of this file only under the
 * terms of either the GPL or the LGPL, and not to allow others to use your
 * version of this file under the terms of the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and other
 * provisions required by the GPL or the LGPL. If you do not delete the
 * provisions above, a recipient may use your version of this file under the
 * terms of any one of the MPL, the GPL or the LGPL.
 */

/*
 * The design paradigm, class name and some code used within have been taken
 * from the java.net JDNC project. This project is licensed under the LGPL and
 * is Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This file was made part of the EchoPoint project on the 15/07/2005.
 *  
 */

/**
 * <code>TreeTableModel</code> is a <code>TreeModel</code> that also adds <code>TableModel</code> like
 * column/row functionality.
 */
public interface TreeTableModel extends TreeModel {
	/**
	 * @see nextapp.echo2.app.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int column);
	/**
	 * @see nextapp.echo2.app.table.TableModel#getColumnCount()
	 */
	public int getColumnCount();

	/**
	 * @see nextapp.echo2.app.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column);

	/**
	 * Much like the <code>TableModel.getValueAt()</code> except that
	 * it asks for a value in the context of a <code>TreeModel</code> node.
	 * 
	 * @see nextapp.echo2.app.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(Object node, int column);
}