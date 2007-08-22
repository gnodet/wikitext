/**
 * TimeEntryData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.xplanner.soap;

@SuppressWarnings({ "unchecked", "serial", "unused", "null" })
public class TimeEntryData  extends org.eclipse.mylyn.xplanner.wsdl.soap.domain.DomainData  implements java.io.Serializable {
    private java.lang.String description;

    private double duration;

    private java.util.Calendar endTime;

    private int person1Id;

    private int person2Id;

    private java.util.Calendar reportDate;

    private java.util.Calendar startTime;

    private int taskId;

    public TimeEntryData() {
    }

    public TimeEntryData(
           int id,
           java.util.Calendar lastUpdateTime,
           java.lang.String description,
           double duration,
           java.util.Calendar endTime,
           int person1Id,
           int person2Id,
           java.util.Calendar reportDate,
           java.util.Calendar startTime,
           int taskId) {
        super(
            id,
            lastUpdateTime);
        this.description = description;
        this.duration = duration;
        this.endTime = endTime;
        this.person1Id = person1Id;
        this.person2Id = person2Id;
        this.reportDate = reportDate;
        this.startTime = startTime;
        this.taskId = taskId;
    }


    /**
     * Gets the description value for this TimeEntryData.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this TimeEntryData.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the duration value for this TimeEntryData.
     * 
     * @return duration
     */
    public double getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this TimeEntryData.
     * 
     * @param duration
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }


    /**
     * Gets the endTime value for this TimeEntryData.
     * 
     * @return endTime
     */
    public java.util.Calendar getEndTime() {
        return endTime;
    }


    /**
     * Sets the endTime value for this TimeEntryData.
     * 
     * @param endTime
     */
    public void setEndTime(java.util.Calendar endTime) {
        this.endTime = endTime;
    }


    /**
     * Gets the person1Id value for this TimeEntryData.
     * 
     * @return person1Id
     */
    public int getPerson1Id() {
        return person1Id;
    }


    /**
     * Sets the person1Id value for this TimeEntryData.
     * 
     * @param person1Id
     */
    public void setPerson1Id(int person1Id) {
        this.person1Id = person1Id;
    }


    /**
     * Gets the person2Id value for this TimeEntryData.
     * 
     * @return person2Id
     */
    public int getPerson2Id() {
        return person2Id;
    }


    /**
     * Sets the person2Id value for this TimeEntryData.
     * 
     * @param person2Id
     */
    public void setPerson2Id(int person2Id) {
        this.person2Id = person2Id;
    }


    /**
     * Gets the reportDate value for this TimeEntryData.
     * 
     * @return reportDate
     */
    public java.util.Calendar getReportDate() {
        return reportDate;
    }


    /**
     * Sets the reportDate value for this TimeEntryData.
     * 
     * @param reportDate
     */
    public void setReportDate(java.util.Calendar reportDate) {
        this.reportDate = reportDate;
    }


    /**
     * Gets the startTime value for this TimeEntryData.
     * 
     * @return startTime
     */
    public java.util.Calendar getStartTime() {
        return startTime;
    }


    /**
     * Sets the startTime value for this TimeEntryData.
     * 
     * @param startTime
     */
    public void setStartTime(java.util.Calendar startTime) {
        this.startTime = startTime;
    }


    /**
     * Gets the taskId value for this TimeEntryData.
     * 
     * @return taskId
     */
    public int getTaskId() {
        return taskId;
    }


    /**
     * Sets the taskId value for this TimeEntryData.
     * 
     * @param taskId
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TimeEntryData)) return false;
        TimeEntryData other = (TimeEntryData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            this.duration == other.getDuration() &&
            ((this.endTime==null && other.getEndTime()==null) || 
             (this.endTime!=null &&
              this.endTime.equals(other.getEndTime()))) &&
            this.person1Id == other.getPerson1Id() &&
            this.person2Id == other.getPerson2Id() &&
            ((this.reportDate==null && other.getReportDate()==null) || 
             (this.reportDate!=null &&
              this.reportDate.equals(other.getReportDate()))) &&
            ((this.startTime==null && other.getStartTime()==null) || 
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime()))) &&
            this.taskId == other.getTaskId();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        _hashCode += new Double(getDuration()).hashCode();
        if (getEndTime() != null) {
            _hashCode += getEndTime().hashCode();
        }
        _hashCode += getPerson1Id();
        _hashCode += getPerson2Id();
        if (getReportDate() != null) {
            _hashCode += getReportDate().hashCode();
        }
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        _hashCode += getTaskId();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TimeEntryData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://xplanner.org/soap", "TimeEntryData")); //$NON-NLS-1$ //$NON-NLS-2$
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "description")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("duration"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "duration")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endTime"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "endTime")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("person1Id"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "person1Id")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("person2Id"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "person2Id")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reportDate"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "reportDate")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startTime"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "startTime")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taskId"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "taskId")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
