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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;
import echopointng.model.CalendarEvent;
import echopointng.model.CalendarSelectionListener;
import echopointng.model.CalendarSelectionModel;
import echopointng.model.DefaultCalendarSelectionModel;
import echopointng.util.ColorKit;

/**
 * The <code>DateChooser</code> class can be used to navigate dates and select
 * a specified date.
 * <p>
 * The class uses the <code>CalendarSelectionModel</code> interface to keep
 * track of the currently selected date. If no calendar is provided on the the
 * constructor, then todays date is used.
 * <p>
 * The control consists of three sections, the top level navigation section, the
 * calendar section and the selection sections. You can use the relevant
 * accessor to set/get properties that control the apperance of each section.
 * <p>
 * When the <code>DateChooser</code> is in fast mode, the navigation between
 * months does not result in the <code>DateSelectionModel</code>'s
 * displayedDate being updated. The model will be updated if the user selects a
 * date however. Fast mode is the default.
 * 
 * @author Brad Baker
 */

public class DateChooser extends AbleComponent {

	/**
	 * <code>ModelChangeListener</code> is used to forward
	 * <code>CalendarSelectionModel</code> events into components updates.
	 */
	private class ModelChangeListener implements CalendarSelectionListener, Serializable {
		/**
		 * @see echopointng.model.CalendarSelectionListener#displayedDateChange(echopointng.model.CalendarEvent)
		 */
		public void displayedDateChange(CalendarEvent calEvent) {
			firePropertyChange("displayedDate", null, calEvent.getCalendar());
		}

		/**
		 * @see echopointng.model.CalendarSelectionListener#selectedDateChange(echopointng.model.CalendarEvent)
		 */
		public void selectedDateChange(CalendarEvent calEvent) {
			firePropertyChange("selectedDate", null, calEvent.getCalendar());
		}
	}

	private static class DateOnlyCalendar extends GregorianCalendar {

		public static Calendar getInstance(Locale locale) {
			return new DateOnlyCalendar(locale);
		}

		public DateOnlyCalendar(Locale locale) {
			super();
			Calendar now = Calendar.getInstance(locale);
			this.setTime(now.getTime());
			clearTimePortion(now);
		}

		public String toString() {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy E");
			return sdf.format(this.getTime());
		}

		public static Calendar clearTimePortion(Calendar cal) {
			if (cal != null) {
				cal.clear(Calendar.HOUR);
				cal.clear(Calendar.MINUTE);
				cal.clear(Calendar.SECOND);
				cal.clear(Calendar.MILLISECOND);
			}
			return cal;
		}
	}

	/**
	 * A black and white Style for DateChooser
	 */
	public static final Style BLACK_AND_WHITE_STYLE;

	/**
	 * A colorful Style for DateChooser
	 */
	public static final Style COLORFUL_STYLE;

	public static final ImageReference DEFAULT_NAVIGATION_BACKWARD_IMAGE = new ResourceImageReference(
			"/echopointng/resource/images/smallleftarrow.gif", new Extent(12), new Extent(11));

	public static final ImageReference DEFAULT_NAVIGATION_FORWARD_IMAGE = new ResourceImageReference(
			"/echopointng/resource/images/smallrightarrow.gif", new Extent(12), new Extent(11));

	/**
	 * The default Style for DateChooser
	 */
	public static final Style DEFAULT_STYLE;

	public static final String PROPERTY_CALENDAR_BACKGROUND = "calendarBackground";

	public static final String PROPERTY_CALENDAR_BORDER = "calendarBorder";

	public static final String PROPERTY_CALENDAR_FONT = "calendarFont";

	public static final String PROPERTY_WEEK_NUMBER_FOREGROUND = "weekNumberForeground";

	public static final String PROPERTY_CALENDAR_FOREGROUND = "calendarForeground";

	public static final String PROPERTY_CALENDAR_NON_MONTH_FOREGROUND = "calendarNonMonthForeground";

	public static final String PROPERTY_CALENDAR_ROLLOVER_BACKGROUND = "calendarRolloverBackground";

	public static final String PROPERTY_CALENDAR_ROLLOVER_BORDER = "calendarRolloverBorder";

	public static final String PROPERTY_CALENDAR_ROLLOVER_FOREGROUND = "calendarRolloverForeground";

	public static final String PROPERTY_CALENDAR_SELECTED_BACKGROUND = "calendarSelectedBackground";

	public static final String PROPERTY_CALENDAR_SELECTED_BORDER = "calendarSelectedBorder";

	public static final String PROPERTY_CALENDAR_SELECTED_FONT = "calendarSelectedFont";

	public static final String PROPERTY_CALENDAR_SELECTED_FOREGROUND = "calendarSelectedForeground";

	public static final String PROPERTY_DOW_NAME_LENGTH = "dowNameLength";

	public static final String PROPERTY_FAST_MODE = "fastMode";

	public static final String PROPERTY_FIRST_DAY_OF_WEEK = "firstDayOfWeek";

	public static final String PROPERTY_MAXIMUM_DATE = "maximumDate";

	public static final String PROPERTY_MINIMUM_DATE = "minimumDate";

	public static final String PROPERTY_MODEL = "model";

	public static final String PROPERTY_MONTH_NAME_LENGTH = "monthNameLength";

	public static final String PROPERTY_MONTH_SELECTABLE = "monthSelectable";

	public static final String PROPERTY_NAVIGATION_ARROWS_VISIBLE = "navigationArrowsVisible";

