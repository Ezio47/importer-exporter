package org.citydb.modules.kml.util;

import org.citydb.api.geometry.GeometryObject;
import org.citydb.api.geometry.GeometryObject.GeometryType;
import org.citydb.textureAtlas.model.TextureImage;

public class TextureImageScaler {
	public static double DEFAULT_SCALE_FACTOR = 0.1;
	public static double MINIMUM_SCALE_FACTOR = 0;
	public static double MAXIMUM_SCALE_FACTOR = 100;
	private final double scaleFactor;
	
	public TextureImageScaler(double scaleFactor) {
		// must be given in m/pixel
		this.scaleFactor = scaleFactor;
	}
	
	public double getScaleFactor(TextureImage texImage, GeometryObject surface, GeometryObject textureSpace) {
		if (scaleFactor <= MINIMUM_SCALE_FACTOR || scaleFactor > MAXIMUM_SCALE_FACTOR)
			return 1;
		
		if (surface.getGeometryType() != GeometryType.POLYGON || textureSpace.getGeometryType() != GeometryType.POLYGON)
			return 1;
		
		double surfaceArea = calcSurfaceArea(surface.getCoordinates(0), surface.getDimension());		
		if (surfaceArea == 0)
			return 1;
		
		double textureArea = calcSurfaceArea(textureSpace.getCoordinates(0), textureSpace.getDimension());
		if (textureArea == 0)
			return 1;

		int height = texImage.getHeight();
		int width = texImage.getWidth();
		int size = height * width;
		double squarePixel = Math.min(size * textureArea, size);
		
		return 1.0 / (Math.sqrt(squarePixel) * scaleFactor / Math.sqrt(surfaceArea));
	}
	
	private double[] unit_normal(double[] p1, double[] p2, double[] p3) {
		double[] P12 = new double[]{p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]};
		double[] P13 = new double[]{p3[0] - p1[0], p3[1] - p1[1], p3[2] - p1[2]};
		return cross(P12, P13);
	}

	private double[] cross(double[] p1, double[] p2) {
		double x = p1[1] * p2[2] - p1[2] * p2[1];
		double y = p1[2] * p2[0] - p1[0] * p2[2];
		double z = p1[0] * p2[1] - p1[1] * p2[0];
		return new double[]{x, y, z};
	}

	private boolean collinear(double[] p1, double[] p2, double[] p3) {
		double[] v1 = new double[]{p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]};
		double[] v2 = new double[]{p3[0] - p1[0], p3[1] - p1[1], p3[2] - p1[2]};
		double[] cross = cross(v1, v2);
		double dist = Math.sqrt(cross[0]*cross[0] + cross[1]*cross[1] + cross[2]*cross[2]);
		return dist < 0.1;
	}
	
	private double calcSurfaceArea(double[] coords, int dim) {
		double[][] points = new double[coords.length / dim ][];
		int k = 0;

		for (int i = 0; i < coords.length; i += dim) {
			double[] point = new double[dim];			
			for (int j = 0; j < dim; j++)
				point[j] = coords[i + j];

			points[k++] = point;
		}

		return dim == 3 ? area3d(points) : area2d(points);
	}

	private double area3d(double[][] points){
		if (points.length < 3)
			return 0;

		double area = 0;
		double an, ax, ay, az;
		int n = points.length;
		
		// calc normal vector
		double[] p1 = points[0];
		double[] p2 = points[1];		
		double[] p3 = null;
		
		for (int i = 2; i < n - 1; i++) {
			if (!collinear(p1, p2, points[i])) {
				p3 = points[i];
				break;
			}
		}
		
		if (p3 == null)
			return 0;
		
		double[] norm = unit_normal(p1, p2, p3);
		ax = (norm[0] > 0 ? norm[0] : -norm[0]);
		ay = (norm[1] > 0 ? norm[1] : -norm[1]);
		az = (norm[2] > 0 ? norm[2] : -norm[2]);
		if (ax == 0 && ay == 0 && az == 0)
			return 0;			

		int coord = 3;
		if (ax > ay) {
			if (ax > az)
				coord = 1;
		} else if (ay > az)
			coord = 2;

		// determine projection axes
		byte axis1 = 0;
		byte axis2 = 1;
		
		switch (coord) {
		case 1:
			axis1 = 1;
			axis2 = 2;
			break;
		case 2:
			axis1 = 0;
			axis2 = 2;
			break;
		}

		// compute area of 2d projection
		int i, j, k;
		for (i = 1, j = 2, k = 0; i < n - 1; i++, j++, k++)
			area += points[i][axis1] * (points[j][axis2] - points[k][axis2]);

		area += points[n-1][axis1] * (points[1][axis2] - points[n-2][axis2]);
		area = Math.abs(area);

		an = Math.sqrt(ax * ax + ay * ay + az * az);

		switch (coord) {
		case 1:
			area *= (an / (2 * ax));
			break;
		case 2:
			area *= (an / (2 * ay));
			break;
		case 3:
			area *= (an / (2 * az));
		}

		return area;
	}

	private double area2d(double[][] points) {
		if (points.length < 3)
			return 0;

		int n = points.length;
		double area = 0.0;
		int i, j, k;
		for (i = 1, j = 2, k = 0; i < n - 1; i++, j++, k++)
			area += points[i][0] * (points[j][1] - points[k][1]);

		area += points[n-1][0] * (points[1][1] - points[n-2][1]);		
		return Math.abs(area / 2.0);
	}
	
}
