/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.core.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;

/**
 * @author Jingwen Ou
 */
public class CommentGroupStrategy {
	public class CommentGroup {
		private final List<TaskAttribute> commentAttributes;

		private final String groupName;

		CommentGroup(String groupName, List<TaskAttribute> commentAttributes) {
			this.groupName = groupName;
			this.commentAttributes = commentAttributes;
		}

		public List<TaskAttribute> getCommentAttributes() {
			return commentAttributes;
		}

		public String getGroupName() {
			return groupName;
		}
	}

	private TaskComment convertToTaskComment(TaskDataModel taskDataModel, TaskAttribute commentAttribute) {
		TaskComment taskComment = new TaskComment(taskDataModel.getTaskRepository(), taskDataModel.getTask(),
				commentAttribute);
		taskDataModel.getTaskData().getAttributeMapper().updateTaskComment(taskComment, commentAttribute);

		return taskComment;
	}

	/**
	 * Groups comment according to "Older", "Recent" and "Current".
	 * 
	 * @param taskDataModel
	 *            extracts groups of comment for the model
	 * @return list of comment groups. Groups will be ignored if there are no comments under them.
	 */
	public List<CommentGroup> groupCommentsFromModel(TaskDataModel taskDataModel) {
		List<TaskAttribute> taskAttributes = taskDataModel.getTaskData().getAttributeMapper().getAttributesByType(
				taskDataModel.getTaskData(), TaskAttribute.TYPE_COMMENT);
		List<CommentGroup> commentGroups = new ArrayList<CommentGroup>();
		List<TaskAttribute> comments = new ArrayList<TaskAttribute>();

		int currentFromIndex = -1;
		String currentPersonId = taskDataModel.getTaskRepository().getUserName();

		// current
		List<TaskAttribute> current = new ArrayList<TaskAttribute>();

		// update task comment and get current group index
		TaskComment latestComment = null;
		for (int i = 0; i < taskAttributes.size(); i++) {
			TaskAttribute commentAttribute = taskAttributes.get(i);
			final TaskComment taskComment = convertToTaskComment(taskDataModel, commentAttribute);
			comments.add(commentAttribute);

			// add all incoming changes
			if (taskDataModel.hasIncomingChanges(taskComment.getTaskAttribute())) {
				current.add(commentAttribute);
			}

			IRepositoryPerson person = taskComment.getAuthor();
			if (person != null && person.getPersonId().equals(currentPersonId)) {
				currentFromIndex = i;
				latestComment = taskComment;
			}
		}

		if (current.size() > 0) {
			comments.removeAll(current);
		}

		// group by last author
		if (currentFromIndex != -1 && currentFromIndex < comments.size()) {
			// bug 238038 comment #58, if the latest comment is generated automatically, lookback one comment
			if (latestComment != null && latestComment.getText().contains(AttachmentUtil.CONTEXT_DESCRIPTION)
					&& currentFromIndex > 0) {
				TaskComment secondLatestComment = convertToTaskComment(taskDataModel,
						comments.get(currentFromIndex - 1));
				IRepositoryPerson person = secondLatestComment.getAuthor();
				if (person != null && person.getPersonId().equals(currentPersonId)) {
					currentFromIndex--;
				}
			}

			current.addAll(0, new ArrayList<TaskAttribute>(comments.subList(currentFromIndex, comments.size())));
			if (current.size() > 0) {
				comments.removeAll(current);
			}
		}

		// recent
		int recentFromIndex = comments.size() - 20 < 0 ? 0 : comments.size() - 20;
		List<TaskAttribute> recent = new ArrayList<TaskAttribute>(comments.subList(recentFromIndex, comments.size()));
		if (recent.size() > 0) {
			comments.removeAll(recent);
		}

		// ignore groups that have no comment

		// the rest goes to Older
		if (comments.size() > 0) {
			commentGroups.add(new CommentGroup("Older", comments));
		}

		if (recent.size() > 0) {
			commentGroups.add(new CommentGroup("Recent", recent));
		}

		if (current.size() > 0) {
			commentGroups.add(new CommentGroup("Current", current));
		}

		return commentGroups;
	}
}
