/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.tasks.core.RepositoryTemplate;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Settings page for generic web-based repository connector
 * 
 * @author Eugene Kuleshov
 */
public class WebRepositorySettingsPage extends AbstractRepositorySettingsPage implements IPropertyChangeListener {
	
	private static final String TITLE = "Web Repository Settings";

	private static final String DESCRIPTION = "Generic web-based repository connector";

	private Text taskUrlText;
	private Text newTaskText;
	private Text queryUrlText;
	private Text queryPatternText;

	private ParametersEditor parametersEditor;
	
	private FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	private Map<String, String> oldProperties;

	
	public WebRepositorySettingsPage(AbstractRepositoryConnectorUi repositoryUi) {
		super(TITLE, DESCRIPTION, repositoryUi);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		for (RepositoryTemplate template : connector.getTemplates()) {
			serverUrlCombo.add(template.label);
		}		
		
		serverUrlCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				String text = serverUrlCombo.getText();
				RepositoryTemplate template = connector.getTemplate(text);
				if(template != null) {
					repositoryLabelEditor.setStringValue(template.label);
					setUrl(template.repositoryUrl);
					taskUrlText.setText(template.taskPrefixUrl);
					newTaskText.setText(template.newTaskUrl);
					queryUrlText.setText(template.taskQueryUrl);
					queryPatternText.setText(template.getAttribute(WebRepositoryConnector.PROPERTY_QUERY_REGEXP));

					parametersEditor.removeAll();
					
					for(Map.Entry<String, String> entry : template.getAttributes().entrySet()) {
						String key = entry.getKey();
						if(key.startsWith(WebRepositoryConnector.PARAM_PREFIX)) {
							parametersEditor.add(key.substring(WebRepositoryConnector.PARAM_PREFIX.length()), entry.getValue());
						}
					}
					
					getContainer().updateButtons();
					return; 
				}			
			}  

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore 
			}

		});

		Composite editor = getParameterEditor(parent);
		GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 240).grab(true, true).span(2, 1).applyTo(editor);
		
		if (repository != null) {
			taskUrlText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_TASK_URL));
			newTaskText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_TASK_CREATION_URL));
			queryUrlText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_QUERY_URL));
			queryPatternText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_QUERY_REGEXP));
			
			oldProperties = repository.getProperties();
			parametersEditor.addParams(oldProperties, new LinkedHashMap<String, String>());
		}
	}

	private String getTextProperty(String name) {
		String value = repository.getProperty(name);
		if(value==null) {
			return "";
		}
		return value;
	}

	protected boolean isValidUrl(String name) {
		return true;
	}

	protected void validateSettings() {
		// ignore
	}

	private Composite getParameterEditor(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginBottom = 10;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);

		parametersEditor = new ParametersEditor(composite, SWT.NONE);
		GridData gridData_1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData_1.heightHint = 80;
		gridData_1.minimumHeight = 80;
		parametersEditor.setLayoutData(gridData_1);

		ExpandableComposite expComposite = toolkit.createExpandableComposite(composite, Section.COMPACT | Section.TWISTIE | Section.TITLE_BAR);
		expComposite.clientVerticalSpacing = 0;
		GridData gridData_2 = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData_2.horizontalIndent = -5;
		expComposite.setLayoutData(gridData_2);
		expComposite.setFont(parent.getFont());
		expComposite.setBackground(parent.getBackground());
		expComposite.setText("Advanced &Configuration");
		expComposite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				composite.layout();
			}
		});
		toolkit.paintBordersFor(expComposite);

		Composite composite2 = toolkit.createComposite(expComposite, SWT.BORDER);
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		gridLayout2.verticalSpacing = 0;
		composite2.setLayout(gridLayout2);
		expComposite.setClient(composite2);

		Label taskUrlLabel = toolkit.createLabel(composite2, "&Task URL:", SWT.NONE);
		taskUrlLabel.setLayoutData(new GridData());

		taskUrlText = new Text(composite2, SWT.BORDER);
		taskUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label newTaskLabel = toolkit.createLabel(composite2, "&New Task URL:", SWT.NONE);
		newTaskLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		newTaskText = new Text(composite2, SWT.BORDER);
		newTaskText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label queryUrlLabel = toolkit.createLabel(composite2, "&Query URL:", SWT.NONE);
		queryUrlLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

		queryUrlText = new Text(composite2, SWT.BORDER);
		queryUrlText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label queryPatternLabel = toolkit.createLabel(composite2, "Query &Pattern:", SWT.NONE);
		queryPatternLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

		queryPatternText = new Text(composite2, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 40;
		queryPatternText.setLayoutData(gridData);

		return composite;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		Object source = event.getSource();
		if (source == taskUrlText || source == taskUrlText) {
			getWizard().getContainer().updateButtons();
		}
	}

	@Override
	public void updateProperties(TaskRepository repository) {
		repository.setProperty(WebRepositoryConnector.PROPERTY_TASK_URL, taskUrlText.getText());
		repository.setProperty(WebRepositoryConnector.PROPERTY_TASK_CREATION_URL, newTaskText.getText());
		repository.setProperty(WebRepositoryConnector.PROPERTY_QUERY_URL, queryUrlText.getText());
		repository.setProperty(WebRepositoryConnector.PROPERTY_QUERY_REGEXP, queryPatternText.getText());

		if(oldProperties!=null) {
			for(Map.Entry<String, String> e : oldProperties.entrySet()) {
				String key = e.getKey();
				if(key.startsWith(WebRepositoryConnector.PARAM_PREFIX)) {
					repository.removeProperty(key);
				}
			}
		}
		
		for(Map.Entry<String, String> e : parametersEditor.getParameters().entrySet()) {
			repository.setProperty(e.getKey(), e.getValue());
		}
	}

}

