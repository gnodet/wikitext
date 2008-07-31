/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ui.annotation;

import junit.framework.TestCase;

/**
 *
 *
 * @author David Green
 */
public class AnchorNameAnnotationTest extends TestCase {

	public void testSimple() {
		AnchorNameAnnotation annotation = new AnchorNameAnnotation("foo");
		assertEquals("foo",annotation.getText());
		assertEquals(AnchorNameAnnotation.TYPE,annotation.getType());
	}
}
