package net.sourceforge.fluxion.ajax.controller;

import net.sourceforge.fluxion.ajax.AjaxExporter;
import net.sourceforge.fluxion.ajax.handler.AjaxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Spring controller that can handle ajax requests (handled by an {@link AjaxHandler}),
 * pushing content into a {@link net.sf.json.JSONObject} in the response.
 *
 * @author Rob Davey
 * @author Tony Burdett 
 */
@Controller
public class AjaxController {
  protected static final Logger log = LoggerFactory.getLogger(AjaxController.class);
  
  private static String responseContentType = "application/json";

  @Autowired
  private AjaxExporter ajaxExporter;

  @Autowired
  private AjaxHandler ajaxHandler;

  private HttpSession session;
  private String invalidSessionUrl = "/session-timeout";
  
  public String getInvalidSessionUrl() {
    return invalidSessionUrl;
  }

  public void setInvalidSessionUrl(String invalidSessionUrl) {
    this.invalidSessionUrl = invalidSessionUrl;
  }
  
  public void setAjaxExporter(AjaxExporter ajaxExporter) {
    this.ajaxExporter = ajaxExporter;
  }

  public AjaxExporter getAjaxExporter() {
    return ajaxExporter;
  }

  public void setAjaxHandler(AjaxHandler ajaxHandler) {
    this.ajaxHandler = ajaxHandler;
  }

  public AjaxHandler getAjaxHandler() {
    return ajaxHandler;
  }

  public HttpSession getHttpSession() {
    return session;
  }

  public void setHttpSession(HttpSession session) {
    this.session = session;
  }

  @RequestMapping("/fluxion.ajax")
  protected void handleRequestInternal(
      HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String ajaxName = request.getParameter("servicename");
    Object ajax = ajaxExporter.getAjaxBeans().get(ajaxName);
    ajaxHandler.setContentType(responseContentType);
/*    if (request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()) {
      //invalid or expired session, redirect to defined invalid session url
      System.out.println("Session expired! Redirecting to login page...");
      return new ModelAndView(new RedirectView(invalidSessionUrl));
    }
    else {
      ajaxHandler.handle(ajax, request, response);
      return null;
    } */
    log.debug("Forwarding request to "+ ajaxName +" using Fluxion AJAX");
    ajaxHandler.handle(ajax, request, response);
  }
}