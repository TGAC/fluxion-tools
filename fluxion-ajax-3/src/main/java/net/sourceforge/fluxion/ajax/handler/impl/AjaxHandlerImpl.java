package net.sourceforge.fluxion.ajax.handler.impl;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.handler.AjaxHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc net.sourceforge.fluxion.ajax.handler.AjaxHandler}
 */
@Service("ajaxHandler")
public class AjaxHandlerImpl implements AjaxHandler {
  private boolean encodeResponse = true;
  private String encodingCharset = "UTF-8";
  private String contentType = "application/json";
  private Log log = LogFactory.getLog(this.getClass());
  
  public boolean isEncodeResponse() {
    return encodeResponse;
  }

  public void setEncodeResponse(boolean encodeResponse) {
    this.encodeResponse = encodeResponse;
  }

  public void setEncodingCharset(String encodingCharset) {
    this.encodingCharset = encodingCharset;
  }

  public String getEncodingCharset() {
    return this.encodingCharset;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getContentType() {
    return this.contentType;
  }

  public void handle(
    Object ajaxedObject, HttpServletRequest request,
    HttpServletResponse response)
    throws InvocationTargetException, IllegalAccessException, IOException,
    NoSuchMethodException {

    //session OK, handle as normal
    String operation = "makeResponse";
    if (request.getParameter("action") != null) {
      operation = request.getParameter("action");
    }

    ArrayList<Object> args = determineArguments(request);
    Object result = null;
    JSONObject jsonObject = null;

    if (ajaxedObject != null) {
      try {
        Method method =
            ajaxedObject.getClass()
                .getMethod(operation, toClassArray(args.toArray()));
        args.add(0, request.getSession());
        log.debug("Invoking " + method.toGenericString() + " with params " + args.toString());
        //System.out.println("Invoking " + method.toGenericString() + " with params " + args.toString());
        result = method.invoke(ajaxedObject, args.toArray());
      }
      catch (Exception e) {
        String errorMsg = "Error invoking " + operation + " on " + ajaxedObject.getClass().getSimpleName() + ": " + e.getCause();
        log.error(errorMsg);
        e.printStackTrace();
        if (encodeResponse) {
          jsonObject = JSONObject.fromObject("{'error':'" + URLEncoder.encode(errorMsg, encodingCharset) + "'}");
        }
        else {
          jsonObject = JSONObject.fromObject("{'error':'" + errorMsg + "'}");
        }
        response.setContentType(contentType);
        jsonObject.write(response.getWriter());
      }

      if (result != null) {
        if (result instanceof JSONObject) {
          jsonObject = (JSONObject) result;
        }
        else {
          jsonObject = JSONObject.fromObject(result);
        }
        response.setContentType(contentType);
        jsonObject.write(response.getWriter());
      }
    }

  }

  private Class[] toClassArray(Object[] args) {

    Class[] clsa = new Class[args.length + 1];
    for (int i = 0; i < args.length + 1; i++) {
      if (i == 0) {
        clsa[i] = HttpSession.class;
      }
      else {
        clsa[i] = args[i - 1].getClass();
      }
    }
    return clsa;
  }

  protected ArrayList<Object> determineArguments(HttpServletRequest request)
      throws
      JSONException {

    ArrayList<Object> args = new ArrayList<Object>();
    String arg = request.getParameter("params");
    if (arg != null) {
      if (arg.startsWith("{")) {
        args.add(JSONObject.fromObject(arg));
      }
      else {
        args.add(arg);
      }
    }
    else {
      args.add(new JSONObject());
    }
    return args;
  }
}
