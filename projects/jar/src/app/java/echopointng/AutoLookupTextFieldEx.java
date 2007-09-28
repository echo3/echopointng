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
package echopointng;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.text.Document;
import echopointng.able.MouseCursorable;
import echopointng.text.AutoLookupModel;
import echopointng.text.AutoLookupService;
import echopointng.util.ColorKit;
import echopointng.util.FontKit;

/**
 * <code>AutoLookupTextFieldEx</code> is a <code>TextFieldEx</code> that
 * allows a cache of lookup entries to be searched as the user types in values.
 * <p>
 * The <code>AutoLookupModel</code> model class is used to provide lookup
 * values and other cache information.
 * 
 */
public class AutoLookupTextFieldEx extends TextFieldEx {

	public static final String PROPERTY_AUTO_LOOKUP_MODEL = "autoLookupModel";

	public static final String PROPERTY_POPUP_PROPERTIES = "popupProperties";

	public static final String PROPERTY_ENTRY_PROPERTIES = "entryProperties";

	public static final String PROPERTY_ENTRY_ROLLOVER_PROPERTIES = "entryRolloverProperties";

	public static final String PROPERTY_SEARCH_BAR_PROPERTIES = "searchBarProperties";

	public static final String PROPERTY_SEARCH_BAR_ROLLOVER_PROPERTIES = "searchBarRolloverProperties";

	public static final String PROPERTY_SEARCH_BAR_ICON = "searchBarIcon";

	public static final String PROPERTY_SEARCH_BAR_SEARCHING_ICON = "searchBarSearchingIcon";

	public static final String PROPERTY_SEARCH_BAR_TEXT = "searchBarText";

	public static final String PROPERTY_SEARCH_BAR_SHOWN = "searchBarShown";

	public static final String PROPERTY_SEARCH_BAR_SEARCHING_TEXT = "searchBarSearchingText";

	public static final String PROPERTY_NO_MATCHING_OPTION_TEXT = "noMatchingOptionText";
	
	/** The default image used as the searching image */
	public static final ImageReference DEFAULT_SEARCH_BAR_SEARCHING_ICON = new ResourceImageReference(
			"/echopointng/resource/images/spinning-wait-icons/wait16trans.gif", new Extent(16), new Extent(16));

	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();

		// popup defaults
		AbleProperties ableProperties = new AbleProperties();
		ableProperties.setBackground(ColorKit.clr("#FFFFFF"));
		ableProperties.setInsets(new Insets(1));
		ableProperties.setOutsets(new Insets(0,2,0,0));
		ableProperties.setBorder(new BorderEx(ColorKit.clr("#7F9DB9")));
		style.setProperty(PROPERTY_POPUP_PROPERTIES, ableProperties);

		// entry defaults
		ableProperties = new AbleProperties();
		ableProperties.setBorder(new Border(1, ColorKit.makeColor("#FFFFFF"), Border.STYLE_SOLID));
		ableProperties.setInsets(new Insets(1, 0));
		ableProperties.setOutsets(new Insets(1, 0, 1, 0));
		ableProperties.setMouseCursor(MouseCursorable.CURSOR_POINTER);
		style.setProperty(PROPERTY_ENTRY_PROPERTIES, ableProperties);

		ableProperties = new AbleProperties();
		ableProperties.setBackground(ColorKit.makeColor("#DEF3FF"));
		ableProperties.setBorder(new Border(1, ColorKit.makeColor("#3169C6"), Border.STYLE_SOLID));
		ableProperties.setInsets(new Insets(1, 0));
		ableProperties.setOutsets(new Insets(1, 0, 1, 0));
		ableProperties.setMouseCursor(MouseCursorable.CURSOR_POINTER);
		style.setProperty(PROPERTY_ENTRY_ROLLOVER_PROPERTIES, ableProperties);

		// seach area defaults
		ableProperties = new AbleProperties();
		ableProperties.setBackground(ColorKit.makeColor("#F2F2ED"));
		ableProperties.setBorder(new Border(1, ColorKit.makeColor("#F2F2ED"), Border.STYLE_SOLID));
		ableProperties.setFont(FontKit.makeFont("Verdana,plain,9pt"));
		ableProperties.setInsets(new Insets(2, 0));
		ableProperties.setOutsets(new Insets(1, 3, 0, 1));
		ableProperties.setMouseCursor(MouseCursorable.CURSOR_POINTER);
		style.setProperty(PROPERTY_SEARCH_BAR_PROPERTIES, ableProperties);

		ableProperties = new AbleProperties();
		ableProperties.setBackground(ColorKit.makeColor("#DEF3FF"));
		ableProperties.setBorder(new Border(1, ColorKit.makeColor("#3169C6"), Border.STYLE_SOLID));
		ableProperties.setFont(FontKit.makeFont("Verdana,plain,9pt"));
		ableProperties.setInsets(new Insets(2, 0));
		ableProperties.setOutsets(new Insets(1, 3, 0, 1));
		ableProperties.setMouseCursor(MouseCursorable.CURSOR_POINTER);
		style.setProperty(PROPERTY_SEARCH_BAR_ROLLOVER_PROPERTIES, ableProperties);

		style.setProperty(PROPERTY_SEARCH_BAR_SHOWN, true);
		style.setProperty(PROPERTY_SEARCH_BAR_TEXT, "Search");
		style.setProperty(PROPERTY_SEARCH_BAR_SEARCHING_TEXT, "Searching...");
		style.setProperty(PROPERTY_NO_MATCHING_OPTION_TEXT, "No matching options");
		style.setProperty(PROPERTY_SEARCH_BAR_SEARCHING_ICON, DEFAULT_SEARCH_BAR_SEARCHING_ICON);

