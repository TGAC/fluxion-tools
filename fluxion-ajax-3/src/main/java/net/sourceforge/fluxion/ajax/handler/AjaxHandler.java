package net.sourceforge.fluxion.ajax.handler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

/**
 * Handles methods called on beans designated as {@link net.sourceforge.fluxion.ajax.Ajaxified}
 * that have been exported by an {@link net.sourceforge.fluxion.ajax.AjaxExporter}.
 *
 * @author Rob Davey
 * @author Tony Burdett
 */
public interface AjaxHandler {
  public void setContentType(String contentType);
  public String getContentType();
  public void handle(Object o, HttpServletRequest request, HttpServletResponse response)
      throws InvocationTargetException, IllegalAccessException, IOException,
      NoSuchMethodException;
}
