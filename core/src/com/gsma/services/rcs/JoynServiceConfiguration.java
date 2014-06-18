/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright (C) 2010 France Telecom S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.gsma.services.rcs;

import com.gsma.services.rcs.JoynServiceConfiguration.Settings.DefaultMessagingMethods;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Joyn Service configuration
 *  
 * @author Jean-Marc AUFFRET
 * @author YPLO6403
 */
public class JoynServiceConfiguration {
	private static final String[] PROJECTION = new String[] { Settings.VALUE };
	private static final String WHERE_CLAUSE = new StringBuilder(Settings.KEY).append("=?").toString();
	
	/**
	 * Checks the RCS configuration validity.
	 * 
	 * @param ctx
	 *            Context
	 * @return Boolean True if the joyn configuration is valid.
	 */
	public static boolean isConfigValid(Context ctx) {
		try {
			return Boolean.parseBoolean(getStringValueSetting(ctx, Settings.CONFIGURATION_VALIDITY));
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * Returns the display name associated to the joyn user account.<br>
	 * The display name may be updated by the end user via the joyn settings application.
	 * 
	 * @param ctx
	 *            Context
	 * @return Display name
	 */
	public static String getMyDisplayName(Context ctx) {
		return getStringValueSetting(ctx, Settings.MY_DISPLAY_NAME);
	}
	
	/**
	 * Update the display name associated to the joyn user account.
	 * @param ctx Context
	 * @param name the new display name
	 */
	public static void setMyDisplayName(Context ctx, String name) {
		if (name == null) {
			throw new IllegalArgumentException("Display name is null");
		}
		ContentValues values = new ContentValues();
		values.put(Settings.VALUE, name);
		updateSettings( ctx, values, Settings.MY_DISPLAY_NAME );
	}
	
	/**
	 * Returns the user country code.
	 * 
	 * @param ctx
	 *            the context
	 * @return the contact ID
	 */
	public static String getMyCountryCode(Context ctx) {
		return getStringValueSetting(ctx, Settings.MY_COUNTRY_CODE);
	}
	
	/**
	 * Returns the user contact Identifier (i.e. username part of the IMPU).
	 * 
	 * @param ctx
	 *            the context
	 * @return the contact ID
	 */
	public static String getMyContactId(Context ctx) {
		return getStringValueSetting(ctx, Settings.MY_CONTACT_ID);
	}
	
	/**
	 * Returns the messaging client mode which can be INTEGRATED, CONVERGED, SEAMLESS or NONE.
	 * 
	 * @param ctx
	 *            the context
	 * @return the messaging client mode
	 */
	public static int getMessagingUX(Context ctx) {
		try {
			return Integer.parseInt(getStringValueSetting(ctx, Settings.MESSAGING_MODE));
		} catch (Exception e) {
		}
		return Settings.MessagingModes.NONE;
	}
	
	/**
	 * Returns the default messaging method which can be AUTOMATIC, JOYN or NON_JOYN.
	 * 
	 * @param ctx
	 *            the context
	 * @return the default messaging method
	 */
	public static int getDefaultMessagingMethod(Context ctx) {
		try {
			return Integer.parseInt(getStringValueSetting(ctx, Settings.DEFAULT_MESSAGING_METHOD));
		} catch (Exception e) {
		}
		// TODO CR026 exception handling : check if appropriate
		throw new IllegalArgumentException("Default messaging method is invalid");
	}
	
	/**
	 * Update the default messaging method.
	 * 
	 * @param ctx
	 *            Context
	 * @param method
	 *            the default messaging method which can be AUTOMATIC, JOYN or NON_JOYN.
	 */
	public static void setDefaultMessagingMethod(Context ctx, int method) {
		if (method < DefaultMessagingMethods.AUTOMATIC || method > DefaultMessagingMethods.NON_JOYN) {
			throw new IllegalArgumentException("Invalid default messaging method");
		}
		ContentValues values = new ContentValues();
		values.put(Settings.VALUE, method);
		updateSettings( ctx, values, Settings.DEFAULT_MESSAGING_METHOD);
	}
	
	/**
	 * Update a setting
	 * 
	 * @param context
	 *            the context
	 * @param contentValues
	 *            the content values
	 * @param key
	 *            the key of the setting
	 */
	private static void updateSettings(Context context, final ContentValues contentValues, String key) {
		ContentResolver cr = context.getContentResolver();
		String[] whereArgs = new String[] { key };
		cr.update(Settings.CONTENT_URI, contentValues, WHERE_CLAUSE, whereArgs);
	}
	
	/**
	 * Query a string value knowing the corresponding key.
	 * 
	 * @param ctx
	 *            the context
	 * @param key
	 *            the key
	 * @return the string value
	 */
	private static String getStringValueSetting(Context ctx, final String key) {
		ContentResolver cr = ctx.getContentResolver();
		String[] selectionArgs = new String[] { key };
		Cursor c = null;
		try {
			c = cr.query(Settings.CONTENT_URI, PROJECTION, WHERE_CLAUSE, selectionArgs, null);
			if (c.moveToFirst()) {
				return c.getString(0);
			}
		} finally {
			if (c != null)
				c.close();
		}
		return null;
	}
	
	/**
     * Joyn Service Configuration Settings class
     *
     */
	public static class Settings {
		/**
		 * Content provider URI for RCS settings
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://com.gsma.services.rcs.provider.settings/settings");

		/**
		 * The name of the column containing the unique ID for a row.
		 */
		public static final String ID = "_id";
		
		/**
		 * The name of the column containing the key setting for a row.
		 */
		public static final String KEY = "key";

		/**
		 * The name of the column containing the value setting for a row.
		 */
		public static final String VALUE = "value";

		/**
		 * Key to get MyDisplayName setting
		 */
		public static final String MY_DISPLAY_NAME = "MyDisplayName";
		
		/**
		 * Key to get MyContactId setting
		 */
		public static final String MY_CONTACT_ID = "MyContactId";
	
		/**
		 * Key to get MyCountryCode setting
		 */
		public static final String MY_COUNTRY_CODE = "MyCountryCode";
		
		/**
		 * Key to get MessagingMode setting
		 */
		public static final String MESSAGING_MODE = "MessagingMode";

		/**
		 * Key to get ConfigurationValidity setting
		 */
		public static final String CONFIGURATION_VALIDITY = "ConfigurationValidity";

		/**
		 * Messaging modes class
		 */
		public static class MessagingModes {
			/**
			 * Integrated mode
			 */
			public static final int INTEGRATED = 0;

			/**
			 * Converged mode
			 */
			public static final int CONVERGED = 1;

			/**
			 * Seamless mode
			 */
			public static final int SEAMLESS = 2;

			
			/**
			 * None mode
			 */
			public static final int NONE = 3;

		}
		
		/**
		 * Key to get DefaultMessagingMethod setting
		 * <p>
		 * default sending method to be used when the customer sends a message before the answer to the corresponding capability
		 * check is received
		 */
		public static final String DEFAULT_MESSAGING_METHOD = "DefaultMessagingMethod";
		
		/**
		 * Default Messaging Methods class
		 */
		public static class DefaultMessagingMethods {
			/**
			 * Automatic
			 */
			public static final int AUTOMATIC = 0;

			/**
			 * Joyn
			 */
			public static final int JOYN = 1;

			/**
			 * other methods
			 */
			public static final int NON_JOYN = 2;

		}
	}
}
