/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  http://geobatch.geo-solutions.it/
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
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

package it.geosolutions.geobatch.octave;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.ange.octave.type.OctaveObject;

public class OctaveExecutor implements Callable<List<OctaveObject>> {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(OctaveExecutor.class.toString());
    
    /**
     * Octave Environment
     */
    private OctaveEnv<OctaveExecutableSheet> env;
    
    /**
     * Octave Engine
     */
    private Engine engine;
    
    protected OctaveExecutor(OctaveEnv<OctaveExecutableSheet> e, Engine eng)throws InterruptedException{
        env=e;
        engine=eng;
    }
    
    /**
     * @note this is thread safe
     * @param env the environment to execute
     * @param eng the engine to use
     * @return returns the results obtained by this OctaveEnv
     * @throws Exception
     */
    public static List<OctaveObject> call(OctaveEnv<OctaveExecutableSheet> env, Engine eng) throws Exception {
        if (eng==null){
            final String message="OctaveExecutor.call(): Unable to run the call using a null Engine.";
            if (LOGGER.isErrorEnabled()){
                LOGGER.error(message);
            }
            throw new IllegalArgumentException(message);
        }
        /**
         * Objects are extracted from the list
         * since each returning value should be returned
         * to the requesting process using an XML message
         */
        OctaveExecutableSheet es=null;
        int exit=1;
        while (exit!=0 && env.hasNext()){
            // extract next ExecutableSheet
            es=env.pop();
            try {
                if (LOGGER.isTraceEnabled())
                    LOGGER.trace("Octave extracted a new OctaveExecutableSheet");
                /*
                 * Execute the 
                 */
                exit=eng.exec(es, true);

                if (LOGGER.isTraceEnabled())
                    LOGGER.trace("Octave extecuted sheet with exit status: "
                        +((exit>=0)?"GOOD":"BED"));
                
            }
            catch (Exception e) {
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("Octave throws an exception: "+e.getLocalizedMessage(),e);
                throw e;
            }

            if (LOGGER.isTraceEnabled())
                LOGGER.trace("Octave process exiting");
        } // comm!="quit"
        
        return eng.getResults();
    }
    
    /**
     * @note never call this method directly
     * This method should be called by the OctaveManager's ExecutorService
     * @return returns the results obtained by this OctaveEnv
     */
    public List<OctaveObject> call() throws Exception {
        /**
         * Objects are extracted from the list
         * since each returning value should be returned
         * to the requesting process using an XML message
         */
        OctaveExecutableSheet es=null;
        int exit=1;
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Octave extecutor started");
        while (exit!=0 && env.hasNext()){
            // extract next ExecutableSheet
            es=env.pop();
            try {
                if (LOGGER.isTraceEnabled())
                    LOGGER.trace("Octave extracted a new ExecutableSheet from env "+env.getUniqueID());
                
                exit=engine.exec(es, true);

                if (LOGGER.isTraceEnabled())
                    LOGGER.trace("Octave extecuted sheet with exit status: "+((exit>=0)?"GOOD":"BED"));
                
            }
            catch (Exception e) {
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("Octave throws an exception: "+e.getLocalizedMessage());
                throw e;
            }
        } // comm!="quit"
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Octave process exiting");
//        if (exit>0)
            return engine.getResults();
//        else
//            return null;
    }

}
