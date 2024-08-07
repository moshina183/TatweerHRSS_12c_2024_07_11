package com.oracle.xmlns.oxp.service.v2;

import javax.xml.ws.WebFault;

@WebFault(faultBean="com.oracle.xmlns.oxp.service.v2.OperationFailedException",
  targetNamespace="http://xmlns.oracle.com/oxp/service/v2", name="fault1")
public class OperationFailedException_Exception
  extends Exception
{
  private com.oracle.xmlns.oxp.service.v2.OperationFailedException faultInfo;

  public OperationFailedException_Exception(String message,
                                            com.oracle.xmlns.oxp.service.v2.OperationFailedException faultInfo)
  {
    super(message);
    this.faultInfo = faultInfo;
  }

  public OperationFailedException_Exception(String message,
                                            com.oracle.xmlns.oxp.service.v2.OperationFailedException faultInfo,
                                            Throwable t)
  {
    super(message,t);
    this.faultInfo = faultInfo;
  }

  public com.oracle.xmlns.oxp.service.v2.OperationFailedException getFaultInfo()
  {
    return faultInfo;
  }

  public void setFaultInfo(com.oracle.xmlns.oxp.service.v2.OperationFailedException faultInfo)
  {
    this.faultInfo = faultInfo;
  }
}
// !DO NOT EDIT THIS FILE!
// This source file is generated by Oracle tools
// Contents may be subject to change
// For reporting problems, use the following
// Version = Oracle WebServices (11.1.1.0.0, build 130224.1947.04102)
