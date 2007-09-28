package echopointng.richtext;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import echopointng.RichTextArea;
import echopointng.util.ColorKit;

/**
 * The <code>DefaultRichTextRenderer</code> is the default RichTextArea
 * renderer.
 * <p>
 * Some notes on browser capabilities as of the 10 September 2003.
 * <ul>
 * <li>Mozilla must be 1.3 or better to support Rich Text Editing</li>
 * <li>Internet Explorer must be 5.0 or better to support Rich Text Editing
 * </li>
 * <li>Mozilla does not allow programmatic copy/cut/paste to occur for security
 * reasons. For this reason, the commands buttons are not shown. However the key
 * combinations and menubar initiated versions of these commands still work
 * </li>
 * <li>Internet Explorer does not have support for the undo or redo commands.
 * For this reason, the command buttons are not shown</li>
 * <li>Mozilla does not seem to handle WingDings or WebDings properly. These
 * extended fonts however are not removed if the browser is Mozilla</li>
 * <li></li>
 * </ul>
 */
public class DefaultRichTextRenderer implements RichTextRenderer, Serializable {

	/**
	 * contains a mapping of supported commands (String) and descriptive titles
	 * (String)
	 */
	public static String[][] DEFAULT_SUPPORTED_COMMANDS = { { RichTextRenderer.CMD_HINT_NEWLINE, null },

			{ RichTextRenderer.CMD_BOLD, "Bold (Ctrl+B)" }, 
			{ RichTextRenderer.CMD_ITALIC, "Italic (Ctrl+I)" },
			{ RichTextRenderer.CMD_UNDERLINE, "Underline (Ctrl+U)" }, 
			{ RichTextRenderer.CMD_SUBSCRIPT, "Subscript (Ctrl+-)" },
			{ RichTextRenderer.CMD_SUPERSCRIPT, "Superscript (Ctrl+=)" }, 
			{ RichTextRenderer.CMD_HINT_SPACER, null },

			{ RichTextRenderer.CMD_FORECOLOR, "Foreground Color" }, 
			{ RichTextRenderer.CMD_BACKCOLOR, "Background Color" },
			{ RichTextRenderer.CMD_HINT_SPACER, null },

			{ RichTextRenderer.CMD_COPY, "Copy (Ctrl+C)" }, 
			{ RichTextRenderer.CMD_CUT, "Cut (Ctrl+X)" },
			{ RichTextRenderer.CMD_PASTE, "Paste (Ctrl+V)" }, 
			{ RichTextRenderer.CMD_UNDO, "Undo (Ctrl+Z)" }, 
			{ RichTextRenderer.CMD_REDO, "Redo (Ctrl+Y)" },
			{ RichTextRenderer.CMD_HINT_SPACER, null },

			{ RichTextRenderer.CMD_ALIGN_LEFT, "Align Left (Ctrl+L)" }, { RichTextRenderer.CMD_ALIGN_CENTER, "Center (Ctrl+E)" },
			{ RichTextRenderer.CMD_ALIGN_RIGHT, "Align Right (Ctrl+R)" }, { RichTextRenderer.CMD_JUSTIFY, "Justify (Ctrl+J)" },
			{ RichTextRenderer.CMD_INDENT, "Increase Indent" }, { RichTextRenderer.CMD_OUTDENT, "Decrease Indent" },
			{ RichTextRenderer.CMD_HINT_SPACER, null },

			{ RichTextRenderer.CMD_NUMBERS, "Numbers" },
			{ RichTextRenderer.CMD_BULLETS, "Bullets" }, 
			{ RichTextRenderer.CMD_HINT_SPACER, null },

			{ RichTextRenderer.CMD_SPELLCHECK, "Spell Check" },
			{ RichTextRenderer.CMD_SELECTALL, "Select All (Ctrl+A)" },
			{ RichTextRenderer.CMD_REMOVEFORMAT, "Remove All Format" },
			{ RichTextRenderer.CMD_HINT_SPACER, null },

			{ RichTextRenderer.CMD_INSERTHR, "Insert Horizontal Ruler" }, { RichTextRenderer.CMD_CREATELINK, "Create Hyperlink (Ctrl+K)" },
			{ RichTextRenderer.CMD_INSERTIMAGE, "Insert Image" },
	//{RichTextRenderer.CMD_INSERTTABLE, "Insert Table"},
	};