	public static final String PROPERTY_NAVIGATION_BACKGROUND = "navigationBackground";

	public static final String PROPERTY_NAVIGATION_BACKWARD_IMAGE = "navigationBackwardImage";

	public static final String PROPERTY_NAVIGATION_FONT = "navigationFont";

	public static final String PROPERTY_NAVIGATION_FOREGROUND = "navigationForeground";

	public static final String PROPERTY_NAVIGATION_FORWARD_IMAGE = "navigationForwardImage";

	public static final String PROPERTY_NAVIGATION_INHIBITED = "navigationInhibited";

	public static final String PROPERTY_SELECTED_BACKGROUND = "selectedBackground";

	public static final String PROPERTY_SELECTED_BORDER = "selectedBorder";

	public static final String PROPERTY_SELECTED_FONT = "selectedFont";

	public static final String PROPERTY_SELECTED_FOREGROUND = "selectedForeground";

	public static final String PROPERTY_SELECTED_ROLLOVER_BACKGROUND = "selectedRolloverBackground";

	public static final String PROPERTY_SELECTED_ROLLOVER_BORDER = "selectedRolloverBorder";

	public static final String PROPERTY_SELECTED_ROLLOVER_FOREGROUND = "selectedRolloverForeground";

	public static final String PROPERTY_SELECTION_DISPLAYBAR_VISIBLE = "selectionDisplayBarVisible";

	public static final String PROPERTY_WEEK_NUMBER_ABBREVIATION = "weekNumberAbbreviation";

	public static final String PROPERTY_YEAR_RANGE = "yearRange";

	public static final String PROPERTY_YEAR_SELECTABLE = "yearSelectable";

	public static final String SEL_YEAR_PREFIX = "year";

