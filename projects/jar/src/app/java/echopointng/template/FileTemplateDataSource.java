package echopointng.template;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <code>FileTemplateDataSource</code> takes it template
 * data from a File.
 */
public class FileTemplateDataSource extends AbstractTemplateDataSource {

	private File file;
	private String canonicalName; 

	/**
	 * Constructs a <code>FileTemplateDataSource</code> with no
	 * template file.
	 */
	public FileTemplateDataSource() {
		this((File) null);
	}
	
	/**
	 * Constructs a <code>FileTemplateDataSource</code> using the
	 * specified fileName as the template data source.
	 * 
	 * @param fileName - the file name to read
	 */
	public FileTemplateDataSource(String fileName) {
		this(new File(fileName));
	}
	
	/**
	 * Constructs a <code>FileTemplateDataSource</code> with the 
	 * specified File object as the template source
	 * 
	 * @param file - the File to use
	 */
	public FileTemplateDataSource(File file) {
		this.file = file;
		try {
			canonicalName = "file:" + file.getCanonicalPath();
		} catch (Exception e) {
			System.out.println(e);
			/* should never happen */
		}
		
		SimpleTemplateCachingHints hints = (SimpleTemplateCachingHints) getCachingHints();
		hints.setLastModified(file.lastModified());
	}
	/**
	 * @see echopointng.template.TemplateDataSource#getCanonicalName()
	 */
	public String getCanonicalName() {
		return canonicalName;
	}

	/**
	 * @see echopointng.template.TemplateDataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}
	
	/**
	 * @return Returns the file.
	 */
	public File getFile() {
		return file;
	}
	/**
	 * @param file The file to set.
	 */
	public void setFile(File file) {
		this.file = file;
	}
}
