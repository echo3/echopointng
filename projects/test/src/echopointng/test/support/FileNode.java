package echopointng.test.support;

import java.io.File;
import java.util.List;
import java.util.Vector;

import echopointng.tree.DefaultMutableTreeNode;

/**
 * FileNode
 */
public class FileNode extends DefaultMutableTreeNode {
	public FileNode(File file) {
		super(file);
		this.file = file;
		this.isDir = file.isDirectory();
	}

	public boolean getAllowsChildren() {
		return isDir;
	}

	protected List getChildren() {
		if (children == null) {
			try {
				final String[] files = file.list();
				if (files != null) {
					// Create an empty list of FileNodes (#elements = files.length)
					children = new Vector(files.length);
					final String path = file.getPath();
					for (int i = 0; i < files.length; i++) {
						final File childFile = new File(path, files[i]);
						FileNode childNode = new FileNode(childFile);
						children.add(childNode);
					}
				}
			} catch (SecurityException se) {}
			return children;
		}

		return children;
	}

	public File getFile() {
		return file;
	}

	public boolean isLeaf() {
		return !isDir;
	}

	public String toString() {
		String s = file.getName();
		if (s.trim().length() == 0) {
			s = File.separator;
		}
		return s;
	}

	private final File file;

	private final boolean isDir;
}
