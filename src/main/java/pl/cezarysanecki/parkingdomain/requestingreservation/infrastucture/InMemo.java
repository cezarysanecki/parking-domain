package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;

import java.util.Map;

class InMemo {

    private static final Map<ReservationRequestsTimeSlotId, Entity> DATABASE;
    private static final Map<ReservationRequestsTemplateId, Entity> DATABASE;
    private static final Map<ReservationRequesterId, Entity> DATABASE;

}
