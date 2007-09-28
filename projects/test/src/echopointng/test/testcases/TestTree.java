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
package echopointng.test.testcases;

import java.io.Serializable;

import nextapp.echo2.app.Button;
import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import echopointng.BorderEx;
import echopointng.DateChooser;
import echopointng.DirectHtml;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.Tree;
import echopointng.TreeTable;
import echopointng.test.controller.TestController;
import echopointng.test.helpers.TestCallback;
import echopointng.test.helpers.TestDisplaySink;
import echopointng.test.helpers.TestHelper;
import echopointng.test.support.FileSystemModel;
import echopointng.tree.DefaultMutableTreeNode;
import echopointng.tree.DefaultTreeCellRenderer;
import echopointng.tree.DefaultTreeModel;
import echopointng.tree.MutableTreeNode;
import echopointng.tree.TreeActionEventEx;
import echopointng.tree.TreeCellRenderer;
import echopointng.tree.TreeExpansionEvent;
import echopointng.tree.TreeExpansionListener;
import echopointng.tree.TreeModel;
import echopointng.tree.TreeModelEvent;
import echopointng.tree.TreeModelListener;
import echopointng.tree.TreePath;
import echopointng.tree.TreeSelectionEvent;
import echopointng.tree.TreeSelectionListener;
import echopointng.tree.TreeSelectionModel;
import echopointng.treetable.TreeTableModel;
import echopointng.util.ColorKit;
import echopointng.util.HtmlKit;
import echopointng.util.RandKit;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>TestTree</code>
 */

public class TestTree extends TestCaseBaseNG {

	private static class TreeListenerImpl implements TreeModelListener, TreeSelectionListener, TreeExpansionListener, Serializable {

		/**
		 * @see echopointng.tree.TreeModelListener#treeNodesChanged(echopointng.tree.TreeModelEvent)
		 */
		public void treeNodesChanged(TreeModelEvent e) {
			System.out.println("treeNodesChanged - " + e.toString());
		}

		/**
		 * @see echopointng.tree.TreeModelListener#treeNodesInserted(echopointng.tree.TreeModelEvent)
		 */
		public void treeNodesInserted(TreeModelEvent e) {
			System.out.println("treeNodesInserted - " + e.toString());
		}

		/**
		 * @see echopointng.tree.TreeModelListener#treeNodesRemoved(echopointng.tree.TreeModelEvent)
		 */
		public void treeNodesRemoved(TreeModelEvent e) {
			System.out.println("treeNodesRemoved - " + e.toString());
		}

		/**
		 * @see echopointng.tree.TreeModelListener#treeStructureChanged(echopointng.tree.TreeModelEvent)
		 */
		public void treeStructureChanged(TreeModelEvent e) {
			System.out.println("treeStructureChanged - " + e.toString());
		}

		/**
		 * @see echopointng.tree.TreeSelectionListener#valueChanged(echopointng.tree.TreeSelectionEvent)
		 */
		public void valueChanged(TreeSelectionEvent e) {
			System.out.println("Tree selection  - " + e.toString());
		}

		/**
		 * @see echopointng.tree.TreeExpansionListener#treeCollapsed(echopointng.tree.TreeExpansionEvent)
		 */
		public void treeCollapsed(TreeExpansionEvent e) {
			System.out.println("treeCollapsed - " + e.toString());
		}

		/**
		 * @see echopointng.tree.TreeExpansionListener#treeExpanded(echopointng.tree.TreeExpansionEvent)
		 */
		public void treeExpanded(TreeExpansionEvent e) {
			System.out.println("treeExpanded - " + e.toString());
		}
	}

	private static TreeListenerImpl treeListenerImpl = new TreeListenerImpl();

	public String getTestCategory() {
		return "Tree";
	}

