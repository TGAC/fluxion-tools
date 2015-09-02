package net.sourceforge.fluxion.ajax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Exports beans designated as {@link net.sourceforge.fluxion.ajax.Ajaxified}
 * and configured in the relevant config file (/fluxion/ajax.xml).  Ajaxifiable
 * beans will be loaded, discovered and made available to any spring webmvc
 * framework that uses an {@link AjaxExporter}.
 *
 * @author Rob Davey
 * @author Tony Burdett
 */
@Service("ajaxExporter")
public class AjaxExporter implements ApplicationContextAware, InitializingBean {
  private ApplicationContext applicationContext;
  private Map<String, Object> ajaxBeans = new HashMap<String, Object>();

  private Log log = LogFactory.getLog(this.getClass());

  public void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }

  public void afterPropertiesSet() throws Exception {
    autodetectAjaxEnabledBeans();
  }

  public Map<String, Object> getAjaxBeans() {
    return ajaxBeans;
  }

  protected void autodetectAjaxEnabledBeans() {
    log.info("Exporting Ajaxified beans...");
    try {
      ClassPathXmlApplicationContext childContext =
          new ClassPathXmlApplicationContext(
              new String[]{"classpath*:/fluxion/ajax.xml"},
              applicationContext);

      // get the bean factory
      ListableBeanFactory factory = childContext.getBeanFactory();

      // now, use this factory to discover ajaxified beans
      String[] beanNames = factory.getBeanNamesForType(null);
      for (String beanName : beanNames) {
        Class beanClass = factory.getType(beanName);
        if (beanClass.getAnnotation(Ajaxified.class) != null) {
          log.debug("Found @Ajaxified annotation on " + beanClass.getName());
          ajaxBeans.put(beanName, factory.getBean(beanName));
        }
      }
    }
    catch (RuntimeException e) {
      log.error("Unable to load beans from ajax.xml [" + e.getCause() + "]");
      e.printStackTrace();
      throw e;
    }
    finally {
      log.info("... Ajaxified bean export complete");
    }
  }
}
