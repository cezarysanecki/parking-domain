package pl.cezarysanecki.parkingdomain.commons;

public class EntityNotFound extends RuntimeException {

  public EntityNotFound(String msg) {
    super(msg);
  }
}
