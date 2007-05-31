/**
 * XPlannerSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.xplanner.soap.XPlanner;

@SuppressWarnings("unchecked")
public class XPlannerSoapBindingStub extends org.apache.axis.client.Stub implements org.xplanner.soap.XPlanner.XPlanner {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[43];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
        _initOperationDesc5();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAttributes"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "objectId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(java.util.HashMap.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAttributesReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("update"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "object"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "ProjectData"), org.xplanner.soap.ProjectData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("update"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "object"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "PersonData"), org.xplanner.soap.PersonData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("update"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "note"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "NoteData"), org.xplanner.soap.NoteData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("update"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "object"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "TimeEntryData"), org.xplanner.soap.TimeEntryData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("update"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "object"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "TaskData"), org.xplanner.soap.TaskData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("update"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "object"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "IterationData"), org.xplanner.soap.IterationData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("update"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "object"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "UserStoryData"), org.xplanner.soap.UserStoryData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAttribute"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "objectId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAttributeReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setAttribute"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "objectId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "value"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getNote"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "NoteData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.NoteData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getNoteReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeNote"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPerson"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "PersonData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.PersonData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getPersonReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getUserStories"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "containerId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_UserStoryData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.UserStoryData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getUserStoriesReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTasks"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "containerId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_TaskData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.TaskData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTasksReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTimeEntries"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "containerId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_TimeEntryData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.TimeEntryData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTimeEntriesReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addTask"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "task"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "TaskData"), org.xplanner.soap.TaskData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "TaskData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.TaskData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addTaskReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getIterations"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "projectId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_IterationData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.IterationData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getIterationsReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCurrentIteration"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "projectId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "IterationData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.IterationData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getCurrentIterationReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPeople"); //$NON-NLS-1$
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_PersonData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.PersonData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getPeopleReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getProject"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "ProjectData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.ProjectData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getProjectReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getUserStory"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "UserStoryData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.UserStoryData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getUserStoryReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getIteration"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "IterationData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.IterationData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getIterationReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTask"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "TaskData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.TaskData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTaskReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getProjects"); //$NON-NLS-1$
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_ProjectData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.ProjectData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getProjectsReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addProject"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "project"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "ProjectData"), org.xplanner.soap.ProjectData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "ProjectData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.ProjectData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addProjectReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeProject"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addIteration"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "iteration"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "IterationData"), org.xplanner.soap.IterationData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "IterationData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.IterationData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addIterationReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeIteration"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addUserStory"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "story"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "UserStoryData"), org.xplanner.soap.UserStoryData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "UserStoryData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.UserStoryData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addUserStoryReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeUserStory"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCurrentTasksForPerson"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "personId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_TaskData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.TaskData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getCurrentTasksForPersonReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "fault"), //$NON-NLS-1$ //$NON-NLS-2$
                      "org.eclipse.mylar.xplanner.wsdl.db.QueryException", //$NON-NLS-1$
                      new javax.xml.namespace.QName("http://db.xplanner.technoetic.com", "QueryException"),  //$NON-NLS-1$ //$NON-NLS-2$
                      true
                     ));
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPlannedTasksForPerson"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "personId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_TaskData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.TaskData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getPlannedTasksForPersonReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "fault"), //$NON-NLS-1$ //$NON-NLS-2$
                      "org.eclipse.mylar.xplanner.wsdl.db.QueryException", //$NON-NLS-1$
                      new javax.xml.namespace.QName("http://db.xplanner.technoetic.com", "QueryException"),  //$NON-NLS-1$ //$NON-NLS-2$
                      true
                     ));
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeTask"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTimeEntry"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "TimeEntryData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.TimeEntryData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTimeEntryReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addTimeEntry"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "timeEntry"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "TimeEntryData"), org.xplanner.soap.TimeEntryData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "TimeEntryData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.TimeEntryData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addTimeEntryReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeTimeEntry"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addNote"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "note"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "NoteData"), org.xplanner.soap.NoteData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "NoteData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.NoteData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addNoteReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getNotesForObject"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "attachedToId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_NoteData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.NoteData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getNotesForObjectReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[38] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addPerson"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "object"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://xplanner.org/soap", "PersonData"), org.xplanner.soap.PersonData.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xplanner.org/soap", "PersonData")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(org.xplanner.soap.PersonData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "addPersonReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[39] = oper;

    }

    private static void _initOperationDesc5(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removePerson"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[40] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteAttribute"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "objectId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[41] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAttributesWithPrefix"); //$NON-NLS-1$
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "objectId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "prefix"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), java.lang.String.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setReturnClass(java.util.HashMap.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAttributesWithPrefixReturn")); //$NON-NLS-1$ //$NON-NLS-2$
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[42] = oper;

    }

    public XPlannerSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public XPlannerSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public XPlannerSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2"); //$NON-NLS-1$
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://db.xplanner.technoetic.com", "QueryException"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.eclipse.mylar.xplanner.wsdl.db.QueryException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://domain.soap.xplanner.technoetic.com", "DomainData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.eclipse.mylar.xplanner.wsdl.soap.domain.DomainData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "IterationData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.IterationData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "NoteData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.NoteData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "PersonData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.PersonData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "ProjectData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.ProjectData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "TaskData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.TaskData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "TimeEntryData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.TimeEntryData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "UserStoryData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.UserStoryData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_IterationData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.IterationData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "IterationData"); //$NON-NLS-1$ //$NON-NLS-2$
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_NoteData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.NoteData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "NoteData"); //$NON-NLS-1$ //$NON-NLS-2$
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_PersonData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.PersonData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "PersonData"); //$NON-NLS-1$ //$NON-NLS-2$
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_ProjectData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.ProjectData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "ProjectData"); //$NON-NLS-1$ //$NON-NLS-2$
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_TaskData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.TaskData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "TaskData"); //$NON-NLS-1$ //$NON-NLS-2$
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_TimeEntryData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.TimeEntryData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "TimeEntryData"); //$NON-NLS-1$ //$NON-NLS-2$
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "ArrayOf_tns1_UserStoryData"); //$NON-NLS-1$ //$NON-NLS-2$
            cachedSerQNames.add(qName);
            cls = org.xplanner.soap.UserStoryData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://xplanner.org/soap", "UserStoryData"); //$NON-NLS-1$ //$NON-NLS-2$
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t); //$NON-NLS-1$
        }
    }

    public java.util.HashMap getAttributes(int objectId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getAttributes")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(objectId)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.util.HashMap) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.util.HashMap) org.apache.axis.utils.JavaUtils.convert(_resp, java.util.HashMap.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void update(org.xplanner.soap.ProjectData object) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "update")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {object});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void update(org.xplanner.soap.PersonData object) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "update")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {object});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void update(org.xplanner.soap.NoteData note) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "update")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {note});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void update(org.xplanner.soap.TimeEntryData object) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "update")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {object});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void update(org.xplanner.soap.TaskData object) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "update")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {object});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void update(org.xplanner.soap.IterationData object) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "update")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {object});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void update(org.xplanner.soap.UserStoryData object) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "update")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {object});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String getAttribute(int objectId, java.lang.String key) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getAttribute")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(objectId), key});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void setAttribute(int objectId, java.lang.String key, java.lang.String value) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "setAttribute")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(objectId), key, value});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.NoteData getNote(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getNote")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.NoteData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.NoteData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.NoteData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void removeNote(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "removeNote")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.PersonData getPerson(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getPerson")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.PersonData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.PersonData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.PersonData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.UserStoryData[] getUserStories(int containerId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getUserStories")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(containerId)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.UserStoryData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.UserStoryData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.UserStoryData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.TaskData[] getTasks(int containerId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getTasks")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(containerId)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.TaskData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.TaskData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.TaskData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.TimeEntryData[] getTimeEntries(int containerId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getTimeEntries")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(containerId)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.TimeEntryData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.TimeEntryData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.TimeEntryData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.TaskData addTask(org.xplanner.soap.TaskData task) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "addTask")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {task});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.TaskData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.TaskData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.TaskData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.IterationData[] getIterations(int projectId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getIterations")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(projectId)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.IterationData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.IterationData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.IterationData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.IterationData getCurrentIteration(int projectId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getCurrentIteration")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(projectId)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.IterationData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.IterationData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.IterationData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.PersonData[] getPeople() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getPeople")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.PersonData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.PersonData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.PersonData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.ProjectData getProject(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getProject")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.ProjectData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.ProjectData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.ProjectData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.UserStoryData getUserStory(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getUserStory")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.UserStoryData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.UserStoryData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.UserStoryData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.IterationData getIteration(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getIteration")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.IterationData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.IterationData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.IterationData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.TaskData getTask(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getTask")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.TaskData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.TaskData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.TaskData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.ProjectData[] getProjects() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getProjects")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.ProjectData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.ProjectData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.ProjectData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.ProjectData addProject(org.xplanner.soap.ProjectData project) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "addProject")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {project});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.ProjectData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.ProjectData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.ProjectData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void removeProject(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "removeProject")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.IterationData addIteration(org.xplanner.soap.IterationData iteration) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "addIteration")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {iteration});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.IterationData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.IterationData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.IterationData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void removeIteration(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "removeIteration")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.UserStoryData addUserStory(org.xplanner.soap.UserStoryData story) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "addUserStory")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {story});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.UserStoryData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.UserStoryData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.UserStoryData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void removeUserStory(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "removeUserStory")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.TaskData[] getCurrentTasksForPerson(int personId) throws java.rmi.RemoteException, org.eclipse.mylar.xplanner.wsdl.db.QueryException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getCurrentTasksForPerson")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(personId)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.TaskData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.TaskData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.TaskData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.eclipse.mylar.xplanner.wsdl.db.QueryException) {
              throw (org.eclipse.mylar.xplanner.wsdl.db.QueryException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.xplanner.soap.TaskData[] getPlannedTasksForPerson(int personId) throws java.rmi.RemoteException, org.eclipse.mylar.xplanner.wsdl.db.QueryException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getPlannedTasksForPerson")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(personId)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.TaskData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.TaskData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.TaskData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.eclipse.mylar.xplanner.wsdl.db.QueryException) {
              throw (org.eclipse.mylar.xplanner.wsdl.db.QueryException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public void removeTask(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "removeTask")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.TimeEntryData getTimeEntry(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getTimeEntry")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.TimeEntryData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.TimeEntryData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.TimeEntryData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.TimeEntryData addTimeEntry(org.xplanner.soap.TimeEntryData timeEntry) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "addTimeEntry")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {timeEntry});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.TimeEntryData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.TimeEntryData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.TimeEntryData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void removeTimeEntry(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "removeTimeEntry")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.NoteData addNote(org.xplanner.soap.NoteData note) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "addNote")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {note});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.NoteData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.NoteData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.NoteData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.NoteData[] getNotesForObject(int attachedToId) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getNotesForObject")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(attachedToId)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.NoteData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.NoteData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.NoteData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.xplanner.soap.PersonData addPerson(org.xplanner.soap.PersonData object) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[39]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "addPerson")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {object});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.xplanner.soap.PersonData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.xplanner.soap.PersonData) org.apache.axis.utils.JavaUtils.convert(_resp, org.xplanner.soap.PersonData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void removePerson(int id) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[40]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "removePerson")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(id)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void deleteAttribute(int objectId, java.lang.String key) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[41]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "deleteAttribute")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(objectId), key});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.util.HashMap getAttributesWithPrefix(int objectId, java.lang.String prefix) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[42]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI(""); //$NON-NLS-1$
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://soap.xplanner.technoetic.com", "getAttributesWithPrefix")); //$NON-NLS-1$ //$NON-NLS-2$

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(objectId), prefix});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.util.HashMap) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.util.HashMap) org.apache.axis.utils.JavaUtils.convert(_resp, java.util.HashMap.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
