package pl.cezarysanecki.parkingdomain.management.client

import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import spock.lang.Specification
import spock.lang.Subject

class RegisteringClientTest extends Specification {
  
  CatalogueClientDatabase database = Mock()
  EventPublisher eventPublisher = Mock()
  
  @Subject
  RegisteringClient registeringClient = new RegisteringClient(database, eventPublisher)
  
  def "allow to register client"() {
    when:
      def result = registeringClient.registerClient(ClientType.INDIVIDUAL, "123123123")
    
    then:
      result.isSuccess()
    and:
      1 * database.saveNew(_ as Client)
  }
  
  def "fail to register client when storing in database"() {
    given:
      database.saveNew(_ as Client) >> {
        throw new IllegalStateException()
      }
    
    when:
      def result = registeringClient.registerClient(ClientType.BUSINESS, "123123123")
    
    then:
      result.isFailure()
  }
  
}
