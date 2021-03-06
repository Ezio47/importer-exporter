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
package org.citydb.modules.citygml.exporter.gui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.citydb.config.Config;
import org.citydb.config.language.Language;
import org.citydb.config.project.resources.ThreadPoolConfig;
import org.citydb.config.project.resources.UIDCacheConfig;
import org.citydb.gui.factory.PopupMenuDecorator;
import org.citydb.gui.preferences.AbstractPreferencesComponent;
import org.citydb.util.gui.GuiUtil;

@SuppressWarnings("serial")
public class ResourcesPanel extends AbstractPreferencesComponent{
	private JPanel block1;
	private JPanel block2;
	private JLabel expResMinThreadsLabel;
	private JFormattedTextField expResMinThreadsText;
	private JLabel expResMaxThreadsLabel;
	private JFormattedTextField expResMaxThreadsText;	
	private JLabel expResGeomLabel;
	private JFormattedTextField expResGeomCacheText;
	private JLabel expResGeomCacheLabel;	
	private JFormattedTextField expResGeomDrainText;
	private JLabel expResGeomDrainLabel;
	private JLabel expResGeomPartLabel;
	private JFormattedTextField expResGeomPartText;
	private JLabel expResFeatLabel;
	private JFormattedTextField expResFeatCacheText;
	private JLabel expResFeatCacheLabel;
	private JFormattedTextField expResFeatDrainText;
	private JLabel expResFeatDrainLabel;
	private JLabel expResFeatPartLabel;
	private JFormattedTextField expResFeatPartText;

	public ResourcesPanel(Config config) {
		super(config);
		initGui();
	}

	@Override
	public boolean isModified() {
		ThreadPoolConfig threadPool = config.getProject().getExporter().getResources().getThreadPool().getDefaultPool();
		UIDCacheConfig geometry = config.getProject().getExporter().getResources().getGmlIdCache().getGeometry();
		UIDCacheConfig feature = config.getProject().getExporter().getResources().getGmlIdCache().getFeature();

		try { expResMinThreadsText.commitEdit(); } catch (ParseException e) { }
		try { expResMaxThreadsText.commitEdit(); } catch (ParseException e) { }
		try { expResGeomCacheText.commitEdit(); } catch (ParseException e) { }
		try { expResGeomDrainText.commitEdit(); } catch (ParseException e) { }
		try { expResGeomPartText.commitEdit(); } catch (ParseException e) { }
		try { expResFeatCacheText.commitEdit(); } catch (ParseException e) { }
		try { expResFeatDrainText.commitEdit(); } catch (ParseException e) { }
		try { expResFeatPartText.commitEdit(); } catch (ParseException e) { }
		
		if (((Number)expResMinThreadsText.getValue()).intValue() != threadPool.getMinThreads()) return true;
		if (((Number)expResMaxThreadsText.getValue()).intValue() != threadPool.getMaxThreads()) return true;
		if (((Number)expResGeomCacheText.getValue()).intValue() != geometry.getCacheSize()) return true;
		if (((Number)expResGeomDrainText.getValue()).intValue() != (int)(geometry.getPageFactor() * 100)) return true;
		if (((Number)expResGeomPartText.getValue()).intValue() != geometry.getPartitions()) return true;
		if (((Number)expResFeatCacheText.getValue()).intValue() != feature.getCacheSize()) return true;
		if (((Number)expResFeatDrainText.getValue()).intValue() != (int)(feature.getPageFactor() * 100)) return true;
		if (((Number)expResFeatPartText.getValue()).intValue() != feature.getPartitions()) return true;

		return false;
	}

