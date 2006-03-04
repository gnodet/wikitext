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
package org.eclipse.mylar.internal.viz.seesoft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.contribution.visualiser.core.Stripe;
import org.eclipse.contribution.visualiser.interfaces.IGroup;
import org.eclipse.contribution.visualiser.interfaces.IMarkupKind;
import org.eclipse.contribution.visualiser.interfaces.IMarkupProvider;
import org.eclipse.contribution.visualiser.interfaces.IMember;
import org.eclipse.contribution.visualiser.jdtImpl.JDTMember;
import org.eclipse.contribution.visualiser.simpleImpl.SimpleMarkupKind;
import org.eclipse.contribution.visualiser.utils.JDTUtils;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.ui.UiUtil;
import org.eclipse.mylar.provisional.core.IDegreeOfInterest;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Provides horizontal stripes that represent interesting members of a class in
 * the visualiser view
 * 
 * @author Wesley Coelho
 */
public class JavaContextMarkupProvider implements IMarkupProvider {

	/** 
	 * Assigned when there is no highlight color for a task or it is too light 
	 * TODO: dispose
	 */
	public Color defaultStripeColor = new Color(null, new RGB(128, 128, 255));

	/** Holds all IMarkupKinds (for the visualiser menu) */
	SortedSet<IMarkupKind> markupKindSet = new TreeSet<IMarkupKind>();

	/** Contains the color assigned to a markupkind for a particular task */
	protected Map<IMarkupKind, Color> kindToColorMap = new HashMap<IMarkupKind, Color>();

	/**
	 * Records colors that have been explicitly set in the visualiser menu so
	 * they are not overridden
	 */
	protected Map<IMarkupKind, Object> colorSetByVisualiserMap = new HashMap<IMarkupKind, Object>();

	public void initialise() {

	}

	/**
	 * Returns a list of Stripes representing the interesting declarations in
	 * the specified IMember where the IMember represents a Java class.
	 */
	public List getMemberMarkups(IMember member) {
		try {
			List<Stripe> stripeList = new ArrayList<Stripe>();
			IJavaElement classElt = (IJavaElement) ((JDTMember) member).getResource();

			if (classElt instanceof IType) {
				IJavaElement[] classMembers = ((IType) classElt).getChildren();
				for (int i = 0; i < classMembers.length; i++) {
					Stripe memberStripe = makeStripeForJavaMember(classMembers[i]);
					if (memberStripe != null) {
						stripeList.add(memberStripe);
					}
				}
			}

			return stripeList;
		} catch (JavaModelException e) {
			MylarStatusHandler.fail(e, "Could not produce markups for member", false);
			return new ArrayList<Stripe>();
		}
	}

	/**
	 * Produces a stripe for the given member with the offset set to the line
	 * where the member appears in the file and the depth set to the number of
	 * lines in a method or 1 for fields. Returns null if the member was not
	 * interesting or if there was a problem getting all the required
	 * information.
	 */
	protected Stripe makeStripeForJavaMember(IJavaElement member) {
		try {

			IMylarElement memberMylarElement = MylarPlugin.getContextManager().getElement(member.getHandleIdentifier());

			if (memberMylarElement != null) {

				IDegreeOfInterest degreeOfInterest = memberMylarElement.getInterest();
				if (!degreeOfInterest.isInteresting() && !degreeOfInterest.isLandmark()) {
					return null;
				}

				// Set the depth of the stripe to the number of lines in the
				// member
				Stripe stripe = new Stripe();
				int stripeDepth = 1;
				if (member instanceof IMethod) {
					stripeDepth = Util.getLength((IMethod) member);
				}
				stripe.setDepth(stripeDepth);

				// Set the offset to the starting line of the member in the file
				int offset = 0;
				if (member instanceof ISourceReference) {
					try {
						offset = JDTUtils.getLineNumber(Util.getCompilationUnit(member), ((ISourceReference) member)
								.getSourceRange().getOffset());
					} catch (JavaModelException e) {
						MylarStatusHandler.fail(e, "could not get member line number", false);
					}
				}
				stripe.setOffset(offset);

				// Set the task description
				String taskHandle = MylarPlugin.getContextManager().getDominantContextHandleForElement(
						memberMylarElement);
				if (taskHandle == null) {
					return null;
				}
				ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(taskHandle, false);
				if (task == null) {
					return null;
				}
				IMarkupKind kind = new SimpleMarkupKind(task.getDescription());
				markupKindSet.add(kind);

				// Set the color
				Color kindColor = UiUtil.getBackgroundForElement(memberMylarElement, true);
				if (kindColor == null || colorTooLight(kindColor)) {
					kindColor = defaultStripeColor;
				}
				if (!colorSetByVisualiserMap.containsKey(kind)) {
					kindToColorMap.put(kind, kindColor);
				}

				List<IMarkupKind> kindList = new ArrayList<IMarkupKind>();
				kindList.add(kind);
				stripe.setKinds(kindList);
				return stripe;
			}

			return null;

		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "could not get stripe for java member", false);
			return null;
		}
	}

	/**
	 * True if the specified stripe color is too light to show up clearly in the
	 * visualiser
	 */
	protected boolean colorTooLight(Color color) {
		return (color.getRed() > 225 && color.getGreen() > 225 && color.getBlue() > 200);
	}

	/**
	 * Get the markups for a group. Group markups are a stacked set of member
	 * markups. Note: copied from
	 * org.eclipse.contribution.visualiser.SimpleMarkupProvider
	 */
	public List getGroupMarkups(IGroup group) {
		List<Stripe> stripes = new ArrayList<Stripe>();
		List kids = group.getMembers();
		int accumulatedOffset = 0;

		// Go through all the children of the group
		for (Iterator iter = kids.iterator(); iter.hasNext();) {
			IMember element = (IMember) iter.next();
			List l = getMemberMarkups(element);
			if (l != null) {
				for (Iterator iterator = l.iterator(); iterator.hasNext();) {
					Stripe elem = (Stripe) iterator.next();
					stripes.add(new Stripe(elem.getKinds(), elem.getOffset() + accumulatedOffset, elem.getDepth()));
				}
			}
			accumulatedOffset += element.getSize().intValue();
		}
		return stripes;
	}

	public SortedSet getAllMarkupKinds() {
		return markupKindSet;
	}

	public void setColorFor(IMarkupKind kind, Color color) {
		kindToColorMap.put(kind, color);
		colorSetByVisualiserMap.put(kind, null);
	}

	public Color getColorFor(IMarkupKind kind) {
		Color kindColor = kindToColorMap.get(kind);
		if (kindColor == null) {
			kindColor = defaultStripeColor;
		}
		return kindColor;
	}

	/** Go to the member represented by the clicked stripe */
	public boolean processMouseclick(IMember member, Stripe stripe, int buttonClicked) {
		if (buttonClicked != 1) {
			return false;
		}

		if (member instanceof JDTMember) {
			IJavaElement javaElement = ((JDTMember) member).getResource();
			if (javaElement != null) {
				JDTUtils.openInEditor(javaElement.getResource(), JDTUtils.getClassDeclLineNum(javaElement)
						+ stripe.getOffset());
			}
		}

		return false;
	}

	public void activate() {
	}

	public void deactivate() {
		
	}
}
