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
package org.citydb.database;

import org.citydb.api.database.DatabaseMetaData;
import org.citydb.api.database.DatabaseSrs;
import org.citydb.api.log.LogLevel;
import org.citydb.log.Logger;

public class DatabaseMetaDataImpl implements DatabaseMetaData {
	private final Logger LOG = Logger.getInstance();	
	
	// database related information
	private String cityDBVersion;
	private int cityDBMajorVersion;
	private int cityDBMinorVersion;
	private int cityDBMinorRevision;
	private String databaseProductName;
	private String databaseProductString;
	private int databaseMajorVersion;
	private int databaseMinorVersion;
	
	// 3DCityDB related information
	private DatabaseSrs srs =  DatabaseSrs.createDefaultSrs();
	private Versioning versioning = Versioning.OFF;
	
	public void reset() {
		databaseProductName = null;
		databaseProductString = null;
		databaseMajorVersion = 0;
		databaseMinorVersion = 0;
		srs = DatabaseSrs.createDefaultSrs();
		versioning = Versioning.OFF;
	}

	@Override
	public String getCityDBVersion() {
		return cityDBVersion;
	}
	
	public void setCityDBVersion(String cityDBVersion) {
		this.cityDBVersion = cityDBVersion;
	}

	@Override
	public int getCityDBMajorVersion() {
		return cityDBMajorVersion;
	}
	
	public void setCityDBMajorVersion(int cityDBMajorVersion) {
		this.cityDBMajorVersion = cityDBMajorVersion;
	}

	@Override
	public int getCityDBMinorVersion() {
		return cityDBMinorVersion;
	}
	
	public void setCityDBMinorVersion(int cityDBMinorVersion) {
		this.cityDBMinorVersion = cityDBMinorVersion;
	}

	@Override
	public int getCityDBMinorRevision() {
		return cityDBMinorRevision;
	}
	
	public void setCityDBMinorRevision(int cityDBMinorRevision) {
		this.cityDBMinorRevision = cityDBMinorRevision;
	}
	
	@Override
	public String getDatabaseProductName() {
		return databaseProductName;
	}

	public void setDatabaseProductName(String databaseProductName) {
		this.databaseProductName = databaseProductName;
	}

	@Override
	public String getDatabaseProductVersion() {
		return databaseProductString;
	}
	
	@Override
	public String getShortDatabaseProductVersion() {
		return getDatabaseProductVersion().replaceAll("\\n.*", "");
	}

	public void setDatabaseProductVersion(String databaseProductString) {
		this.databaseProductString = databaseProductString;
	}

	@Override
	public int getDatabaseMajorVersion() {
		return databaseMajorVersion;
	}

	public void setDatabaseMajorVersion(int databaseMajorVersion) {
		this.databaseMajorVersion = databaseMajorVersion;
	}

	@Override
	public int getDatabaseMinorVersion() {
		return databaseMinorVersion;
	}

	public void setDatabaseMinorVersion(int databaseMinorVersion) {
		this.databaseMinorVersion = databaseMinorVersion;
	}

	@Override
	public String getDatabaseProductString() {
		return databaseProductString;
	}

	public void setDatabaseProductString(String databaseProductString) {
		this.databaseProductString = databaseProductString;
	}

	public DatabaseSrs getReferenceSystem() {
		return srs;
	}

	public void setReferenceSystem(DatabaseSrs srs) {
		this.srs = srs;
	}

	@Override
	public boolean isVersionEnabled() {
		return versioning == Versioning.ON;
	}

	public Versioning getVersioning() {
		return versioning;
	}

	public void setVersioning(Versioning versioning) {
		this.versioning = versioning;
	}

	@Override
	public void printToConsole() {
        LOG.all(LogLevel.INFO, "3D City Database: " + getCityDBVersion());
		LOG.all(LogLevel.INFO, "Database: " + getDatabaseProductName());
		LOG.all(LogLevel.INFO, "Version: " + getDatabaseProductVersion());
		LOG.all(LogLevel.INFO, "SRID: " + srs.getSrid() + " (" + srs.getType() + ')');
		LOG.all(LogLevel.INFO, "SRS: " + srs.getDatabaseSrsName());
		LOG.all(LogLevel.INFO, "gml:srsName: " + srs.getGMLSrsName());
		LOG.all(LogLevel.INFO, "Versioning: " + versioning);
	}
	
	public enum Versioning {
		ON("On"),
		OFF("Off"),
		PARTLY("Partly"),
		NOT_SUPPORTED("Not supported");
		
		private final String value;
		
		private Versioning(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public static Versioning fromValue(String v) {
			for (Versioning c: Versioning.values()) {
				if (c.value.toLowerCase().equals(v.toLowerCase())) {
					return c;
				}
			}

			return NOT_SUPPORTED;
		}
		
		public String toString() {
			return value;
		}
	}
}
