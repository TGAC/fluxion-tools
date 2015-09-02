package net.sourceforge.fluxion.spi;

import java.lang.annotation.*;

/**
 * An annotation that marks an interface as being an Service Provider Interface
 * (SPI).
 *
 * @author Tony Burdett
 * @author Matthew Pocock
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Spi {
}
