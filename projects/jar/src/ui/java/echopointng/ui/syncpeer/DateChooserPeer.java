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
package echopointng.ui.syncpeer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.util.DomUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.DateChooser;
import echopointng.EPNG;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssRolloverStyleEx;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.LayoutStrut;
import echopointng.ui.util.RenderingContext;
import echopointng.util.FontKit;

/**
 * <code>DateChooserPeer</code>
 */
public class DateChooserPeer extends AbstractEchoPointPeer implements ActionProcessor, PropertyUpdateProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service DATECHOOSER_SERVICE = JavaScriptService.forResource("EPNG.DateChooser", "/echopointng/ui/resource/js/datechooser.js");
	static {
		WebRenderServlet.getServiceRegistry().add(DATECHOOSER_SERVICE);
	}

	public DateChooserPeer() {
		// displayed date handler
		partialUpdateManager.add("displayedDate", new PartialUpdateParticipant() {

			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
						"EPDateChooser.MessageProcessor", "datesChanged", new String[0], new String[0]);

				Element itemElement = rc.getServerMessage().getDocument().createElement("item");
				itemizedUpdateElement.appendChild(itemElement);

				DateChooser dc = (DateChooser) update.getParent();
				Calendar cal = dc.getDisplayedDate();
				itemElement.setAttribute("eid", ContainerInstance.getElementId(dc));
				if (cal != null) {
					itemElement.setAttribute("displayedDate", toYearMonthDayStr(cal));
				}

			}

			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}
		});
		// selected date handler
		partialUpdateManager.add("selectedDate", new PartialUpdateParticipant() {

			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
						"EPDateChooser.MessageProcessor", "datesChanged", new String[0], new String[0]);

				Element itemElement = rc.getServerMessage().getDocument().createElement("item");
				itemizedUpdateElement.appendChild(itemElement);

				DateChooser dc = (DateChooser) update.getParent();
				Calendar cal = dc.getSelectedDate();
				itemElement.setAttribute("eid", ContainerInstance.getElementId(dc));
				if (cal != null) {
					itemElement.setAttribute("selectedDate", toYearMonthDayStr(cal));
				}

			}

			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}
		});

	}

	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		String name = actionElement.getAttribute(ActionProcessor.ACTION_NAME);
		String value = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
		ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, name, value);
	}

	/**
	 * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
		String name = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
		if ("displayedDate".equals(name)) {
			String value = DomUtil.getElementText(propertyElement);
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, name, value);
		}
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		Element directiveXML = createInitDirectiveItem(rc, component, fallbackStyle);
		createInitDirective(rc, component, directiveXML);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(DATECHOOSER_SERVICE);

		CssStyleEx style = new CssStyleEx(component, fallbackStyle);

		// outside inner table
		Element calTABLE = rc.createE("table");
		calTABLE.setAttribute("id", rc.getElementId());
		rc.addStandardWebSupport(calTABLE);

		calTABLE.setAttribute("border", "0");
		calTABLE.setAttribute("cellpadding", "0");
		calTABLE.setAttribute("cellspacing", "0");
		calTABLE.setAttribute("style", style.renderInline());

		Element tbody = rc.createE("tbody");
		calTABLE.appendChild(tbody);

		Element calTR;
		Element calTD;
		// /////////////////////////////////////////////
		// nav bar row
		// ////////////////////////////////////////////
		calTR = rc.createE("tr");
		tbody.appendChild(calTR);

		calTD = renderNavBar(rc, component, fallbackStyle, directiveXML);
		calTR.appendChild(calTD);

		// /////////////////////////////////////////////
		// calendar rows
		// /////////////////////////////////////////////

		calTR = rc.createE("tr");
		tbody.appendChild(calTR);

		calTD = renderCalendarBar(component, rc, fallbackStyle, directiveXML);
		calTR.appendChild(calTD);

		// /////////////////////////////////////////////
		// selection bar row
		// /////////////////////////////////////////////

		if (rc.getRP(DateChooser.PROPERTY_SELECTION_DISPLAYBAR_VISIBLE, fallbackStyle, true)) {
			calTR = rc.createE("tr");
			tbody.appendChild(calTR);

			calTD = renderSelectionBar(component, rc, fallbackStyle, directiveXML);
			calTR.appendChild(calTD);
		}

		// /////////////////////////////////////////////
		// finally add the outer most element to the stream
		// /////////////////////////////////////////////
		parent.appendChild(calTABLE);
	}

	protected void createInitDirective(RenderingContext rc, Component component, Element itemXML) {
		ServerMessage serverMessage = rc.getServerMessage();

		// Create the element containing initialisation parameters for the
		// ComboBox
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPDateChooser.MessageProcessor",
				"init", new String[0], new String[0]);

		itemizedUpdateElement.appendChild(itemXML);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		return super.renderUpdate(rc, update, targetId);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(DATECHOOSER_SERVICE.getId());
		createDisposeDirective(rc.getServerMessage(), component);
	}

	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPDateChooser.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}

	protected Element createInitDirectiveItem(RenderingContext rc, Component component, Style fallbackStyle) {
		String elementId = rc.getElementId();
		Element itemElement = rc.getDocument().createElement("item");
		itemElement.setAttribute("eid", elementId);
		itemElement.setAttribute("enabled", String.valueOf(component.isRenderEnabled()));
		itemElement.setAttribute("fastMode", String.valueOf(rc.getRP(DateChooser.PROPERTY_FAST_MODE, fallbackStyle, true)));
		itemElement.setAttribute("yearRange", String.valueOf(rc.getRP(DateChooser.PROPERTY_YEAR_RANGE, fallbackStyle, 30)));
		itemElement.setAttribute("navigationInhibited", String.valueOf(rc.getRP(DateChooser.PROPERTY_NAVIGATION_INHIBITED, fallbackStyle, false)));


		DateChooser chooser = (DateChooser) component;
		Calendar cal = chooser.getSelectedDate();
		if (cal != null) {
			itemElement.setAttribute("selectedDate", toYearMonthDayStr(cal));
		}
		cal = getDisplayedDate(chooser);
		if (cal != null) {
			itemElement.setAttribute("displayedDate", toYearMonthDayStr(cal));
		}
		
		cal = (cal != null) ? cal : Calendar.getInstance(component.getLocale());
		int firstDayOfWeek = cal.getFirstDayOfWeek();
		itemElement.setAttribute("firstDayOfWeek", String.valueOf(rc.getRP(DateChooser.PROPERTY_FIRST_DAY_OF_WEEK, fallbackStyle, firstDayOfWeek)));
		
		cal = chooser.getMaximumDate();
		if (cal != null) {
			itemElement.setAttribute("maximumDate", toYearMonthDayStr(cal));
		}
		cal = chooser.getMinimumDate();
		if (cal != null) {
			itemElement.setAttribute("minimumDate", toYearMonthDayStr(cal));
		}

		StringBuffer monthNames = new StringBuffer();
		String monthNamesArr[] = createMonthNames(rc, component);
		for (int i = 0; i < monthNamesArr.length; i++) {
			monthNames.append(monthNamesArr[i]);
			if (i != monthNamesArr.length - 1) {
				monthNames.append("|");
			}
		}
		itemElement.setAttribute("monthNames", String.valueOf(monthNames));

		// -----------------------------
		// Styles needed
		// -----------------------------

		// style for in month text
		CssRolloverStyleEx styleInMonth = new CssRolloverStyleEx();
		styleInMonth.setAttribute("text-align", "center");
		styleInMonth.setBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_BACKGROUND, fallbackStyle));
		styleInMonth.setForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_FOREGROUND, fallbackStyle));
		styleInMonth.setFont((Font) rc.getRP(DateChooser.PROPERTY_CALENDAR_FONT, fallbackStyle));
		styleInMonth.setBorder((Border) rc.getRP(DateChooser.PROPERTY_CALENDAR_BORDER, fallbackStyle));

		styleInMonth.setRolloverBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_ROLLOVER_BACKGROUND, fallbackStyle));
		styleInMonth.setRolloverForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_ROLLOVER_FOREGROUND, fallbackStyle));
		styleInMonth.setRolloverBorder((Border) rc.getRP(DateChooser.PROPERTY_CALENDAR_ROLLOVER_BORDER, fallbackStyle));

		itemElement.setAttribute("styleInMonth", styleInMonth.renderInline());
		itemElement.setAttribute("styleInMonthRollover", styleInMonth.renderRolloverSupportInline());

		// style for out of month text
		CssRolloverStyleEx styleOutOfMonth = new CssRolloverStyleEx();
		styleOutOfMonth.setAttribute("text-align", "center");
		styleOutOfMonth.setBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_BACKGROUND, fallbackStyle));
		styleOutOfMonth.setForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_NON_MONTH_FOREGROUND, fallbackStyle));
		styleOutOfMonth.setFont((Font) rc.getRP(DateChooser.PROPERTY_CALENDAR_FONT, fallbackStyle));
		styleOutOfMonth.setBorder((Border) rc.getRP(DateChooser.PROPERTY_CALENDAR_BORDER, fallbackStyle));

		styleOutOfMonth.setRolloverBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_ROLLOVER_BACKGROUND, fallbackStyle));
		styleOutOfMonth.setRolloverForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_ROLLOVER_FOREGROUND, fallbackStyle));
		styleOutOfMonth.setRolloverBorder((Border) rc.getRP(DateChooser.PROPERTY_CALENDAR_ROLLOVER_BORDER, fallbackStyle));

		itemElement.setAttribute("styleOutOfMonth", styleOutOfMonth.renderInline());
		itemElement.setAttribute("styleOutOfMonthRollover", styleOutOfMonth.renderRolloverSupportInline());

		// style for non selectable dates
		CssRolloverStyleEx styleNotSelectable = new CssRolloverStyleEx();
		styleNotSelectable.setAttribute("text-align", "center");
		styleNotSelectable.setAttribute("cursor", "not-allowed");
		styleNotSelectable.setBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_BACKGROUND, fallbackStyle));
		styleNotSelectable.setForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_NON_MONTH_FOREGROUND, fallbackStyle));
		styleNotSelectable.setFont((Font) rc.getRP(DateChooser.PROPERTY_CALENDAR_FONT, fallbackStyle));
		styleNotSelectable.setBorder((Border) rc.getRP(DateChooser.PROPERTY_CALENDAR_BORDER, fallbackStyle));

		itemElement.setAttribute("styleNotSelectable", styleNotSelectable.renderInline());
		itemElement.setAttribute("styleNotSelectableRollover", styleNotSelectable.renderRolloverSupportInline());

		// style for selected day
		CssRolloverStyleEx styleSelectedDate = new CssRolloverStyleEx();
		styleSelectedDate.setAttribute("text-align", "center");
		styleSelectedDate.setBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_SELECTED_BACKGROUND, fallbackStyle));
		styleSelectedDate.setForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_SELECTED_FOREGROUND, fallbackStyle));
		styleSelectedDate.setFont((Font) rc.getRP(DateChooser.PROPERTY_CALENDAR_SELECTED_FONT, fallbackStyle));
		styleSelectedDate.setBorder((Border) rc.getRP(DateChooser.PROPERTY_CALENDAR_SELECTED_BORDER, fallbackStyle));

		styleSelectedDate.setRolloverBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_ROLLOVER_BACKGROUND, fallbackStyle));
		styleSelectedDate.setRolloverForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_ROLLOVER_FOREGROUND, fallbackStyle));
		styleSelectedDate.setBorder((Border) rc.getRP(DateChooser.PROPERTY_CALENDAR_ROLLOVER_BORDER, fallbackStyle));

		itemElement.setAttribute("styleSelectedDate", styleSelectedDate.renderInline());
		itemElement.setAttribute("styleSelectedDateRollover", styleSelectedDate.renderRolloverSupportInline());

		CssRolloverStyleEx styleSelection = new CssRolloverStyleEx();
		styleSelection.setAttribute("text-align", "center");
		styleSelection.setBackground((Color) rc.getRP(DateChooser.PROPERTY_SELECTED_BACKGROUND, fallbackStyle));
		styleSelection.setForeground((Color) rc.getRP(DateChooser.PROPERTY_SELECTED_FOREGROUND, fallbackStyle));
		styleSelection.setFont((Font) rc.getRP(DateChooser.PROPERTY_SELECTED_FONT, fallbackStyle));

		styleSelection.setRolloverBackground((Color) rc.getRP(DateChooser.PROPERTY_SELECTED_ROLLOVER_BACKGROUND, fallbackStyle));
		styleSelection.setRolloverForeground((Color) rc.getRP(DateChooser.PROPERTY_SELECTED_ROLLOVER_FOREGROUND, fallbackStyle));

		itemElement.setAttribute("styleSelection", styleSelection.renderInline());
		itemElement.setAttribute("styleSelectionRollover", styleSelection.renderRolloverSupportInline());

		return itemElement;
	}

	private String toYearMonthDayStr(Calendar cal) {
		if (cal != null) {
			String s = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
			return s;
		} else {
			return null;
		}
	}

	/**
	 * Renders the calendar portion into a TD and returns it.
	 */
	private Element renderCalendarBar(Component component, RenderingContext rc, Style fallbackStyle, Element directiveXML) {

		String alignment = "center";

		Element calOutsideTD = rc.createE("td");

		CssStyleEx style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_BACKGROUND, fallbackStyle));
		style.setForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_FOREGROUND, fallbackStyle));
		style.setFont((Font) rc.getRP(DateChooser.PROPERTY_CALENDAR_FONT, fallbackStyle));
		style.setAttribute("text-align", alignment);
		style.setAttribute("width", "100%");
		calOutsideTD.setAttribute("style", style.renderInline());

		// style for day headers text
		style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_BACKGROUND, fallbackStyle));
		style.setForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_FOREGROUND, fallbackStyle));
		style.setFont(FontKit.addBold((Font) rc.getRP(DateChooser.PROPERTY_CALENDAR_FONT, fallbackStyle)));
		style.setAttribute("text-align", alignment);
		style.setAttribute("cursor", "auto");
		String calStyleDayHeadersName = style.renderInline();

		// style for weekOfYear headers text
		style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_BACKGROUND, fallbackStyle));
		style.setForeground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_FOREGROUND, fallbackStyle));
		style.setFont(FontKit.addBold((Font) rc.getRP(DateChooser.PROPERTY_CALENDAR_FONT, fallbackStyle)));
		style.setAttribute("text-align", alignment);
		style.setAttribute("cursor", "auto");
		String calStyleWeekNumberHeadersName = style.renderInline();

		// style for weekOfYear text
		style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(DateChooser.PROPERTY_CALENDAR_BACKGROUND, fallbackStyle));
		style.setForeground((Color) rc.getRP(DateChooser.PROPERTY_WEEK_NUMBER_FOREGROUND, fallbackStyle));
		style.setFont(FontKit.addBold((Font) rc.getRP(DateChooser.PROPERTY_CALENDAR_FONT, fallbackStyle)));
		style.setAttribute("text-align", alignment);
		style.setAttribute("cursor", "auto");
		String calStyleWeekNumberName = style.renderInline();


		DateChooser dateChooser = (DateChooser) component;

		Calendar maxCal = (Calendar) rc.getRP(DateChooser.PROPERTY_MAXIMUM_DATE);
		Calendar minCal = (Calendar) rc.getRP(DateChooser.PROPERTY_MINIMUM_DATE);

		Calendar selectedDate = dateChooser.getSelectedDate();

		//
		// manipulate the calendar
		Calendar displayedDate = getDisplayedDate(dateChooser);
		Calendar rollingDisplayedDate = DateChooser.calendarCopy(displayedDate);
		rollingDisplayedDate.setTime(displayedDate.getTime());
		rollingDisplayedDate.setTimeZone(displayedDate.getTimeZone());
		
		// what day do we start out week on
		int firstDayOfWeek = displayedDate.getFirstDayOfWeek();
		firstDayOfWeek = rc.getRP(DateChooser.PROPERTY_FIRST_DAY_OF_WEEK, fallbackStyle, firstDayOfWeek);
		int prevMonthDays = 0;

		// build our calendar table
		Element calTABLE = rc.createE("table");
		calOutsideTD.appendChild(calTABLE);
		calTABLE.setAttribute("border", "0");
		calTABLE.setAttribute("width", "100%");
		calTABLE.setAttribute("cellpadding", "1");
		calTABLE.setAttribute("cellspacing", "1");

		Element tbody = rc.createE("tbody");
		calTABLE.appendChild(tbody);
		
		// add the days names first
		Element calTR = rc.createE("tr");
		tbody.appendChild(calTR);

		// roll to the start of the month and the first day of the week before
		// that
		rollingDisplayedDate.set(Calendar.DATE, 1);
		int changingDOW = rollingDisplayedDate.get(Calendar.DAY_OF_WEEK);
		while (changingDOW != firstDayOfWeek) {
			rollingDisplayedDate.add(Calendar.DATE, -1);
			changingDOW = rollingDisplayedDate.get(Calendar.DAY_OF_WEEK);
			prevMonthDays++;
		}

		Locale locale = component.getLocale() == null ? Locale.getDefault() : component.getLocale();
		SimpleDateFormat df = new SimpleDateFormat("EEE", locale);

		String dowName = null;
		String weekNumberAbbreviation = (String) rc.getRP(DateChooser.PROPERTY_WEEK_NUMBER_ABBREVIATION, fallbackStyle);
		boolean hasWeekNumber = (weekNumberAbbreviation != null);
		if (hasWeekNumber) {
			createTextTD(weekNumberAbbreviation, calTR, calStyleWeekNumberHeadersName);
		}

		int dowThatStartsCal = rollingDisplayedDate.get(Calendar.DAY_OF_WEEK);
		Calendar dayNamesCal = Calendar.getInstance(locale);
		dayNamesCal.set(Calendar.DAY_OF_WEEK, dowThatStartsCal);

		dowName = createDowName(rc, component, df.format(dayNamesCal.getTime()));
		createTextTD(dowName, calTR, calStyleDayHeadersName);
		dayNamesCal.add(Calendar.DAY_OF_WEEK, 1);

		dowName = createDowName(rc, component, df.format(dayNamesCal.getTime()));
		createTextTD(dowName, calTR, calStyleDayHeadersName);
		dayNamesCal.add(Calendar.DAY_OF_WEEK, 1);

		dowName = createDowName(rc, component, df.format(dayNamesCal.getTime()));
		createTextTD(dowName, calTR, calStyleDayHeadersName);
		dayNamesCal.add(Calendar.DAY_OF_WEEK, 1);

		dowName = createDowName(rc, component, df.format(dayNamesCal.getTime()));
		createTextTD(dowName, calTR, calStyleDayHeadersName);
		dayNamesCal.add(Calendar.DAY_OF_WEEK, 1);

		dowName = createDowName(rc, component, df.format(dayNamesCal.getTime()));
		createTextTD(dowName, calTR, calStyleDayHeadersName);
		dayNamesCal.add(Calendar.DAY_OF_WEEK, 1);

		dowName = createDowName(rc, component, df.format(dayNamesCal.getTime()));
		createTextTD(dowName, calTR, calStyleDayHeadersName);
		dayNamesCal.add(Calendar.DAY_OF_WEEK, 1);

		dowName = createDowName(rc, component, df.format(dayNamesCal.getTime()));
		createTextTD(dowName, calTR, calStyleDayHeadersName);
		dayNamesCal.add(Calendar.DAY_OF_WEEK, 1);

		Element td;
		for (int row = 0; row < 6; row++) {
			calTR = rc.createE("tr");
			tbody.appendChild(calTR);

			if (hasWeekNumber) {
				Calendar woyDate = DateChooser.calendarCopy(rollingDisplayedDate, locale);
				if (row == 0) {
					// woyDate.set(Calendar.DAY_OF_MONTH,1);
				}
				td = createTextTD(String.valueOf(woyDate.get(Calendar.WEEK_OF_YEAR)), calTR, calStyleWeekNumberName);
				td.setAttribute("id", rc.getElementId() + "|" + row + "|woy");
			}

			for (int col = 0; col < 7; col++) {

				boolean isSelectable = true;
				if (!isDateInRange(rollingDisplayedDate, minCal, maxCal)) {
					isSelectable = false;
				}

				boolean inSameMonth = rollingDisplayedDate.get(Calendar.MONTH) == displayedDate.get(Calendar.MONTH);
				// which font should we use
				String styleToUse = directiveXML.getAttribute("styleInMonth");
				if (!inSameMonth) {
					styleToUse = directiveXML.getAttribute("styleOutOfMonth");
				}
				// bold it if its our selected date
				if (selectedDate != null && rollingDisplayedDate.getTime().equals(selectedDate.getTime())) {
					styleToUse = directiveXML.getAttribute("styleSelectedDate");
				}
				if (!isSelectable) {
					styleToUse = directiveXML.getAttribute("styleNotSelectable");
				}

				String dofm = String.valueOf(rollingDisplayedDate.get(Calendar.DAY_OF_MONTH));
				td = createTextTD(dofm, calTR, styleToUse);
				td.setAttribute("id", rc.getElementId() + "|" + row + "|" + col);

				// and bump to the next day
				rollingDisplayedDate.add(Calendar.DATE, 1);
			}
		}
		return calOutsideTD;
	}

	private boolean isDateInRange(Calendar cal, Calendar minCal, Calendar maxCal) {
		boolean ok = true;
		if (maxCal != null) {
			if (cal.after(maxCal)) {
				return false;
			}
		}
		if (minCal != null) {
			if (cal.before(minCal)) {
				return false;
			}
		}
		return ok;
	}

	/**
	 * Renders the selectable month combo box
	 */
	private void renderMonth(RenderingContext rc, Component component, Element parent, Style fallbackStyle, Calendar displayedDate) {
		Locale locale = component.getLocale() == null ? Locale.getDefault() : component.getLocale();
		boolean isMonthSelectable = rc.getRP(DateChooser.PROPERTY_MONTH_SELECTABLE, fallbackStyle, true);

		CssStyleEx style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(DateChooser.PROPERTY_NAVIGATION_BACKGROUND, fallbackStyle));
		style.setForeground((Color) rc.getRP(DateChooser.PROPERTY_NAVIGATION_FOREGROUND, fallbackStyle));
		style.setFont((Font) rc.getRP(DateChooser.PROPERTY_NAVIGATION_FONT, fallbackStyle));
		String styleName = style.renderInline();

		Element select = rc.createE("select");
		if (!isMonthSelectable) {
			select = rc.createE("span");
			String monthName = new SimpleDateFormat("MMMM", locale).format(displayedDate.getTime());
			select.appendChild(rc.createText(createMonthName(rc, monthName)));
		}
		parent.appendChild(select);
		select.setAttribute("style", styleName);

		String id = rc.getElementId() + "|month";
		select.setAttribute("id", id);
		if (!component.isRenderEnabled()) {
			select.setAttribute("disabled", "disabled");
		}
	}

	/**
	 * Renders the selectable year combo box
	 */
	private void renderYear(RenderingContext rc, Component component, Element parent, Style fallbackStyle, Calendar displayedDate) {
		Locale locale = component.getLocale() == null ? Locale.getDefault() : component.getLocale();
		boolean isYearSelectable = rc.getRP(DateChooser.PROPERTY_YEAR_SELECTABLE, fallbackStyle, false);

		CssStyleEx style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(DateChooser.PROPERTY_NAVIGATION_BACKGROUND, fallbackStyle));
		style.setForeground((Color) rc.getRP(DateChooser.PROPERTY_NAVIGATION_FOREGROUND, fallbackStyle));
		style.setFont((Font) rc.getRP(DateChooser.PROPERTY_NAVIGATION_FONT, fallbackStyle));
		String styleName = style.renderInline();

		Element select = rc.createE("select");
		if (!isYearSelectable) {
			select = rc.createE("span");
			String yearName = new SimpleDateFormat("yyyy", locale).format(displayedDate.getTime());
			select.appendChild(rc.createText(yearName));
		}
		parent.appendChild(select);
		select.setAttribute("style", styleName);

		String id = rc.getElementId() + "|year";
		select.setAttribute("id", id);

		if (!component.isRenderEnabled()) {
			select.setAttribute("disabled", "disabled");
		}
	}

	/**
	 * Renders the nav bar of the DateChooser inside a TD element and returns
	 * that TD.
	 */
	private Element renderNavBar(RenderingContext rc, Component component, Style fallbackStyle, Element directiveXML) {
		DateChooser dateChooser = (DateChooser) component;

		Calendar displayedDate = getDisplayedDate(dateChooser);

		CssStyleEx style;

		ImageReference icon;

		CssRolloverStyleEx styleNavArrows = new CssRolloverStyleEx();
		// styleNavArrows.setBackground((Color)
		// rc.getRP(DateChooser.PROPERTY_NAVIGATION_BACKGROUND));
		// styleNavArrows.setForeground((Color)
		// rc.getRP(DateChooser.PROPERTY_NAVIGATION_FOREGROUND));
		// styleNavArrows.setFont((Font)
		// rc.getRP(DateChooser.PROPERTY_NAVIGATION_FONT));
		//
		// styleNavArrows.setRolloverBackground((Color)
		// rc.getRP(DateChooser.PROPERTY_NAVIGATION_ROLLOVER_BACKGROUND));
		// styleNavArrows.setRolloverForeground((Color)
		// rc.getRP(DateChooser.PROPERTY_NAVIGATION_ROLLOVER_FOREGROUND));

		icon = (ImageReference) rc.getRP(DateChooser.PROPERTY_NAVIGATION_BACKWARD_IMAGE, fallbackStyle);
		Element bwdImgE = ImageManager.createImgE(rc, styleNavArrows, icon);
		bwdImgE.setAttribute("id", rc.getElementId() + "|bwd");

		icon = (ImageReference) rc.getRP(DateChooser.PROPERTY_NAVIGATION_FORWARD_IMAGE, fallbackStyle);
		Element fwdImgE = ImageManager.createImgE(rc, styleNavArrows, icon);
		fwdImgE.setAttribute("id", rc.getElementId() + "|fwd");

		// outside TD style
		style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(DateChooser.PROPERTY_NAVIGATION_BACKGROUND, fallbackStyle));
		style.setForeground((Color) rc.getRP(DateChooser.PROPERTY_NAVIGATION_FOREGROUND, fallbackStyle));
		style.setFont((Font) rc.getRP(DateChooser.PROPERTY_NAVIGATION_FONT, fallbackStyle));
		style.setNoWrap();
		String styleName = style.renderInline();

		// outside TD
		Element navOutsideTD = rc.createE("td");
		navOutsideTD.setAttribute("style", styleName);

		Element navTABLE = rc.createE("table");
		navOutsideTD.appendChild(navTABLE);
		navTABLE.setAttribute("border", "0");
		navTABLE.setAttribute("cellpadding", "3");
		navTABLE.setAttribute("cellspacing", "1");

		Element tbody = rc.createE("tbody");
		navTABLE.appendChild(tbody);

		Element navTR = rc.createE("tr");
		tbody.appendChild(navTR);

		Element navTD = rc.createE("td");
		navTR.appendChild(navTD);
		navTD.setAttribute("align", "left");
		navTD.setAttribute("style", styleName);

		if (rc.getRP(DateChooser.PROPERTY_NAVIGATION_ARROWS_VISIBLE, fallbackStyle, true)) {
			navTD.appendChild(bwdImgE);
		}

		navTD = rc.createE("td");
		navTR.appendChild(navTD);
		navTD.setAttribute("align", "center");
		navTD.setAttribute("width", "100%");
		navTD.setAttribute("style", styleName);

		// will there be a combo box or not
		renderMonth(rc, component, navTD, fallbackStyle, displayedDate);

		navTD.appendChild(LayoutStrut.createStrut(rc, 10, 1));

		renderYear(rc, component, navTD, fallbackStyle, displayedDate);

		navTD = rc.createE("td");
		navTR.appendChild(navTD);
		navTD.setAttribute("align", "right");
		navTD.setAttribute("style", styleName);

		if (rc.getRP(DateChooser.PROPERTY_NAVIGATION_ARROWS_VISIBLE, fallbackStyle, true)) {
			navTD.appendChild(fwdImgE);
		}

		// outside element
		return navOutsideTD;
	}

	/**
	 * Renders the selection bar of the DateChooser inside a TD and returns ID
	 */
	private Element renderSelectionBar(Component component, RenderingContext rc, Style fallbackStyle, Element directiveXML) {
		String styleName = directiveXML.getAttribute("styleSelection");

		Element selOutsideTD = rc.createE("td");
		selOutsideTD.setAttribute("align", "center");
		selOutsideTD.setAttribute("style", styleName);

		Element selTABLE = rc.createE("table");
		selOutsideTD.appendChild(selTABLE);
		selTABLE.setAttribute("border", "0");
		selTABLE.setAttribute("cellpadding", "3");
		selTABLE.setAttribute("cellspacing", "1");
		selTABLE.setAttribute("style", "width:100%");

		Element tbody = rc.createE("tbody");
		selTABLE.appendChild(tbody);

		Element selTR = rc.createE("tr");
		tbody.appendChild(selTR);

		Element selTD = rc.createE("td");
		selTR.appendChild(selTD);
		selTD.setAttribute("align", "center");
		selTD.setAttribute("style", styleName);
		selTD.setAttribute("id", rc.getElementId() + "|sel");

		DateChooser dateChooser = (DateChooser) component;
		Calendar selectedDate = dateChooser.getSelectedDate();
		if (selectedDate != null) {
			Locale locale = component.getLocale();
			if (locale == null) {
				locale = Locale.getDefault();
			}
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
			String dtStr = df.format(selectedDate.getTime());
			selTD.appendChild(rc.createText(dtStr));
		}

		// outside element
		return selOutsideTD;
	}

	/**
	 * Shortens a day of the week name down to the specified length
	 * 
	 * @return the newly created dow name
	 */
	private String createDowName(RenderingContext rc, Component component, String string) {
		int maxLen = rc.getRP(DateChooser.PROPERTY_DOW_NAME_LENGTH, 3);
		if (string == null || string.length() <= maxLen)
			return string;

		string = string.substring(0, maxLen);
		return string;
	}

	/**
	 * Creates a TD with the given text style and adds it to the TR
	 */
	private Element createTextTD(String text, Element tr, String styleName) {
		Element calTD = tr.getOwnerDocument().createElement("td");
		tr.appendChild(calTD);
		calTD.appendChild(tr.getOwnerDocument().createTextNode(text));
		calTD.setAttribute("style", styleName);
		return calTD;
	}

	/**
	 * Shortens a month name down to the specified length
	 */
	private String createMonthName(RenderingContext rc, String string) {
		int maxLen = rc.getRP(DateChooser.PROPERTY_MONTH_NAME_LENGTH, 3);
		if (string == null || string.length() <= maxLen)
			return string;

		string = string.substring(0, maxLen);
		return string;
	}

	private String[] createMonthNames(RenderingContext rc, Component component) {
		String monthNames[] = new String[Calendar.DECEMBER + 1];
		Locale locale = component.getLocale() == null ? Locale.getDefault() : component.getLocale();
		Calendar cal = Calendar.getInstance(locale);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++) {
			cal.set(Calendar.MONTH, month);
			String monthName = new SimpleDateFormat("MMMM", locale).format(cal.getTime());
			monthNames[month] = createMonthName(rc, monthName);
		}
		return monthNames;

	}

	/**
	 * Returns a copy/clone of the selected date using standard semantics.
	 */
	protected Calendar getDisplayedDate(DateChooser dateChooser) {
		Locale locale = dateChooser.getLocale();
		if (locale == null) {
			locale = Locale.getDefault();
		}
		Calendar displayedDate = dateChooser.getDisplayedDate();
		if (displayedDate == null) {
			displayedDate = Calendar.getInstance(locale);
		}
		return DateChooser.calendarCopy(displayedDate, locale);
	}

}
