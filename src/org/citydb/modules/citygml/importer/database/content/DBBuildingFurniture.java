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
package org.citydb.modules.citygml.importer.database.content;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.citydb.api.geometry.GeometryObject;
import org.citydb.config.Config;
import org.citydb.database.TableEnum;
import org.citydb.log.Logger;
import org.citydb.modules.citygml.common.database.xlink.DBXlinkSurfaceGeometry;
import org.citydb.util.Util;
import org.citygml4j.geometry.Matrix;
import org.citygml4j.model.citygml.building.BuildingFurniture;
import org.citygml4j.model.citygml.core.ImplicitGeometry;
import org.citygml4j.model.citygml.core.ImplicitRepresentationProperty;
import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.citygml4j.model.gml.geometry.GeometryProperty;

public class DBBuildingFurniture implements DBImporter {
	private final Logger LOG = Logger.getInstance();
	
	private final Connection batchConn;
	private final DBImporterManager dbImporterManager;

	private PreparedStatement psBuildingFurniture;
	private DBCityObject cityObjectImporter;
	private DBSurfaceGeometry surfaceGeometryImporter;
	private DBOtherGeometry otherGeometryImporter;
	private DBImplicitGeometry implicitGeometryImporter;
	private DBOtherGeometry geometryImporter;

	private boolean affineTransformation;
	private int batchCounter;

	public DBBuildingFurniture(Connection batchConn, Config config, DBImporterManager dbImporterManager) throws SQLException {
		this.batchConn = batchConn;
		this.dbImporterManager = dbImporterManager;

		affineTransformation = config.getProject().getImporter().getAffineTransformation().isSetUseAffineTransformation();
		init();
	}

	private void init() throws SQLException {
		StringBuilder stmt = new StringBuilder()
		.append("insert into BUILDING_FURNITURE (ID, CLASS, CLASS_CODESPACE, FUNCTION, FUNCTION_CODESPACE, USAGE, USAGE_CODESPACE, ROOM_ID, ")
		.append("LOD4_BREP_ID, LOD4_OTHER_GEOM, ")
		.append("LOD4_IMPLICIT_REP_ID, LOD4_IMPLICIT_REF_POINT, LOD4_IMPLICIT_TRANSFORMATION) values ")
		.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		psBuildingFurniture = batchConn.prepareStatement(stmt.toString());

		surfaceGeometryImporter = (DBSurfaceGeometry)dbImporterManager.getDBImporter(DBImporterEnum.SURFACE_GEOMETRY);
		otherGeometryImporter = (DBOtherGeometry)dbImporterManager.getDBImporter(DBImporterEnum.OTHER_GEOMETRY);
		cityObjectImporter = (DBCityObject)dbImporterManager.getDBImporter(DBImporterEnum.CITYOBJECT);
		implicitGeometryImporter = (DBImplicitGeometry)dbImporterManager.getDBImporter(DBImporterEnum.IMPLICIT_GEOMETRY);
		geometryImporter = (DBOtherGeometry)dbImporterManager.getDBImporter(DBImporterEnum.OTHER_GEOMETRY);
	}