	private void createChildren(int i, DefaultMutableTreeNode parent) {
		for (int j = 1; j <= 7; j++) {
			DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child " + i + ":" + j);
			if (j % 3 == 0) {
				Column cell = new Column();
				cell.add(new Label("Column Component " + i + ":" + j));

				child = new DefaultMutableTreeNode(cell);
			}
			if (j == 1) {
				Column cell = new Column();
				cell.add(new Label("Column Component " + i + ":" + j));
				cell.add(new DateChooser());
				cell.setBorder(new BorderEx());

				child = new DefaultMutableTreeNode(cell);
			}
			parent.add(child);
		}
	}

	private Tree createTree() {
		TreeModel treeModel = createTreeModel();
		Tree tree = new Tree(treeModel);

		treeModel.addTreeModelListener(treeListenerImpl);
		tree.addTreeSelectionListener(treeListenerImpl);
		tree.addTreeExpansionListener(treeListenerImpl);

		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

		tree.setCellRenderer(renderer);

		return tree;

	}

	/**
	 * @return a TreeModel
	 */
	private TreeModel createTreeModel() {
		// if (true) {
		// return new LargeLazyTreeModel();
		// }
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		DefaultTreeModel treeModel = new DefaultTreeModel(root);

		DefaultMutableTreeNode parent = root;
		for (int i = 0; i < 10; i++) {
			createChildren(i, parent);
			if (i % 2 == 0) {
				DefaultMutableTreeNode tempParent = (DefaultMutableTreeNode) parent.getFirstChild();
				createChildren(i + 1, tempParent);
			}
			parent = parent.getLastLeaf();
		}
		return treeModel;
	}

	public Component testTree() {
		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10mm"));

		Tree tree;
		tree = createTree();
		col.add(tree);

		tree = createTree();
		tree.setEnabled(false);
		col.add(tree);

		return col;
	}

