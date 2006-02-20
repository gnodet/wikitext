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

package org.eclipse.mylar.internal.hypertext;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.mylar.provisional.core.AbstractRelationProvider;
import org.eclipse.mylar.provisional.core.IDegreeOfSeparation;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.swt.browser.LocationEvent;

/**
 * TODO: there is schitzophrenia between content types and protocols
 * 
 * @author Mik Kersten
 */
public class HypertextStructureBridge implements IMylarStructureBridge {

	public static final String CONTENT_TYPE = "html"; // HACK: should be
														// protocol

	public void setParentBridge(IMylarStructureBridge bridge) {
		// TODO Auto-generated method stub
	}

	public String getContentType() {
		return CONTENT_TYPE;
	}

	public String getContentType(String elementHandle) {
		return CONTENT_TYPE;
	}

	public String getHandleIdentifier(Object object) {
		if (object instanceof LocationEvent) {
			return ((LocationEvent) object).location;
		} else {
			return null;
		}
	}

	public Object getObjectForHandle(String handle) {
		return null;
	}

	public String getParentHandle(String handle) {
		if (handle == null)
			return null;
		int protocolEnd = handle.indexOf("//") + 2;
		if (protocolEnd != -1) {
			String withoutProtocol = handle.substring(protocolEnd);
			int siteEnd = withoutProtocol.indexOf("/");
			if (siteEnd != -1) {
				String site = handle.substring(0, protocolEnd + siteEnd);
				return site;
			}
		}
		return null;
	}

	public String getName(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canBeLandmark(String handle) {
		return false;
	}

	public boolean acceptsObject(Object object) {
		if (object instanceof LocationEvent) {
			return true;
			// LocationEvent event = (LocationEvent)object;
			// return event.location.startsWith("http");
		} else {
			return false;
		}
	}

	public boolean canFilter(Object element) {
		return false;
	}

	public boolean isDocument(String handle) {
		return true;
	}

	public String getHandleForOffsetInObject(Object resource, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AbstractRelationProvider> getRelationshipProviders() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getChildHandles(String handle) {
		return Collections.emptyList();
	}

	public IProject getProjectForObject(Object object) {
		return null;
	}

}