	public long insert(BuildingFurniture buildingFurniture, long roomId) throws SQLException {
		long buildingFurnitureId = dbImporterManager.getDBId(DBSequencerEnum.CITYOBJECT_ID_SEQ);
		if (buildingFurnitureId == 0)
			return 0;

		// CityObject
		cityObjectImporter.insert(buildingFurniture, buildingFurnitureId);

		// BuildingFurniture
		// ID
		psBuildingFurniture.setLong(1, buildingFurnitureId);

		// bldg:class
		if (buildingFurniture.isSetClazz() && buildingFurniture.getClazz().isSetValue()) {
			psBuildingFurniture.setString(2, buildingFurniture.getClazz().getValue());
			psBuildingFurniture.setString(3, buildingFurniture.getClazz().getCodeSpace());
		} else {
			psBuildingFurniture.setNull(2, Types.VARCHAR);
			psBuildingFurniture.setNull(3, Types.VARCHAR);
		}

		// bldg:function
		if (buildingFurniture.isSetFunction()) {
			String[] function = Util.codeList2string(buildingFurniture.getFunction());
			psBuildingFurniture.setString(4, function[0]);
			psBuildingFurniture.setString(5, function[1]);
		} else {
			psBuildingFurniture.setNull(4, Types.VARCHAR);
			psBuildingFurniture.setNull(5, Types.VARCHAR);
		}

		// bldg:usage
		if (buildingFurniture.isSetUsage()) {
			String[] usage = Util.codeList2string(buildingFurniture.getUsage());
			psBuildingFurniture.setString(6, usage[0]);
			psBuildingFurniture.setString(7, usage[1]);
		} else {
			psBuildingFurniture.setNull(6, Types.VARCHAR);
			psBuildingFurniture.setNull(7, Types.VARCHAR);
		}

		// ROOM_ID
		psBuildingFurniture.setLong(8, roomId);

		// Geometry		
		long geometryId = 0;
		GeometryObject geometryObject = null;
		
		if (buildingFurniture.isSetLod4Geometry()) {
			GeometryProperty<? extends AbstractGeometry> geometryProperty = buildingFurniture.getLod4Geometry();

			if (geometryProperty.isSetGeometry()) {
				AbstractGeometry abstractGeometry = geometryProperty.getGeometry();
				if (surfaceGeometryImporter.isSurfaceGeometry(abstractGeometry))
					geometryId = surfaceGeometryImporter.insert(abstractGeometry, buildingFurnitureId);
				else if (otherGeometryImporter.isPointOrLineGeometry(abstractGeometry))
					geometryObject = otherGeometryImporter.getPointOrCurveGeometry(abstractGeometry);
				else {
					StringBuilder msg = new StringBuilder(Util.getFeatureSignature(
							buildingFurniture.getCityGMLClass(), 
							buildingFurniture.getId()));
					msg.append(": Unsupported geometry type ");
					msg.append(abstractGeometry.getGMLClass()).append('.');
					
					LOG.error(msg.toString());
				}

				geometryProperty.unsetGeometry();
			} else {
				// xlink
				String href = geometryProperty.getHref();

				if (href != null && href.length() != 0) {
					dbImporterManager.propagateXlink(new DBXlinkSurfaceGeometry(
							href, 
							buildingFurnitureId, 
							TableEnum.BUILDING_FURNITURE, 
							"LOD4_BREP_ID"));
				}
			}
		}

		if (geometryId != 0)
			psBuildingFurniture.setLong(9, geometryId);
		else
			psBuildingFurniture.setNull(9, Types.NULL);

		if (geometryObject != null)
			psBuildingFurniture.setObject(10, dbImporterManager.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(geometryObject, batchConn));
		else
			psBuildingFurniture.setNull(10, dbImporterManager.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
					dbImporterManager.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());

		// implicit geometry
		GeometryObject pointGeom = null;
		String matrixString = null;
		long implicitId = 0;

		if (buildingFurniture.isSetLod4ImplicitRepresentation()) {
			ImplicitRepresentationProperty implicit = buildingFurniture.getLod4ImplicitRepresentation();

			if (implicit.isSetObject()) {
				ImplicitGeometry geometry = implicit.getObject();

				// reference Point
				if (geometry.isSetReferencePoint())
					pointGeom = geometryImporter.getPoint(geometry.getReferencePoint());

				// transformation matrix
				if (geometry.isSetTransformationMatrix()) {
					Matrix matrix = geometry.getTransformationMatrix().getMatrix();
					if (affineTransformation)
						matrix = dbImporterManager.getAffineTransformer().transformImplicitGeometryTransformationMatrix(matrix);

					matrixString = Util.collection2string(matrix.toRowPackedList(), " ");
				}

				// reference to IMPLICIT_GEOMETRY
				implicitId = implicitGeometryImporter.insert(geometry, buildingFurnitureId);
			}
		}

		if (implicitId != 0)
			psBuildingFurniture.setLong(11, implicitId);
		else
			psBuildingFurniture.setNull(11, Types.NULL);

		if (pointGeom != null)
			psBuildingFurniture.setObject(12, dbImporterManager.getDatabaseAdapter().getGeometryConverter().getDatabaseObject(pointGeom, batchConn));
		else
			psBuildingFurniture.setNull(12, dbImporterManager.getDatabaseAdapter().getGeometryConverter().getNullGeometryType(),
					dbImporterManager.getDatabaseAdapter().getGeometryConverter().getNullGeometryTypeName());

		if (matrixString != null)
			psBuildingFurniture.setString(13, matrixString);
		else
			psBuildingFurniture.setNull(13, Types.VARCHAR);

		psBuildingFurniture.addBatch();
		if (++batchCounter == dbImporterManager.getDatabaseAdapter().getMaxBatchSize())
			dbImporterManager.executeBatch(DBImporterEnum.BUILDING_FURNITURE);

		// insert local appearance
		cityObjectImporter.insertAppearance(buildingFurniture, buildingFurnitureId);

		return buildingFurnitureId;
	}

	@Override
	public void executeBatch() throws SQLException {
		psBuildingFurniture.executeBatch();
		batchCounter = 0;
	}

	@Override
	public void close() throws SQLException {
		psBuildingFurniture.close();
	}

	@Override
	public DBImporterEnum getDBImporterType() {
		return DBImporterEnum.BUILDING_FURNITURE;
	}

}
