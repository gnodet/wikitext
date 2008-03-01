/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eugene Kuleshov
 */
public class NamedPattern {
	private final List<String> groups = new ArrayList<String>();

	private final Pattern pattern;

	public NamedPattern(String namedRegex, int flags) {
		pattern = Pattern.compile(prepare(namedRegex), flags);
	}

	public Pattern getPattern() {
		return pattern;
	}

	public Matcher matcher(CharSequence input) {
		return pattern.matcher(input);
	}

	public List<String> getGroups() {
		return groups;
	}

	public String groupName(int i) {
		return groups.get(i);
	}

	public String group(String name, Matcher m) {
		int n = groups.indexOf(name);
		return n == -1 ? null : m.group(n + 1);
	}

	private String prepare(String namedRegex) {
		StringBuilder regex = new StringBuilder();
		Matcher m = Pattern.compile("\\((\\{(\\S+?)})").matcher(namedRegex);
		int pos = 0;
		while (m.find()) {
			groups.add(m.group(2));
			regex.append(namedRegex.substring(pos, m.start(1)));
			pos = m.end();
		}
		return regex.append(namedRegex.substring(pos)).toString();
	}

}