	static {
		MutableStyleEx style;

		Color GUNMETAL_BLUEISH_WHITE = ColorKit.makeColor("#ACBCDC");
		BorderEx DEFAULT_BORDER = new BorderEx(1, Color.WHITE);
		BorderEx DEFAULT_ROLLOVER_BORDER = new BorderEx(1, GUNMETAL_BLUEISH_WHITE);

		style = new MutableStyleEx();

		style.setProperty(PROPERTY_FAST_MODE, true);

		style.setProperty(PROPERTY_FONT, new Font(Font.ARIAL, 0, new ExtentEx("8pt")));
		style.setProperty(PROPERTY_WEEK_NUMBER_FOREGROUND, ColorKit.clr("#9400D3"));

		style.setProperty(PROPERTY_MOUSE_CURSOR, CURSOR_POINTER);
		style.setProperty(PROPERTY_CALENDAR_BACKGROUND, Color.WHITE);
		style.setProperty(PROPERTY_CALENDAR_FOREGROUND, Color.BLACK);
		style.setProperty(PROPERTY_CALENDAR_BORDER, DEFAULT_BORDER);

		style.setProperty(PROPERTY_CALENDAR_SELECTED_FOREGROUND, Color.ORANGE);
		style.setProperty(PROPERTY_CALENDAR_SELECTED_BACKGROUND, Color.WHITE);
		style.setProperty(PROPERTY_CALENDAR_SELECTED_BORDER, DEFAULT_ROLLOVER_BORDER);

		style.setProperty(PROPERTY_CALENDAR_NON_MONTH_FOREGROUND, Color.LIGHTGRAY);
		style.setProperty(PROPERTY_CALENDAR_ROLLOVER_FOREGROUND, Color.ORANGE);
		style.setProperty(PROPERTY_CALENDAR_ROLLOVER_BACKGROUND, Color.WHITE);
		style.setProperty(PROPERTY_CALENDAR_ROLLOVER_BORDER, DEFAULT_ROLLOVER_BORDER);

		style.setProperty(PROPERTY_NAVIGATION_FOREGROUND, Color.WHITE);
		style.setProperty(PROPERTY_NAVIGATION_BACKGROUND, GUNMETAL_BLUEISH_WHITE);

		style.setProperty(PROPERTY_SELECTED_FOREGROUND, Color.WHITE);
		style.setProperty(PROPERTY_SELECTED_BACKGROUND, GUNMETAL_BLUEISH_WHITE);
		style.setProperty(PROPERTY_SELECTED_BORDER, DEFAULT_BORDER);

		style.setProperty(PROPERTY_SELECTED_ROLLOVER_FOREGROUND, Color.ORANGE);
		style.setProperty(PROPERTY_SELECTED_ROLLOVER_FOREGROUND, Color.ORANGE);
		style.setProperty(PROPERTY_SELECTED_ROLLOVER_BACKGROUND, GUNMETAL_BLUEISH_WHITE);
		style.setProperty(PROPERTY_SELECTED_ROLLOVER_BORDER, DEFAULT_ROLLOVER_BORDER);

		style.setProperty(PROPERTY_NAVIGATION_BACKWARD_IMAGE, DEFAULT_NAVIGATION_BACKWARD_IMAGE);
		style.setProperty(PROPERTY_NAVIGATION_FORWARD_IMAGE, DEFAULT_NAVIGATION_FORWARD_IMAGE);

		DEFAULT_STYLE = style;

		// ----------------------------------------

		style = new MutableStyleEx();

		style.setProperty(PROPERTY_FONT, new Font(Font.ARIAL, 0, new ExtentEx("8pt")));
		style.setProperty(PROPERTY_MOUSE_CURSOR, CURSOR_POINTER);
		style.setProperty(PROPERTY_CALENDAR_BACKGROUND, Color.WHITE);
		style.setProperty(PROPERTY_CALENDAR_FOREGROUND, Color.BLACK);

		style.setProperty(PROPERTY_SELECTED_FOREGROUND, Color.RED);
		style.setProperty(PROPERTY_SELECTED_BACKGROUND, Color.WHITE);

		style.setProperty(PROPERTY_CALENDAR_NON_MONTH_FOREGROUND, Color.LIGHTGRAY);
		style.setProperty(PROPERTY_CALENDAR_ROLLOVER_FOREGROUND, Color.ORANGE);
		style.setProperty(PROPERTY_CALENDAR_ROLLOVER_BACKGROUND, Color.WHITE);

		style.setProperty(PROPERTY_NAVIGATION_FOREGROUND, Color.WHITE);
		style.setProperty(PROPERTY_NAVIGATION_BACKGROUND, Color.BLUE);

		style.setProperty(PROPERTY_SELECTED_FOREGROUND, Color.RED);
		style.setProperty(PROPERTY_SELECTED_ROLLOVER_FOREGROUND, Color.ORANGE);
		style.setProperty(PROPERTY_SELECTED_ROLLOVER_BACKGROUND, new Color(0xFF, 0xFF, 0xCC)); // light
		// yellow
		style.setProperty(PROPERTY_SELECTED_BACKGROUND, new Color(0xFF, 0xFF, 0xCC)); // light
		// yellow

		style.setProperty(PROPERTY_NAVIGATION_BACKWARD_IMAGE, DEFAULT_NAVIGATION_BACKWARD_IMAGE);
		style.setProperty(PROPERTY_NAVIGATION_FORWARD_IMAGE, DEFAULT_NAVIGATION_FORWARD_IMAGE);

		COLORFUL_STYLE = style;

		// ----------------------------------------

		style = new MutableStyleEx();

		style.setProperty(PROPERTY_FONT, new Font(Font.ARIAL, 0, new ExtentEx("8pt")));
		style.setProperty(PROPERTY_MOUSE_CURSOR, CURSOR_POINTER);
		style.setProperty(PROPERTY_CALENDAR_BACKGROUND, Color.WHITE);
		style.setProperty(PROPERTY_CALENDAR_FOREGROUND, Color.BLACK);
		style.setProperty(PROPERTY_SELECTED_FOREGROUND, Color.BLACK);
		style.setProperty(PROPERTY_SELECTED_BACKGROUND, Color.WHITE);

		style.setProperty(PROPERTY_CALENDAR_NON_MONTH_FOREGROUND, Color.LIGHTGRAY);
		style.setProperty(PROPERTY_CALENDAR_ROLLOVER_FOREGROUND, Color.DARKGRAY);
		style.setProperty(PROPERTY_CALENDAR_ROLLOVER_BACKGROUND, Color.WHITE);

		style.setProperty(PROPERTY_NAVIGATION_FOREGROUND, Color.WHITE);
		style.setProperty(PROPERTY_NAVIGATION_BACKGROUND, Color.BLACK);

		style.setProperty(PROPERTY_SELECTED_FOREGROUND, Color.BLACK);
		style.setProperty(PROPERTY_SELECTED_ROLLOVER_FOREGROUND, Color.DARKGRAY);
		style.setProperty(PROPERTY_SELECTED_ROLLOVER_BACKGROUND, Color.WHITE);
		style.setProperty(PROPERTY_SELECTED_BACKGROUND, Color.WHITE);

		style.setProperty(PROPERTY_NAVIGATION_BACKWARD_IMAGE, DEFAULT_NAVIGATION_BACKWARD_IMAGE);
		style.setProperty(PROPERTY_NAVIGATION_FORWARD_IMAGE, DEFAULT_NAVIGATION_FORWARD_IMAGE);

		BLACK_AND_WHITE_STYLE = style;

	}

	/**
	 * Clears a <code>Calendar</code> of its time portion
	 */
	public static Calendar calendarClearTimePortion(Calendar cal) {
		return DateOnlyCalendar.clearTimePortion(cal);
	}

	/**
	 * Copys the values of one <code>Calendar</code> into another, always
	 * clearing the time portion of the copied <code>Calendar</code>
	 * <p>
	 * if <code>cal</code> is null, then null is returned.
	 */
	public static Calendar calendarCopy(Calendar cal) {
		return calendarCopy(cal, null);
	}

	/**
	 * Copys the values of one <code>Calendar</code> into another, always
	 * clearing the time portion of the copied <code>Calendar</code>
	 * <p>
	 * if <code>cal</code> is null, then null is returned.
	 */
	public static Calendar calendarCopy(Calendar cal, Locale locale) {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		if (cal != null) {
			Calendar newCal = DateOnlyCalendar.getInstance(locale);
			newCal.setTime(cal.getTime());
			newCal.setFirstDayOfWeek(cal.getFirstDayOfWeek());
			return calendarClearTimePortion(newCal);
		} else {
			return cal;
		}
	}

