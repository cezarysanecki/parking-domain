package pl.cezarysanecki.parkingdomain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
    exclude = {
        DataSourceAutoConfiguration.class
    }
)
public class ParkingDomainApplication {

  public static void main(
      String[] args
  ) {
    SpringApplication.run(ParkingDomainApplication.class, args);
  }

}
