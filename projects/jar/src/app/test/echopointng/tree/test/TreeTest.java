package echopointng.tree.test;
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
import junit.framework.TestCase;
import echopointng.Tree;
import echopointng.tree.DefaultMutableTreeNode;
import echopointng.tree.DefaultTreeModel;
import echopointng.tree.TreePath;

/**
 * <code>TreeTest</code>
 */
public class TreeTest extends TestCase {

	public void testLargeModel() {
		LargeLazyTreeModel model = new LargeLazyTreeModel();
		Tree tree = new Tree(model);

		LargeLazyTreeNode root = (LargeLazyTreeNode) model.getRoot();
		assertNotNull(root);
		
		TreePath path = new TreePath(model.getPathToRoot(root));
		assertTrue(tree.isExpanded(path));
		
		int cc = model.getChildCount(root);
		assertTrue(cc > 0);
		assertNotNull(model.getChild(root,cc-1));
		assertNotNull(model.getChild(root,0));
		
		
	}
	
	public void testRootExpanded() {
		LargeLazyTreeModel model = new LargeLazyTreeModel();
		Tree tree = new Tree();
		tree.setRootAutoExpanded(false);
		tree.setModel(model);

		LargeLazyTreeNode root = (LargeLazyTreeNode) model.getRoot();
		TreePath rootPath = new TreePath(model.getPathToRoot(root));
		
		assertFalse(tree.isRootAutoExpanded());
		assertFalse(tree.isExpanded(rootPath));
		assertTrue(model.getDepth() == 1);
		assertTrue(model.getChildCreates() == 0);
		//assertTrue(tree.getRowCount() == 1);
		
		tree.expandPath(rootPath); // cause the children to be expanded
		assertTrue(tree.isExpanded(rootPath));
		assertTrue(model.getDepth() == 2);
		assertTrue(model.getChildCreates() == 1);
		//assertTrue(tree.getRowCount() > 1);
		
		LargeLazyTreeNode node = (LargeLazyTreeNode) model.getChild(root,0);
		node = (LargeLazyTreeNode) model.getChild(node,0); // down again
		assertTrue(model.getDepth() == 3);
		assertTrue(model.getChildCreates() == 2);

		model = new LargeLazyTreeModel();
		tree = new Tree();
		tree.setModel(model);
		root = (LargeLazyTreeNode) model.getRoot();
		rootPath = new TreePath(model.getPathToRoot(root));
		
		assertTrue(tree.isRootAutoExpanded());
		assertTrue(tree.isExpanded(rootPath));
		assertTrue(model.getDepth() == 1);
		// the child wont be created now but once the
		// component is rendered it will be because the root is 
		// expanded and hence the 2nd level children are visible
		// and will have to be accessed.
		assertTrue(model.getChildCreates() == 0);		
	}
	
	public void testTreeRows() {
		Tree tree;
		int totalNodeCount = 1;
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("root");
		for (int i = 0; i < 5; i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("child"+i);
			parentNode.add(childNode);
			totalNodeCount++;
			for (int j = 0; j < 5; j++) {
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode("grandchild"+j);
				childNode.add(grandChildNode);
				totalNodeCount++;
				for (int k = 0; k < 5; k++) {
					DefaultMutableTreeNode greatGrandChildNode = new DefaultMutableTreeNode("greatgrandchild"+k);
					grandChildNode.add(greatGrandChildNode);
					totalNodeCount++;
				}
			}
		}
		DefaultTreeModel fivebyThreeModel = new DefaultTreeModel(parentNode);

		parentNode = new DefaultMutableTreeNode("root");
		for (int i = 0; i < 2; i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("child"+i);
			parentNode.add(childNode);
			for (int j = 0; j < 2; j++) {
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode("grandchild"+j);
				childNode.add(grandChildNode);
			}
		}
		DefaultTreeModel twobytwoModel = new DefaultTreeModel(parentNode);

		DefaultTreeModel onebyoneModel = new DefaultTreeModel(new DefaultMutableTreeNode("root"));
		
		tree = new Tree(fivebyThreeModel);
		tree.expandAll();
		assertEquals(tree.getRowCount(),156); // 1 + 5 + 25 + 125
		
		tree = new Tree(onebyoneModel);
		assertEquals(tree.getRowCount(),1); 

		tree = new Tree(twobytwoModel);
		assertEquals(tree.getRowCount(),3); // because of autoexapand property
		tree.expandAll();
		assertEquals(tree.getRowCount(),7); // 1 + 2 + 4
		tree.collapseAll();
		assertEquals(tree.getRowCount(),1);
		
		TreePath path;
		TreePath path2;
		
		tree = new Tree(fivebyThreeModel);
		tree.expandAll();
		assertNotNull(tree.getPathForRow(2));
		assertNotNull(tree.getPathForRow(155));
		assertNull(tree.getPathForRow(156));
		
		path = tree.getPathForRow(2);
		tree.collapsePath(path);
		path2 = tree.getPathForRow(3);
		assertFalse(path.equals(path2));
		
		
		
		
	}
}
