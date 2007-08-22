/**
 * TaskData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.xplanner.soap;

@SuppressWarnings({ "unchecked", "serial", "unused", "null" })
public class TaskData  extends org.eclipse.mylyn.xplanner.wsdl.soap.domain.DomainData  implements java.io.Serializable {
    private int acceptorId;

    private double actualHours;

    private double adjustedEstimatedHours;

    private boolean completed;

    private java.util.Calendar createdDate;

    private java.lang.String description;

    private java.lang.String dispositionName;

    private double estimatedHours;

    private double estimatedOriginalHours;

    private java.lang.String name;

    private double remainingHours;

    private int storyId;

    private java.lang.String type;

    public TaskData() {
    }

    public TaskData(
           int id,
           java.util.Calendar lastUpdateTime,
           int acceptorId,
           double actualHours,
           double adjustedEstimatedHours,
           boolean completed,
           java.util.Calendar createdDate,
           java.lang.String description,
           java.lang.String dispositionName,
           double estimatedHours,
           double estimatedOriginalHours,
           java.lang.String name,
           double remainingHours,
           int storyId,
           java.lang.String type) {
        super(
            id,
            lastUpdateTime);
        this.acceptorId = acceptorId;
        this.actualHours = actualHours;
        this.adjustedEstimatedHours = adjustedEstimatedHours;
        this.completed = completed;
        this.createdDate = createdDate;
        this.description = description;
        this.dispositionName = dispositionName;
        this.estimatedHours = estimatedHours;
        this.estimatedOriginalHours = estimatedOriginalHours;
        this.name = name;
        this.remainingHours = remainingHours;
        this.storyId = storyId;
        this.type = type;
    }


    /**
     * Gets the acceptorId value for this TaskData.
     * 
     * @return acceptorId
     */
    public int getAcceptorId() {
        return acceptorId;
    }


    /**
     * Sets the acceptorId value for this TaskData.
     * 
     * @param acceptorId
     */
    public void setAcceptorId(int acceptorId) {
        this.acceptorId = acceptorId;
    }


    /**
     * Gets the actualHours value for this TaskData.
     * 
     * @return actualHours
     */
    public double getActualHours() {
        return actualHours;
    }


    /**
     * Sets the actualHours value for this TaskData.
     * 
     * @param actualHours
     */
    public void setActualHours(double actualHours) {
        this.actualHours = actualHours;
    }


    /**
     * Gets the adjustedEstimatedHours value for this TaskData.
     * 
     * @return adjustedEstimatedHours
     */
    public double getAdjustedEstimatedHours() {
        return adjustedEstimatedHours;
    }


    /**
     * Sets the adjustedEstimatedHours value for this TaskData.
     * 
     * @param adjustedEstimatedHours
     */
    public void setAdjustedEstimatedHours(double adjustedEstimatedHours) {
        this.adjustedEstimatedHours = adjustedEstimatedHours;
    }


    /**
     * Gets the completed value for this TaskData.
     * 
     * @return completed
     */
    public boolean isCompleted() {
        return completed;
    }


    /**
     * Sets the completed value for this TaskData.
     * 
     * @param completed
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }


    /**
     * Gets the createdDate value for this TaskData.
     * 
     * @return createdDate
     */
    public java.util.Calendar getCreatedDate() {
        return createdDate;
    }


    /**
     * Sets the createdDate value for this TaskData.
     * 
     * @param createdDate
     */
    public void setCreatedDate(java.util.Calendar createdDate) {
        this.createdDate = createdDate;
    }


    /**
     * Gets the description value for this TaskData.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this TaskData.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the dispositionName value for this TaskData.
     * 
     * @return dispositionName
     */
    public java.lang.String getDispositionName() {
        return dispositionName;
    }


    /**
     * Sets the dispositionName value for this TaskData.
     * 
     * @param dispositionName
     */
    public void setDispositionName(java.lang.String dispositionName) {
        this.dispositionName = dispositionName;
    }


    /**
     * Gets the estimatedHours value for this TaskData.
     * 
     * @return estimatedHours
     */
    public double getEstimatedHours() {
        return estimatedHours;
    }


    /**
     * Sets the estimatedHours value for this TaskData.
     * 
     * @param estimatedHours
     */
    public void setEstimatedHours(double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }


    /**
     * Gets the estimatedOriginalHours value for this TaskData.
     * 
     * @return estimatedOriginalHours
     */
    public double getEstimatedOriginalHours() {
        return estimatedOriginalHours;
    }


    /**
     * Sets the estimatedOriginalHours value for this TaskData.
     * 
     * @param estimatedOriginalHours
     */
    public void setEstimatedOriginalHours(double estimatedOriginalHours) {
        this.estimatedOriginalHours = estimatedOriginalHours;
    }


    /**
     * Gets the name value for this TaskData.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this TaskData.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the remainingHours value for this TaskData.
     * 
     * @return remainingHours
     */
    public double getRemainingHours() {
        return remainingHours;
    }


    /**
     * Sets the remainingHours value for this TaskData.
     * 
     * @param remainingHours
     */
    public void setRemainingHours(double remainingHours) {
        this.remainingHours = remainingHours;
    }


    /**
     * Gets the storyId value for this TaskData.
     * 
     * @return storyId
     */
    public int getStoryId() {
        return storyId;
    }


    /**
     * Sets the storyId value for this TaskData.
     * 
     * @param storyId
     */
    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }


    /**
     * Gets the type value for this TaskData.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this TaskData.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TaskData)) return false;
        TaskData other = (TaskData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.acceptorId == other.getAcceptorId() &&
            this.actualHours == other.getActualHours() &&
            this.adjustedEstimatedHours == other.getAdjustedEstimatedHours() &&
            this.completed == other.isCompleted() &&
            ((this.createdDate==null && other.getCreatedDate()==null) || 
             (this.createdDate!=null &&
              this.createdDate.equals(other.getCreatedDate()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.dispositionName==null && other.getDispositionName()==null) || 
             (this.dispositionName!=null &&
              this.dispositionName.equals(other.getDispositionName()))) &&
            this.estimatedHours == other.getEstimatedHours() &&
            this.estimatedOriginalHours == other.getEstimatedOriginalHours() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            this.remainingHours == other.getRemainingHours() &&
            this.storyId == other.getStoryId() &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType())));
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
        _hashCode += getAcceptorId();
        _hashCode += new Double(getActualHours()).hashCode();
        _hashCode += new Double(getAdjustedEstimatedHours()).hashCode();
        _hashCode += (isCompleted() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getCreatedDate() != null) {
            _hashCode += getCreatedDate().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getDispositionName() != null) {
            _hashCode += getDispositionName().hashCode();
        }
        _hashCode += new Double(getEstimatedHours()).hashCode();
        _hashCode += new Double(getEstimatedOriginalHours()).hashCode();
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        _hashCode += new Double(getRemainingHours()).hashCode();
        _hashCode += getStoryId();
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TaskData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://xplanner.org/soap", "TaskData")); //$NON-NLS-1$ //$NON-NLS-2$
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acceptorId"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "acceptorId")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actualHours"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "actualHours")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adjustedEstimatedHours"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "adjustedEstimatedHours")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("completed"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "completed")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createdDate"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "createdDate")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "description")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dispositionName"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "dispositionName")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estimatedHours"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "estimatedHours")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estimatedOriginalHours"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "estimatedOriginalHours")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "name")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("remainingHours"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "remainingHours")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("storyId"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "storyId")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "type")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
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