	public Component testSimpleTree() {
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("root");
		for (int i = 0; i < 2; i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("child" + i);
			parentNode.add(childNode);
			for (int j = 0; j < 2; j++) {
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode("grandchild [" + i + "][" + j + "]");
				childNode.add(grandChildNode);
			}
		}
		DefaultTreeModel twobytwoModel = new DefaultTreeModel(parentNode);

		final Tree tree = new Tree(twobytwoModel);
		tree.expandAll();
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		
		TestController controller = new TestController(this,tree);
		controller.addBooleanTest(Tree.PROPERTY_ROOT_VISIBLE, "Root Visible?");
		
		controller.addBooleanTest(null, "Use Single SelectionModel", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				Boolean flagObj = (Boolean) testHelper.getTestValue();
				if (flagObj != null && flagObj.booleanValue()) {
					tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				} else {
					tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
				}
			}
		});
		
		Column cell = new Column();
		cell.add(tree);
		cell.add(controller.getUI());
		return cell;
	}

	public Component testDeeperTree() {
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("root");
		for (int i = 0; i < 5; i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("child" + i);
			parentNode.add(childNode);
			for (int j = 0; j < 5; j++) {
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode("grandchild" + j);
				childNode.add(grandChildNode);
				for (int k = 0; k < 5; k++) {
					DefaultMutableTreeNode greatGrandChildNode = new DefaultMutableTreeNode("greatgrandchild" + k);
					grandChildNode.add(greatGrandChildNode);
					for (int l = 0; l < 5; l++) {
						DefaultMutableTreeNode greatGreatGrandChildNode = new DefaultMutableTreeNode("greatgreatgrandchild" + l);
						greatGrandChildNode.add(greatGreatGrandChildNode);
					}
				}
			}
		}
		DefaultTreeModel fivebyFourModel = new DefaultTreeModel(parentNode);

		Tree tree = new Tree(fivebyFourModel);
		return tree;
	}

	public Component testTreeTable() {
		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10mm"));

		TreeTableModel treeTableModel = new FileSystemModel();
		TreePath rootPath = new TreePath(treeTableModel.getRoot());

		TreeTable treeTable = new TreeTable(treeTableModel);
		treeTable.setBorder(new BorderEx());
		treeTable.setSelectionEnabled(true);
		treeTable.setSelectionBackground(Color.YELLOW);

		treeTable.setRolloverEnabled(true);
		treeTable.setRolloverBackground(Color.GREEN);

		treeTable.expandPath(rootPath);
		treeTable.setWidth(new ExtentEx("100%"));
		col.add(treeTable);
		return col;
	}

	public Component testTreeTableResizeable() {
		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10mm"));

		TreeTableModel treeTableModel = new FileSystemModel();
		TreePath rootPath = new TreePath(treeTableModel.getRoot());

		TreeTable treeTable = new TreeTable(treeTableModel);
		treeTable.setBorder(new BorderEx());
		treeTable.setSelectionEnabled(true);
		treeTable.setSelectionBackground(Color.YELLOW);
		
		treeTable.setScrollable(true);
		treeTable.setResizeable(true);
		treeTable.setHeight(new ExtentEx("300px"));

		treeTable.setRolloverEnabled(true);
		treeTable.setRolloverBackground(Color.GREEN);

		treeTable.expandPath(rootPath);
		treeTable.setWidth(new ExtentEx("100%"));
		col.add(treeTable);
		return col;
	}
	
	public Component testFileSystemTree() {
		TreeTableModel model = new FileSystemModel();
		Tree tree = new Tree(model);
		return tree;
	}

	public Component testAFLTree() {
		final String clubs[] = new String[] { "Collingwood", "Hawthorn", "Swans", "Carlton", "Richmond", "Essedon", "North Melbourne", "Fitzroy", };

		final String playersCOLL[] = new String[] { "Nathan Buckley", "Ronnie Wearmouth", "Peter Daicos", "Billy Pickering", };
		final String playersHAW[] = new String[] { "Dermott Brereton", "Micheal Tuck", "Peter Knights", "Leigh Mathews", };
		final String playersNEVILLES[] = new String[] { "Neville Nobody", "Nigle No Mates", "Tess Tickler", "Tommy Tom Tomahawk", "Ed Radomski",
				"Hec Speed", "Rastus Watermelon", "Shirty McShirt Shirt", "Fred Farquar", "Toady McToad Toad", "Billy Hunt", "Olaf McVarski", };

		Tree tree1 = null;
		Tree tree2 = null;
		Tree tree3 = null;

		for (int count = 0; count < 3; count++) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("AFL Clubs");
			for (int i = 0; i < clubs.length; i++) {
				String clubName = clubs[i];
				DefaultMutableTreeNode clubNode = new DefaultMutableTreeNode(clubName);
				root.add(clubNode);

				Object players[];
				if (clubName == "Collingwood") {
					players = playersCOLL;
				} else if (clubName == "Hawthorn") {
					players = playersHAW;
				} else {
					int randLen = RandKit.rand(3, playersNEVILLES.length);
					players = RandKit.rand(playersNEVILLES, new String[randLen]);
				}
				for (int j = 0; j < players.length; j++) {
					DefaultMutableTreeNode playerNode = new DefaultMutableTreeNode(players[j]);
					clubNode.add(playerNode);
				}
			}
			DefaultTreeModel model = new DefaultTreeModel(root);
			if (count + 1 == 1) {
				tree1 = new Tree(model);
			} else if (count + 1 == 2) {
				tree2 = new Tree(model);
			} else if (count + 1 == 3) {
				tree3 = new Tree(model);
			}
		}

		TreeExpansionListener tel = new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
			}

			public void treeExpanded(TreeExpansionEvent event) {
				TreePath path = event.getPath();
				Object objNode = path.getLastPathComponent();
				DefaultMutableTreeNode clubNode = (DefaultMutableTreeNode) objNode;
				if (clubNode.getUserObject() == "Swans") {
					clubNode.removeAllChildren();
					//
					// randomly pick some new players
					int randLen = RandKit.rand(3, playersNEVILLES.length);
					Object[] players = RandKit.rand(playersNEVILLES, new String[randLen]);
					for (int j = 0; j < players.length; j++) {
						DefaultMutableTreeNode playerNode = new DefaultMutableTreeNode(players[j]);
						clubNode.add(playerNode);
					}
				}
			}
		};
		tree1.addTreeExpansionListener(tel);
		tree1.setBackground(ColorKit.clr("#CDACCB"));

		tree2.addTreeExpansionListener(tel);
		tree2.setPartialUpdateSupport(false);
		tree2.setBackground(ColorKit.clr("#ACBCDC"));
		tree2.setShowsRootHandles(false);

		tree3.addTreeExpansionListener(tel);
		tree3.setBackground(ColorKit.clr("#ABCDEF"));
		tree3.setRootVisible(false);

		Column col = new Column();
		col.add(new Label("Expand and contract the Swans to see how they change players all the times!"));
		col.add(tree1);
		col.add(new Label("The second Tree does not use partial update support!"));
		col.add(tree2);
		col.add(new Label("The third Tree does show the root level!"));
		col.add(tree3);
		return col;

	}

	int node_number = 0;

	public Component testTreeModelUpdate() {

		final DefaultTreeModel tree_model;
		final DefaultMutableTreeNode root_node = new DefaultMutableTreeNode("Root");
		final Button add_node = new Button("Add a node to the tree.");

		tree_model = new DefaultTreeModel(root_node);

		// handle action listeners.
		add_node.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				root_node.add(new DefaultMutableTreeNode("New Node " + node_number++));
				tree_model.reload(root_node);
			}
		});

		Tree tree = new Tree(tree_model);
		// tree.setModel(tree_model);

		Column col = new Column();
		col.add(add_node);
		col.add(tree);

		return col;

	}

	public Component testGridTree() {
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("root");
		for (int i = 0; i < 5; i++) {
			Grid row = new Grid(1);
			row.add(new Label("Test"));
			row.add(new TextField());

			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(row);
			parentNode.add(childNode);
			for (int j = 0; j < 5; j++) {
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode("grandchild" + j);
				childNode.add(grandChildNode);
				for (int k = 0; k < 5; k++) {
					DefaultMutableTreeNode greatGrandChildNode = new DefaultMutableTreeNode("greatgrandchild" + k);
					grandChildNode.add(greatGrandChildNode);
					for (int l = 0; l < 5; l++) {
						DefaultMutableTreeNode greatGreatGrandChildNode = new DefaultMutableTreeNode("greatgreatgrandchild" + l);
						greatGrandChildNode.add(greatGreatGrandChildNode);
					}
				}
			}
		}
		DefaultTreeModel fivebyFourModel = new DefaultTreeModel(parentNode);

		Tree tree = new Tree(fivebyFourModel);
		return tree;
	}

	private class TreeLabel extends Label {

		public TreeLabel(String text, ImageReference icon) {
			super(text, icon);
		}

		public String toString() {
			return getText() + ":c_" + getRenderId();
		}
	}

	public Component testTreeModelUserObject() {
		Extent iconHeight = new Extent(35);
		Extent iconWidth = new Extent(35);

		ImageReference yesIcon = new HttpImageReference("images/poker_monkey_yes.gif", iconWidth, iconHeight);
		ImageReference noIcon = new HttpImageReference("images/poker_monkey_no.gif", iconWidth, iconHeight);
		Label yesLabel = new TreeLabel("yes", yesIcon);
		Label noLabel = new TreeLabel("no", noIcon);

		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(yesLabel);
		for (int i = 0; i < 2; i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(noLabel);
			parentNode.add(childNode);
			for (int j = 0; j < 2; j++) {
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(yesLabel);
				childNode.add(grandChildNode);
			}
		}
		DefaultTreeModel twobytwoModel = new DefaultTreeModel(parentNode);

		Tree tree = new Tree(twobytwoModel);
		// tree.expandAll();
		return tree;
	}

	public Component testTreeXhtmlSupport() {
		ImageReference astronautIcon = new HttpImageReference("images/t_astronaut.jpg");
		String iconURI = HtmlKit.getImageURI(astronautIcon);

		String xhtml = "<b>Parent</b> Node <img src=\"" + iconURI + "\" style=\"width:20px;height:20px;\" /> With Image via XhtmlFragment";
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(new XhtmlFragment(xhtml));
		for (int i = 0; i < 2; i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new XhtmlFragment("<i>Child</i>[" + i + "]"));
			parentNode.add(childNode);
			for (int j = 0; j < 2; j++) {
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(new XhtmlFragment("<U>Grand Child</U>[" + j + "]"));
				childNode.add(grandChildNode);
			}
		}
		DefaultTreeModel twobytwoModel = new DefaultTreeModel(parentNode);
		Tree tree = new Tree(twobytwoModel);
		return tree;
	}

	public Component testUserModel() {
		Tree tree = new Tree();

		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("root");
		for (int i = 0; i < 5; i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("child" + i);
			parentNode.add(childNode);
			for (int j = 0; j < 5; j++) {
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode("grandchild" + j);
				childNode.add(grandChildNode);
				for (int k = 0; k < 5; k++) {
					DefaultMutableTreeNode greatGrandChildNode = new DefaultMutableTreeNode("greatgrandchild" + k);
					grandChildNode.add(greatGrandChildNode);
					for (int l = 0; l < 5; l++) {
						DefaultMutableTreeNode greatGreatGrandChildNode = new DefaultMutableTreeNode("greatgreatgrandchild" + l);
						greatGrandChildNode.add(greatGreatGrandChildNode);
					}
				}
			}
		}
		DefaultTreeModel fivebyFourModel = new DefaultTreeModel(parentNode);
		tree.setModel(fivebyFourModel);
		return tree;
	}
	
	public Component testWidthContrained() {
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("root");
		for (int i = 0; i < 2; i++) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("child" + i);
			parentNode.add(childNode);
			for (int j = 0; j < 2; j++) {
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode("grandchild" + j);
				childNode.add(grandChildNode);
			}
		}
		DefaultTreeModel twobytwoModel = new DefaultTreeModel(parentNode);

		final Tree tree = new Tree(twobytwoModel);
		tree.expandAll();
		
		final CheckBox checkBoxContrained = new CheckBox("Constrain Cell Widths?");
		checkBoxContrained.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tree.setCellWidthConstrained(checkBoxContrained.isSelected());
			};
		});

		final CheckBox checkBoxSelectionIncludesIcon = new CheckBox("Selection Includes Icon?");
		checkBoxSelectionIncludesIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tree.setSelectionIncludesIcon(checkBoxSelectionIncludesIcon.isSelected());
			};
		});

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(15));
		cell.add(checkBoxContrained);
		cell.add(checkBoxSelectionIncludesIcon);
		cell.add(tree);
		return cell;
	}
	
	public Component testTreeSelectionListener() {
		DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode("root");
		for (int i = 0; i < 2; i++) {
			String name = "child" + i;
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(name);
			childNode.setActionCommand(name);
			parentNode.add(childNode);
			for (int j = 0; j < 2; j++) {
				name = "grandchild [" + i + "][" + j + "]";
				DefaultMutableTreeNode grandChildNode = new DefaultMutableTreeNode(name);
				grandChildNode.setActionCommand(name);
				childNode.add(grandChildNode);
			}
		}
		DefaultTreeModel twobytwoModel = new DefaultTreeModel(parentNode);

		final ImageReference icon = new HttpImageReference("/images/poker_dogs.gif",new ExtentEx(10),new ExtentEx(10));
		
		final TreeCellRenderer cellRenderer = new DefaultTreeCellRenderer() {
			//
			// use this variable 0,1,2 to control which method is in use
			public int whichMethod = 1;
			
			public Label getTreeCellRendererText(Tree tree, Object value, boolean selected, boolean expanded, boolean leaf) {
				if (whichMethod == 0) {
					this.setIcon(icon);
					this.setText(String.valueOf(value));
					this.setBackground(selected ? Color.RED : Color.WHITE);
					return this;
				} else {
					return null;
				}
			}
			public Component getTreeCellRendererComponent(Tree tree, Object value, boolean selected, boolean expanded, boolean leaf) {
				if (whichMethod == 1) {
					Label l = new LabelEx(String.valueOf(value)); 
					l.setIcon(icon);
					l.setBackground(selected ? Color.RED : Color.WHITE);
					return l;
				} else {
					return null;
				}
			}
			public XhtmlFragment getTreeCellRendererXhtml(Tree tree, Object value, boolean selected, boolean expanded, boolean leaf) {
				if (whichMethod == 2) {
					String html = String.valueOf(value);
					if (selected) {
						html = "<span style=\"background:red\">" + html + "</span>";
					}
					return new XhtmlFragment(html);
				} else {
					return null;
				}
			}
		};

		final DirectHtml selectionPaths = new DirectHtml();
		final Tree tree = new Tree(twobytwoModel);
		tree.expandAll();
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(cellRenderer);
		
		TreeSelectionListener tsl = new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				String html = "Selection paths : <br/>";
				TreePath newPaths[]  = e.getPaths();
				for (int i = 0; i < newPaths.length; i++) {
					html += newPaths[i].toString();
					if (e.isAddedPath(newPaths[i])) {
						html += " added";
					} else {
						html += " removed";
					}
					html += "<br/>";
				}
				selectionPaths.setText(html);
			}
		};
		tree.addTreeSelectionListener(tsl);
		
		Column cell = new Column();
		cell.add(tree);
		cell.add(selectionPaths);
		return cell;
	}
	
	public Component testInvisible() {
		Column column1 = new Column();
		final Tree tree1 = new Tree();
		tree1.setVisible(false);
		column1.add(tree1);
		Row row1 = new Row();
		column1.add(row1);
		Button button1 = new Button();
		button1.setText("Make Tree visible");
		button1.setBorder(new BorderEx(new Extent(1, Extent.PX), Color.BLACK,
		BorderEx.STYLE_SOLID));
		button1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
			tree1.setVisible(true);
		}
		});
		row1.add(button1);		
		return column1;
	}

	public Component testRootVisible() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Main Menu");
        for (int i=0; i<10; i++)
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Item :" + i);
            root.add(node);
        }
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        Tree mainMenuTree = new Tree();
        mainMenuTree.setRootAutoExpanded(true);
        mainMenuTree.setLinesDrawn(false);
        mainMenuTree.setShowsRootHandles(true);
        mainMenuTree.setRootVisible(false);
        mainMenuTree.setModel(treeModel);
        return mainMenuTree;
	}

	public Component testChildRemoval() {
		final DefaultMutableTreeNode node = new DefaultMutableTreeNode("Child");
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        root.add(node);
        for (int i=0; i<10; i++)
        {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("Item :" + i);
            createChildren(5, childNode);
            root.add(childNode);
        }        
        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        Tree mainMenuTree = new Tree();
        mainMenuTree.setModel(treeModel);
        mainMenuTree.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
         		/* this wont fire a structure change event */
         		//node.removeFromParent(); 
         		
         		/* this will fire a structure change event */
         		TreeActionEventEx treeActionEventEx = (TreeActionEventEx) e;
         		Object treeNode = treeActionEventEx.getTreeNode();
         		if (treeModel.getParent(treeNode) != null) {
         			treeModel.removeNodeFromParent((MutableTreeNode) treeNode);
         		}
        	}
        });
        return mainMenuTree;
	}
}
