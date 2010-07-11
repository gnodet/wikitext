/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jeff Pound - attachment support
 *     Frank Becker - improvements for bug 204051
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.commands.OpenTaskAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.ColumnState;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTableViewerConfigurator;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.Mode;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskEditorAttachmentPart extends AbstractTaskEditorPart {

	private static final String ID_POPUP_MENU = "org.eclipse.mylyn.tasks.ui.editor.menu.attachments"; //$NON-NLS-1$

	private List<TaskAttribute> attachments;

	private boolean hasIncoming;

	private MenuManager menuManager;

	private Composite attachmentsComposite;

	private class AttachmentTableViewer extends AbstractTableViewerConfigurator {
		public AttachmentTableViewer(File stateFile) {
			super(stateFile);
			// ignore
		}

		@Override
		protected void adjustColumInfos() {
			boolean showAttachmentID = TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.getBoolean(ITasksUiPreferenceConstants.ATTACHMENT_SHOW_ID);
			int idWidth = columnInfos.get(0).getWidths();
			if (!showAttachmentID && idWidth > 0) {
				columnInfos.get(0).setWidths(0);
			} else if (showAttachmentID && idWidth == 0) {
				columnInfos.get(0).setWidths(70);
			}
		}

		@Override
		protected void setDefaultColumnInfos() {
			columnInfos.add(new ColumnState(Messages.TaskEditorAttachmentPart_ID, 70));
			columnInfos.add(new ColumnState(Messages.TaskEditorAttachmentPart_Name, 130));
			columnInfos.add(new ColumnState(Messages.TaskEditorAttachmentPart_Description, 150));
			ColumnState columnState = new ColumnState(Messages.TaskEditorAttachmentPart_Size, 70);
			columnState.setAlignment(SWT.RIGHT);
			columnInfos.add(columnState);
			columnInfos.add(new ColumnState(Messages.TaskEditorAttachmentPart_Creator, 100));
			columnInfos.add(new ColumnState(Messages.TaskEditorAttachmentPart_Created, 100));

			orderArray = new int[6];
			for (int i = 0; i < 6; i++) {
				orderArray[i] = i;
			}

		}

		@Override
		protected void setupTableViewer() {
			tableViewer.setUseHashlookup(true);
			ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);

			tableViewer.setSorter(new ViewerSorter() {
				@Override
				public int compare(Viewer viewer, Object e1, Object e2) {
					ITaskAttachment attachment1 = (ITaskAttachment) e1;
					ITaskAttachment attachment2 = (ITaskAttachment) e2;
					Date created1 = attachment1.getCreationDate();
					Date created2 = attachment2.getCreationDate();
					if (created1 != null && created2 != null) {
						return created1.compareTo(created2);
					} else if (created1 == null && created2 != null) {
						return -1;
					} else if (created1 != null && created2 == null) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			List<ITaskAttachment> attachmentList = new ArrayList<ITaskAttachment>(attachments.size());
			for (TaskAttribute attribute : attachments) {
				TaskAttachment taskAttachment = new TaskAttachment(getModel().getTaskRepository(),
						getModel().getTask(), attribute);
				getTaskData().getAttributeMapper().updateTaskAttachment(taskAttachment, attribute);
				attachmentList.add(taskAttachment);

				tableViewer.setContentProvider(new ArrayContentProvider());
				tableViewer.setLabelProvider(new AttachmentTableLabelProvider(getModel(),
						getTaskEditorPage().getAttributeEditorToolkit()));
				tableViewer.addOpenListener(new IOpenListener() {
					public void open(OpenEvent event) {
						if (!event.getSelection().isEmpty()) {
							StructuredSelection selection = (StructuredSelection) event.getSelection();
							ITaskAttachment attachment = (ITaskAttachment) selection.getFirstElement();
							TasksUiUtil.openUrl(attachment.getUrl());
						}
					}
				});
				tableViewer.addSelectionChangedListener(getTaskEditorPage());
				tableViewer.setInput(attachmentList.toArray());
			}
		}
	}

	private AttachmentTableViewer attachmentsViewer;

	private boolean propertyListenerIstalled = false;

	public TaskEditorAttachmentPart() {
		setPartName(Messages.TaskEditorAttachmentPart_Attachments);
	}

	private final org.eclipse.jface.util.IPropertyChangeListener PROPERTY_LISTENER = new org.eclipse.jface.util.IPropertyChangeListener() {

		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			if (event.getProperty().equals(ITasksUiPreferenceConstants.ATTACHMENT_COLUMN_TO_STD)) {
				if (TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.getBoolean(ITasksUiPreferenceConstants.ATTACHMENT_COLUMN_TO_STD)) {
					if (attachmentsViewer != null) {
						attachmentsViewer.resetColumnInfosToDefault();
						Table table = attachmentsViewer.getTable();
						if (!table.isDisposed()) {
							if (TasksUiPlugin.getDefault()
									.getPreferenceStore()
									.getBoolean(ITasksUiPreferenceConstants.ATTACHMENT_SHOW_ID)) {
								table.getColumn(0).setWidth(70);
							} else {
								table.getColumn(0).setWidth(0);
							}
						}
					} else {
						IPath stateLocation = Platform.getStateLocation(TasksUiPlugin.getDefault().getBundle());
						File attachmentStateFile = stateLocation.append("TaskEditorAttachment.obj").toFile(); //$NON-NLS-1$
						attachmentStateFile.delete();
					}
				}
			} else if (event.getProperty().equals(ITasksUiPreferenceConstants.ATTACHMENT_SHOW_ID)) {
				if (attachmentsViewer != null) {
					Table table = attachmentsViewer.getTable();
					if (!table.isDisposed()) {
						if (TasksUiPlugin.getDefault()
								.getPreferenceStore()
								.getBoolean(ITasksUiPreferenceConstants.ATTACHMENT_SHOW_ID)) {
							table.getColumn(0).setWidth(70);
						} else {
							table.getColumn(0).setWidth(0);
						}
					}
				}
			}
		}
	};

	private void createAttachmentTable(FormToolkit toolkit, final Composite attachmentsComposite) {
		IPath stateLocation = Platform.getStateLocation(TasksUiPlugin.getDefault().getBundle());
		File attachmentStateFile = stateLocation.append("TaskEditorAttachment.obj").toFile(); //$NON-NLS-1$
		attachmentsViewer = new AttachmentTableViewer(attachmentStateFile);
		attachmentsViewer.create(toolkit, attachmentsComposite, 5);
		menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		Menu menu = menuManager.createContextMenu(attachmentsViewer.getTable());
		attachmentsViewer.getTable().setMenu(menu);
	}

	private void createButtons(Composite attachmentsComposite, FormToolkit toolkit) {
		final Composite attachmentControlsComposite = toolkit.createComposite(attachmentsComposite);
		attachmentControlsComposite.setLayout(new GridLayout(2, false));
		attachmentControlsComposite.setLayoutData(new GridData(GridData.BEGINNING));

		Button attachFileButton = toolkit.createButton(attachmentControlsComposite,
				Messages.TaskEditorAttachmentPart_Attach_, SWT.PUSH);
		attachFileButton.setImage(CommonImages.getImage(CommonImages.FILE_PLAIN));
		attachFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.DEFAULT, null);
			}
		});
		getTaskEditorPage().registerDefaultDropListener(attachFileButton);

		Button attachScreenshotButton = toolkit.createButton(attachmentControlsComposite,
				Messages.TaskEditorAttachmentPart_Attach__Screenshot, SWT.PUSH);
		attachScreenshotButton.setImage(CommonImages.getImage(CommonImages.IMAGE_CAPTURE));
		attachScreenshotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.SCREENSHOT, null);
			}
		});
		getTaskEditorPage().registerDefaultDropListener(attachScreenshotButton);
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		initialize();

		final Section section = createSection(parent, toolkit, hasIncoming);
		section.setText(getPartName() + " (" + attachments.size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		if (hasIncoming) {
			expandSection(toolkit, section);
		} else {
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					if (attachmentsComposite == null) {
						expandSection(toolkit, section);
						getTaskEditorPage().reflow();
					}
				}
			});
		}
		setSection(toolkit, section);
	}

	private void expandSection(FormToolkit toolkit, Section section) {
		attachmentsComposite = toolkit.createComposite(section);
		attachmentsComposite.setLayout(EditorUtil.createSectionClientLayout());
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		getTaskEditorPage().registerDefaultDropListener(section);

		if (attachments.size() > 0) {
			createAttachmentTable(toolkit, attachmentsComposite);
		} else {
			Label label = toolkit.createLabel(attachmentsComposite, Messages.TaskEditorAttachmentPart_No_attachments);
			getTaskEditorPage().registerDefaultDropListener(label);
		}

		createButtons(attachmentsComposite, toolkit);

		toolkit.paintBordersFor(attachmentsComposite);
		section.setClient(attachmentsComposite);
	}

	@Override
	public void dispose() {
		if (propertyListenerIstalled) {
			TasksUiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(PROPERTY_LISTENER);
			propertyListenerIstalled = false;
		}
		if (menuManager != null) {
			menuManager.dispose();
		}
		super.dispose();
	}

	private void initialize() {
		attachments = getTaskData().getAttributeMapper().getAttributesByType(getTaskData(),
				TaskAttribute.TYPE_ATTACHMENT);
		for (TaskAttribute attachmentAttribute : attachments) {
			if (getModel().hasIncomingChanges(attachmentAttribute)) {
				hasIncoming = true;
				break;
			}
		}
		if (!propertyListenerIstalled) {
			TasksUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(PROPERTY_LISTENER);
			propertyListenerIstalled = true;
		}
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBarManager) {
		Action attachFileAction = new Action() {
			@Override
			public void run() {
				EditorUtil.openNewAttachmentWizard(getTaskEditorPage(), Mode.DEFAULT, null);
			}
		};
		attachFileAction.setToolTipText(Messages.TaskEditorAttachmentPart_Attach_);
		attachFileAction.setImageDescriptor(CommonImages.FILE_PLAIN_SMALL);
		toolBarManager.add(attachFileAction);
	}

	protected void openAttachments(OpenEvent event) {
		List<ITaskAttachment> attachments = new ArrayList<ITaskAttachment>();

		StructuredSelection selection = (StructuredSelection) event.getSelection();

		List<?> items = selection.toList();
		for (Object item : items) {
			if (item instanceof ITaskAttachment) {
				attachments.add((ITaskAttachment) item);
			}
		}

		if (attachments.isEmpty()) {
			return;
		}

		IWorkbenchPage page = getTaskEditorPage().getSite().getWorkbenchWindow().getActivePage();

		OpenTaskAttachmentHandler.openAttachments(page, attachments);
	}
}