	/**
	 * contains a mapping of command (String) to image resources
	 * (ImageReference)
	 */
	private static final String path = "/echopointng/resource/images/richtext/";

	public static Object[][] DEFAULT_COMMAND_IMAGES = { { RichTextRenderer.CMD_BOLD, new ResourceImageReference(path + "ep_rt_bold.gif") },
			{ RichTextRenderer.CMD_COPY, new ResourceImageReference(path + "ep_rt_copy.gif") },
			{ RichTextRenderer.CMD_CREATELINK, new ResourceImageReference(path + "ep_rt_createlink.gif") },
			{ RichTextRenderer.CMD_CUT, new ResourceImageReference(path + "ep_rt_cut.gif") },
			{ RichTextRenderer.CMD_FORECOLOR, new ResourceImageReference(path + "ep_rt_forecolor.gif") },
			{ RichTextRenderer.CMD_BACKCOLOR, new ResourceImageReference(path + "ep_rt_hilitecolor.gif") },
			{ RichTextRenderer.CMD_INDENT, new ResourceImageReference(path + "ep_rt_indent.gif") },
			{ RichTextRenderer.CMD_INSERTHR, new ResourceImageReference(path + "ep_rt_inserthorizontalrule.gif") },
			{ RichTextRenderer.CMD_INSERTIMAGE, new ResourceImageReference(path + "ep_rt_insertimage.gif") },
			{ RichTextRenderer.CMD_NUMBERS, new ResourceImageReference(path + "ep_rt_insertorderedlist.gif") },
			{ RichTextRenderer.CMD_BULLETS, new ResourceImageReference(path + "ep_rt_insertunorderedlist.gif") },
			{ RichTextRenderer.CMD_ITALIC, new ResourceImageReference(path + "ep_rt_italic.gif") },
			{ RichTextRenderer.CMD_ALIGN_CENTER, new ResourceImageReference(path + "ep_rt_justifycenter.gif") },
			{ RichTextRenderer.CMD_JUSTIFY, new ResourceImageReference(path + "ep_rt_justifyfull.gif") },
			{ RichTextRenderer.CMD_ALIGN_LEFT, new ResourceImageReference(path + "ep_rt_justifyleft.gif") },
			{ RichTextRenderer.CMD_ALIGN_RIGHT, new ResourceImageReference(path + "ep_rt_justifyright.gif") },
			{ RichTextRenderer.CMD_OUTDENT, new ResourceImageReference(path + "ep_rt_outdent.gif") },
			{ RichTextRenderer.CMD_PASTE, new ResourceImageReference(path + "ep_rt_paste.gif") },
			{ RichTextRenderer.CMD_REDO, new ResourceImageReference(path + "ep_rt_redo.gif") },
			{ RichTextRenderer.CMD_REMOVEFORMAT, new ResourceImageReference(path + "ep_rt_removeformat.gif") },
			{ RichTextRenderer.CMD_SELECTALL, new ResourceImageReference(path + "ep_rt_selectall.gif") },
			{ RichTextRenderer.CMD_SPELLCHECK, new ResourceImageReference(path + "ep_rt_spell.gif") },
			{ RichTextRenderer.CMD_SUBSCRIPT, new ResourceImageReference(path + "ep_rt_subscript.gif") },
			{ RichTextRenderer.CMD_SUPERSCRIPT, new ResourceImageReference(path + "ep_rt_superscript.gif") },
			{ RichTextRenderer.CMD_UNDERLINE, new ResourceImageReference(path + "ep_rt_underline.gif") },
			{ RichTextRenderer.CMD_UNDO, new ResourceImageReference(path + "ep_rt_undo.gif") },
	//{RichTextRenderer.CMD_INSERTTABLE, new
	// ResourceImageReference(path+"ep_rt_inserttable.gif")},
	};

