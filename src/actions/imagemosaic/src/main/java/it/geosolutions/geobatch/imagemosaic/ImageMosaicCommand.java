/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  http://code.google.com/p/geobatch/
 *  Copyright (C) 2007-2011 GeoSolutions S.A.S.
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
package it.geosolutions.geobatch.imagemosaic;

import it.geosolutions.geobatch.geoserver.GeoServerActionConfig;
import it.geosolutions.geobatch.geoserver.GeoServerActionConfiguration;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamInclude;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * 
 * A serializable bean used to set the ImageMosaic command list.
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 * @note: on 28 Feb 2011 discussion: carlo: do we need to clean this bean from
 *        Xstream specific methods and annotations? simone: leave it here since
 *        this class is actually used only by geobatch
 * @note: This is public to make it usable from other packages
 */

@XStreamInclude(ImageMosaicCommand.class)
@XStreamAlias("ImageMosaic")
public class ImageMosaicCommand extends ImageMosaicConfiguration implements Serializable {
    @XStreamOmitField
    protected final static Logger LOGGER = LoggerFactory.getLogger(ImageMosaicCommand.class);

    /**
     * Serial version id
     */
    @XStreamOmitField
    private static final long serialVersionUID = 7592430220578935089L;

    @XStreamAlias("base")
    private File baseDir;

    @XStreamImplicit(itemFieldName = "del")
    private List<File> delFiles;

    @XStreamImplicit(itemFieldName = "add")
    private List<File> addFiles;

    @XStreamOmitField
    private static XStream stream;
    static {
        init();
    }

    @Deprecated
    public ImageMosaicCommand() {
        super("imageMosaicCommand", "imageMosaicCommand", "imageMosaicCommand config");
    }

    /**
     * initialize the XStream env
     * 
     * @throws InitializationException - in case of an initialization problem
     */
    private static void init() throws InitializationException {
        stream = new XStream();
        stream.processAnnotations(ImageMosaicCommand.class);
    }

    /**
     * Try to deserialize the command, return null if some goes wrong
     * 
     * @param file the file to deserialize
     * @return the deserialized ImageMosaicCommand object or null
     * @throws FileNotFoundException - if the file exists but is a directory
     *             rather than a regular file, does not exist but cannot be
     *             created, or cannot be opened for any other reason
     * @throws SecurityException - if a security manager exists and its
     *             checkWrite method denies write access to the file
     * @throws XStreamException - if the object cannot be serialized
     */
    public static File serialize(ImageMosaicCommand cmd, String path) throws FileNotFoundException,
        SecurityException {
        final File outFile = new File(path);
        final FileOutputStream fos = new FileOutputStream(outFile);
        if (stream == null)
            init();
        stream.toXML(cmd, fos);
        return outFile;
    }

    /**
     * Try to deserialize the command, return null if some goes wrong
     * 
     * @param file the file to deserialize
     * @return the deserialized ImageMosaicCommand object or null
     * @throws FileNotFoundException - if the file exists but is a directory
     *             rather than a regular file, does not exist but cannot be
     *             created, or cannot be opened for any other reason
     * @throws SecurityException - if a security manager exists and its
     *             checkWrite method denies write access to the file
     * @throws XStreamException - if the object cannot be serialized
     */
    public static ImageMosaicCommand deserialize(File file) throws FileNotFoundException, SecurityException {
        // try {
        final InputStream is = new FileInputStream(file);
        if (stream == null)
            init();
        final ImageMosaicCommand cmd = (ImageMosaicCommand)stream.fromXML(is);
        return cmd;
    }

    public ImageMosaicCommand(final File baseDir, final List<File> addFiles, final List<File> delFiles) {
        super("imageMosaicCommand_" + baseDir, "imageMosaicCommand", "imageMosaicCommand config");
        this.baseDir = baseDir;
        this.addFiles = addFiles;
        this.delFiles = delFiles;
    }

