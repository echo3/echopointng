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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.table.DefaultTableColumnModel;
import nextapp.echo2.app.table.DefaultTableModel;
import nextapp.echo2.app.text.StringDocument;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.HistoryMonitor;
import echopointng.LabelEx;
import echopointng.TableEx;
import echopointng.history.HistoryEvent;
import echopointng.history.HistoryEventListener;
import echopointng.history.HistoryState;
import echopointng.history.HistoryUndoRedo;
import echopointng.table.TableColumnEx;
import echopointng.util.RandKit;

/**
 * <code>TestHistory</code>
 */
public class TestHistory extends TestCaseBaseNG {
	
	public String getTestCategory() {
		return "History";
	}
	
	private void updateHistoryTable(DefaultTableModel model, TableEx table, HistoryMonitor monitor) {
		for (int i = model.getRowCount(); i > 0; i--) {
			model.deleteRow(i - 1);
		}
		table.getSelectionModel().clearSelection();
		HistoryState currentHistory = monitor.getCurrent();
		List historyList = monitor.getHistory();
		int i = 0;
		for (Iterator iter = historyList.iterator(); iter.hasNext();) {
			HistoryState element = (HistoryState) iter.next();
			if (element.equals(currentHistory)) {
				table.getSelectionModel().setSelectedIndex(i, true);
			}
			model.addRow(new Object[] { new Integer(i), element.historyHash(), element });
			i++;
		}
	}
	
	public Component testHistoryMonitor() {

		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.addColumn(new TableColumnEx(0, "index"));
		columnModel.addColumn(new TableColumnEx(1, "historyHash"));
		columnModel.addColumn(new TableColumnEx(2, "toString()"));
		final DefaultTableModel historyTableModel = new DefaultTableModel(3, 0);
		final TableEx historyTable = new TableEx(historyTableModel, columnModel);
		historyTable.setSelectionEnabled(true);
		historyTable.setBorder(BorderEx.DEFAULT);

		final LabelEx explanation = new LabelEx("Each time you click the button, the application state (for the"
				+ " textfield anyway) will be saved and added as a HistoryUndoRedo object encapulating"
				+ " that state.  Then when you press the back and forward button on the browser these"
				+ " values will be retreived and re-instated to undo and redo application state.");
		explanation.setLineWrap(true);

		final Label eventLabel = new LabelEx();
		eventLabel.setForeground(Color.BLUE);

		final HistoryMonitor historyMonitor = new HistoryMonitor();
		final TextField textField1 = new TextField(new StringDocument(), "Initial value", 30);

		ButtonEx buttonUpdateState = new ButtonEx("Cause Application State Update To Occur!");
		buttonUpdateState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HistoryUndoRedo historyUndoRedo = new HistoryUndoRedo() {

					private String undoValue = textField1.getText();

					private String redoValue = genNewText();

					private String genNewText() {
						String newText = new SimpleDateFormat("hh:mm:ss").format(new Date());
						newText += " "
								+ RandKit.roll(new String[] { 
										"Roses", " Daffodils", "Hydranges", "Strezlixia", 
										"ButterCups", "Lillies", "Azalias",
										"Rodademdrems", "Banksias", "Warratahs" });
						return newText;
					}

					public String historyHash() {
						return String.valueOf(hashCode());
					}

					public void redo() {
						textField1.setText(redoValue);
					}

					public void undo() {
						textField1.setText(undoValue);
					}

					public String toString() {
						StringBuffer buf = new StringBuffer();
						buf.append("undo:[");
						buf.append(String.valueOf(undoValue));
						buf.append("] redo:[");
						buf.append(String.valueOf(redoValue));
						buf.append("]");
						return buf.toString();
					}
				};
				historyUndoRedo.redo();
				historyMonitor.addHistory(historyUndoRedo);

				updateHistoryTable(historyTableModel, historyTable, historyMonitor);
			}
		});

		historyMonitor.addHistoryEventListener(new HistoryEventListener() {
			public void onRedo(HistoryEvent historyEvent) {
				eventLabel.setText("History Event : Redo event occurred");
				updateHistoryTable(historyTableModel, historyTable, historyMonitor);
			}

			public void onUndo(HistoryEvent historyEvent) {
				eventLabel.setText("History Event : Undo event occurred");
				updateHistoryTable(historyTableModel, historyTable, historyMonitor);
			}

			public void onNoHistoryAvailable(HistoryEvent historyEvent) {
				eventLabel.setText("History Event : No history available in HistoryMonitor");
				updateHistoryTable(historyTableModel, historyTable, historyMonitor);
			}
		});

		Column cell = new Column();
		cell.add(historyMonitor);
		cell.add(explanation);
		cell.add(textField1);
		cell.add(buttonUpdateState);
		cell.add(eventLabel);
		cell.add(historyTable);
		return cell;
	}
	
}

