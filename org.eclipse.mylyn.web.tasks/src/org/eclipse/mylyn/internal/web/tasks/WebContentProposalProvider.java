/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import java.util.ArrayList;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eugene Kuleshov
 */
public class WebContentProposalProvider implements IContentProposalProvider {

	private final ParametersEditor parametersEditor;

	private boolean includeTemplates;

	public WebContentProposalProvider(ParametersEditor parametersEditor, boolean includeTemplates) {
		this.parametersEditor = parametersEditor;
		this.includeTemplates = includeTemplates;
	}

	public IContentProposal[] getProposals(String contents, int position) {
		ArrayList<IContentProposal> proposals = new ArrayList<IContentProposal>();
		if (includeTemplates) {
			addProposal(proposals, "({Id}.+?)");
			addProposal(proposals, "({Description}.+?)");
			addProposal(proposals, "({Owner}.+?)");
			addProposal(proposals, "({Type}.+?)");
			addProposal(proposals, "({Status}.+?)");
			// proposals.add("({Due}.+?)");
			// proposals.add("({Updated}.+?)");
			// proposals.add("({Created}.+?)");
			addProposal(proposals, "(.+?)");
			addProposal(proposals, ".+?");
		}
		addProposal(proposals, "${" + WebRepositoryConnector.PARAM_SERVER_URL + "}");
		addProposal(proposals, "${" + WebRepositoryConnector.PARAM_USER_ID + "}");
		addProposal(proposals, "${" + WebRepositoryConnector.PARAM_PASSWORD + "}");
		addProposal(proposals, "${" + WebRepositoryConnector.PARAM_LOGIN_TOKEN + "}");
		for (String param : parametersEditor.getParameters().keySet()) {
			addProposal(proposals, "${" + param.substring(WebRepositoryConnector.PARAM_PREFIX.length()) + "}");
		}
		addProposal(proposals, "${}");
		return proposals.toArray(new IContentProposal[proposals.size()]);
	}

	private boolean addProposal(ArrayList<IContentProposal> proposals, final String content) {
		return proposals.add(new IContentProposal() {

			public String getContent() {
				return content;
			}

			public int getCursorPosition() {
				return content.length();
			}

			public String getDescription() {
				return null;
			}

			public String getLabel() {
				return null;
			}

		});
	}

	@SuppressWarnings("deprecation")
	public static ControlDecoration createDecoration(final Text text, //
			ParametersEditor parametersEditor, boolean includeTemplates) {
		FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
		FieldDecoration fieldDecoration = registry.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);

		ControlDecoration decoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		decoration.setImage(fieldDecoration.getImage());
		decoration.setDescriptionText(fieldDecoration.getDescription());
		decoration.setShowOnlyOnFocus(true);

		KeyStroke keystroke = null;
		try {
			keystroke = KeyStroke.getInstance("Ctrl" + KeyStroke.KEY_DELIMITER + "Space");
		} catch (ParseException e) {
		}

		ContentProposalAdapter adapter = new ContentProposalAdapter(text, new TextContentAdapter(), //
				new WebContentProposalProvider(parametersEditor, includeTemplates), keystroke, null);
		adapter.setPopupSize(new Point(200, 150));
		adapter.setPropagateKeys(true);
		adapter.setFilterStyle(ContentProposalAdapter.FILTER_CUMULATIVE);

		// workaround for bug 196565
		WebContentProposalListener listener = new WebContentProposalListener(adapter);
		adapter.addContentProposalListener((IContentProposalListener) listener);
		adapter.addContentProposalListener((IContentProposalListener2) listener);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_IGNORE);

		return decoration;
	}

	static class WebContentProposalListener implements IContentProposalListener, IContentProposalListener2 {

		private final ContentProposalAdapter adapter;

		private int startCaretPosition;

		private int endCaretPosition;

		public WebContentProposalListener(ContentProposalAdapter adapter) {
			this.adapter = adapter;
		}

		public void proposalAccepted(IContentProposal proposal) {
			IControlContentAdapter contentAdapter = adapter.getControlContentAdapter();
			Control control = adapter.getControl();
			StringBuilder sb = new StringBuilder(contentAdapter.getControlContents(control));
			sb.insert(contentAdapter.getCursorPosition(control), //
					proposal.getContent().substring(endCaretPosition - startCaretPosition));
			contentAdapter.setControlContents(control, sb.toString(), //
					startCaretPosition + proposal.getCursorPosition());
		}

		public void proposalPopupOpened(ContentProposalAdapter adapter) {
			startCaretPosition = adapter.getControlContentAdapter().getCursorPosition(adapter.getControl());
		}

		public void proposalPopupClosed(ContentProposalAdapter adapter) {
			endCaretPosition = adapter.getControlContentAdapter().getCursorPosition(adapter.getControl());
		}

	}

}