	/**
	 * Creates a Calendar, based on the Date object provide that has the time
	 * potion cleared from it.
	 * 
	 * @param date -
	 *            a non null Date
	 * @param locale -
	 *            the Locale to use or null to use the default locale
	 * 
	 * @return a Calendar, based on the Date object provide that has the time
	 *         potion cleared from it.
	 */
	public static Calendar calendarCopy(Date date, Locale locale) {
		if (date == null) {
			throw new IllegalArgumentException("The Date must be non null");
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		Calendar newCal = DateOnlyCalendar.getInstance(locale);
		newCal.setTime(date);
		return calendarClearTimePortion(newCal);
	}

	private ModelChangeListener modelChangeForwarder = new ModelChangeListener();

	/**
	 * Constructs a <code>DateChooser</code> with the currently selected date
	 * set to right now.
	 */
	public DateChooser() {
		this(DateOnlyCalendar.getInstance());
	}

	/**
	 * Constructs a <code>DateChooser</code> with the currently selected date
	 * set to the one provided.
	 */
	public DateChooser(Calendar newSelectedDate) {
		this(newSelectedDate, Locale.getDefault());
	}

	/**
	 * Constructs a <code>DateChooser</code> with the currently selected date
	 * set to the one provided and a the specified Locale.
	 */
	public DateChooser(Calendar newSelectedDate, Locale locale) {
		super();
		setFocusTraversalParticipant(true);
		setLocale(locale);

		Calendar cal = calendarCopy(newSelectedDate, locale);
		setModel(new DefaultCalendarSelectionModel(cal));
	}

	/**
	 * @return The <code>Color</code> of the <code>DateChooser's</code>
	 *         calendar area.
	 */
	public Color getCalendarBackground() {
		return (Color) getProperty(PROPERTY_CALENDAR_BACKGROUND);
	}

	/**
	 * @return The <code>Border</code> of the <code>DateChooser's</code>
	 *         calendar area.
	 */
	public Border getCalendarBorder() {
		return (Border) getProperty(PROPERTY_CALENDAR_BORDER);
	}

	/**
	 * @return The <code>Font</code> of the <code>DateChooser's</code>
	 *         calendar area.
	 */
	public Font getCalendarFont() {
		return (Font) getProperty(PROPERTY_CALENDAR_FONT);
	}

	/**
	 * @return The foreground <code>Color</code> of the
	 *         <code>DateChooser's</code> calendar area.
	 */
	public Color getCalendarForeground() {
		return (Color) getProperty(PROPERTY_CALENDAR_FOREGROUND);
	}

	/**
	 * @return The foreground <code>Color</code> of days that are not within
	 *         the the displayed month.
	 */
	public Color getCalendarNonMonthForeground() {
		return (Color) getProperty(PROPERTY_CALENDAR_NON_MONTH_FOREGROUND);
	}

	/**
	 * @return The rollover background <code>Color</code> of the
	 *         <code>DateChooser's</code> calendar area.
	 */
	public Color getCalendarRolloverBackground() {
		return (Color) getProperty(PROPERTY_CALENDAR_ROLLOVER_BACKGROUND);
	}

	/**
	 * @return The rollover <code>Border</code> of the
	 *         <code>DateChooser's</code> calendar area.
	 */
	public Border getCalendarRolloverBorder() {
		return (Border) getProperty(PROPERTY_CALENDAR_ROLLOVER_BORDER);
	}

	/**
	 * @return The rollover foreground <code>Color</code> of the
	 *         <code>DateChooser's</code> calendar area.
	 */
	public Color getCalendarRolloverForeground() {
		return (Color) getProperty(PROPERTY_CALENDAR_ROLLOVER_FOREGROUND);
	}

	/**
	 * @return The <code>Color</code> of the selected date in the
	 *         <code>DateChooser's</code> calendar area.
	 */
	public Color getCalendarSelectedBackground() {
		return (Color) getProperty(PROPERTY_CALENDAR_SELECTED_BACKGROUND);
	}

	/**
	 * @return The <code>Border</code> of the selected date in the
	 *         <code>DateChooser's</code> calendar area.
	 */
	public Border getCalendarSelectedBorder() {
		return (Border) getProperty(PROPERTY_CALENDAR_SELECTED_BORDER);
	}

	/**
	 * @return The code>Font</code> of the selected date in the <code>
	 *         DateChooser's</code> calendar area.
	 */
	public Font getCalendarSelectedFont() {
		return (Font) getProperty(PROPERTY_CALENDAR_SELECTED_FONT);
	}

	/**
	 * @return The foreground <code>Color</code> of the selected date in the
	 *         <code>DateChooser's</code> calendar area.
	 */
	public Color getCalendarSelectedForeground() {
		return (Color) getProperty(PROPERTY_CALENDAR_SELECTED_FOREGROUND);
	}

	/**
	 * A shortcurt method to the model's getDisplayedDate() method.
	 * 
	 * @return The currently displayed date.
	 */
	public Calendar getDisplayedDate() {
		return calendarCopy(getModel().getDisplayedDate(), getLocale());
	}

	/**
	 * When the <code>DateChooser</code> is in fast mode, the navigation
	 * between months does not result in the <code>DateSelectionModel</code>
	 * 's displayedDate being updated. The model will be updated if the user
	 * selects a date however.
	 * 
	 * @return true if the <code>DateChoooser</code> is in fast mode
	 */
	public boolean isFastMode() {
		return getProperty(PROPERTY_FAST_MODE, true);
	}

	/**
	 * @return the value to be used as the first day of the week. This affects
	 *         how the calendar view will be built, with this day of the week as
	 *         the starting field. It defaults to the system default day of the
	 *         week as provided by Calendar. Sunday = 1, Monday = 2.... Saturday =
	 *         7.
	 */
	public int getFirstDayOfWeek() {
		Calendar dt = getDisplayedDate();
		if (dt == null) {
			dt = Calendar.getInstance(getLocale());
		}
		int defaultDow = dt.getFirstDayOfWeek();
		return getProperty(PROPERTY_FIRST_DAY_OF_WEEK, defaultDow);
	}

	/**
	 * Set the value to be used as the first day of the week. This affects how
	 * the calendar view will be built, with this day of the week as the
	 * starting field. It defaults to the system default day of the week as
	 * provided by Calendar. Sunday = 1, Monday = 2.... Saturday = 7.
	 * 
	 * @param newValue -
	 *            the new first day of the week.
	 */
	public void setFirstDayOfWeek(int newValue) {
		setProperty(PROPERTY_FIRST_DAY_OF_WEEK, newValue);
	}

	/**
	 * When the <code>DateChooser</code> is in fast mode, the navigation
	 * between months does not result in the <code>DateSelectionModel</code>
	 * 's displayedDate being updated. The model will be updated if the user
	 * selects a date however.
	 * 
	 * @param newValue -
	 *            the new state of fast mode.
	 */
	public void setFastMode(boolean newValue) {
		setProperty(PROPERTY_FAST_MODE, newValue);
	}

	/**
	 * Returns the maximum date the DateChooser will display.
	 * 
	 * @return the maximum date the DateChooser will display.
	 */
	public Calendar getMaximumDate() {
		return calendarCopy((Calendar) getProperty(PROPERTY_MAXIMUM_DATE), getLocale());
	}

	/**
	 * Returns the minimum date the DateChooser will display.
	 * 
	 * @return the minimum date the DateChooser will display.
	 */
	public Calendar getMinimumDate() {
		return calendarCopy((Calendar) getProperty(PROPERTY_MINIMUM_DATE), getLocale());
	}

	/**
	 * Gets the length of the day of the week names when displayed.
	 * <p>
	 * ie. 4=Mond, 3=Mon, 2=Mo, 1=M
	 * <p>
	 * The default is 3.
	 * 
	 * @return the length of the day or the week names
	 */
	public int getDowNameLength() {
		return getProperty(PROPERTY_DOW_NAME_LENGTH, 3);
	}

	/**
	 * @return The <code>CalendarSelectionModel</code> currently used by the
	 *         <code>DateChooser's</code>.
	 */
	public CalendarSelectionModel getModel() {
		return (CalendarSelectionModel) getProperty(PROPERTY_MODEL);
	}

	/**
	 * Gets the length of the month names when displayed.
	 * <p>
	 * ie. 10=September, 4=Sept, 3=Sep, 1=S
	 * <p>
	 * The default is 3.
	 * 
	 * @return the length of the month names
	 */
	public int getMonthNameLength() {
		return getProperty(PROPERTY_MONTH_NAME_LENGTH, 3);
	}

	/**
	 * @return The <code>Color</code> of the <code>DateChooser's</code>
	 *         navigation area.
	 */
	public Color getNavigationBackground() {
		return (Color) getProperty(PROPERTY_NAVIGATION_BACKGROUND);
	}

	/**
	 * Gets the image used as the navigate the <code>DateChooser</code>
	 * backward in time.
	 */
	public ImageReference getNavigationBackwardImage() {
		return (ImageReference) getProperty(PROPERTY_NAVIGATION_BACKWARD_IMAGE);
	}

	/**
	 * @return The <code>Font</code> of the <code>DateChooser's</code>
	 *         navigation area.
	 */
	public Font getNavigationFont() {
		return (Font) getProperty(PROPERTY_NAVIGATION_FONT);
	}

	/**
	 * @return The foreground <code>Color</code> of the
	 *         <code>DateChooser's</code> navigation area.
	 */
	public Color getNavigationForeground() {
		return (Color) getProperty(PROPERTY_NAVIGATION_FOREGROUND);
	}

	/**
	 * Gets the image used as the navigate the <code>DateChooser</code>
	 * forward in time.
	 */
	public ImageReference getNavigationForwardImage() {
		return (ImageReference) getProperty(PROPERTY_NAVIGATION_FORWARD_IMAGE);
	}

	/**
	 * Returns true if the user is inhbited from navigating beyond the maximum
	 * and minimum dates.
	 * 
	 * @return true if the user is inhbited from navigating beyond the maximum
	 *         and minimum dates.
	 */
	public boolean getNavigationInhibited() {
		return getProperty(PROPERTY_NAVIGATION_INHIBITED, false);
	}

	/**
	 * @return The background <code>Color</code> of the
	 *         <code>DateChooser's</code> selected area.
	 */
	public Color getSelectedBackground() {
		return (Color) getProperty(PROPERTY_SELECTED_BACKGROUND);
	}

	/**
	 * @return The <code>Border</code> of the <code>DateChooser's</code>
	 *         selected area.
	 */
	public Border getSelectedBorder() {
		return (Border) getProperty(PROPERTY_SELECTED_BORDER);
	}

	/**
	 * A shortcurt method to the underlying DateChooser's model
	 * getSelectedDate() method.
	 * 
	 * @return The currently selected date
	 */
	public Calendar getSelectedDate() {
		return calendarCopy(getModel().getSelectedDate(), getLocale());
	}

	/**
	 * @return The <code>Font</code> of the <code>DateChooser's</code>
	 *         selected area.
	 */
	public Font getSelectedFont() {
		return (Font) getProperty(PROPERTY_SELECTED_FONT);
	}

	/**
	 * @return The foreground <code>Color</code> of the
	 *         <code>DateChooser's</code> selected area.
	 */
	public Color getSelectedForeground() {
		return (Color) getProperty(PROPERTY_SELECTED_FOREGROUND);
	}

	/**
	 * @return The rollover background <code>Color</code> of the
	 *         <code>DateChooser's</code> selected area.
	 */
	public Color getSelectedRolloverBackground() {
		return (Color) getProperty(PROPERTY_SELECTED_ROLLOVER_BACKGROUND);
	}

	/**
	 * @return The rollover <code>Border</code> of the
	 *         <code>DateChooser's</code> selected area.
	 */
	public Border getSelectedRolloverBorder() {
		return (Border) getProperty(PROPERTY_SELECTED_ROLLOVER_BORDER);
	}

	/**
	 * @return The rollover foreground <code>Color</code> of the
	 *         <code>DateChooser's</code> selected area.
	 */
	public Color getSelectedRolloverForeground() {
		return (Color) getProperty(PROPERTY_SELECTED_ROLLOVER_FOREGROUND);
	}

	/**
	 * Returns String for weekNumberAbbreviation (WN)
	 * 
	 * @return String
	 */
	public String getWeekNumberAbbreviation() {
		return (String) getProperty(PROPERTY_WEEK_NUMBER_ABBREVIATION);
	}

	/**
	 * Returns the number of years that will appear in the selectable Year
	 * field.
	 * 
	 * @return int
	 */
	public int getYearRange() {
		return getProperty(PROPERTY_YEAR_RANGE, 30);
	}

	/**
	 * Returns true if the Month display is selectable
	 * 
	 * @return boolean
	 */
	public boolean isMonthSelectable() {
		return getProperty(PROPERTY_MONTH_SELECTABLE, true);
	}

	/**
	 * Returns true if the NavigationsArrows are visible
	 * 
	 * @return boolean
	 */
	public boolean isNavigationArrowsVisible() {
		return getProperty(PROPERTY_NAVIGATION_ARROWS_VISIBLE, true);
	}

	/**
	 * Returns true if the SelectionDisplayBar is visible
	 * 
	 * @return boolean
	 */
	public boolean isSelectionDisplayBarVisible() {
		return getProperty(PROPERTY_SELECTION_DISPLAYBAR_VISIBLE, true);
	}

	/**
	 * Returns true if the Year display is selectable.
	 * 
	 * @return boolean
	 */
	public boolean isYearSelectable() {
		return getProperty(PROPERTY_YEAR_SELECTABLE, false);
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		Locale locale = getLocale() == null ? Locale.getDefault() : getLocale();

		Calendar displayedDate = calendarCopy(getDisplayedDate(), locale);
		if (inputName.startsWith("month")) {
			int index = Integer.parseInt((String) inputValue);

			displayedDate.set(Calendar.MONTH, Calendar.JANUARY + index);
			setDisplayedDate(displayedDate);
		}

		if (inputName.startsWith("year")) {
			int year = Integer.parseInt((String) inputValue);
			displayedDate.set(Calendar.YEAR, year);
			setDisplayedDate(displayedDate);
		}

		if (inputName.equals("fwd")) {
			//
			// no need to do anything because a displayedDate update will
			// be inbound and hence we will navigate along.
			//
			// displayedDate.add(Calendar.MONTH, 1);
			// setDisplayedDate(displayedDate);

		} else if (inputName.equals("bwd")) {
			//
			// no need to do anything because a displayedDate update will
			// be inbound and hence we will navigate along.
			//
			// displayedDate.add(Calendar.MONTH, -1);
			// setDisplayedDate(displayedDate);

		} else if (inputName.equals("sel")) {
			setDisplayedDate(getSelectedDate());

		} else if (inputName.equals("displayedDate")) {
			Calendar newVal = parseYYYYMMD(inputValue);
			setDisplayedDate(newVal);

		} else if (inputName.startsWith("click")) {
			Calendar newVal = parseYYYYMMD(inputValue);
			setSelectedDate(newVal);
		}
	}

	private Calendar parseYYYYMMD(Object inputValue) {
		Locale locale = getLocale() == null ? Locale.getDefault() : getLocale();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date dt;
		try {
			dt = sdf.parse((String) inputValue);
		} catch (ParseException e) {
			throw new IllegalStateException("This should never happen!");
		}
		Calendar newCal = calendarCopy(dt, locale);
		return newCal;
	}

	/**
	 * Sets the background <code>Color</code> of the
	 * <code>DateChooser's</code> calendar area.
	 */
	public void setCalendarBackground(Color newCalendarBackground) {
		setProperty(PROPERTY_CALENDAR_BACKGROUND, newCalendarBackground);
	}

	/**
	 * Sets the <code>Border</code> of the <code>DateChooser's</code>
	 * calendar area.
	 */
	public void setCalendarBorder(Border newValue) {
		setProperty(PROPERTY_CALENDAR_BORDER, newValue);
	}

	/**
	 * Sets the background <code>Font</code> of the <code>DateChooser's</code>
	 * calendar area.
	 */
	public void setCalendarFont(Font newCalendarFont) {
		setProperty(PROPERTY_CALENDAR_FONT, newCalendarFont);
	}

	/**
	 * Sets the foreground <code>Color</code> of the
	 * <code>DateChooser's</code> calendar area.
	 */
	public void setCalendarForeground(Color newCalendarForeground) {
		setProperty(PROPERTY_CALENDAR_FOREGROUND, newCalendarForeground);
	}

	/**
	 * Sets the foreground <code>Color</code> of days that are not within the
	 * currently displayed date of the <code>DateChooser's</code> calendar
	 * area.
	 */
	public void setCalendarNonMonthForeground(Color newCalendarNonMonthForeground) {
		setProperty(PROPERTY_CALENDAR_NON_MONTH_FOREGROUND, newCalendarNonMonthForeground);
	}

	/**
	 * Sets the rollover background <code>Color</code> of the
	 * <code>DateChooser's</code> calendar area.
	 */
	public void setCalendarRolloverBackground(Color newCalendarRolloverBackground) {
		setProperty(PROPERTY_CALENDAR_ROLLOVER_BACKGROUND, newCalendarRolloverBackground);
	}

	/**
	 * Sets the rollover <code>Border</code> of the selected date in the
	 * <code>DateChooser's</code> calendar area.
	 */
	public void setCalendarRolloverBorder(Border newValue) {
		setProperty(PROPERTY_CALENDAR_ROLLOVER_BORDER, newValue);
	}

	/**
	 * Sets the rollover foreground <code>Color</code> of the
	 * <code>DateChooser's</code> calendar area.
	 */
	public void setCalendarRolloverForeground(Color newCalendarRolloverForeground) {
		setProperty(PROPERTY_CALENDAR_ROLLOVER_FOREGROUND, newCalendarRolloverForeground);
	}

	/**
	 * Sets the background <code>Color</code> of the selected date in the
	 * <code>DateChooser's</code> calendar area.
	 */
	public void setCalendarSelectedBackground(Color newCalendarBackground) {
		setProperty(PROPERTY_CALENDAR_SELECTED_BACKGROUND, newCalendarBackground);
	}

	/**
	 * Sets the <code>Border</code> of the selected date in the
	 * <code>DateChooser's</code> calendar area.
	 */
	public void setCalendarSelectedBorder(Border newValue) {
		setProperty(PROPERTY_CALENDAR_SELECTED_BORDER, newValue);
	}

	/**
	 * Sets the foreground <code>Color</code> of the selected date in the
	 * <code>DateChooser's</code> calendar area.
	 */
	public void setCalendarSelectedFont(Font newFont) {
		setProperty(PROPERTY_CALENDAR_SELECTED_FONT, newFont);
	}

	/**
	 * Sets the foreground <code>Color</code> of the selected date in the
	 * <code>DateChooser's</code> calendar area.
	 */
	public void setCalendarSelectedForeground(Color newCalendarForeground) {
		setProperty(PROPERTY_CALENDAR_SELECTED_FOREGROUND, newCalendarForeground);
	}

	/**
	 * Sets the currently displayed date within the <code>DateChooser</code>
	 * into the model.
	 * 
	 * @param newDisplayedDate -
	 *            the new displayed date
	 */
	public void setDisplayedDate(Calendar newDisplayedDate) {
		getModel().setDisplayedDate(newDisplayedDate);
	}

	/**
	 * Sets the maximum date the <code>DateChooser</code> will display. If
	 * this is null then no date restriction is put in place.
	 * 
	 * @param newValue -
	 *            the new Maximum date.
	 */
	public void setMaximumDate(Calendar newValue) {
		setProperty(PROPERTY_MAXIMUM_DATE, calendarCopy(newValue, getLocale()));
	}

	/**
	 * Sets the minimum date the <code>DateChooser</code> will display. If
	 * this is null then no date restriction is put in place.
	 * 
	 * @param newValue -
	 *            the new Maximum date.
	 */
	public void setMinimumDate(Calendar newValue) {
		setProperty(PROPERTY_MINIMUM_DATE, calendarCopy(newValue, getLocale()));
	}

	/**
	 * Sets the length of the day of the week names when displayed.
	 * <p>
	 * ie. 4=Mond, 3=Mon, 2=Mo, 1=M
	 * <p>
	 * The default is 3.
	 * 
	 * @param newValue -
	 *            the maximum length of the day of the week names
	 */
	public void setDowNameLength(int newValue) {
		setProperty(PROPERTY_DOW_NAME_LENGTH, newValue);
	}

	/**
	 * Sets a new <code>CalendarSelectionModel</code> model into the
	 * <code>DateChooser</code>
	 */
	public void setModel(CalendarSelectionModel newModel) {
		if (newModel == null)
			throw new IllegalArgumentException("The CalendarSelectionModel mus be non null!");

		CalendarSelectionModel oldModel = getModel();
		if (oldModel != newModel) {
			if (oldModel != null)
				oldModel.removeListener(modelChangeForwarder);
			newModel.addListener(modelChangeForwarder);
		}
		setProperty(PROPERTY_MODEL, newModel);
	}

	/**
	 * Sets the length of the month names when displayed.
	 * <p>
	 * ie. 10=September, 4=Sept, 3=Sep, 1=S
	 * <p>
	 * The default is 3.
	 * 
	 * @param newValue -
	 *            the maximum length of the month names
	 */
	public void setMonthNameLength(int newValue) {
		setProperty(PROPERTY_MONTH_NAME_LENGTH, newValue);
	}

	/**
	 * Sets whether the Month field is selectable by the user.
	 */
	public void setMonthSelectable(boolean newMonthSelectable) {
		setProperty(PROPERTY_MONTH_SELECTABLE, newMonthSelectable);
	}

	/**
	 * Sets whether the NavigationArrows are visible
	 */
	public void setNavigationArrowsVisible(boolean newValue) {
		setProperty(PROPERTY_NAVIGATION_ARROWS_VISIBLE, newValue);
	}

	/**
	 * Sets the background <code>Color</code> of the
	 * <code>DateChooser's</code> navigation area.
	 */
	public void setNavigationBackground(Color newNavigationBackground) {
		setProperty(PROPERTY_NAVIGATION_BACKGROUND, newNavigationBackground);
	}

	/**
	 * Sets the image used as the navigate the <code>DateChooser</code>
	 * backward in time.
	 */
	public void setNavigationBackwardImage(ImageReference newImage) {
		setProperty(PROPERTY_NAVIGATION_BACKWARD_IMAGE, newImage);
	}

	/**
	 * Sets the background <code>Font</code> of the <code>DateChooser's</code>
	 * navigation area.
	 */
	public void setNavigationFont(Font newNavigationFont) {
		setProperty(PROPERTY_NAVIGATION_FONT, newNavigationFont);
	}

	/**
	 * Sets the foreground <code>Color</code> of the
	 * <code>DateChooser's</code> navigation area.
	 */
	public void setNavigationForeground(Color newNavigationForeground) {
		setProperty(PROPERTY_NAVIGATION_FOREGROUND, newNavigationForeground);
	}

	/**
	 * Sets the image used as the navigate the <code>DateChooser</code>
	 * forward in time.
	 */
	public void setNavigationForwardImage(ImageReference newImage) {
		setProperty(PROPERTY_NAVIGATION_FORWARD_IMAGE, newImage);
	}

	/**
	 * If set to true the user is inhbited from navigating beyond the maximum
	 * and minimum dates.
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setNavigationInhibited(boolean newValue) {
		setProperty(PROPERTY_NAVIGATION_INHIBITED, newValue);
	}

	/**
	 * Sets the background <code>Color</code> of the
	 * <code>DateChooser's</code> selected area.
	 */
	public void setSelectedBackground(Color newSelectedBackground) {
		setProperty(PROPERTY_SELECTED_BACKGROUND, newSelectedBackground);
	}

	/**
	 * Sets the <code>Border</code> in the <code>DateChooser's</code>
	 * selected area.
	 */
	public void setSelectedBorder(Border newValue) {
		setProperty(PROPERTY_SELECTED_BORDER, newValue);
	}

	/**
	 * Sets the currently selected date within the <code>DateChooser</code>
	 */
	public void setSelectedDate(Calendar newSelectedDate) {
		getModel().setSelectedDate(newSelectedDate);
	}

	/**
	 * Sets the <code>Font</code> of the <code>DateChooser's</code> selected
	 * area.
	 */
	public void setSelectedFont(Font newSelectedFont) {
		setProperty(PROPERTY_SELECTED_FONT, newSelectedFont);
	}

	/**
	 * Sets the foreground <code>Color</code> of the
	 * <code>DateChooser's</code> selected area.
	 */
	public void setSelectedForeground(Color newSelectedForeground) {
		setProperty(PROPERTY_SELECTED_FOREGROUND, newSelectedForeground);
	}

	/**
	 * Sets the rollover background <code>Color</code> of the
	 * <code>DateChooser's</code> selected area.
	 */
	public void setSelectedRolloverBackground(Color newSelectedRolloverBackground) {
		setProperty(PROPERTY_SELECTED_ROLLOVER_BACKGROUND, newSelectedRolloverBackground);
	}

	/**
	 * Sets the rollover <code>Border</code> in the <code>DateChooser's</code>
	 * selected area.
	 */
	public void setSelectedRolloverBorder(Border newValue) {
		setProperty(PROPERTY_SELECTED_ROLLOVER_BORDER, newValue);
	}

	/**
	 * Sets the rollover foreground <code>Color</code> of the
	 * <code>DateChooser's</code> selected area.
	 */
	public void setSelectedRolloverForeground(Color newSelectedRolloverForeground) {
		setProperty(PROPERTY_SELECTED_ROLLOVER_FOREGROUND, newSelectedRolloverForeground);
	}

	/**
	 * Sets whether the SelectionDisplayBar is visible
	 */
	public void setSelectionDisplayBarVisible(boolean newValue) {
		setProperty(PROPERTY_SELECTION_DISPLAYBAR_VISIBLE, newValue);
	}

	/**
	 * Sets weekNumberAbbreviation
	 */
	public void setWeekNumberAbbreviation(String abbreviation) {
		setProperty(PROPERTY_WEEK_NUMBER_ABBREVIATION, abbreviation);
	}

	/**
	 * Returns the number of years that will appear in the selectable Year
	 * field.
	 */
	public void setYearRange(int newYearRange) {
		setProperty(PROPERTY_YEAR_RANGE, newYearRange);
	}

	/**
	 * Sets whether the Year field is selectable by the user.
	 */
	public void setYearSelectable(boolean newYearSelectable) {
		setProperty(PROPERTY_YEAR_SELECTABLE, newYearSelectable);
	}

	/**
	 * @return the foreground color of the Week Number area
	 */
	public Color getWeekNumberForeground() {
		return (Color) getProperty(PROPERTY_WEEK_NUMBER_FOREGROUND);
	}

	/**
	 * Sets the foreground color of the Week Number area
	 * 
	 * @param newValue -
	 *            the new color
	 */
	public void setWeekNumberForeground(Color newValue) {
		setProperty(PROPERTY_WEEK_NUMBER_FOREGROUND, newValue);
	}

}
