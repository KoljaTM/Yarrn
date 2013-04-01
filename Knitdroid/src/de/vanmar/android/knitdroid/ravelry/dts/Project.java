package de.vanmar.android.knitdroid.ravelry.dts;

import java.util.LinkedList;
import java.util.List;

/** Data structure to represent projects in Ravelry */
public class Project {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public static List<Project> fromString(final String body) {
		final LinkedList<Project> list = new LinkedList<Project>();
		final Project project = new Project();
		project.setName(body);
		list.add(project);
		return list;
	}
}
