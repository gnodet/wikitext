/**
 *
 */
package org.eclipse.mylyn.sandbox.dev.spy.adapters;

import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author maarten
 */
public class AbstractTaskCategoryPropertySource implements IPropertySource {
	/**
	 *
	 */
	private static final String SUMMARY = "summary";

	private AbstractTaskCategory category;
	/**
	 * @param adaptableObject
	 */
	public AbstractTaskCategoryPropertySource(AbstractTaskCategory adaptableObject) {
		this.category = adaptableObject;
	}

	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		TextPropertyDescriptor summary = new TextPropertyDescriptor(SUMMARY, "Summary");
		summary.setCategory(category.getClass().getName());
		return new IPropertyDescriptor[] {
				summary
		};
	}

	public Object getPropertyValue(Object id) {
		if(SUMMARY.equals(id)) {
			return category.getSummary();
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub

	}

}
