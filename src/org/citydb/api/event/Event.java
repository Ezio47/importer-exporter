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
package org.citydb.api.event;

import java.lang.ref.WeakReference;

public abstract class Event {
	public static Object GLOBAL_CHANNEL = new Object();
	
	private final Enum<?> eventType;
	private final WeakReference<Object> source;
	private final WeakReference<Object> channel;
	private boolean cancelled;

	public Event(Enum<?> eventType, Object channel, Object source) {
		if (eventType == null)
			throw new IllegalArgumentException("The type of an event may not be null.");
			
		if (source == null)
			throw new IllegalArgumentException("The source of an event may not be null.");
		
		this.eventType = eventType;
		this.source = new WeakReference<Object>(source);
		this.channel = new WeakReference<Object>(channel);
		cancelled = false;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Enum<?> getEventType() {
		return eventType;
	}

	public Object getSource() {
		return source.get();
	}

	public Object getChannel() {
		return channel.get();
	}
	
}
