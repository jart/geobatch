/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  http://geobatch.codehaus.org/
 *  Copyright (C) 2007-2008-2009 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.geosolutions.geobatch.configuration.event.action;

import it.geosolutions.geobatch.catalog.Configuration;
import it.geosolutions.geobatch.catalog.impl.BaseConfiguration;
import it.geosolutions.geobatch.configuration.event.listener.ProgressListenerConfiguration;
import java.util.ArrayList;
import java.util.List;

public abstract class ActionConfiguration 
        extends BaseConfiguration
        implements Configuration, Cloneable {

    private List<String> listenerIds = null;
    private List<ProgressListenerConfiguration> listenerConfigurations = new ArrayList<ProgressListenerConfiguration>();
    
	public ActionConfiguration() {
        super();
    }

    public ActionConfiguration(String id, String name, String description, boolean dirty) {
        super(id, name, description, dirty);
    }
    
    public List<String> getListenerIds() {
        return listenerIds;
    }

    protected void setListenerId(List<String> listenerIds) {
        this.listenerIds = listenerIds;
    }
    
    public void addListenerConfiguration(ProgressListenerConfiguration plc) {
        listenerConfigurations.add(plc);
    }

    public List<ProgressListenerConfiguration> getListenerConfigurations() {
        return listenerConfigurations;
    }

    public void setListenerConfigurations(List<ProgressListenerConfiguration> listenerConfigurations) {
        if(listenerConfigurations == null)
            throw new NullPointerException("Can't set listenerConfig list to null");
        this.listenerConfigurations = listenerConfigurations;
    }

    @Override
    public ActionConfiguration clone() {
        ActionConfiguration bc = (ActionConfiguration) super.clone();

        bc.listenerIds = new ArrayList<String>(listenerIds);
        for (String lid : listenerIds) {
            bc.listenerIds.add(lid);
        }

        bc.listenerConfigurations = new ArrayList<ProgressListenerConfiguration>();
        for (ProgressListenerConfiguration plc : listenerConfigurations) {
            bc.listenerConfigurations.add(plc); // CHECKME: shall we clone the configs?
        }
        return bc;
    }


}