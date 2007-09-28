package echopointng.test.support;

import java.io.File;

import echopointng.tree.TreeNode;
import echopointng.treetable.DefaultTreeTableModel;

/**
 * FileSystemModel
 */
public class FileSystemModel extends DefaultTreeTableModel {
	protected boolean asksAllowsChildren;

	public FileSystemModel() {
		this(new FileNode(new File(File.separator)));
	}

	public FileSystemModel(int directoryDepth) {
		this(createNodeNDeep(directoryDepth),false);
		
	}
	private static FileNode createNodeNDeep(int directoryDepth) {
		FileNode root = new FileNode(new File(File.separator));
		createNodeNDeep(root,directoryDepth,1);
		return root;
	}

	private static void createNodeNDeep(FileNode parentNode, int directoryDepth, int currentDepth) {
		if (currentDepth >= directoryDepth) {
			return;
		}
		File parentFile = parentNode.getFile();
		if (parentFile.isDirectory()) {
			File[] files = parentFile.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					FileNode childNode = new FileNode(files[i]);
					parentNode.add(childNode);
					if (files[i].isDirectory()) {
						createNodeNDeep(childNode,directoryDepth,currentDepth+1);
					}
				}
			}
		}
	}
	
	public FileSystemModel(TreeNode root) {
		this(root, false);
	}

	public FileSystemModel(TreeNode root, boolean asksAllowsChildren) {
		super(root);
		this.asksAllowsChildren = asksAllowsChildren;
	}
	
	/**
	 * @see echopointng.treetable.AbstractTreeTableModel#getParent(java.lang.Object)
	 */
	public Object getParent(Object child) {
		return ((FileNode)child).getParent();
	}

	public Object getChild(Object parent, int index) {
		try {
			FileNode parentNode = (FileNode) parent;
			FileNode childNode = (FileNode) parentNode.getChildren().get(index);
			if (childNode.getParent() != parentNode) {
				//childNode.setParent(parentNode);
			}
			return childNode;
		} catch (Exception ex) {
			return super.getChild(parent, index);
		}
	}

	public int getChildCount(Object parent) {
		try {
			FileNode parentNode = (FileNode) parent;
			int childCount  = parentNode.getChildren().size(); 
			return childCount;
		} catch (Exception ex) {
			return super.getChildCount(parent);
		}
	}

	public int getColumnCount() {
		return 4;
	}

	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Size";
		case 2:
			return "Directory";
		case 3:
			return "Modification Date";
		default:
			return "Column " + column;
		}
	}

	public Object getValueAt(Object node, int column) {
		final File file = ((FileNode) node).getFile();
		try {
			switch (column) {
			case 0:
				return file.getName();
			case 1:
				return file.isFile() ? new Integer((int) file.length()) : ZERO;
			case 2:
				return Boolean.valueOf(!file.isFile());
			case 3:
				return new java.util.Date(file.lastModified());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	// The the returned file length for directories.
	private static final Integer ZERO = new Integer(0);
}
