/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Parameters editor table
 * 
 * @author Eugene Kuleshov
 */
public class ParametersEditor extends Composite {

	private Table paramsTable;
	
	public ParametersEditor(Composite parent, int style) {
		super(parent, style);
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		paramsTable = new Table(this, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		gridData1.minimumHeight = 70;
		paramsTable.setLayoutData(gridData1);
		paramsTable.setLinesVisible(true);
		paramsTable.setHeaderVisible(true);
		
		TableColumn colVariable = new TableColumn(paramsTable, SWT.NONE);
		colVariable.setWidth(100);
		colVariable.setText("Parameter");
		
		TableColumn colValue = new TableColumn(paramsTable, SWT.NONE);
		colValue.setWidth(219);
		colValue.setText("Value");

		Button bAdd = new Button(this, SWT.NONE);
		bAdd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		bAdd.setText("&Add...");
		bAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ParameterEditorDialog dlg = new ParameterEditorDialog(Display.getCurrent().getActiveShell());
				if(dlg.open()==Window.OK) {
					TableItem item = new TableItem(paramsTable, SWT.NONE);
					item.setText(new String[] { dlg.getName(), dlg.getValue() });
				}
			}
		});

		final Button bRemove = new Button(this, SWT.NONE);
		bRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		bRemove.setText("&Remove");
		bRemove.setEnabled(false);
		bRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] indices = paramsTable.getSelectionIndices();
				paramsTable.remove(indices);
			}
		});

		final Button bEdit = new Button(this, SWT.NONE);
		bEdit.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
		bEdit.setText("&Edit...");
		bEdit.setEnabled(false);
		bEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem item = paramsTable.getSelection()[0];
				ParameterEditorDialog dlg = new ParameterEditorDialog(Display.getCurrent().getActiveShell(), item.getText(0), item.getText(1));
				if(dlg.open()==Window.OK) {
					item.setText(0, dlg.getName());
					item.setText(1, dlg.getValue());
				}
			}
		});
		
		paramsTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bRemove.setEnabled(paramsTable.getSelectionCount()>0);
				bEdit.setEnabled(paramsTable.getSelectionCount()==1);
			}
		});
	}

	
	public void removeAll() {
		paramsTable.removeAll();
	}

	public void add(String name, String value) {
		TableItem item = new TableItem(paramsTable, SWT.NONE);
		item.setText(new String[] { name, value });
	}

	public Map<String, String> getParameters() {
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		for(TableItem item : paramsTable.getItems()) {
			parameters.put(WebRepositoryConnector.PARAM_PREFIX + item.getText(0), item.getText(1));
		}
		return parameters;
	}

	public void addParams(Map<String, String> props, LinkedHashMap<String, String> variables) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		for(Map.Entry<String, String> e : props.entrySet()) {
			String key = e.getKey();
			String value = e.getValue();
			if(key.startsWith(WebRepositoryConnector.PARAM_PREFIX)) {
				params.put(key.substring(WebRepositoryConnector.PARAM_PREFIX.length()), value);
			}
			for (String var : WebRepositoryConnector.getTemplateVariables(value)) {
				variables.put(var, "");
			}
		}
		
		variables.remove(WebRepositoryConnector.PARAM_SERVER_URL);
		variables.remove(WebRepositoryConnector.PARAM_USER_ID);
		variables.remove(WebRepositoryConnector.PARAM_PASSWORD);
		
		for (String var : variables.keySet()) {
			if(!params.containsKey(var)) {
				params.put(var, "");
			}
		}
		
		for(Map.Entry<String, String> e : params.entrySet()) {
			add(e.getKey(), e.getValue());
		}
	}
	
}

