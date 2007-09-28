/* 
 * This file is part of the Echo2 Table Extension (hereinafter "ETE").
 * Copyright (C) 2002-2005 NextApp, Inc.
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
package echopointng.table;

import java.util.Arrays;
import java.util.List;

import nextapp.echo2.app.Button;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

/**
 * A controller for tables containing 
 * <code>PageableTableModel</code> backed tables.
 * 
 * BUGBUG: Need to support internationalization
 * 
 * @author Jason Dalton
 * 
 */
public class PageableTableNavigation extends Row {

    private PageableTableModel model;
    private static final Object[] ROWS_PER_PAGE_OPTIONS = new String[]{"10","25","50","100"};
    private static final List ROWS_PER_PAGE_LIST = Arrays.asList(ROWS_PER_PAGE_OPTIONS);
    
    public PageableTableNavigation(Table table){
        this.model = (PageableTableModel)table.getModel();
    }
    
    /**
     * @see nextapp.echo2.app.Component#init()
     */
    public void init() {
        super.init();
        reset();
    }
    
    protected void doLayout() {
        setCellSpacing(new Extent(10));
        add(getPreviousButton());
        add(getResultsPerPageSelect());
        add(getPageLabel());
        add(getPageSelect());
        add(getPageCountLabel());
        add(getNextButton());
    }
    
    protected void reset() {
        removeAll();
        doLayout();
    }
    
    protected PageableTableModel getModel(){
        return model;
    }
    
    private SelectField getPageSelect() {
        String[] pages = new String[model.getTotalPages()];
        for (int i=0; i<pages.length; i++){
            pages[i] = "" + (i+1);
        }
        
        SelectField select = new SelectField(pages);
        select.setSelectedIndex(model.getCurrentPage());
        select.addActionListener(getPageSelectListener());        
        return select;
    }
    
    private ActionListener getPageSelectListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SelectField select = (SelectField)e.getSource();
                int selected = select.getSelectedIndex();
                model.setCurrentPage(selected);
                reset();
            }
        };
    }
    
    private Label getPageLabel() {
        Label label = new Label(" Page ");
        return label;
    }
    
    private Label getPageCountLabel() {
        Label label = new Label();
        label.setText(" of " + (model.getTotalPages()) + " ");
        setPageCountLabelText();
        return label;
    }
    
    private void setPageCountLabelText() {
    }
    
    private Button getPreviousButton(){
        Button previousButton = new Button(" < Previous ");
        previousButton.addActionListener(getPreviousListener());
        return previousButton;
    }
    
    private SelectField getResultsPerPageSelect(){
        SelectField resultsPerPage = new SelectField(ROWS_PER_PAGE_OPTIONS);
        resultsPerPage.addActionListener(getRowsPerPageListener());
        int index = ROWS_PER_PAGE_LIST.indexOf("" + model.getRowsPerPage());
        resultsPerPage.setSelectedIndex(index);
        return resultsPerPage;
    }
    
    private Button getNextButton(){
        Button previousButton = new Button(" Next > ");
        previousButton.addActionListener(getNextListener());
        return previousButton;
    }
    
    private ActionListener getPreviousListener(){
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getModel().getCurrentPage() > 0) {
                    getModel().setCurrentPage(getModel().getCurrentPage() - 1);
                    reset();
                }
            }
        };
    }
    
    private ActionListener getNextListener(){
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
             	int currentPage = getModel().getCurrentPage(); 
                int maxPage = getModel().getTotalPages();
                if (currentPage+1 < maxPage) {
                    getModel().setCurrentPage(currentPage + 1);
                    reset();
                }
            }
        };
    }
    
    private ActionListener getRowsPerPageListener(){
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SelectField select = (SelectField)e.getSource();
                Integer selected = new Integer((String)select.getSelectedItem());
                getModel().setRowsPerPage(selected.intValue());
                getModel().setCurrentPage(0);
                reset();
            }
        };
    }
    
}
