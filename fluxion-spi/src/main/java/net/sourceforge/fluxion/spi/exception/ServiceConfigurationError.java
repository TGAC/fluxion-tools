package net.sourceforge.fluxion.spi.exception;

/**
 * An error that gets thrown whenever a service discovery hits a problem; this
 * may be due to classloading problems, class-cast problems or other related
 * difficulties.
 *
 * @author Tony Burdett
 * @author Matthew Pocock
 */
public class ServiceConfigurationError extends Error {

  public ServiceConfigurationError() {
    super();
  }

  public ServiceConfigurationError(String message) {
    super(message);
  }

  public ServiceConfigurationError(Throwable cause) {
    super(cause);
  }

  public ServiceConfigurationError(String message, Throwable cause) {
    super(message, cause);
  }

}
