/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.hypertext;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.mylar.core.AbstractRelationProvider;
import org.eclipse.mylar.core.IDegreeOfSeparation;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.swt.browser.LocationEvent;

/**
 * @author Mik Kersten
 */
public class HypertextStructureBridge implements IMylarStructureBridge {

	public static final String CONTENT_TYPE = "html"; // HACK: should be protocol
	
	public void setParentBridge(IMylarStructureBridge bridge) {
		// TODO Auto-generated method stub
	}

	public String getContentType() {
		return CONTENT_TYPE;
	}

	public String getHandleIdentifier(Object object) {
		if (object instanceof LocationEvent) {
			return ((LocationEvent)object).location;
		} else {
			return null;
		}
	}

	public Object getObjectForHandle(String handle) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getParentHandle(String handle) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canBeLandmark(String handle) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean acceptsObject(Object object) {
		if (object instanceof LocationEvent) {
			return true;
		} else { 
			return false;
		}
	}

	public boolean canFilter(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDocument(String handle) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getHandleForOffsetInObject(Object resource, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContentType(String elementHandle) {
		return CONTENT_TYPE;
	}

	public List<AbstractRelationProvider> getRelationshipProviders() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean containsProblem(IMylarElement node) {
		// TODO Auto-generated method stub
		return false;
	}

    public IProject getProjectForObject(Object object) {
    	return null;
    }

}
