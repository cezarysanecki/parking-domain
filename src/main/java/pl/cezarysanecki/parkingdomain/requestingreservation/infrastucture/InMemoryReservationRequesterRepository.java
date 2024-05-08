package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequesterViewRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class InMemoryReservationRequesterRepository implements
        ReservationRequesterRepository,
        ReservationRequesterViewRepository {

    private static final Map<ReservationRequesterId, ReservationRequester> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void save(ReservationRequester reservationRequester) {
        DATABASE.put(reservationRequester.getRequesterId(), reservationRequester);
    }

    @Override
    public Option<ReservationRequester> findBy(ReservationRequesterId requesterId) {
        return Option.of(DATABASE.get(requesterId));
    }

    @Override
    public Option<ReservationRequester> findBy(ReservationRequestId reservationRequestId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(requester -> requester.getReservationRequests().contains(reservationRequestId))
                        .findFirst());
    }

    @Override
    public List<ReservationRequesterView> queryForAllReservationRequesters() {
        return DATABASE.values()
                .stream()
                .map(requester -> new ReservationRequesterView(
                        requester.getRequesterId().getValue(),
                        requester.getReservationRequests().map(ReservationRequestId::getValue).toJavaList()
                ))
                .toList();
    }

}
