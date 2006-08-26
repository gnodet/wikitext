/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests.support;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.eclipse.mylar.internal.trac.core.TracXmlRpcClient;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;

/**
 * @author Steffen Pingel
 */
public class XmlRpcServer {

	public abstract class AbstractTracItem {

		public abstract void delete() throws Exception;

		void itemCreated() {
			data.items.add(this);
		}

		void itemDeleted() {
			data.items.remove(this);
		}

	}

	/**
	 * Represents a Trac type with multiple attributes such as a milestone.
	 */
	public class ModelEnum extends AbstractTracItem {

		private String[] attributes;

		private String id;

		private String module;

		public ModelEnum(String module, String id, String... attributes) {
			this.module = module;
			this.id = id;
			this.attributes = attributes;
		}

		public ModelEnum create(Object... params) throws Exception {
			call(module + ".create", id, toMap(params));
			itemCreated();
			return this;
		}

		public void delete() throws Exception {
			call(module + ".delete", id);
			itemDeleted();
		}

		public void deleteAll() throws Exception {
			String[] ids = getAll();
			for (String id : ids) {
				call(module + ".delete", id);
			}
		}

		public ModelEnum deleteAndCreate(Object... params) throws Exception {
			if (Arrays.asList(getAll()).contains(id)) {
				delete();
			}

			return create(params);
		}

		public Object[] get() throws Exception {
			Hashtable values = (Hashtable) call(module + ".get", id);
			Object[] result = new Object[values.size()];
			for (int i = 0; i < result.length && i < attributes.length; i++) {
				result[i] = values.get(attributes[i]);
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		public String[] getAll() throws Exception {
			return Arrays.asList((Object[]) call(module + ".getAll")).toArray(new String[0]);
		}

		private Hashtable<String, Object> toMap(Object... params) {
			Hashtable<String, Object> attrs = new Hashtable<String, Object>();
			for (int i = 0; i < attributes.length && i < params.length; i++) {
				attrs.put((String) attributes[i], params[i]);
			}
			return attrs;
		}

		public ModelEnum update(Object... params) throws Exception {
			call(module + ".update", id, toMap(params));
			return this;
		}

	}

	/**
	 * Records changes to the repository.
	 */
	public class TestData {

		// all created items
		List<AbstractTracItem> items = new ArrayList<AbstractTracItem>();
		
		// all created tickets
		public List<Ticket> tickets = new ArrayList<Ticket>();
		
		public int attachmentTicketId = 5;
		
		public int htmlEntitiesTicketId = 6;
		
		public int offlineHandlerTicketId = 7;
		
		/**
		 * Undo all changes.
		 */
		public void cleanup() throws Exception {
			while (!items.isEmpty()) {
				items.get(0).delete();
			}
		}

	}

	/**
	 * Represents a Trac ticket.
	 */
	public class Ticket extends AbstractTracItem {

		private Integer id;

		public Ticket(Integer id) {
			this.id = id;
		}

		public Ticket create(String summary, String description) throws Exception {
			this.id = (Integer) call("ticket.create", summary, description, new Hashtable());
			if (id == null) {
				throw new RuntimeException("Could not create ticket: " + summary);
			}
			itemCreated();
			return this;
		}

		public void delete() throws Exception {
			call("ticket.delete", id);
			itemDeleted();
		}

		public void deleteAll() throws Exception {
			Integer[] ids = getAll();
			for (Integer id : ids) {
				call("ticket.delete", id);
			}
		}

		public Object getValue(String key) throws Exception {
			return getValues().get(key);
		}

		public Map getValues() throws Exception {
			return (Map) ((Object[]) call("ticket.get", id))[3];
		}

		@SuppressWarnings("unchecked")
		public Integer[] getAll() throws Exception {
			return Arrays.asList((Object[]) call("ticket.query", "order=id")).toArray(new Integer[0]);
		}

		public int getId() {
			return id;
		}

		@Override
		protected void itemCreated() {
			super.itemCreated();
			data.tickets.add(this);
		}
		
		@Override
		protected void itemDeleted() {
			super.itemDeleted();
			data.tickets.remove(this);
		}
		
		public Ticket update(String comment, String key, String value) throws Exception {
			Hashtable<String, Object> attrs = new Hashtable<String, Object>();
			attrs.put(key, value);
			call("ticket.update", id, comment, attrs);
			return this;
		}

	}

	/**
	 * Represents a Trac type that has a single attribute such as a priority.
	 */
	public class TicketEnum extends AbstractTracItem {

		private String id;

		private String module;

		public TicketEnum(String module, String id) {
			this.module = module;
			this.id = id;
		}

		public TicketEnum create(String param) throws Exception {
			call(module + ".create", id, param);
			itemCreated();
			return this;
		}

		public void delete() throws Exception {
			call(module + ".delete", id);
			itemDeleted();
		}

		public void deleteAll() throws Exception {
			String[] ids = getAll();
			for (String id : ids) {
				call(module + ".delete", id);
			}
		}

		public TicketEnum deleteAndCreate(String param) throws Exception {
			if (Arrays.asList(getAll()).contains(id)) {
				delete();
			}

			return create(param);
		}

		public String get() throws Exception {
			return (String) call(module + ".get", id);
		}

		@SuppressWarnings("unchecked")
		public String[] getAll() throws Exception {
			return Arrays.asList((Object[]) call(module + ".getAll")).toArray(new String[0]);
		}

		public TicketEnum update(String param) throws Exception {
			call(module + ".update", id, param);
			return this;
		}

	}

	private XmlRpcClient client;

	private TestData data;

	private String password;

	private TracXmlRpcClient repository;

	private String url;

	private String username;

	public XmlRpcServer(String url, String username, String password) throws Exception {
		this.url = url;
		this.username = username;
		this.password = password;

		this.data = new TestData();

		this.repository = new TracXmlRpcClient(new URL(url), Version.XML_RPC, username, password);
		this.client = repository.getClient();
	}

	private Object call(String method, Object... parameters) throws XmlRpcException, IOException {
		Vector<Object> params = new Vector<Object>(parameters.length);
		for (Object parameter : parameters) {
			params.add(parameter);
		}

		Object result = client.execute(method, params);
		if (result instanceof XmlRpcException) {
			throw (XmlRpcException) result;
		}
		return result;
	}

	public TestData getData() {
		return data;
	}

	public String getPassword() {
		return password;
	}

	public TracXmlRpcClient getRepository() throws MalformedURLException {
		return repository;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public Ticket ticket() throws Exception {
		return new Ticket(null);
	}

	public Ticket ticket(int id) throws Exception {
		return new Ticket(id);
	}

	public ModelEnum ticketComponent(String id) throws Exception {
		return new ModelEnum("ticket.component", id, "owner", "description");
	}

	public ModelEnum ticketMilestone(String id) throws Exception {
		return new ModelEnum("ticket.milestone", id, "due", "completed", "description");
	}

	public TicketEnum ticketPriority(String id) throws Exception {
		return new TicketEnum("ticket.priority", id);
	}

	public TicketEnum ticketSeverity(String id) throws Exception {
		return new TicketEnum("ticket.severity", id);
	}

	public TicketEnum ticketStatus(String id) throws Exception {
		return new TicketEnum("ticket.status", id);
	}

	public TicketEnum ticketType(String id) throws Exception {
		return new TicketEnum("ticket.type", id);
	}

	public ModelEnum ticketVersion(String id) throws Exception {
		return new ModelEnum("ticket.version", id, "time", "description");
	}

}
