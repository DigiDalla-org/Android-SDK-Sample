package com.oovoo.sdk.plugin;

import com.oovoo.sdk.api.LogSdk;
import com.oovoo.sdk.interfaces.PluginFactory;

public class ooVooPluginFactory implements PluginFactory {
	private static final String TAG = "ooVooPluginFactory" ;
	private static boolean is_lib_loaded = false ;
	
	@Override
	public synchronized long createPluginInstance() {
		try
		{
			if(!is_lib_loaded)
			{
				/***
				 * We loaded the library here for be sure that ooVooSdk shared library loaded first.
				 */
				System.loadLibrary("ooVooPlugin");
			}
			is_lib_loaded = true ;
			return createPlugin();
		}
		catch(Exception err){
			LogSdk.d(TAG, "createPluginInstance failed "+err);
			return 0;
		}
	}
	
	private native long createPlugin();
	
}