	private void initGui(){
		block1 = new JPanel();
		block2 = new JPanel();
		expResMinThreadsLabel = new JLabel();
		expResMaxThreadsLabel = new JLabel();
		expResGeomLabel = new JLabel();
		expResGeomCacheLabel = new JLabel();	
		expResGeomDrainLabel = new JLabel();
		expResGeomPartLabel = new JLabel();
		expResFeatLabel = new JLabel();		
		expResFeatCacheLabel = new JLabel();
		expResFeatDrainLabel = new JLabel();
		expResFeatPartLabel = new JLabel();

		DecimalFormat threeIntFormat = new DecimalFormat("###");	
		threeIntFormat.setMaximumIntegerDigits(3);
		threeIntFormat.setMinimumIntegerDigits(1);
		expResMinThreadsText = new JFormattedTextField(threeIntFormat);
		expResMaxThreadsText = new JFormattedTextField(threeIntFormat);
		expResGeomDrainText = new JFormattedTextField(threeIntFormat);
		expResFeatDrainText = new JFormattedTextField(threeIntFormat);
		expResGeomPartText = new JFormattedTextField(threeIntFormat);
		expResFeatPartText = new JFormattedTextField(threeIntFormat);
		
		DecimalFormat cacheEntryFormat = new DecimalFormat("########");
		cacheEntryFormat.setMaximumIntegerDigits(8);
		cacheEntryFormat.setMinimumIntegerDigits(1);		
		expResGeomCacheText = new JFormattedTextField(cacheEntryFormat);
		expResFeatCacheText = new JFormattedTextField(cacheEntryFormat);
		
		PopupMenuDecorator.getInstance().decorate(expResMinThreadsText, expResMaxThreadsText, expResGeomDrainText, 
				expResFeatDrainText, expResGeomPartText, expResFeatPartText, expResGeomCacheText, expResFeatCacheText);
		
		expResMinThreadsText.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				checkNonNegative(expResMinThreadsText, 1);
			}
		});
		
		expResMaxThreadsText.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				checkNonNegative(expResMaxThreadsText, 1);
			}
		});
		
		expResGeomCacheText.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				checkNonNegative(expResGeomCacheText, 200000);
			}
		});
		
		expResFeatCacheText.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				checkNonNegative(expResFeatCacheText, 200000);
			}
		});

		expResGeomDrainText.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				checkNonNegativeRange(expResGeomDrainText, 85, 100);
			}
		});
		
		expResFeatDrainText.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				checkNonNegativeRange(expResFeatDrainText, 85, 100);
			}
		});
		
		expResGeomPartText.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				checkNonNegativeRange(expResGeomPartText, 10, 100);
			}
		});
		
		expResFeatPartText.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				checkNonNegativeRange(expResFeatPartText, 10, 100);
			}
		});
		
		setLayout(new GridBagLayout());
		add(block1, GuiUtil.setConstraints(0,0,1.0,0.0,GridBagConstraints.BOTH,5,0,5,0));
		block1.setBorder(BorderFactory.createTitledBorder(""));
		block1.setLayout(new GridBagLayout());
		{
			block1.add(expResMinThreadsLabel, GuiUtil.setConstraints(0,0,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block1.add(expResMinThreadsText, GuiUtil.setConstraints(1,0,1.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block1.add(expResMaxThreadsLabel, GuiUtil.setConstraints(0,1,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block1.add(expResMaxThreadsText, GuiUtil.setConstraints(1,1,1.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
		}

		add(block2, GuiUtil.setConstraints(0,1,1.0,0.0,GridBagConstraints.BOTH,5,0,5,0));
		block2.setBorder(BorderFactory.createTitledBorder(""));
		block2.setLayout(new GridBagLayout());
		{
			block2.add(expResGeomLabel, GuiUtil.setConstraints(0,0,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResGeomCacheText, GuiUtil.setConstraints(1,0,1.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResGeomCacheLabel, GuiUtil.setConstraints(2,0,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResGeomDrainText, GuiUtil.setConstraints(1,1,1.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResGeomDrainLabel, GuiUtil.setConstraints(2,1,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResGeomPartText, GuiUtil.setConstraints(1,2,1.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResGeomPartLabel, GuiUtil.setConstraints(2,2,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResFeatLabel, GuiUtil.setConstraints(0,3,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResFeatCacheText, GuiUtil.setConstraints(1,3,1.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResFeatCacheLabel, GuiUtil.setConstraints(2,3,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResFeatDrainText, GuiUtil.setConstraints(1,4,1.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResFeatDrainLabel, GuiUtil.setConstraints(2,4,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResFeatPartText, GuiUtil.setConstraints(1,5,1.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
			block2.add(expResFeatPartLabel, GuiUtil.setConstraints(2,5,0.0,1.0,GridBagConstraints.BOTH,0,5,5,5));
		}
	}
	
	private void checkNonNegative(JFormattedTextField field, int defaultValue) {
		if (((Number)field.getValue()).intValue() < 0)
			field.setValue(defaultValue);
	}
	
	private void checkNonNegativeRange(JFormattedTextField field, int min, int max) {
		if (((Number)field.getValue()).intValue() < 0)
			field.setValue(min);
		else if (((Number)field.getValue()).intValue() > 100)
			field.setValue(max);
	}

	@Override
	public void doTranslation() {
		((TitledBorder)block1.getBorder()).setTitle(Language.I18N.getString("common.pref.resources.border.multiCPU"));
		((TitledBorder)block2.getBorder()).setTitle(Language.I18N.getString("common.pref.resources.border.idCache"));

		expResMinThreadsLabel.setText(Language.I18N.getString("common.pref.resources.label.minThreads"));
		expResMaxThreadsLabel.setText(Language.I18N.getString("common.pref.resources.label.maxThreads"));

		expResGeomLabel.setText(Language.I18N.getString("common.pref.resources.label.geometry"));
		expResGeomCacheLabel.setText(Language.I18N.getString("common.pref.resources.label.cache.entry"));
		expResGeomDrainLabel.setText(Language.I18N.getString("common.pref.resources.label.cache.drain"));
		expResGeomPartLabel.setText(Language.I18N.getString("common.pref.resources.label.cache.partition"));
		expResFeatLabel.setText(Language.I18N.getString("common.pref.resources.label.feature"));
		expResFeatCacheLabel.setText(Language.I18N.getString("common.pref.resources.label.cache.entry"));
		expResFeatDrainLabel.setText(Language.I18N.getString("common.pref.resources.label.cache.drain"));
		expResFeatPartLabel.setText(Language.I18N.getString("common.pref.resources.label.cache.partition"));
	}

	@Override
	public void loadSettings() {
		ThreadPoolConfig threadPool = config.getProject().getExporter().getResources().getThreadPool().getDefaultPool();
		UIDCacheConfig geometry = config.getProject().getExporter().getResources().getGmlIdCache().getGeometry();
		UIDCacheConfig feature = config.getProject().getExporter().getResources().getGmlIdCache().getFeature();

		expResMinThreadsText.setValue(threadPool.getMinThreads());
		expResMaxThreadsText.setValue(threadPool.getMaxThreads());
		expResGeomCacheText.setValue(geometry.getCacheSize());
		expResFeatCacheText.setValue(feature.getCacheSize());		
		expResGeomDrainText.setValue((int)(geometry.getPageFactor() * 100));
		expResFeatDrainText.setValue((int)(feature.getPageFactor() * 100));		
		expResGeomPartText.setValue(geometry.getPartitions());
		expResFeatPartText.setValue(feature.getPartitions());
	}

	@Override
	public void setSettings() {
		ThreadPoolConfig threadPool = config.getProject().getExporter().getResources().getThreadPool().getDefaultPool();
		UIDCacheConfig geometry = config.getProject().getExporter().getResources().getGmlIdCache().getGeometry();
		UIDCacheConfig feature = config.getProject().getExporter().getResources().getGmlIdCache().getFeature();

		int minThreads = ((Number)expResMinThreadsText.getValue()).intValue();
		int maxThreads = ((Number)expResMaxThreadsText.getValue()).intValue();

		if (minThreads > maxThreads) {
			minThreads = maxThreads;
			expResMinThreadsText.setValue(minThreads);
		}
		
		threadPool.setMinThreads(minThreads);
		threadPool.setMaxThreads(maxThreads);

		geometry.setCacheSize(((Number)expResGeomCacheText.getValue()).intValue());			
		feature.setCacheSize(((Number)expResFeatCacheText.getValue()).intValue());
		geometry.setPageFactor(((Number)expResGeomDrainText.getValue()).floatValue() / 100);
		feature.setPageFactor(((Number)expResFeatDrainText.getValue()).floatValue() / 100);
		geometry.setPartitions(((Number)expResGeomPartText.getValue()).intValue());
		feature.setPartitions(((Number)expResFeatPartText.getValue()).intValue());
	}
	
	@Override
	public String getTitle() {
		return Language.I18N.getString("pref.tree.export.resources");
	}
}

