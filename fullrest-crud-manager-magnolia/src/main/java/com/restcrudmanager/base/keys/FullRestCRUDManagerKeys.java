package com.restcrudmanager.base.keys;


/**
 * This class is optional and lets you manager the versions of your module, by
 * registering "deltas" to maintain the module's configuration, or other type of
 * content. If you don't need this, simply remove the reference to this class in
 * the module descriptor xml.
 */
public class FullRestCRUDManagerKeys{

//	public static final String APP_NAME = "languagesmanagerapp";
//	
	public static final String FIELD_ID = "idLanguage"; //TODO ELIMINAR
	public static final String FIELD_PRIMARYKEY = "primaryKey"; //TODO ELIMINAR
	public static final String FIELD_TTOOID = "idTtoo";//TODO borrar
	public static final String FIELD_CODE = "code";//TODO borrar
	public static final String FIELD_TTOO = "ttoo";//TODO borrar
//	public static final String FIELD_FOREIGNKEY = "foreignKey";
	public static final String FIELD_NAME = "name";//TODO borrar
//	
//	
	public static final String MODE_OPERATION_ADD = "ADD";
	public static final String MODE_OPERATION_EDIT = "EDIT";
	public static final String MODE_OPERATION_DELETE = "DELETE";
//	
	public static final String HTTP_MODE_POST = "POST";
	public static final String HTTP_MODE_PUT = "PUT";
	public static final String HTTP_MODE_GET = "GET";
//	
	public static final String CTE_MODULEPATH = "/modules/restcrudmanager/apps/";
	public static final String CTE_SERVICECONFIG_NODE = "/serviceConfig";
	public static final String CTE_URLSERVICEBASE_PROPERTY = "urlBase";
////	public static final String CTE_URLSERVICE_PROPERTY = "urlService";
	public static final String CTE_CONFIGWORKSPACE = "config";
	public static final String CTE_URLSERVICEHEADERSPATH = CTE_SERVICECONFIG_NODE+"/headersService";
	public static final String CTE_FIELDSCORRESPONDENCY_NODE = CTE_SERVICECONFIG_NODE+"/fields";
//	
	public static final String LOGGERTEXT_VALUEPROPERTY = "Property value: ";
	public static final String LOGGERTEXT_NAMEPROPERTY = "Property name: ";
	public static final String LOGGERTEXT_CALLINGSERVICE = "CALLING LANGUAGES SERVICE";
	public static final String LOGGERTEXT_ERRORSERVICE_GETALL = "Service.getItems ERROR: ";
	public static final String LOGGERTEXT_ERRORSERVICE_DELETE = "Service.delItem ERROR: ";
	public static final String LOGGERTEXT_ERRORSERVICE_EDIT = "Service.editItem ERROR: ";
	public static final String LOGGERTEXT_ERRORSERVICE_ADD = "Service.addItem ERROR: ";
	public static final String LOGGERTEXT_ERRORCREATEPOPULATEPROPERTY = "createAndPopulateProperties ERROR: ";
	
//	
//	public static final String SERVICE_PATH = "/entities/language";
	public static final String TTOO_SERVICE_PATH = "/entities/ttoo";//TODO A eliminar
//	
	public static final String CTE_COLON = ":";
	public static final String CTE_COMMA = ",";
	public static final String CTE_SLASH = "/";
//	
	public static final String ERROR_NOTSUPPORTED_TEXT = "Class does NOT SUPPORT this method."; 
	public static final String DEFAULT_ID = "999999";//TODO eliminar
//	
	public static final String FIELD_DESCRIPTION = "description";//TODO a eliminar
	
	private FullRestCRUDManagerKeys(){
		//Nothing to do here
	}
	
}
