/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * (C) 2013 - 2015,
 * Chair of Geoinformatics,
 * Technische Universitaet Muenchen, Germany
 * http://www.gis.bgu.tum.de/
 * 
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 * 
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Muenchen <http://www.moss.de/>
 * 
 * The 3D City Database Importer/Exporter program is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */
package org.citydb.config.project.general;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(name="PathType", propOrder={
		"mode",
		"standardPath",
		"lastUsedPath"
})
public class Path {
	@XmlElement(required=true)
	private PathMode mode = PathMode.LASTUSED;
	private String standardPath = "";
	private String lastUsedPath = "";
	
	public Path() {
	}
	
	public boolean isSetLastUsedMode() {
		return mode == PathMode.LASTUSED;
	}

	public boolean isSetStandardMode() {
		return mode == PathMode.STANDARD;
	}
	
	public PathMode getPathMode() {
		return mode;
	}

	public void setPathMode(PathMode mode) {
		this.mode = mode;
	}

	public String getStandardPath() {	
		return standardPath;
	}

	public void setStandardPath(String standardPath) {
		this.standardPath = standardPath;
	}

	public String getLastUsedPath() {
		return lastUsedPath;
	}

	public void setLastUsedPath(String lastUsedPath) {
		this.lastUsedPath = lastUsedPath;
	}
	
}
