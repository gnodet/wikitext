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

package org.eclipse.mylyn.internal.tasks.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AttributeContainer;

/**
 * Class describing the configuration of products and components for a given
 * Bugzilla installation.
 * 
 * @author Rob Elves
 */
public class CopyOfRepositoryConfiguration extends AttributeContainer implements Serializable {

	/**
	 * Container for product information: name, components.
	 */
	private static class ProductEntry implements Serializable {

		private static final long serialVersionUID = 4120139521246741120L;

		List<String> components = new ArrayList<String>();

		List<String> milestones = new ArrayList<String>();

		String productName;

		List<String> versions = new ArrayList<String>();

		ProductEntry(String name) {
			this.productName = name;
		}

		void addComponent(String componentName) {
			if (!components.contains(componentName)) {
				components.add(componentName);
			}
		}

		void addTargetMilestone(String target) {
			milestones.add(target);
		}

		void addVersion(String name) {
			if (!versions.contains(name)) {
				versions.add(name);
			}
		}

		List<String> getComponents() {
			return components;
		}

		List<String> getTargetMilestones() {
			return milestones;
		}

		List<String> getVersions() {
			return versions;
		}
	}

	public static final String CONFIG_COMPONENT = "repository.configuration.component";

	public static final String CONFIG_KEYWORDS = "repository.configuration.keywords";

	public static final String CONFIG_MILESTONE = "repository.configuration.milestone";

	public static final String CONFIG_OPSYS = "repository.configuration.opsys";

	public static final String CONFIG_PLATFORM = "repository.configuration.platform";

	public static final String CONFIG_PRIORITY = "repository.configuration.priority";

	public static final String CONFIG_PRODUCT = "repository.configuration.product";

	public static final String CONFIG_RESOLUTION = "repository.configuration.resolution";

	public static final String CONFIG_SEVERITY = "repository.configuration.severity";

	public static final String CONFIG_STATUS = "repository.configuration.status";

	public static final String CONFIG_VERSION = "repository.configuration.version";

	private static final long serialVersionUID = -3623617786905114255L;

	private static final String VERSION_UNKNOWN = "unknown";

	private List<String> bugStatus = new ArrayList<String>();

	private List<String> components = new ArrayList<String>();

	private List<String> keywords = new ArrayList<String>();

	private List<String> milestones = new ArrayList<String>();

	private List<String> openStatusValues = new ArrayList<String>();

	private List<String> operatingSystems = new ArrayList<String>();

	private List<String> platforms = new ArrayList<String>();

	private List<String> priorities = new ArrayList<String>();

	private Map<String, ProductEntry> products = new HashMap<String, ProductEntry>();

	// master lists

	private String repositoryUrl = "<unknown>";

	private List<String> resolutionValues = new ArrayList<String>();

	private List<String> severities = new ArrayList<String>();

	private String version = VERSION_UNKNOWN;

	private List<String> versions = new ArrayList<String>();

