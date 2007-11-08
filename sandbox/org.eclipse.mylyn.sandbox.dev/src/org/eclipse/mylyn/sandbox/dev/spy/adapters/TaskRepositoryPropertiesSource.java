/**
 *
 */
package org.eclipse.mylyn.sandbox.dev.spy.adapters;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author maarten
 */
public class TaskRepositoryPropertiesSource implements IPropertySource {
	/**
	 *
	 */
	private TaskRepository repository;

	/**
	 * @param adaptableObject
	 */
	public TaskRepositoryPropertiesSource(TaskRepository repository) {
		// TODO Auto-generated constructor stub
		this.repository = repository;
	}

	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		Set<String> properties = repository.getProperties().keySet();
		IPropertyDescriptor[] result = new IPropertyDescriptor[properties.size()];
		Iterator<String> keys = properties.iterator();
		int i = 0;
		while(keys.hasNext()) {
			String key = keys.next();
			PropertyDescriptor descriptor = new TextPropertyDescriptor(key, key);
			descriptor.setCategory(repository.getClass().getName());
			result[i] = descriptor;
			++i;
		}
		return result;
	}

	public Object getPropertyValue(Object id) {
		return repository.getProperty((String) id);
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