	/** contains the simple font names most likely to be available to use */
	public static final String[][] SIMPLE_FONT_NAMES = { { "arial,helvetica,sans-serif", "Arial" }, { "courier,monospace", "Courier" },
			{ "courier new,courier,monospace", "Courier New" }, { "helvetica,sans-serif", "Helvetica" }, { "times,serif", "Times" },
			{ "times new roman,times,serif", "Times New Roman" }, { "verdana,arial,helvetica,sans-serif", "Verdana" }, };

	/** contains the extended font names that may be available for use */
	public static final String[][] EXTENDED_FONT_NAMES = { { "arial,helvetica,sans-serif", "Arial" }, { "courier,monospace", "Courier" },
			{ "courier new,courier,monospace", "Courier New" }, { "helvetica,sans-serif", "Helvetica" }, { "times,serif", "Times" },
			{ "times new roman,times,serif", "Times New Roman" }, { "verdana,arial,helvetica,sans-serif", "Verdana" },

			{ "bookman old style", "Bookman Old Style" }, { "tahoma", "Tahoma" }, { "garamond", "Garamond" }, { "georgia", "Georgia" },
			{ "haettenschweiler", "Haettenschweiler" }, { "lucida console", "Lucinda Console" }, { "impact", "Impact" },
			{ "century gothic", "Century Gothic" }, { "book antiqua", "Book Antiqua" }, { "comic sans ms", "Comic Sans MS" },
			{ "webdings", "Webdings" }, { "wingdings", "Wingdings" },

	};

	/** contains the default font names to use */
	public static final String[][] DEFAULT_FONT_NAMES = EXTENDED_FONT_NAMES;

	/** contains the default font sizes to use */
	public static final String[][] DEFAULT_FONT_SIZES = { { "1", "1 (8 pt)" }, { "2", "2 (10 pt)" }, { "3", "3 (12 pt)" }, { "4", "4 (14 pt)" },
			{ "5", "5 (18 pt)" }, { "6", "6 (24 pt)" }, { "7", "7 (36 pt)" }, };

	/** contains the default paragraph styles */
	public static final String[][] DEFAULT_PARAGRAPH_STYLES = { { "p", "Normal" }, { "h1", "Heading 1" }, { "h2", "Heading 2" },
			{ "h3", "Heading 3" }, { "h4", "Heading 4" }, { "h5", "Heading 5" }, { "h6", "Heading 6" }, { "pre", "Preformatted" },
			{ "address", "Address" }, };

