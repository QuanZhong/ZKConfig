package com.lashou.v.zkconfig.monitor;

public class MonitorFactory {
	public enum MonitorType{
		CONF_FILE_MONITOR(1),
		CONF_PROPERTIES_FILE_MONITOR(2),
		CONF_XML_FILE_MONITOR(3),
		NONE(0);
		
		private final int intValue;
		MonitorType(int intValue){
			this.intValue = intValue;
		}
		public int getIntValue() {
			return intValue;
		}
		public static MonitorType getMonitorType(int value){
			for(MonitorType type : MonitorType.values()){
				if(value == type.getIntValue()){
					return type;
				}
			}
			return MonitorType.NONE;
		}
		
	}
	public static Monitor getMonitor(MonitorType type){
		
		switch(type.intValue){
			case 1:return new ConfFileMonitor();
			case 2:return new PropertiesFileMonitor();
			default: throw new RuntimeException("MonitorType暂未实现！type="+type.intValue);
		}
		
	}
}
