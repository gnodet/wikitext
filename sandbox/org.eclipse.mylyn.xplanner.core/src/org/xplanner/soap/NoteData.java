/**
 * NoteData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.xplanner.soap;

@SuppressWarnings( { "unchecked", "serial" })
public class NoteData extends org.eclipse.mylyn.xplanner.wsdl.soap.domain.DomainData implements java.io.Serializable {
	private int attachedToId;

	private int attachmentId;

	private int authorId;

	private java.lang.String body;

	private java.lang.String subject;

	private java.util.Calendar submissionTime;

	public NoteData() {
	}

	public NoteData(int id, java.util.Calendar lastUpdateTime, int attachedToId, int attachmentId, int authorId,
			java.lang.String body, java.lang.String subject, java.util.Calendar submissionTime) {
		super(id, lastUpdateTime);
		this.attachedToId = attachedToId;
		this.attachmentId = attachmentId;
		this.authorId = authorId;
		this.body = body;
		this.subject = subject;
		this.submissionTime = submissionTime;
	}

	/**
	 * Gets the attachedToId value for this NoteData.
	 * 
	 * @return attachedToId
	 */
	public int getAttachedToId() {
		return attachedToId;
	}

	/**
	 * Sets the attachedToId value for this NoteData.
	 * 
	 * @param attachedToId
	 */
	public void setAttachedToId(int attachedToId) {
		this.attachedToId = attachedToId;
	}

	/**
	 * Gets the attachmentId value for this NoteData.
	 * 
	 * @return attachmentId
	 */
	public int getAttachmentId() {
		return attachmentId;
	}

	/**
	 * Sets the attachmentId value for this NoteData.
	 * 
	 * @param attachmentId
	 */
	public void setAttachmentId(int attachmentId) {
		this.attachmentId = attachmentId;
	}

	/**
	 * Gets the authorId value for this NoteData.
	 * 
	 * @return authorId
	 */
	public int getAuthorId() {
		return authorId;
	}

	/**
	 * Sets the authorId value for this NoteData.
	 * 
	 * @param authorId
	 */
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	/**
	 * Gets the body value for this NoteData.
	 * 
	 * @return body
	 */
	public java.lang.String getBody() {
		return body;
	}

	/**
	 * Sets the body value for this NoteData.
	 * 
	 * @param body
	 */
	public void setBody(java.lang.String body) {
		this.body = body;
	}

	/**
	 * Gets the subject value for this NoteData.
	 * 
	 * @return subject
	 */
	public java.lang.String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject value for this NoteData.
	 * 
	 * @param subject
	 */
	public void setSubject(java.lang.String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the submissionTime value for this NoteData.
	 * 
	 * @return submissionTime
	 */
	public java.util.Calendar getSubmissionTime() {
		return submissionTime;
	}

	/**
	 * Sets the submissionTime value for this NoteData.
	 * 
	 * @param submissionTime
	 */
	public void setSubmissionTime(java.util.Calendar submissionTime) {
		this.submissionTime = submissionTime;
	}

	private java.lang.Object __equalsCalc = null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof NoteData)) {
			return false;
		}
		NoteData other = (NoteData) obj;
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj)
				&& this.attachedToId == other.getAttachedToId()
				&& this.attachmentId == other.getAttachmentId()
				&& this.authorId == other.getAuthorId()
				&& ((this.body == null && other.getBody() == null) || (this.body != null && this.body.equals(other.getBody())))
				&& ((this.subject == null && other.getSubject() == null) || (this.subject != null && this.subject.equals(other.getSubject())))
				&& ((this.submissionTime == null && other.getSubmissionTime() == null) || (this.submissionTime != null && this.submissionTime.equals(other.getSubmissionTime())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	@Override
	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = super.hashCode();
		_hashCode += getAttachedToId();
		_hashCode += getAttachmentId();
		_hashCode += getAuthorId();
		if (getBody() != null) {
			_hashCode += getBody().hashCode();
		}
		if (getSubject() != null) {
			_hashCode += getSubject().hashCode();
		}
		if (getSubmissionTime() != null) {
			_hashCode += getSubmissionTime().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			NoteData.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://xplanner.org/soap", "NoteData")); //$NON-NLS-1$ //$NON-NLS-2$
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("attachedToId"); //$NON-NLS-1$
		elemField.setXmlName(new javax.xml.namespace.QName("", "attachedToId")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("attachmentId"); //$NON-NLS-1$
		elemField.setXmlName(new javax.xml.namespace.QName("", "attachmentId")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("authorId"); //$NON-NLS-1$
		elemField.setXmlName(new javax.xml.namespace.QName("", "authorId")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("body"); //$NON-NLS-1$
		elemField.setXmlName(new javax.xml.namespace.QName("", "body")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("subject"); //$NON-NLS-1$
		elemField.setXmlName(new javax.xml.namespace.QName("", "subject")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("submissionTime"); //$NON-NLS-1$
		elemField.setXmlName(new javax.xml.namespace.QName("", "submissionTime")); //$NON-NLS-1$ //$NON-NLS-2$
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime")); //$NON-NLS-1$ //$NON-NLS-2$
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
	public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
	}

}