	/** contains the default color choices */
	public static final Color[] DEFAULT_COLOR_CHOICES = { ColorKit.makeColor("#FFFFFF"), ColorKit.makeColor("#FFCCCC"),
			ColorKit.makeColor("#FFCC99"), ColorKit.makeColor("#FFFF99"), ColorKit.makeColor("#FFFFCC"), ColorKit.makeColor("#99FF99"),
			ColorKit.makeColor("#99FFFF"), ColorKit.makeColor("#CCFFFF"), ColorKit.makeColor("#CCCCFF"), ColorKit.makeColor("#FFCCFF"),

			ColorKit.makeColor("#CCCCCC"), ColorKit.makeColor("#FF6666"), ColorKit.makeColor("#FF9966"), ColorKit.makeColor("#FFFF66"),
			ColorKit.makeColor("#FFFF33"), ColorKit.makeColor("#66FF99"), ColorKit.makeColor("#33FFFF"), ColorKit.makeColor("#66FFFF"),
			ColorKit.makeColor("#9999FF"), ColorKit.makeColor("#FF99FF"),

			ColorKit.makeColor("#C0C0C0"), ColorKit.makeColor("#FF0000"), ColorKit.makeColor("#FF9900"), ColorKit.makeColor("#FFCC66"),
			ColorKit.makeColor("#FFFF00"), ColorKit.makeColor("#33FF33"), ColorKit.makeColor("#66CCCC"), ColorKit.makeColor("#33CCFF"),
			ColorKit.makeColor("#6666CC"), ColorKit.makeColor("#CC66CC"),

			ColorKit.makeColor("#999999"), ColorKit.makeColor("#CC0000"), ColorKit.makeColor("#FF6600"), ColorKit.makeColor("#FFCC33"),
			ColorKit.makeColor("#FFCC00"), ColorKit.makeColor("#33CC00"), ColorKit.makeColor("#00CCCC"), ColorKit.makeColor("#3366FF"),
			ColorKit.makeColor("#6633FF"), ColorKit.makeColor("#CC33CC"),

			ColorKit.makeColor("#666666"), ColorKit.makeColor("#990000"), ColorKit.makeColor("#CC6600"), ColorKit.makeColor("#CC9933"),
			ColorKit.makeColor("#999900"), ColorKit.makeColor("#009900"), ColorKit.makeColor("#339999"), ColorKit.makeColor("#3333FF"),
			ColorKit.makeColor("#6600CC"), ColorKit.makeColor("#993399"),

			ColorKit.makeColor("#333333"), ColorKit.makeColor("#660000"), ColorKit.makeColor("#993300"), ColorKit.makeColor("#996633"),
			ColorKit.makeColor("#666600"), ColorKit.makeColor("#006600"), ColorKit.makeColor("#336666"), ColorKit.makeColor("#000099"),
			ColorKit.makeColor("#333399"), ColorKit.makeColor("#663366"),

			ColorKit.makeColor("#000000"), ColorKit.makeColor("#330000"), ColorKit.makeColor("#663300"), ColorKit.makeColor("#663333"),
			ColorKit.makeColor("#333300"), ColorKit.makeColor("#003300"), ColorKit.makeColor("#003333"), ColorKit.makeColor("#000066"),
			ColorKit.makeColor("#330099"), ColorKit.makeColor("#330033"), };

	private class StringArrayComparator implements Comparator {
		private int compareIndex = 0;

		StringArrayComparator(int compareIndex) {
			this.compareIndex = compareIndex;
		}

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			String[] s1 = (String[]) o1;
			String[] s2 = (String[]) o2;

