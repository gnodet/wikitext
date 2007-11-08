/**
 *
 */
package org.eclipse.mylyn.internal.sandbox.dev.properties;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author maarten
 */
public class MylynPropertiesSourceAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IPropertySource.class && adaptableObject instanceof TaskRepository)
			return new TaskRepositoryPropertiesSource((TaskRepository)adaptableObject);
		if (adapterType == IPropertySource.class && adaptableObject instanceof AbstractTask)
			return new AbstractTaskPropertiesSource((AbstractTask)adaptableObject);
		if (adapterType == IPropertySource.class && adaptableObject instanceof AbstractRepositoryQuery)
			return new RepositoryQueryPropertySource((AbstractRepositoryQuery) adaptableObject);
		if (adapterType == IPropertySource.class && adaptableObject instanceof AbstractTaskCategory)
			return new AbstractTaskCategoryPropertySource( (AbstractTaskCategory) adaptableObject);
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		// TODO Auto-generated method stub
		return new Class[] {IPropertySource.class};
	}

}