		DEFAULT_STYLE = style;
	}

	/**
	 * Constructs a <code>AutoLookupTextFieldEx</code>
	 */
	public AutoLookupTextFieldEx() {
		super();
	}

	/**
	 * Constructs a <code>AutoLookupTextFieldEx</code>
	 * 
	 * @param document
	 */
	public AutoLookupTextFieldEx(Document document) {
		super(document);
	}

	/**
	 * Constructs a <code>AutoLookupTextFieldEx</code>
	 * 
	 * @param document
	 * @param text
	 * @param columns
	 */
	public AutoLookupTextFieldEx(Document document, String text, int columns) {
		super(document, text, columns);
	}

	/**
	 * Constructs a <code>AutoLookupTextFieldEx</code>
	 * 
	 * @param text
	 */
	public AutoLookupTextFieldEx(String text) {
		super(text);
	}

	/**
	 * @see nextapp.echo2.app.Component#init()
	 */
	public void init() {
		super.init();
		AutoLookupService.INSTANCE.register(this);
	}

	/**
	 * @see nextapp.echo2.app.Component#dispose()
	 */
	public void dispose() {
		super.dispose();
		AutoLookupService.INSTANCE.deregister(this);
	}

	public AutoLookupModel getAutoLookupModel() {
		return (AutoLookupModel) getProperty(PROPERTY_AUTO_LOOKUP_MODEL);
	}

	public void setAutoLookupModel(AutoLookupModel autoLookupModel) {
		setProperty(PROPERTY_AUTO_LOOKUP_MODEL, autoLookupModel);
	}

	public AbleProperties getPopupProperties() {
		return (AbleProperties) getProperty(PROPERTY_POPUP_PROPERTIES);
	}

	public void setPopupProperties(AbleProperties newValue) {
		setProperty(PROPERTY_POPUP_PROPERTIES, newValue);
	}

	public AbleProperties getEntryProperties() {
		return (AbleProperties) getProperty(PROPERTY_ENTRY_PROPERTIES);
	}

	public AbleProperties getEntryRolloverProperties() {
		return (AbleProperties) getProperty(PROPERTY_ENTRY_ROLLOVER_PROPERTIES);
	}

	public void setEntryProperties(AbleProperties newValue) {
		setProperty(PROPERTY_ENTRY_PROPERTIES, newValue);
	}

	public void setEntryRolloverProperties(AbleProperties newValue) {
		setProperty(PROPERTY_ENTRY_ROLLOVER_PROPERTIES, newValue);
	}

	public AbleProperties getSearchBarProperties() {
		return (AbleProperties) getProperty(PROPERTY_SEARCH_BAR_PROPERTIES);
	}

	public AbleProperties getSearchBarRolloverProperties() {
		return (AbleProperties) getProperty(PROPERTY_SEARCH_BAR_ROLLOVER_PROPERTIES);
	}

	public void setSearchBarProperties(AbleProperties newValue) {
		setProperty(PROPERTY_SEARCH_BAR_PROPERTIES, newValue);
	}

	public void setSearchBarRolloverProperties(AbleProperties newValue) {
		setProperty(PROPERTY_SEARCH_BAR_ROLLOVER_PROPERTIES, newValue);
	}
	

	public boolean getSearchBarShown() {
		return ComponentEx.getProperty(this, PROPERTY_SEARCH_BAR_SHOWN,true);
	}
	public void setSearchBarShown(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_SEARCH_BAR_SHOWN, newValue);
	}

	public String getNoMatchingOptionText() {
		return (String) ComponentEx.getProperty(this, PROPERTY_NO_MATCHING_OPTION_TEXT);
	}
	
	public void setNoMatchingOptionText(String newValue) {
		ComponentEx.setProperty(this, PROPERTY_NO_MATCHING_OPTION_TEXT, newValue);
	}

	public String getSearchBarText() {
		return (String) ComponentEx.getProperty(this, PROPERTY_SEARCH_BAR_TEXT);
	}
	public void setSearchBarText(String newValue) {
		ComponentEx.setProperty(this, PROPERTY_SEARCH_BAR_TEXT, newValue);
	}
	
	public ImageReference getSearchBarIcon() {
		return (ImageReference) ComponentEx.getProperty(this, PROPERTY_SEARCH_BAR_ICON);
	}
	public void setSearchBarIcon(ImageReference newValue) {
		ComponentEx.setProperty(this, PROPERTY_SEARCH_BAR_ICON, newValue);
	}
	
	public String getSearchBarSearchingText() {
		return (String) ComponentEx.getProperty(this, PROPERTY_SEARCH_BAR_SEARCHING_TEXT);
	}
	public void setSearchBarSearchingText(String newValue) {
		ComponentEx.setProperty(this, PROPERTY_SEARCH_BAR_SEARCHING_TEXT, newValue);
	}
	
	public ImageReference getSearchBarSearchingIcon() {
		return (ImageReference) ComponentEx.getProperty(this, PROPERTY_SEARCH_BAR_SEARCHING_ICON);
	}
	
	public void setSearchBarSearchingIcon(ImageReference newValue) {
		ComponentEx.setProperty(this, PROPERTY_SEARCH_BAR_SEARCHING_ICON, newValue);
	}
	

}
