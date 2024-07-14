package genericsoap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
// !DO NOT EDIT THIS FILE!
// This source file is generated by Oracle tools
// Contents may be subject to change
// For reporting problems, use the following
// Version = Oracle WebServices (11.1.1.0.0, build 130224.1947.04102)

@WebService(wsdlLocation="https://egwo-dev1.fa.em2.oraclecloud.com/idcws/GenericSoapPort?wsdl",
  targetNamespace="urn:GenericSoap", name="GenericSoapPortType")
@XmlSeeAlso(
  { com.oracle.ucm.ObjectFactory.class })
@SOAPBinding(style=Style.DOCUMENT, parameterStyle=ParameterStyle.BARE)
public interface GenericSoapPortType
{
  @WebMethod(operationName="GenericSoapOperation", action="urn:GenericSoap/GenericSoapOperation")
  @SOAPBinding(parameterStyle=ParameterStyle.BARE)
  @Action(input="urn:GenericSoap/GenericSoapOperation", output="urn:GenericSoap/GenericSoapPortType/GenericSoapOperationResponse")
  @WebResult(targetNamespace="http://www.oracle.com/UCM", partName="GenericResponse",
    name="GenericResponse")
  public com.oracle.ucm.Generic genericSoapOperation(@WebParam(targetNamespace="http://www.oracle.com/UCM",
      partName="GenericRequest", name="GenericRequest")
    com.oracle.ucm.Generic GenericRequest);
}