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
/*
 * Created on Dec 29, 2004
 */
package org.eclipse.mylar.core;

import java.util.List;

/**
 * @author Mik Kersten
 */
public interface IMylarContext {

    public abstract List<InteractionEvent> getInteractionHistory();
    
    public List<IMylarElement> getInteresting();
    
    public abstract IMylarElement get(String element);

    public abstract IMylarElement getActiveNode();

    public abstract void remove(IMylarElement node);

    public abstract List<IMylarElement> getAllElements();

//  public abstract Set<IMylarElement> getInterestingResources();
    
//  public abstract List<IMylarElement> getLandmarks();

}
