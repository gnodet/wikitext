/**
 * UserStoryData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.xplanner.soap;

@SuppressWarnings("unchecked")
public class UserStoryData  extends org.eclipse.mylar.xplanner.wsdl.soap.domain.DomainData  implements java.io.Serializable {
    private double actualHours;

    private double adjustedEstimatedHours;

    private boolean completed;

    private int customerId;

    private java.lang.String description;

    private java.lang.String dispositionName;

    private double estimatedHours;

    private double estimatedOriginalHours;

    private int iterationId;

    private java.lang.String name;

    private double postponedHours;

    private int priority;

    private double remainingHours;

    private int trackerId;

    public UserStoryData() {
    }

    public UserStoryData(
           int id,
           java.util.Calendar lastUpdateTime,
           double actualHours,
           double adjustedEstimatedHours,
           boolean completed,
           int customerId,
           java.lang.String description,
           java.lang.String dispositionName,
           double estimatedHours,
           double estimatedOriginalHours,
           int iterationId,
           java.lang.String name,
           double postponedHours,
           int priority,
           double remainingHours,
           int trackerId) {
        super(
            id,
            lastUpdateTime);
        this.actualHours = actualHours;
        this.adjustedEstimatedHours = adjustedEstimatedHours;
        this.completed = completed;
        this.customerId = customerId;
        this.description = description;
        this.dispositionName = dispositionName;
        this.estimatedHours = estimatedHours;
        this.estimatedOriginalHours = estimatedOriginalHours;
        this.iterationId = iterationId;
        this.name = name;
        this.postponedHours = postponedHours;
        this.priority = priority;
        this.remainingHours = remainingHours;
        this.trackerId = trackerId;
    }


    /**
     * Gets the actualHours value for this UserStoryData.
     * 
     * @return actualHours
     */
    public double getActualHours() {
        return actualHours;
    }


    /**
     * Sets the actualHours value for this UserStoryData.
     * 
     * @param actualHours
     */
    public void setActualHours(double actualHours) {
        this.actualHours = actualHours;
    }


    /**
     * Gets the adjustedEstimatedHours value for this UserStoryData.
     * 
     * @return adjustedEstimatedHours
     */
    public double getAdjustedEstimatedHours() {
        return adjustedEstimatedHours;
    }


    /**
     * Sets the adjustedEstimatedHours value for this UserStoryData.
     * 
     * @param adjustedEstimatedHours
     */
    public void setAdjustedEstimatedHours(double adjustedEstimatedHours) {
        this.adjustedEstimatedHours = adjustedEstimatedHours;
    }


    /**
     * Gets the completed value for this UserStoryData.
     * 
     * @return completed
     */
    public boolean isCompleted() {
        return completed;
    }


    /**
     * Sets the completed value for this UserStoryData.
     * 
     * @param completed
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }


    /**
     * Gets the customerId value for this UserStoryData.
     * 
     * @return customerId
     */
    public int getCustomerId() {
        return customerId;
    }


    /**
     * Sets the customerId value for this UserStoryData.
     * 
     * @param customerId
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }


    /**
     * Gets the description value for this UserStoryData.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this UserStoryData.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the dispositionName value for this UserStoryData.
     * 
     * @return dispositionName
     */
    public java.lang.String getDispositionName() {
        return dispositionName;
    }


    /**
     * Sets the dispositionName value for this UserStoryData.
     * 
     * @param dispositionName
     */
    public void setDispositionName(java.lang.String dispositionName) {
        this.dispositionName = dispositionName;
    }


    /**
     * Gets the estimatedHours value for this UserStoryData.
     * 
     * @return estimatedHours
     */
    public double getEstimatedHours() {
        return estimatedHours;
    }


    /**
     * Sets the estimatedHours value for this UserStoryData.
     * 
     * @param estimatedHours
     */
    public void setEstimatedHours(double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }


    /**
     * Gets the estimatedOriginalHours value for this UserStoryData.
     * 
     * @return estimatedOriginalHours
     */
    public double getEstimatedOriginalHours() {
        return estimatedOriginalHours;
    }


    /**
     * Sets the estimatedOriginalHours value for this UserStoryData.
     * 
     * @param estimatedOriginalHours
     */
    public void setEstimatedOriginalHours(double estimatedOriginalHours) {
        this.estimatedOriginalHours = estimatedOriginalHours;
    }


    /**
     * Gets the iterationId value for this UserStoryData.
     * 
     * @return iterationId
     */
    public int getIterationId() {
        return iterationId;
    }


    /**
     * Sets the iterationId value for this UserStoryData.
     * 
     * @param iterationId
     */
    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }


    /**
     * Gets the name value for this UserStoryData.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this UserStoryData.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the postponedHours value for this UserStoryData.
     * 
     * @return postponedHours
     */
    public double getPostponedHours() {
        return postponedHours;
    }


    /**
     * Sets the postponedHours value for this UserStoryData.
     * 
     * @param postponedHours
     */
    public void setPostponedHours(double postponedHours) {
        this.postponedHours = postponedHours;
    }


    /**
     * Gets the priority value for this UserStoryData.
     * 
     * @return priority
     */
    public int getPriority() {
        return priority;
    }


    /**
     * Sets the priority value for this UserStoryData.
     * 
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }


    /**
     * Gets the remainingHours value for this UserStoryData.
     * 
     * @return remainingHours
     */
    public double getRemainingHours() {
        return remainingHours;
    }


    /**
     * Sets the remainingHours value for this UserStoryData.
     * 
     * @param remainingHours
     */
    public void setRemainingHours(double remainingHours) {
        this.remainingHours = remainingHours;
    }


    /**
     * Gets the trackerId value for this UserStoryData.
     * 
     * @return trackerId
     */
    public int getTrackerId() {
        return trackerId;
    }


    /**
     * Sets the trackerId value for this UserStoryData.
     * 
     * @param trackerId
     */
    public void setTrackerId(int trackerId) {
        this.trackerId = trackerId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UserStoryData)) return false;
        UserStoryData other = (UserStoryData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.actualHours == other.getActualHours() &&
            this.adjustedEstimatedHours == other.getAdjustedEstimatedHours() &&
            this.completed == other.isCompleted() &&
            this.customerId == other.getCustomerId() &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.dispositionName==null && other.getDispositionName()==null) || 
             (this.dispositionName!=null &&
              this.dispositionName.equals(other.getDispositionName()))) &&
            this.estimatedHours == other.getEstimatedHours() &&
            this.estimatedOriginalHours == other.getEstimatedOriginalHours() &&
            this.iterationId == other.getIterationId() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            this.postponedHours == other.getPostponedHours() &&
            this.priority == other.getPriority() &&
            this.remainingHours == other.getRemainingHours() &&
            this.trackerId == other.getTrackerId();
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
        _hashCode += new Double(getActualHours()).hashCode();
        _hashCode += new Double(getAdjustedEstimatedHours()).hashCode();
        _hashCode += (isCompleted() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getCustomerId();
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getDispositionName() != null) {
            _hashCode += getDispositionName().hashCode();
        }
        _hashCode += new Double(getEstimatedHours()).hashCode();
        _hashCode += new Double(getEstimatedOriginalHours()).hashCode();
        _hashCode += getIterationId();
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        _hashCode += new Double(getPostponedHours()).hashCode();
        _hashCode += getPriority();
        _hashCode += new Double(getRemainingHours()).hashCode();
        _hashCode += getTrackerId();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UserStoryData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://xplanner.org/soap", "UserStoryData")); //$NON-NLS-1$ //$NON-NLS-2$
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("customerId"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "customerId")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
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
        elemField.setFieldName("iterationId"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "iterationId")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "name")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postponedHours"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "postponedHours")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priority"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "priority")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("remainingHours"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "remainingHours")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double")); //$NON-NLS-1$ //$NON-NLS-2$
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trackerId"); //$NON-NLS-1$
        elemField.setXmlName(new javax.xml.namespace.QName("", "trackerId")); //$NON-NLS-1$ //$NON-NLS-2$
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
