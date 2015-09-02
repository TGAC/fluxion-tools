package net.sourceforge.fluxion.ajax;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that designates a spring-enabled bean as a bean that can be
 * used to populate content that can be requested via a browser ajax request.
 * <p/>
 * This is the specification for {@link net.sourceforge.fluxion.ajax.Ajaxified}
 * objects.  The object tagged as ajaxified should be configured in a spring
 * configuration file, ajax.xml, located on the classpath under
 * fluxion/ajax.xml.  Any beans declared in this file will be loaded by an
 * {@link AjaxExporter} at runtime, and any ajaxified beans will be available to
 * redirect ajax requests to.
 * <p/>
 * Ajaxifiable beans can act as normal, spring managed beans.  Clients can
 * request ajax functionality by posting a request specifying a servicename and
 * an action parameter.  The servivename binds to the name of the bean, and the
 * action binds to the method name.  Note that any methods that are intended to
 * be used as actions have specified parameters: the first param should be a
 * {@link javax.servlet.http.HttpSession}, and the second a {@link
 * net.sf.json.JSONObject}
 *
 * @author Rob Davey
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ajaxified {
}