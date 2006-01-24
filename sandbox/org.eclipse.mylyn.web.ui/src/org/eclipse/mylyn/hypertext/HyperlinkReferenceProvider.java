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

package org.eclipse.mylar.hypertext;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.core.AbstractRelationProvider;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.search.IMylarSearchOperation;

/**
 * @author Mik Kersten
 */
public class HyperlinkReferenceProvider extends AbstractRelationProvider {

	public static List<Job> runningJobs = new ArrayList<Job>();

	public static final String ID = "org.eclipse.mylar.hypertext.link";

	public static final String NAME = "Hyperlink";

	public static final int DEFAULT_DEGREE = 2;

	public String getGenericId() {
		return ID;
	}

	protected HyperlinkReferenceProvider(String structureKind, String id) {
		super(structureKind, id);
	}

	@Override
	protected int getDefaultDegreeOfSeparation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void findRelated(IMylarElement node, int degreeOfSeparation) {
		// TODO Auto-generated method stub

	}

	@Override
	public IMylarSearchOperation getSearchOperation(IMylarElement node, int limitTo, int degreeOfSeparation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSourceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopAllRunningJobs() {
		// TODO Auto-generated method stub

	}
}
