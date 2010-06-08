/*******************************************************************************
 * Copyright (c) 2004, 2009 John Anvik and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Anvik - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core.history;

/**
 * @author John Anvik
 */
public enum AttachmentFlagStatus {
	REVIEW /* Firefox */, SUPERREVIEW /* Firefox */, APPROVAL /* Firefox */, UI /* Firefox */, BRANCH /* Firefox */, OBSOLETE, UNKNOWN, COMMITTED /* Gnome */, ACCEPTED /* Gnome accepted-committ_now*/, COMMENTED /* Gnome */, NONE /* Gnome */, REJECTED/* Gnome */;
}
