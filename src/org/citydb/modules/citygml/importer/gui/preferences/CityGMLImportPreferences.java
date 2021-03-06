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
package org.citydb.modules.citygml.importer.gui.preferences;

import org.citydb.config.Config;
import org.citydb.gui.preferences.AbstractPreferences;
import org.citydb.gui.preferences.DefaultPreferencesEntry;
import org.citydb.modules.common.filter.FilterMode;

public class CityGMLImportPreferences extends AbstractPreferences {
	
	public CityGMLImportPreferences(Config config) {
		super(new CityGMLImportEntry());
		
		root.addChildEntry(new DefaultPreferencesEntry(new ContinuationPanel(config)));
		root.addChildEntry(new DefaultPreferencesEntry(new IdHandlingPanel(config)));
		root.addChildEntry(new DefaultPreferencesEntry(new BoundingBoxPanel(config)));
		root.addChildEntry(new DefaultPreferencesEntry(new AddressPanel(config)));
		root.addChildEntry(new DefaultPreferencesEntry(new AppearancePanel(config)));
		root.addChildEntry(new DefaultPreferencesEntry(new GeometryPanel(FilterMode.IMPORT, config)));
		root.addChildEntry(new DefaultPreferencesEntry(new IndexPanel(config)));
		root.addChildEntry(new DefaultPreferencesEntry(new XMLValidationPanel(config)));
		root.addChildEntry(new DefaultPreferencesEntry(new ImportLogPanel(config)));
		root.addChildEntry(new DefaultPreferencesEntry(new ResourcesPanel(config)));
	}

}
