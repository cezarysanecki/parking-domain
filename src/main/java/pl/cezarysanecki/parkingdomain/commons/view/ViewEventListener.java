package pl.cezarysanecki.parkingdomain.commons.view;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

@Order(Integer.MIN_VALUE)
@EventListener
public @interface ViewEventListener {
}