    public ImageMosaicCommand(final String baseDir, final List<String> addFiles, final List<String> delFiles) {
        super("imageMosaicCommand_" + baseDir, "imageMosaicCommand", "imageMosaicCommand config");
        this.baseDir = new File(baseDir);
        if (addFiles != null) {
            this.addFiles = new ArrayList<File>();
            for (String fileName : addFiles) {
                this.addFiles.add(new File(fileName));
            }
        }
        if (delFiles != null) {
            this.delFiles = new ArrayList<File>();
            for (String fileName : delFiles) {
                this.delFiles.add(new File(fileName));
            }
        }
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public List<File> getAddFiles() {
        return addFiles;
    }

    public void setAddFiles(List<File> addFiles) {
        this.addFiles = addFiles;
    }

    public List<File> getDelFiles() {
        return delFiles;
    }

    public void setDelFiles(List<File> delFiles) {
        this.delFiles = delFiles;
    }

    @Override
    public String toString() {
        if (stream == null)
            init();

        return stream.toXML(this);

    }

    /**
     * clone
     */
    @Override
    public ImageMosaicCommand clone() {
        try {
            return (ImageMosaicCommand)BeanUtils.cloneBean(this);
        } catch (IllegalAccessException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(e.getLocalizedMessage());
        } catch (InstantiationException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(e.getLocalizedMessage());
        } catch (InvocationTargetException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(e.getLocalizedMessage());
        } catch (NoSuchMethodException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Override passed ImageMosaicConfiguration with values from this instance.
     * 
     * @see ImageMosaicConfiguration
     * @see GeoServerActionConfig
     * @see GeoServerActionConfiguration
     * 
     */
    public void overrideImageMosaicConfiguration(final ImageMosaicConfiguration conf) {

        /**
         * for the following overrides see {@link GeoServerActionConfig}
         */
        if (getGeoserverURL() != null) {
            conf.setGeoserverURL(getGeoserverURL());
        }

        if (getGeoserverUID() != null) {
            conf.setGeoserverUID(getGeoserverUID());
        }

        if (getGeoserverPWD() != null) {
            conf.setGeoserverPWD(getGeoserverPWD());
        }

        /**
         * for the following overrides see {@link GeoServerActionConfiguration}
         */
        if (getCrs() != null)
            conf.setCrs(getCrs());
        if (getDataTransferMethod() != null)
            conf.setDataTransferMethod(getDataTransferMethod());

        if (getDatatype() != null)
            conf.setDatatype(getDatatype());

        if (getDefaultNamespace() != null)
            conf.setDefaultNamespace(getDefaultNamespace());

        // configuration.setDefaultNamespaceUri(defaultNamespaceUri);

        if (getDefaultStyle() != null)
            conf.setDefaultStyle(getDefaultStyle());

        if (getEnvelope() != null)
            conf.setEnvelope(getEnvelope());

        if (getStoreFilePrefix() != null)
            conf.setStoreFilePrefix(getStoreFilePrefix());

        if (getStyles() != null)
            conf.setStyles(getStyles());

        if (getWmsPath() != null)
            conf.setWmsPath(getWmsPath());

        /**
         * for the following overrides see {@link ImageMosaicConfiguration}
         */

        // wins the one which set different from default (false)
        if ((isAllowMultithreading() != false) || (conf.isAllowMultithreading() != false))
            conf.setAllowMultithreading(true);

        if (getBackgroundValue() != null)
            conf.setBackgroundValue(getBackgroundValue());

        if (getDatastorePropertiesPath() != null)
            conf.setDatastorePropertiesPath(getDatastorePropertiesPath());

        if (getElevationPresentationMode() != null)
            conf.setElevationRegex(getElevationRegex());

        if (getElevDimEnabled() != null)
            conf.setElevDimEnabled(getElevDimEnabled());
        if (getElevationPresentationMode() != null)
            conf.setElevationPresentationMode(getElevationPresentationMode());
        if (getInputTransparentColor() != null)
            conf.setInputTransparentColor(getInputTransparentColor());
        if (getLatLonMaxBoundingBoxX() != null)
            conf.setLatLonMaxBoundingBoxX(getLatLonMaxBoundingBoxX());
        if (getLatLonMaxBoundingBoxY() != null)
            conf.setLatLonMaxBoundingBoxY(getLatLonMaxBoundingBoxY());
        if (getLatLonMinBoundingBoxX() != null)
            conf.setLatLonMinBoundingBoxX(getLatLonMinBoundingBoxX());
        if (getLatLonMinBoundingBoxY() != null)
            conf.setLatLonMinBoundingBoxY(getLatLonMinBoundingBoxY());
        if (getNativeMaxBoundingBoxX() != null)
            conf.setNativeMaxBoundingBoxX(getNativeMaxBoundingBoxX());
        if (getNativeMaxBoundingBoxY() != null)
            conf.setNativeMaxBoundingBoxY(getNativeMaxBoundingBoxY());
        if (getNativeMinBoundingBoxX() != null)
            conf.setNativeMinBoundingBoxX(getNativeMinBoundingBoxX());
        if (getNativeMinBoundingBoxY() != null)
            conf.setNativeMinBoundingBoxY(getNativeMinBoundingBoxY());
        if (getOutputTransparentColor() != null)
            conf.setOutputTransparentColor(getOutputTransparentColor());
        if (getProjectionPolicy() != null)
            conf.setProjectionPolicy(getProjectionPolicy());
        if (getRuntimeRegex() != null)
            conf.setRuntimeRegex(getRuntimeRegex());

        // wins the one which set different from default (0)
        if (getTileSizeH() != 0)
            conf.setTileSizeH(getTileSizeH());

        // wins the one which set different from default (0)
        if ((getTileSizeW() != 0))
            conf.setTileSizeW(getTileSizeW());

        if (getTimeDimEnabled() != null)
            conf.setTimeDimEnabled(getTimeDimEnabled());
        if (getTimePresentationMode() != null)
            conf.setTimePresentationMode(getTimePresentationMode());
        if (getTimeRegex() != null)
            conf.setTimeRegex(getTimeRegex());

        // wins the one which set different from default (false)
        if ((isUseJaiImageRead() != false) || (conf.isUseJaiImageRead() != false))
            conf.setUseJaiImageRead(true);
    }

    /**
     * set this instance null properties with the passed configuration
     * @param conf
     */
    public void copyConfigurationIntoCommand(final ImageMosaicConfiguration conf) {

            final PropertyDescriptor[] props=PropertyUtils.getPropertyDescriptors(conf);
            for (PropertyDescriptor prop:props){
                final String name=prop.getName();
                if (name=="class"){
                    continue;
                }
                final Object obj;
                try {
                    obj = PropertyUtils.getProperty(this,name);
                    if (obj==null){
                        // override
                        PropertyUtils.setProperty(this, name, PropertyUtils.getProperty(conf,name));
                    }
                } catch (InvocationTargetException e) {
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn(e.getMessage());
                } catch (NoSuchMethodException e) {
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn(e.getMessage());
                } catch (IllegalAccessException e) {
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn(e.getMessage());
                }
            }   
    }
    
    /**
     * @deprecated use {@link #copyConfigurationIntoCommand(ImageMosaicConfiguration)}
     */
    public void overrideImageMosaicCommand(final ImageMosaicConfiguration conf) {
        copyConfigurationIntoCommand(conf);
    }

}
