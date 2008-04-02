/*
 * $Id: AppMessageResourcesImpl.java,v 1.1 2003/05/18 20:54:47 eburns Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.context;

import com.sun.faces.RIConstants;
import com.sun.faces.util.Util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Locale;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.MessageImpl;
import javax.faces.application.Message;
import javax.faces.context.MessageResources;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import java.text.MessageFormat;
import java.io.IOException;

import com.sun.faces.config.ConfigMessageResources;
import com.sun.faces.config.ConfigMessage;

import org.mozilla.util.Assert;
import org.mozilla.util.ParameterCheck;

/**

* <p>This class is for message-resources in the config file for which no
* <code>message-resources-class</code> was specified.</p>

*/

public class AppMessageResourcesImpl extends MessageResourcesImpl
{
    //
    // Protected Constants
    //

    //
    // Class Variables
    //

    //
    // Instance Variables
    //

    // Attribute Instance Variables

    // Relationship Instance Variables

    ConfigMessageResources yourConfig = null;

    //
    // Constructors and Initializers    
    //

    public AppMessageResourcesImpl(ConfigMessageResources configResources) {
	yourConfig = configResources;
    }
    
    //
    // Class methods
    //

    //
    // General Methods
    //
    
    protected Message getMessage(Locale locale, String messageId, 
				 Object params[]) {
	ParameterCheck.nonNull(messageId);
	Message result = null;
	ConfigMessage template = null;
	String 
	    messageClass = null,
	    summary = null,
	    detail = null;
	Class messageClazz = null;

	// if we have a message for this messageId
	if (null != (template = (ConfigMessage)
		     yourConfig.getMessages().get(messageId))) {
	    // if we have message-class, just instantiate and return it.
	    if (null != (messageClass = template.getMessageClass())) {
		try {
		    messageClazz = Util.loadClass(messageClass, this);
		    result = (Message) messageClazz.newInstance();
		}
		catch (Throwable e) {
		    result = null;
		}
	    }
	    else {
		// We need to manufacture a Message and populate it
		// with the data from ConfigMessage

		// substitute parameters
		// Look first under the given locale, and if not found,
		// use the fallback locale, if specified.
		if (null != (summary = 
			     getLocalizedValueFromMap(template.getSummaries(),
						      locale))) {
		    summary = substituteParams(locale, summary, params);
		}
		if (null != (detail = 
			     getLocalizedValueFromMap(template.getDetails(),
						      locale))) {
		    detail = substituteParams(locale, detail, params);
		}
		result = new MessageImpl(template.getSeverity(), 
					 summary, detail);
	    }
	}

	return result;
    }

    /**
     * <p>Apply logic similar to that found in
     * <code>java.lang.ResourceBundle</code> to derive a key into the
     * argument map.</p>
     *
     * <p>For each of the following values generated from the argument
     * Locale, use that value as a key into the argument map.  Return
     * the first non-null value from the map.</p>
     *
     * language_country_variant<br>
     * language_country<br>
     * language<br>
     * null<br>
     *
     * @return the first value in the argument map that matches a key
     * generated by Locale using the above rules to derive the key.
     */

    protected String getLocalizedValueFromMap(Map map, Locale locale) {
	String 
	    key = null,
	    result = null;

	// PENDING(edburns): faster to use '_' or "_" for string
	// concatenation?
	key = locale.toString();
	if (null != (result = (String) map.get(key))) {
	    return result;
	}

	key = locale.getLanguage() + "_" + locale.getCountry();
	if (null != (result = (String) map.get(key))) {
	    return result;
	}

	key = locale.getLanguage();
	if (null != (result = (String) map.get(key))) {
	    return result;
	}
	
	result = (String) map.get(null);
	return result;
    }


    //
    // Methods from MessageResources
    // 
    public Message getMessage(FacesContext context, String messageId) {
        return getMessage(context, messageId, null);
    }    

    public Message getMessage(FacesContext context, String messageId,
			      Object params[]) {
        if (context == null || messageId == null ) {
            throw new NullPointerException("One or more parameters could be null");
        }
        
        Locale locale = context.getLocale();
        Assert.assert_it(locale != null);
        return getMessage(locale, messageId, params);
    }  
    
    public Message getMessage(FacesContext context, String messageId,
                                       Object param0) {
        return getMessage(context, messageId, new Object[]{param0});                                       
    }                                       
    
    public Message getMessage(FacesContext context, String messageId,
                                       Object param0, Object param1) {
         return getMessage(context, messageId, new Object[]{param0, param1});                                        
    }                                       

    public Message getMessage(FacesContext context, String messageId,
                                       Object param0, Object param1,
                                       Object param2) {
         return getMessage(context, messageId, 
             new Object[]{param0, param1, param2});                                        
    }                                       

    public Message getMessage(FacesContext context, String messageId,
                                       Object param0, Object param1,
                                       Object param2, Object param3) {
         return getMessage(context, messageId, 
                 new Object[]{param0, param1, param2, param3});                                        
    }                                       

} // end of class AppMessageResourcesImpl
