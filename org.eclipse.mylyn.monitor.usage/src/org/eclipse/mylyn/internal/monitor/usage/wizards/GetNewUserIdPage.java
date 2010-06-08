/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.usage.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.monitor.usage.StudyParameters;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.internal.monitor.usage.UsageDataException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Page to get a user study id for the user.
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class GetNewUserIdPage extends WizardPage {

	private Text firstName;

	private Text lastName;

	private Text emailAddress;

	private Button contactAgreement;

	// private Button anonymous;
	private Button getNewUid;

	private Button getExistingUid;

	private String first;

	private String last;

	private String email;

	private boolean contactEmail = false;

	private boolean anon;

	private boolean hasValidated = false;

	private String jobFunction = Messages.GetNewUserIdPage_Select_Below;

	private String companySize = Messages.GetNewUserIdPage_Select_Below;

	private String companyFunction = Messages.GetNewUserIdPage_Select_Below;

	private final UsageSubmissionWizard wizard;

	private final boolean performUpload;

	private boolean extendedMonitor = false;

	private final StudyParameters studyParameters;

	public GetNewUserIdPage(UsageSubmissionWizard wizard, StudyParameters studyParameters, boolean performUpload) {
		super(Messages.GetNewUserIdPage_Statistics_Wizard);
		this.studyParameters = studyParameters;
		this.performUpload = performUpload;

		setTitle(NLS.bind(Messages.GetNewUserIdPage_Get_X_Feedback_Id, studyParameters.getStudyName()));
		setDescription(Messages.GetNewUserIdPage_In_Order_To_Submit_User_Id);
		this.wizard = wizard;
		if (studyParameters.getCustomizingPlugin() != null) {
			extendedMonitor = true;
			String customizedTitle = studyParameters.getTitle();
			if (!customizedTitle.equals("")) { //$NON-NLS-1$
				setTitle(NLS.bind(Messages.GetNewUserIdPage_X_Consent_Form_And_User_Id, customizedTitle));
			}
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		if (extendedMonitor) {
			createBrowserSection(container);
			// createAnonymousSection(container);
			createInstructionSection(container);
			createNamesSection(container);
			createJobDetailSection(container);
			if (studyParameters.usingContactField()) {
				createContactSection(container);
			}
			createUserIdButtons(container);
		} else {
			createAnonymousParticipationButtons(container);
		}
		setControl(container);
	}

	@SuppressWarnings("deprecation")
	private void createBrowserSection(Composite parent) {
		if (extendedMonitor) {
			Label label = new Label(parent, SWT.NULL);
			label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
			label.setText(studyParameters.getCustomizedByMessage());

			Composite container = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			container.setLayout(layout);
			layout.numColumns = 1;
			Browser browser = new Browser(parent, SWT.NONE);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 200;
			gd.widthHint = 600;
			browser.setLayoutData(gd);

			URL url = Platform.getBundle(studyParameters.getCustomizingPlugin()).getEntry(
					studyParameters.getFormsConsent());
			try {
				URL localURL = Platform.asLocalURL(url);
				browser.setUrl(localURL.toString());
			} catch (Exception e) {
				browser.setText(Messages.GetNewUserIdPage_Feedback_Description_Not_Located);
			}
		} else {
			Label label = new Label(parent, SWT.NULL);
			label.setText(""); //$NON-NLS-1$
		}
	}

	// private void createAnonymousSection(Composite parent) {
	// Composite container = new Composite(parent, SWT.NULL);
	// GridLayout layout = new GridLayout();
	// container.setLayout(layout);
	// layout.numColumns = 1;
	//        
	// anonymous = new Button(container, SWT.CHECK);
	// GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
	// anonymous.setLayoutData(gd);
	// anonymous.setSelection(false);
	// anonymous.setText("Anonymous (you must still provide your name and email
	// for consent purposes)");
	// anonymous.addSelectionListener(new SelectionListener() {
	// public void widgetSelected(SelectionEvent e) {
	// if (e.widget instanceof Button) {
	// Button b = (Button) e.widget;
	// anon = b.getSelection();
	// updateEnablement();
	// // boolean edit = !anon;
	// // firstName.setEditable(edit);
	// // lastName.setEditable(edit);
	// // emailAddress.setEditable(edit);
	// GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
	// }
	// }
	// public void widgetDefaultSelected(SelectionEvent e) {
	// // don't care about default selection
	// }
	// });
	// }

	private void createNamesSection(Composite parent) {
		Composite names = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(6, true);
		layout.verticalSpacing = 9;
		layout.horizontalSpacing = 4;
		names.setLayout(layout);

		Label label = new Label(names, SWT.NULL);
		label.setText(Messages.GetNewUserIdPage_First_Name);

		firstName = new Text(names, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		firstName.setLayoutData(gd);
		firstName.setEditable(true);
		firstName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				first = firstName.getText();
				updateEnablement();
				GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
			}
		});

		label = new Label(names, SWT.NULL);
		label.setText(Messages.GetNewUserIdPage_Last_Name);

		lastName = new Text(names, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		lastName.setLayoutData(gd);
		lastName.setEditable(true);
		lastName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				last = lastName.getText();
				updateEnablement();
				GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
			}
		});

		label = new Label(names, SWT.NONE);
		label.setText(Messages.GetNewUserIdPage_Email_Address);

		emailAddress = new Text(names, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH);
		gd.horizontalSpan = 5;
		emailAddress.setLayoutData(gd);
		emailAddress.setEditable(true);
		emailAddress.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				email = emailAddress.getText();
				updateEnablement();
				GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
			}
		});
	}

	private void createJobDetailSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Label l = new Label(container, SWT.NULL);
		l.setText(Messages.GetNewUserIdPage_Job_Function);
		final Combo jobFunctionCombo = new Combo(container, SWT.DROP_DOWN);
		jobFunctionCombo.setText(jobFunction);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Application_Developer);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_QA);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Program_Director);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_CIO);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_VP_Development);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Application_Architect);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Project_Manager);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Student);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Faculty);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Business);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Analyst);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Database_Administrator);
		jobFunctionCombo.add(Messages.GetNewUserIdPage_Other);
		jobFunctionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				jobFunction = jobFunctionCombo.getText();
				updateEnablement();
			}
		});

		l = new Label(container, SWT.NULL);
		l.setText(Messages.GetNewUserIdPage_Company_Size);
		final Combo companySizecombo = new Combo(container, SWT.DROP_DOWN);
		companySizecombo.setText(companySize);
		companySizecombo.add(Messages.GetNewUserIdPage_Individual);
		companySizecombo.add(Messages.GetNewUserIdPage_Gt_Fifty);
		companySizecombo.add(Messages.GetNewUserIdPage_Fifty_Hundred);
		companySizecombo.add(Messages.GetNewUserIdPage_Hundred_Five_Hundred);
		companySizecombo.add(Messages.GetNewUserIdPage_Five_Hundred_Thousand);
		companySizecombo.add(Messages.GetNewUserIdPage_Thousand_Twenty_Five_Hundred);
		companySizecombo.add(Messages.GetNewUserIdPage_Gt_Twenty_Five_Hundred);
		companySizecombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				companySize = companySizecombo.getText();
				updateEnablement();
			}
		});

		l = new Label(container, SWT.NULL);
		l.setText(Messages.GetNewUserIdPage_Company_Business);
		final Combo companyBuisnesscombo = new Combo(container, SWT.DROP_DOWN);
		companyBuisnesscombo.setText(companyFunction);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Financial);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Energy);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Government);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Hardware);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Networking);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Pharmaceutical);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Automotive);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Software);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Communications);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Transportation);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Retail);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Utilities);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Other_Manufacturing);
		companyBuisnesscombo.add(Messages.GetNewUserIdPage_Academic);
		companyBuisnesscombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				companyFunction = companyBuisnesscombo.getText();
				updateEnablement();
			}
		});
	}

	private void createInstructionSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		Label l = new Label(container, SWT.NONE);
		// l.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		l.setText(Messages.GetNewUserIdPage_To_Create_User_Id_Fill_In);

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		l.setLayoutData(gd);
	}

	private void createContactSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		contactAgreement = new Button(container, SWT.CHECK);
		contactAgreement.setText(Messages.GetNewUserIdPage_Willing_To_Receive_Email);
		contactAgreement.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				contactEmail = contactAgreement.getSelection();
			}
		});
	}

	private void createUserIdButtons(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		Label l = new Label(container, SWT.NONE);
		l.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		l.setText(Messages.GetNewUserIdPage_I_Consent_Acknowledge);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		l.setLayoutData(gd);

		container = new Composite(parent, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		getNewUid = new Button(container, SWT.PUSH);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		getNewUid.setLayoutData(gd);
		getNewUid.setSelection(false);
		getNewUid.setText(Messages.GetNewUserIdPage_I_Consent);
		getNewUid.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (e.widget instanceof Button) {
					if (hasAllFields(false)) {
						final int[] uid = new int[1];
						try {
							getWizard().getContainer().run(false, true, new IRunnableWithProgress() {

								public void run(IProgressMonitor monitor) throws InvocationTargetException,
										InterruptedException {
									try {
										uid[0] = UiUsageMonitorPlugin.getDefault().getUploadManager().getNewUid(
												studyParameters, first, last, email, anon, jobFunction, companySize,
												companyFunction, contactEmail, monitor);
									} catch (UsageDataException e) {
										StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
												e.getMessage(), e));
										uid[0] = -1;
									}
								}
							});
						} catch (InvocationTargetException e1) {
							StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
									e1.getMessage(), e1));
						} catch (InterruptedException e1) {
							StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
									e1.getMessage(), e1));
						}
						if (uid[0] != -1 && uid[0] != 0) {
							UiUsageMonitorPlugin.getDefault().getPreferenceStore().setValue(
									studyParameters.getUserIdPreferenceId(), uid[0]);
							if (wizard.getUploadPage() != null) {
								wizard.getUploadPage().updateUid();
							}
							hasValidated = true;
							MessageDialog.openInformation(Display.getDefault().getActiveShell(), NLS.bind(
									Messages.GetNewUserIdPage_X_User_Study_Id, studyParameters.getStudyName()),
									NLS.bind(Messages.GetNewUserIdPage_Your_X_User_Study_Id_Y,
											studyParameters.getStudyName(), wizard.getUid()));
						} else {
							MessageDialog.openError(null, Messages.UsageSubmissionWizard_Error_Getting_User_Id,
									Messages.UsageSubmissionWizard_Unable_To_Get_New_User_Id);
						}
					} else {
						MessageDialog.openError(Display.getDefault().getActiveShell(),
								Messages.GetNewUserIdPage_Incomplete_Form_Input,
								Messages.GetNewUserIdPage_Please_Complete_All_Fields);
					}
					GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about default selected
			}
		});

		getExistingUid = new Button(container, SWT.PUSH);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		getExistingUid.setLayoutData(gd);
		getExistingUid.setSelection(false);
		getExistingUid.setText(Messages.GetNewUserIdPage_Already_Consented);
		getExistingUid.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (e.widget instanceof Button) {
					if (hasAllFields(true)) {
						final int[] uid = new int[1];
						try {
							getWizard().getContainer().run(false, true, new IRunnableWithProgress() {

								public void run(IProgressMonitor monitor) throws InvocationTargetException,
										InterruptedException {
									try {
										uid[0] = UiUsageMonitorPlugin.getDefault().getUploadManager().getExistingUid(
												studyParameters, first, last, email, anon, monitor);
									} catch (UsageDataException e) {
										uid[0] = -1;
										StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
												e.getMessage(), e));
									}
								}
							});
						} catch (InvocationTargetException e1) {
							StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
									e1.getMessage(), e1));
						} catch (InterruptedException e1) {
							StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
									e1.getMessage(), e1));
						}
						if (uid[0] != -1 && uid[0] != 0) {

							UiUsageMonitorPlugin.getDefault().getPreferenceStore().setValue(
									studyParameters.getUserIdPreferenceId(), uid[0]);
							if (wizard.getUploadPage() != null) {
								wizard.getUploadPage().updateUid();
							}
							hasValidated = true;
							MessageDialog.openInformation(Display.getDefault().getActiveShell(), NLS.bind(
									Messages.GetNewUserIdPage_X_User_Study_Id, studyParameters.getStudyName()),
									NLS.bind(Messages.GetNewUserIdPage_Your_X_User_Study_Id_Y_Retrieve_By_Repeating,
											studyParameters.getStudyName(), wizard.getUid()));
						} else {
							MessageDialog.openError(null, Messages.UsageSubmissionWizard_Error_Getting_User_Id,
									Messages.UsageSubmissionWizard_Unable_To_Get_New_User_Id);
						}
					} else {
						MessageDialog.openError(Display.getDefault().getActiveShell(),
								Messages.GetNewUserIdPage_Incomplete_Form_Input,
								Messages.GetNewUserIdPage_Please_Complete_All_Fields);
					}
					GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about default selected
			}
		});

		updateEnablement();
	}

	private void createAnonymousParticipationButtons(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		Label label = new Label(container, SWT.NONE);
		label.setText(Messages.GetNewUserIdPage_Your_Data_Not_Traceable);
		label = new Label(container, SWT.NONE);
		label.setText(Messages.GetNewUserIdPage_Before_Switching_Retrieve_From_Preferences);
		// GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		// label.setLayoutData(gd);

		container = new Composite(parent, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		getNewUid = new Button(container, SWT.PUSH);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		getNewUid.setLayoutData(gd);
		getNewUid.setSelection(false);
		getNewUid.setText(Messages.GetNewUserIdPage_Create_Or_Retrieve_Id);
		getNewUid.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (e.widget instanceof Button) {
					final int[] uid = new int[1];
					try {
						getWizard().getContainer().run(false, true, new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor) throws InvocationTargetException,
									InterruptedException {
								try {
									uid[0] = UiUsageMonitorPlugin.getDefault().getUploadManager().getNewUid(
											studyParameters, monitor);
								} catch (UsageDataException e) {
									uid[0] = -1;
									StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
											e.getMessage(), e));
								}
							}
						});
					} catch (InvocationTargetException e1) {
						StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, e1.getMessage(), e1));
					} catch (InterruptedException e1) {
						StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, e1.getMessage(), e1));
					}
					if (uid[0] != -1 && uid[0] != 0) {
						UiUsageMonitorPlugin.getDefault().getPreferenceStore().setValue(
								studyParameters.getUserIdPreferenceId(), uid[0]);
						if (wizard.getUploadPage() != null) {
							wizard.getUploadPage().updateUid();
						}
						hasValidated = true;
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), NLS.bind(
								Messages.GetNewUserIdPage_X_User_Study_Id, studyParameters.getStudyName()), NLS.bind(
								Messages.GetNewUserIdPage_Your_X_User_Study_Id_Y_Record,
								studyParameters.getStudyName(), wizard.getUid()));
					} else {
						MessageDialog.openError(null, Messages.UsageSubmissionWizard_Error_Getting_User_Id,
								Messages.UsageSubmissionWizard_Unable_To_Get_New_User_Id);
					}
					GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about default selected
			}
		});
		updateEnablement();
	}

	private void updateEnablement() {
		if (!extendedMonitor) {
			return;
		}
		boolean nameFilled = (!firstName.getText().equals("") && !lastName.getText().equals("") && !emailAddress.getText() //$NON-NLS-1$ //$NON-NLS-2$
				.equals("")) //$NON-NLS-1$
				|| anon;
		// if(nameFilled){
		// getExistingUid.setEnabled(true);
		boolean jobFilled = !jobFunction.equals(Messages.GetNewUserIdPage_Select_Below)
				&& !companyFunction.equals(Messages.GetNewUserIdPage_Select_Below)
				&& !companySize.equals(Messages.GetNewUserIdPage_Select_Below);
		// if(jobFilled){
		// getNewUid.setEnabled(true);
		// } else {
		// getNewUid.setEnabled(false);
		// }
		if (nameFilled && jobFilled) {
			getNewUid.setEnabled(true);
			getExistingUid.setEnabled(true);
		} else {
			getExistingUid.setEnabled(false);
			getNewUid.setEnabled(false);
		}
	}

	public boolean hasAllFields(boolean existing) {
		if (!extendedMonitor) {
			return true;
		}
		boolean nameFilled = !firstName.getText().equals("") && !lastName.getText().equals("") //$NON-NLS-1$//$NON-NLS-2$
				&& !emailAddress.getText().equals(""); //$NON-NLS-1$
		if (!existing) {
			boolean jobFilled = !jobFunction.equals(Messages.GetNewUserIdPage_Select_Below)
					&& !companyFunction.equals(Messages.GetNewUserIdPage_Select_Below)
					&& !companySize.equals(Messages.GetNewUserIdPage_Select_Below);
			return (jobFilled && nameFilled);
		} else {
			return nameFilled || anon;
		}
	}

	@Override
	public boolean isPageComplete() {
		if (hasAllFields(true) && hasValidated) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public IWizardPage getNextPage() {
		if (isPageComplete() && performUpload) {
			wizard.addPage(wizard.getUploadPage());
		}

		return super.getNextPage();

	}

	public boolean isAnonymous() {
		return anon;
	}

	public String getEmailAddress() {
		return email;
	}

	public String getFirstName() {
		return first;
	}

	public String getLastName() {
		return last;
	}
}