	public CopyOfRepositoryConfiguration(AbstractAttributeFactory factory) {
		super(factory);
		// ignore
	}

//	/**
//	 * Adds a component to the given product.
//	 */
//	public void addComponent(String product, String component) {
//		if (!components.contains(component))
//			components.add(component);
//		ProductEntry entry = products.get(product);
//		if (entry == null) {
//			entry = new ProductEntry(product);
//			products.put(product, entry);
//		}
//		entry.addComponent(component);
//	}
//
//	public void addKeyword(String keyword) {
//		keywords.add(keyword);
//	}
//
//	public void addOpenStatusValue(String value) {
//		openStatusValues.add(value);
//	}
//
//	public void addOS(String os) {
//		operatingSystems.add(os);
//	}
//
//	public void addPlatform(String platform) {
//		platforms.add(platform);
//	}
//
//	public void addPriority(String priority) {
//		priorities.add(priority);
//	}
//
//	/**
//	 * Adds a product to the configuration.
//	 */
//	public void addProduct(String name) {
//		if (!products.containsKey(name)) {
//			ProductEntry product = new ProductEntry(name);
//			products.put(name, product);
//		}
//	}
//
//	public void addResolution(String res) {
//		resolutionValues.add(res);
//	}
//
//	public void addSeverity(String severity) {
//		severities.add(severity);
//
//	}
//
//	public void addStatus(String status) {
//		bugStatus.add(status);
//	}
//
//	public void addTargetMilestone(String product, String target) {
//		if (!milestones.contains(target))
//			milestones.add(target);
//		ProductEntry entry = products.get(product);
//		if (entry == null) {
//			entry = new ProductEntry(product);
//			products.put(product, entry);
//		}
//
//		entry.addTargetMilestone(target);
//
//	}
//
//	public void addVersion(String product, String version) {
//		if (!versions.contains(version))
//			versions.add(version);
//		ProductEntry entry = products.get(product);
//		if (entry == null) {
//			entry = new ProductEntry(product);
//			products.put(product, entry);
//		}
//		entry.addVersion(version);
//	}
//
//	public List<String> getComponents() {
//		return components;
//	}
//
//	// /**
//	// * Adds a list of components to the given product.
//	// */
//	// public void addComponents(String product, List<String> components) {
//	// ProductEntry entry = products.get(product);
//	// if (entry == null) {
//	// entry = new ProductEntry(product);
//	// products.put(product, entry);
//	// }
//	// for (String component : components) {
//	// entry.addComponent(component);
//	// }
//	// }
//	// /**
//	// * Adds a list of components to the given product.
//	// */
//	// public void addComponents(String product, List<String> components) {
//	// ProductEntry entry = products.get(product);
//	// if (entry == null) {
//	// entry = new ProductEntry(product);
//	// products.put(product, entry);
//	// }
//	// for (String component : components) {
//	// entry.addComponent(component);
//	// }
//	// }
//
//	/**
//	 * Returns an array of names of component that exist for a given product or
//	 * <code>null</code> if the product does not exist.
//	 */
//	public List<String> getComponents(String product) {
//		ProductEntry entry = products.get(product);
//		if (entry != null) {
//			return entry.getComponents();
//		} else
//			return Collections.emptyList();
//	}
//
//	// /**
//	// * Adds a list of components to the given product.
//	// */
//	// public void addVersions(String product, List<String> versions) {
//	// ProductEntry entry = products.get(product);
//	// if (entry == null) {
//	// entry = new ProductEntry(product);
//	// products.put(product, entry);
//	// }
//	// for (String version : versions) {
//	// entry.addVersion(version);
//	// }
//	// }
//
//	public String getInstallVersion() {
//		return version;
//	}
//
//	public List<String> getKeywords() {
//		return keywords;
//	}
//
//	public List<String> getOpenStatusValues() {
//		return openStatusValues;
//	}
//
//	/*
//	 * Intermediate step until configuration is made generic.
//	 */
//	public List<String> getOptionValues(String attributeKey, String product) {		
//		if (attributeKey.equals(CONFIG_PRODUCT))
//			return getProducts();
//		if (attributeKey.equals(CONFIG_MILESTONE))
//			return getTargetMilestones(product);
//		if (attributeKey.equals(CONFIG_STATUS))
//			return getStatusValues();
//		if (attributeKey.equals(CONFIG_VERSION))
//			return getVersions(product);
//		if (attributeKey.equals(CONFIG_COMPONENT))
//			return getComponents(product);
//		if (attributeKey.equals(CONFIG_PLATFORM))
//			return getPlatforms();
//		if (attributeKey.equals(CONFIG_OPSYS))
//			return getOSs();
//		if (attributeKey.equals(CONFIG_PRIORITY))
//			return getPriorities();
//		if (attributeKey.equals(CONFIG_SEVERITY))
//			return getSeverities();
//		if (attributeKey.equals(CONFIG_KEYWORDS))
//			return getKeywords();
//		if (attributeKey.equals(CONFIG_RESOLUTION))
//			return getResolutions();
//
//		return new ArrayList<String>();
//	}
//
//	/**
//	 * Returns an array of names of valid OS values.
//	 */
//	public List<String> getOSs() {
//		//return operatingSystems;
//		return getAttributeValues("op_sys");
//	}
//
//	/**
//	 * Returns an array of names of valid platform values.
//	 */
//	public List<String> getPlatforms() {
//		return platforms;
//	}
//
//	/**
//	 * Returns an array of names of valid platform values.
//	 */
//	public List<String> getPriorities() {
//		return priorities;
//	}
//
//	/**
//	 * Returns an array of names of current products.
//	 */
//	public List<String> getProducts() {
//		ArrayList<String> productList = new ArrayList<String>(products.keySet());
//		Collections.sort(productList);
//		return productList;
//	}
//
//	public List<String> getResolutions() {
//		return resolutionValues;
//	}
//
//	/**
//	 * Returns an array of names of valid severity values.
//	 */
//	public List<String> getSeverities() {
//		return severities;
//	}
//
//	public List<String> getStatusValues() {
//		return bugStatus;
//	}
//
//	public List<String> getTargetMilestones() {
//		return milestones;
//	}
//
//	public List<String> getTargetMilestones(String product) {
//		ProductEntry entry = products.get(product);
//		if (entry != null) {
//			return entry.getTargetMilestones();
//		} else
//			return Collections.emptyList();
//	}
//
//	public List<String> getVersions() {
//		return versions;
//	}
//
//	/**
//	 * Returns an array of names of versions that exist for a given product or
//	 * <code>null</code> if the product does not exist.
//	 */
//	public List<String> getVersions(String product) {
//		ProductEntry entry = products.get(product);
//		if (entry != null) {
//			return entry.getVersions();
//		} else
//			return Collections.emptyList();
//	}
//
//	public void setInstallVersion(String version) {
//		this.version = version;
//	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}
	
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

}
