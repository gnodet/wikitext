package org.eclipse.mylar.viz.seesoft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Provides horizontal stripes that represent interesting members
 * of a class in the visualiser view
 * 
 * @author Wesley Coelho
 */
public class JavaContextMarkupProvider implements IMarkupProvider {

	public static final String LANDMARK_KIND = "Landmark";
	public static final String INTERESTING_KIND = "Interesting";
	protected IMarkupKind landmarkKind = null;
	protected IMarkupKind interestingKind = null;
	
	//From org.eclipse.contribution.visualiser.utils.ColorUtils:
	private static Color blue1 = new Color(null, new RGB(128,128,255));
	private static Color blue2 = new Color(null, new RGB(64,64,255));
	private static Color blue5 = new Color(null, new RGB(0,0,128));
	
	public Color landmarkColor = blue2;
	public Color interestingColor = blue1;
	public Color defaultColor = blue5;
	
	public void initialise() {
		landmarkKind = new SimpleMarkupKind(LANDMARK_KIND);
		interestingKind = new SimpleMarkupKind(INTERESTING_KIND);
	}

	/**
	 * Returns a list of Stripes representing the interesting
	 * declarations in the specified IMember where the IMember
	 * represents a Java class.
	 */
	public List getMemberMarkups(IMember member) {
		try {
			List<Stripe> stripeList = new ArrayList<Stripe>();
			IJavaElement classElt = (IJavaElement)((JDTMember)member).getResource();
			
			if (classElt instanceof IType){
				IJavaElement[] classMembers = ((IType)classElt).getChildren();
				for(int i = 0; i < classMembers.length; i++){
					Stripe memberStripe = makeStripeForJavaMember(classMembers[i]);
					if (memberStripe != null){
						stripeList.add(memberStripe);
					}
				}
			}
			
			return stripeList;
		} catch (JavaModelException e) {
			MylarPlugin.fail(e, "Could not produce markups for member", false);
			return new ArrayList<Stripe>();
		}
	}

	/**
	 * Produces a stripe for the given member with the offset set to
	 * the line where the member appears in the file and the 
	 * depth set to the number of lines in a method or 1 for fields.
	 */
	protected Stripe makeStripeForJavaMember(IJavaElement member){
		Stripe stripe = new Stripe();
		IMylarElement memberContextNode = MylarPlugin.getContextManager().getElement(member.getHandleIdentifier());

		if (memberContextNode != null){
			
			int stripeDepth = 1;
			if (member instanceof IMethod){
				stripeDepth = Util.getLength((IMethod)member);
			}
			stripe.setDepth(stripeDepth);
			
			int offset = 0;
			if (member instanceof ISourceReference){
				try {
					offset = JDTUtils.getLineNumber(Util.getCompilationUnit(member),  ((ISourceReference)member).getSourceRange().getOffset());
				} catch (JavaModelException e) {
					MylarPlugin.fail(e, "could not get member line number",false);
				}
			}
			stripe.setOffset(offset);
			
			List<IMarkupKind> kindList = new ArrayList<IMarkupKind>();
			if (memberContextNode.getDegreeOfInterest().isLandmark()){
				kindList.add(landmarkKind);
			}
			else if(memberContextNode.getDegreeOfInterest().isInteresting()){
				kindList.add(interestingKind);
			}
			stripe.setKinds(kindList);
		}
		return stripe;
	}
	
	
	/**
	 * Get the markups for a group. Group markups are a stacked set of member markups.
	 * Note: copied from org.eclipse.contribution.visualiser.SimpleMarkupProvider
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
		SortedSet<IMarkupKind> kindSet = new TreeSet<IMarkupKind>();
		kindSet.add(landmarkKind);
		kindSet.add(interestingKind);
		return kindSet;
	}

	public boolean changeMode() {
		return false;
	}

	public boolean hasMultipleModes() {
		return false;
	}

	public void setColorFor(IMarkupKind kind, Color color) {
		if (kind.getName().equals(INTERESTING_KIND)){
			interestingColor = color;
		}
		else if (kind.getName().equals(LANDMARK_KIND)){
			landmarkColor = color;
		}	
	}

	public Color getColorFor(IMarkupKind kind) {
		if (kind.getName().equals(INTERESTING_KIND)){
			return interestingColor;
		}
		else if (kind.getName().equals(LANDMARK_KIND)){
			return landmarkColor;
		}
		
		return defaultColor;
	}

	/** Go to the member represented by the clicked stripe */
	public boolean processMouseclick(IMember member, Stripe stripe, int buttonClicked) {
		if(buttonClicked != 1){
			return false;	
		}
		
		if(member instanceof JDTMember) {
			IJavaElement javaElement = ((JDTMember)member).getResource();
			if(javaElement != null) {
				JDTUtils.openInEditor(javaElement.getResource(), JDTUtils.getClassDeclLineNum(javaElement) + stripe.getOffset());
			}
		}
		
		return false;
	}

	public void activate() {
		// TODO Auto-generated method stub
		
	}

	public void deactivate() {
		// TODO Auto-generated method stub
		
	}
}