			return s1[compareIndex].compareTo(s2[compareIndex]);
		}
	}

	private String[][] fontNames;

	private String[][] fontSizes;

	private String[][] paragraphStyles;

	private Map commandImages;

	/**
	 * Constructs a <code>DefaultRichTextRenderer</code>
	 */
	public DefaultRichTextRenderer() {
		fontNames = DEFAULT_FONT_NAMES;
		fontSizes = DEFAULT_FONT_SIZES;
		paragraphStyles = DEFAULT_PARAGRAPH_STYLES;

		commandImages = new HashMap();
		for (int i = 0; i < DEFAULT_COMMAND_IMAGES.length; i++) {
			commandImages.put(DEFAULT_COMMAND_IMAGES[i][0], DEFAULT_COMMAND_IMAGES[i][1]);
		}
	}

	/*
	 * Called to copy all the supported commands except, suprise suprise, the
	 * exceptions.
	 */
	private String[][] copyCommandsExcept(String[][] allCommands, String[] exceptCmds) {
		int len = allCommands.length - exceptCmds.length;
		String[][] commands = new String[len][2];
		int j = 0;
		for (int i = 0; i < allCommands.length; i++) {
			String cmd = allCommands[i][0];

			boolean doCopy = true;
			for (int k = 0; k < exceptCmds.length; k++) {
				if (cmd.equals(exceptCmds[k])) {
					doCopy = false;
					break;
				}
			}

			if (doCopy) {
				commands[j][0] = allCommands[i][0];
				commands[j][1] = allCommands[i][1];
				j++;
			}
		}
		return commands;
	}

	/**
	 * @see echopointng.richtext.RichTextRenderer#getFontNames(echopointng.RichTextArea,
	 *      java.lang.String)
	 */
	public String[][] getFontNames(RichTextArea rta, String userAgent) {

		List list = Arrays.asList(fontNames);
		Collections.sort(list, new StringArrayComparator(1));
		return (String[][]) list.toArray();
	}

	/**
	 * @see echopointng.richtext.RichTextRenderer#getFontSizes(echopointng.RichTextArea,
	 *      java.lang.String)
	 */
	public String[][] getFontSizes(RichTextArea rta, String userAgent) {
		return fontSizes;
	}

	/**
	 * @see echopointng.richtext.RichTextRenderer#getParagraphStyles(echopointng.RichTextArea,
	 *      java.lang.String)
	 */
	public String[][] getParagraphStyles(RichTextArea rta, String userAgent) {
		return paragraphStyles;
	}

	/**
	 * @see echopointng.richtext.RichTextRenderer#getSupportedCommands(echopointng.RichTextArea,
	 *      java.lang.String)
	 */
	public String[][] getSupportedCommands(RichTextArea rta, String userAgent) {
		//
		// Mozilla does not allow copy/cut/paste operations from code because of
		// security
		// concerns. So we dont show the commands. Neither does it have UI
		// versions of the insert image and hyperlink so we skip them as well.
		//
		if (userAgent.indexOf("Gecko") != -1) {
			return copyCommandsExcept(DEFAULT_SUPPORTED_COMMANDS, new String[] { CMD_COPY, CMD_CUT, CMD_PASTE, CMD_INSERTIMAGE, CMD_CREATELINK });
		}
		//
		// IE does not have undo or redo commands, so we get rid of them
		//
		if (userAgent.indexOf("Explorer") != -1) {
			return copyCommandsExcept(DEFAULT_SUPPORTED_COMMANDS, new String[] { CMD_UNDO, CMD_REDO });
		} else {
			return DEFAULT_SUPPORTED_COMMANDS;
		}
	}

	/**
	 * @see echopointng.richtext.RichTextRenderer#getCommandImage(echopointng.RichTextArea,
	 *      java.lang.String, java.lang.String)
	 */
	public ImageReference getCommandImage(RichTextArea rta, String userAgent, String command) {
		return (ImageReference) commandImages.get(command);
	}



	/**
	 * Sets a new set of font name values to descriptive names mappings
	 * 
	 * @param strings
	 */
	public void setFontNames(String[][] strings) {
		fontNames = strings;
	}

	/**
	 * Sets a new set of font size values to descriptive names mappings
	 * 
	 * @param strings
	 */
	public void setFontSizes(String[][] strings) {
		fontSizes = strings;
	}

	/**
	 * Sets a new set of paragraph style values to descriptive names mappings
	 * 
	 * @param strings -
	 *            the array of mappings
	 */
	public void setParagraphStyles(String[][] strings) {
		paragraphStyles = strings;
	}

	/**
	 * Sets the image for a given command.
	 * 
	 * @param command -
	 *            the command in question
	 * @param image -
	 *            the new image for the command
	 */
	public void setCommandImage(String command, ImageReference image) {
		commandImages.put(command, image);
	}

	/**
	 * @see echopointng.richtext.RichTextRenderer#getCommandAppearance(echopointng.RichTextArea,
	 *      java.lang.String)
	 */
	public CommandAppearance getCommandAppearance(RichTextArea rta, String userAgent) {
		return new CommandAppearance() {

			public Color getBackground() {
				return ColorKit.makeColor("#efefef");
			}

			public Border getBorder() {
				return new Border(1, ColorKit.makeColor("#D6D3CE"), Border.STYLE_SOLID);
			}

			public Color getRolloverBackground() {
				return ColorKit.makeColor("#DEF3FF");
			}

			public Border getRolloverBorder() {
				return new Border(1, ColorKit.makeColor("#3169C6"), Border.STYLE_SOLID);
			}

			public Color getSelectedBackground() {
				return ColorKit.makeColor("#C6D3EF");
			}

			public Border getSelectedBorder() {
				return new Border(1, ColorKit.makeColor("#3169C6"), Border.STYLE_SOLID);
			}
		};
	}
}
