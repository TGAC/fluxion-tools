package net.sourceforge.fluxion.spi;

import java.lang.annotation.*;

/**
 * Annotate a class as being a provider of an SPI. This may be used to create
 * the services manifest entry.
 *
 * @author Tony Burdett
 * @author Matthew Pocock
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ServiceProvider {
}
