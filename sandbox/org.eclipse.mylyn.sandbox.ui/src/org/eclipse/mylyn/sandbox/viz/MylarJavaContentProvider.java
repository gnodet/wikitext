package org.eclipse.mylar.sandbox.viz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.contribution.visualiser.VisualiserPlugin;
import org.eclipse.contribution.visualiser.core.ProviderManager;
import org.eclipse.contribution.visualiser.core.resources.VisualiserImages;
import org.eclipse.contribution.visualiser.interfaces.IContentProvider;
import org.eclipse.contribution.visualiser.interfaces.IGroup;
import org.eclipse.contribution.visualiser.interfaces.IMember;
import org.eclipse.contribution.visualiser.jdtImpl.JDTGroup;
import org.eclipse.contribution.visualiser.jdtImpl.JDTMember;
import org.eclipse.contribution.visualiser.utils.JDTUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.JavaStructureBridge;


/**
 * Provides interesting java packages and classes for display in the Visualiser view.
 * 
 * Note: This currently updates after the interest changes for any context node. 
 * If performance is a problem, this could be modified to only update when a 
 * landmark is added or removed. 
 * 
 * @author Wesley Coelho
 */
public class MylarJavaContentProvider implements IContentProvider, IMylarContextListener {

	/** IGroups representing packages with interesting elements in them */
	protected List<JDTGroup> groups = new ArrayList<JDTGroup>();	
	
	/** True if there is currently an active context */
	protected boolean contextActive = false;

	/** True if the visualiser has set this provider active*/
	protected boolean viewActive = false;
	
	public void initialise(){
		MylarPlugin.getContextManager().addListener(this);
	}

	/**
	 * Returns all registered groups
	 * @see org.eclipse.contribution.visualiser.interfaces.IContentProvider#getAllGroups()
	 * @return List of IGroups
	 */
	public List getAllGroups() {
		buildModel();
		return groups;
	}
	
	public List getAllMembers(IGroup group) {
		return group.getMembers();
	}

	/**
	 * Returns the List of all IMembers in all registered groups
	 * Adapted from org.eclipse.contribution.visualiser.SimpleContentProvider
	 */
	public List getAllMembers() {
		List grps = getAllGroups();
		List<IMember> members = new ArrayList<IMember>();
		if (grps == null) return members;
		Iterator iter = grps.iterator();
		while (iter.hasNext()) {
			IGroup grp = (IGroup)iter.next();
			List membersInGroup = getAllMembers(grp);
			Iterator iter2 = membersInGroup.iterator();
			while (iter2.hasNext()) {
				IMember im = (IMember)iter2.next();
				members.add(im);
			}
		}
		return members;
	}
	
	/** 
	 * Constructs the model to be displayed in the visualiser by requesting
	 * the active context's interesting elements and converting them to the
	 * groups and members structure
	 */
	protected void buildModel(){
		try {
			if(!(ProviderManager.getContentProvider().equals(this))){
				return;
			}

			groups.clear();
		
			if (!contextActive || !viewActive){
				return;
			}
			
			List<IMylarElement> interestingNodes = MylarPlugin.getContextManager().getActiveContext().getInteresting();
			JavaStructureBridge jBridge = new JavaStructureBridge();
			
			for (int i = 0; i < interestingNodes.size(); i++){
				IMylarElement node = interestingNodes.get(i);
				
				if (node.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)){
					IJavaElement jElement = (IJavaElement) jBridge.getObjectForHandle(node.getHandleIdentifier());
					
					if (jElement.getElementType() == IJavaElement.TYPE){
						JDTMember javaType = new JDTMember(jBridge.getName(jElement), jElement);
						addTypeToGroups(javaType);
					}
				}
			}
			
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "Could not provide data for the visualiser", false);
		}
	}
	
	/**
	 * Adds the specified visualiser member to the group representing its package
	 * in the groups list. The package group is created as required.
	 */
	protected void addTypeToGroups(JDTMember javaType){
		
		IJavaElement packageElt = javaType.getResource().getParent();
		while (packageElt.getParent() != null){
			if (packageElt.getElementType() == IJavaElement.PACKAGE_FRAGMENT){
				break;
			}
			packageElt = packageElt.getParent();
		}
		
		if (packageElt.getElementType() != IJavaElement.PACKAGE_FRAGMENT){
			MylarPlugin.fail(new Exception("Could not find package for java element"), "Could not find package for java element", false);
			return;
		}
		
		ICompilationUnit compilationUnit = Util.getCompilationUnit(javaType.getResource());
		if (compilationUnit == null || !compilationUnit.exists()){
			return;
		}
		
		javaType.setSize(Util.getLength(compilationUnit));
		
		JDTGroup parentGroup = new JDTGroup(packageElt.getElementName());
		
		//Check for an existing group for this package
		for (int i = 0; i < groups.size(); i++){
			JDTGroup currGroup = groups.get(i);
			
			if (currGroup.getFullname().equals(parentGroup.getFullname())){
				parentGroup = currGroup;
				break;
			}
		}
		
		List<IMember> memberList = new ArrayList<IMember>();
		memberList.add(javaType);
		parentGroup.addMembers(memberList);
		
		if (!groups.contains(parentGroup)){
			groups.add(parentGroup);
		}
	}
	
	/**
	 * @see org.eclipse.contribution.visualiser.interfaces.IContentProvider#getMemberViewIcon()
	 */
	public ImageDescriptor getMemberViewIcon() {
		return VisualiserImages.CLASS_VIEW;
	}


	/**
	 * @see org.eclipse.contribution.visualiser.interfaces.IContentProvider#getGroupViewIcon()
	 */
	public ImageDescriptor getGroupViewIcon() {
		return VisualiserImages.PACKAGE_VIEW;
	}


	/**
	 * Process a mouse click on a member
	 * @see org.eclipse.contribution.visualiser.interfaces.IContentProvider#processMouseclick(IMember, boolean, int)
	 * Copied from JDTContentProvider
	 */
	public boolean processMouseclick(
		IMember member,
		boolean markupWasClicked,
		int buttonClicked) {
		
		if(buttonClicked != 1){
			return true;	
		}
		if(markupWasClicked) {
			return false;
		}
		if(member instanceof JDTMember) {
			IJavaElement jEl = ((JDTMember)member).getResource();
			if(jEl != null) {
				JDTUtils.openInEditor(jEl.getResource(), JDTUtils.getClassDeclLineNum(jEl));
			}
		}
		
		return false;
	}


	public String getEmptyMessage() {
		return "No active context to display.";
	}

	
	//Begin implementation of IMylarContextListener
	
	public void contextActivated(IMylarContext context) {
		contextActive = true;
		VisualiserPlugin.refresh();
	}

	public void contextDeactivated(IMylarContext context) {
		contextActive = false;
		VisualiserPlugin.refresh();
	}

	public void presentationSettingsChanging(UpdateKind kind) {

	}

	public void presentationSettingsChanged(UpdateKind kind) {

	}

	public void interestChanged(IMylarElement node) {
		VisualiserPlugin.refresh();
	}

	public void interestChanged(List<IMylarElement> nodes) {
		VisualiserPlugin.refresh();
	}

	public void nodeDeleted(IMylarElement node) {
		VisualiserPlugin.refresh();
	}

	public void landmarkAdded(IMylarElement node) {
		VisualiserPlugin.refresh();
	}

	public void landmarkRemoved(IMylarElement node) {
		VisualiserPlugin.refresh();
	}

	public void edgesChanged(IMylarElement node) {

	}

	public void activate() {
		viewActive = true;
	}

	public void deactivate() {
		viewActive = false;
	}
	
}
