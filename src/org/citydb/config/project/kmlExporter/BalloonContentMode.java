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
package org.citydb.config.project.kmlExporter;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="BalloonContentMode")
@XmlEnum
public enum BalloonContentMode {
	@XmlEnumValue("gen_attrib")
    GEN_ATTRIB("gen_attrib"),
    @XmlEnumValue("file")
    FILE("file"),
    @XmlEnumValue("gen_attrib_and_file")
    GEN_ATTRIB_AND_FILE("gen_attrib_and_file");

    private final String value;

    BalloonContentMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BalloonContentMode fromValue(String v) {
        for (BalloonContentMode c: BalloonContentMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        return GEN_ATTRIB;
    }
}
