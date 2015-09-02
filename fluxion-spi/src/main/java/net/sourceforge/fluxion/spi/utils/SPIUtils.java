package net.sourceforge.fluxion.spi.utils;

import net.sourceforge.fluxion.spi.exception.ServiceConfigurationError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Utils classes for accessing methods related to this implementation of the Sun
 * SPI framework.  This class provides static methods for service discovery and
 * instantiation.
 * <p/>
 * For example:
 * <p/>
 * <pre><code>
 * // loop over all providers
 * for(MyService ms: SPIUtils.getServiceProviders(MyService.class))
 * {
 *   ...
 * }
 * </code></pre>
 * <p/>
 *
 * @author Tony Burdett
 * @author Matthew Pocock
 */
public class SPIUtils {
  /**
   * Gets the set of all providers in the current classpath for the supplied
   * SPI. Any providers found will be loaded and instantiated when this method
   * is invoked, so a set of ready-created instances will be returned.
   *
   * @param spi the SPI to discover providers for
   * @return an {@link Set} of all providers
   * @throws ServiceConfigurationError if any of the providers could not be
   *                                   loaded
   */
  public static <SPI> Set<SPI> getServiceProviders(Class<SPI> spi)
      throws ServiceConfigurationError {
    return getServiceProviders(Thread.currentThread().getContextClassLoader(),
                               spi);
  }

  /**
   * Get a Set of all providers present in the specified class loader for the
   * given SPI. Any providers found will be loaded when this method is invoked.
   *
   * @param loader the {@link ClassLoader} to scan for providers
   * @param spi    The SPI to locate providers for
   * @return an {@link Set} over all providers
   * @throws ServiceConfigurationError if any of the providers could not be
   *                                   loaded
   */
  public static <SPI> Set<SPI> getServiceProviders(
      ClassLoader loader, Class<SPI> spi)
      throws ServiceConfigurationError {
    Set<SPI> spis = new HashSet<SPI>();

    // read implementations from the services file
    String resourceName = "META-INF/services/" + spi.getName();
    try {
      Enumeration resources = loader.getResources(resourceName);
      while (resources.hasMoreElements()) {
        // load each resource
        URL res = (URL) resources.nextElement();
        // read lines from each resource, and use to load getServiceProviders
        try {
          BufferedReader br =
              new BufferedReader(new InputStreamReader(res.openStream()));
          try {
            String cls;
            while ((cls = br.readLine()) != null) {
              cls = cls.trim();
              // ignore comments and blank lines
              if (cls.length() > 0 && !cls.startsWith("#")) {
                try {
                  Class<?> provider = loader.loadClass(cls);
                  Class<? extends SPI> pspi = provider.asSubclass(spi);
                  SPI nextSpi = pspi.newInstance();
                  spis.add(nextSpi);
                }
                catch (ClassNotFoundException e) {
                  throw new ServiceConfigurationError(
                      "Unable to find service provider class: " + cls,
                      e);
                }
                catch (InstantiationException e) {
                  throw new ServiceConfigurationError(
                      "Unable to instantiate service provider class: " + cls,
                      e);
                }
                catch (IllegalAccessException e) {
                  throw new ServiceConfigurationError(
                      "Unable to instantiate service provider class: " + cls,
                      e);
                }
                catch (ClassCastException e) {
                  throw new ServiceConfigurationError(
                      "SPIUtils provider class: " + cls +
                          " appears not to be castable to " + spi,
                      e);
                }
              }
            }
          }
          catch (IOException e) {
            throw new ServiceConfigurationError(
                "Unable to open resource: " + resourceName + " for reading");
          }
        }
        catch (IOException e) {
          throw new ServiceConfigurationError(
              "A problem occurred whilst reading from resource: " +
                  resourceName);
        }
      }
    }
    catch (IOException e) {
      throw new ServiceConfigurationError(
          "Unable to load resource: " + resourceName);
    }

    return spis;
  }

  /**
   * Get a {@link Set} of all the classes that are providers for this Spi using
   * the current {@link ClassLoader}.  These classes will be returned directly,
   * the caller should take responsibility for instantianting them.
   *
   * @param spi the SPI class to locate
   * @return a {@link Set} of the Spi classes
   * @throws ServiceConfigurationError if the classes listed as SPIs could not
   *                                   be loaded
   */
  public static <SPI> Set<Class<? extends SPI>> getServiceProviderClasses(
      Class<SPI> spi)
      throws ServiceConfigurationError {
    return getServiceProviderClasses(
        Thread.currentThread().getContextClassLoader(), spi);
  }

  /**
   * Get a {@link Set} of all the classes that are providers for this Spi using
   * the supplied {@link ClassLoader}. These classes will be returned directly,
   * the caller should take responsibility for instantianting them.
   *
   * @param loader the {@link ClassLoader} to scan for providers
   * @param spi    the SPI to locate providers for
   * @return an {@link Iterable} over the Spi classes
   * @throws ServiceConfigurationError if the classes listed as SPIs could not
   *                                   be loaded
   */
  public static <SPI> Set<Class<? extends SPI>> getServiceProviderClasses(
      ClassLoader loader,
      Class<SPI> spi)
      throws ServiceConfigurationError {
    try {
      Set<Class<? extends SPI>> spis = new HashSet<Class<? extends SPI>>();

      for (Enumeration e = loader.getResources("META-INF/services/" +
          spi.getName());
           e.hasMoreElements();) {
        URL res = (URL) e.nextElement();
        BufferedReader br =
            new BufferedReader(new InputStreamReader(res.openStream()));
        String cls;
        while ((cls = br.readLine()) != null) {
          cls = cls.trim();
          if (cls.length() == 0 || cls.startsWith("#")) {
            // ignore comments and blank lines
          }
          else {
            Class<?> provider = loader.loadClass(cls);
            if (spi.isAssignableFrom(provider)) {
              spis.add(provider.asSubclass(spi));
            }
            else {
              throw new ServiceConfigurationError(
                  "Can't assign " + provider + " to " + spi);
            }
          }
        }
      }

      return spis;
    }
    catch (IOException e) {
      throw new ServiceConfigurationError(e);
    }
    catch (ClassNotFoundException e) {
      throw new ServiceConfigurationError(e);
    }
    catch (ClassCastException e) {
      throw new ServiceConfigurationError(e);
    }
  }
}
