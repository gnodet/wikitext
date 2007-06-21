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

package org.eclipse.mylyn.internal.web.tasks;

import static org.eclipse.mylyn.internal.web.tasks.Util.isPresent;
import static org.eclipse.mylyn.internal.web.tasks.Util.nvl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
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

	private static final String DESCRIPTION = "Select a server template example and modify to match the settings for "
			+ "your project, \nusually found in the query URL.  For more connectors see http://eclipse.org/mylar";

	private ParametersEditor parametersEditor;

	private Text taskUrlText;

	private Text newTaskText;

	private Text queryUrlText;

	private ComboViewer queryRequestMethod;

	private Text queryPatternText;

	private Text loginFormUrlText;

	private Text loginTokenPatternText;

	private Text loginRequestUrlText;

	private ComboViewer loginRequestMethod;


	private FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	private Map<String, String> oldProperties;


	public WebRepositorySettingsPage(AbstractRepositoryConnectorUi repositoryUi) {
		super(TITLE, DESCRIPTION, repositoryUi);
		setNeedsAnonymousLogin(true);
		setNeedsValidation(false);
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
				if (template != null) {
					repositoryLabelEditor.setStringValue(template.label);
					setUrl(nvl(template.repositoryUrl));

					taskUrlText.setText(nvl(template.taskPrefixUrl));
					newTaskText.setText(nvl(template.newTaskUrl));

					queryUrlText.setText(nvl(template.taskQueryUrl));
					selectMethod(queryRequestMethod, //
							template.getAttribute(WebRepositoryConnector.PROPERTY_QUERY_METHOD));
					queryPatternText.setText(nvl(template.getAttribute(WebRepositoryConnector.PROPERTY_QUERY_REGEXP)));

					loginRequestUrlText.setText(nvl(template.getAttribute(WebRepositoryConnector.PROPERTY_LOGIN_REQUEST_URL)));
					selectMethod(loginRequestMethod, //
							template.getAttribute(WebRepositoryConnector.PROPERTY_LOGIN_REQUEST_METHOD));
					loginFormUrlText
							.setText(nvl(template.getAttribute(WebRepositoryConnector.PROPERTY_LOGIN_FORM_URL)));
					loginTokenPatternText.setText(nvl(template
							.getAttribute(WebRepositoryConnector.PROPERTY_LOGIN_TOKEN_REGEXP)));

					parametersEditor.removeAll();

					for (Map.Entry<String, String> entry : template.getAttributes().entrySet()) {
						String key = entry.getKey();
						if (key.startsWith(WebRepositoryConnector.PARAM_PREFIX)) {
							parametersEditor.add(key.substring(WebRepositoryConnector.PARAM_PREFIX.length()), //
									entry.getValue());
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
//		 GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 240).grab(true, true).span(2, 1).applyTo(editor);
//		 GridDataFactory.fillDefaults().hint(200, SWT.DEFAULT).span(2, SWT.DEFAULT).applyTo(editor);
		 GridDataFactory.fillDefaults().hint(200, 240).grab(true, true).span(2, 1).applyTo(editor);
		
		if (repository != null) {
			taskUrlText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_TASK_URL));
			newTaskText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_TASK_CREATION_URL));

			selectMethod(queryRequestMethod, getTextProperty(WebRepositoryConnector.PROPERTY_QUERY_METHOD));
			queryUrlText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_QUERY_URL));
			queryPatternText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_QUERY_REGEXP));

			loginFormUrlText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_LOGIN_FORM_URL));
			loginTokenPatternText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_LOGIN_TOKEN_REGEXP));

			selectMethod(loginRequestMethod, getTextProperty(WebRepositoryConnector.PROPERTY_LOGIN_REQUEST_METHOD));
			loginRequestUrlText.setText(getTextProperty(WebRepositoryConnector.PROPERTY_LOGIN_REQUEST_URL));

			oldProperties = repository.getProperties();
			parametersEditor.addParams(oldProperties, new LinkedHashMap<String, String>());
		}
	}

	private void selectMethod(ComboViewer viewer, String method) {
		if(!isPresent(method)) {
			method = WebRepositoryConnector.REQUEST_GET;
		}
		viewer.setSelection(new StructuredSelection(new Object[] {method}), true);
	}

	private String getTextProperty(String name) {
		return nvl(repository.getProperty(name));
	}

	@Override
	protected boolean isValidUrl(String name) {
		return true;
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
		gridData_1.minimumHeight = 100;
		parametersEditor.setLayoutData(gridData_1);

		createAdvancedComposite(parent, composite);

		return composite;
	}

	private void createAdvancedComposite(Composite parent, final Composite composite) {
		ExpandableComposite expComposite = toolkit.createExpandableComposite(composite, Section.COMPACT
				| Section.TWISTIE | Section.TITLE_BAR);
		expComposite.clientVerticalSpacing = 0;
		GridData gridData_2 = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData_2.horizontalIndent = -5;
		expComposite.setLayoutData(gridData_2);
		expComposite.setFont(parent.getFont());
		expComposite.setBackground(parent.getBackground());
		expComposite.setText("Advanced &Configuration");
		expComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				getControl().getShell().pack();
			}
		});
		toolkit.paintBordersFor(expComposite);

		Composite composite2 = toolkit.createComposite(expComposite, SWT.BORDER);
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 3;
		gridLayout2.verticalSpacing = 0;
		composite2.setLayout(gridLayout2);
		expComposite.setClient(composite2);

		Label taskUrlLabel = toolkit.createLabel(composite2, "&Task URL:", SWT.NONE);
		taskUrlLabel.setLayoutData(new GridData());

		taskUrlText = new Text(composite2, SWT.BORDER);
		taskUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label newTaskLabel = toolkit.createLabel(composite2, "&New Task URL:", SWT.NONE);
		newTaskLabel.setLayoutData(new GridData());

		newTaskText = new Text(composite2, SWT.BORDER);
		newTaskText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		final Label separatorLabel2 = new Label(composite2, SWT.HORIZONTAL | SWT.SEPARATOR);
		final GridData gridData_4 = new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1);
		gridData_4.verticalIndent = 5;
		gridData_4.heightHint = 5;
		separatorLabel2.setLayoutData(gridData_4);
		toolkit.adapt(separatorLabel2, true, true);

		Label queryUrlLabel = toolkit.createLabel(composite2, "&Query Request URL:", SWT.NONE);
		queryUrlLabel.setLayoutData(new GridData());

		queryUrlText = new Text(composite2, SWT.BORDER);
		queryUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		queryRequestMethod = new ComboViewer(composite2, SWT.BORDER | SWT.READ_ONLY);
		queryRequestMethod.setContentProvider(new MethodTypeContentProvider());
		queryRequestMethod.setInput("");
		{
			Combo combo = queryRequestMethod.getCombo();
			toolkit.adapt(combo, true, true);
			combo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		}

		Label queryPatternLabel = toolkit.createLabel(composite2, "Query &Pattern:", SWT.NONE);
		queryPatternLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));

		queryPatternText = new Text(composite2, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		gridData.heightHint = 40;
		queryPatternText.setLayoutData(gridData);

		final Label separatorLabel1 = new Label(composite2, SWT.HORIZONTAL | SWT.SEPARATOR);
		final GridData gridData_3 = new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1);
		gridData_3.heightHint = 5;
		gridData_3.verticalIndent = 5;
		separatorLabel1.setLayoutData(gridData_3);
		toolkit.adapt(separatorLabel1, true, true);

		final Label loginrequestUrlLabel = new Label(composite2, SWT.NONE);
		loginrequestUrlLabel.setLayoutData(new GridData());
		toolkit.adapt(loginrequestUrlLabel, true, true);
		loginrequestUrlLabel.setText("Login &Request URL:");

		loginRequestUrlText = new Text(composite2, SWT.BORDER);
		toolkit.adapt(loginRequestUrlText, true, true);
		loginRequestUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		loginRequestMethod = new ComboViewer(composite2, SWT.BORDER | SWT.READ_ONLY);
		loginRequestMethod.setContentProvider(new MethodTypeContentProvider());
		loginRequestMethod.setInput("");
		{
			Combo combo = loginRequestMethod.getCombo();
			toolkit.adapt(combo, true, true);
			combo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		}

		Label loginPageLabel = toolkit.createLabel(composite2, "Login &Form URL:", SWT.NONE);
		loginPageLabel.setLayoutData(new GridData());

		loginFormUrlText = new Text(composite2, SWT.BORDER);
		loginFormUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		Label loginTokenLabel = toolkit.createLabel(composite2, "Login &Token Pattern:", SWT.NONE);
		loginTokenLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));

		loginTokenPatternText = new Text(composite2, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		final GridData gridData_1 = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1);
		gridData_1.widthHint = 200;
		gridData_1.heightHint = 30;
		loginTokenPatternText.setLayoutData(gridData_1);

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
		repository.setProperty(WebRepositoryConnector.PROPERTY_QUERY_METHOD, getSelection(queryRequestMethod));

		String loginRequestUrl = loginRequestUrlText.getText();
		repository.setProperty(WebRepositoryConnector.PROPERTY_LOGIN_REQUEST_URL, loginRequestUrl);
		if(loginRequestUrl.length()>0) {
			repository.setProperty(WebRepositoryConnector.PROPERTY_LOGIN_REQUEST_METHOD, getSelection(loginRequestMethod));
		} else {
			repository.removeProperty(WebRepositoryConnector.PROPERTY_LOGIN_REQUEST_METHOD);
		}
		repository.setProperty(WebRepositoryConnector.PROPERTY_LOGIN_FORM_URL, loginFormUrlText.getText());
		repository.setProperty(WebRepositoryConnector.PROPERTY_LOGIN_TOKEN_REGEXP, loginTokenPatternText.getText());

		if (oldProperties != null) {
			for (Map.Entry<String, String> e : oldProperties.entrySet()) {
				String key = e.getKey();
				if (key.startsWith(WebRepositoryConnector.PARAM_PREFIX)) {
					repository.removeProperty(key);
				}
			}
		}

		for (Map.Entry<String, String> e : parametersEditor.getParameters().entrySet()) {
			repository.setProperty(e.getKey(), e.getValue());
		}
	}

	private String getSelection(ComboViewer viewer) {
		return (String) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
	}


	private static class MethodTypeContentProvider implements IStructuredContentProvider {

		private static final Object[] ELEMENTS = new Object[] { WebRepositoryConnector.REQUEST_GET,
				WebRepositoryConnector.REQUEST_POST };

		public Object[] getElements(Object inputElement) {
			return ELEMENTS;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return null;
	}

}
