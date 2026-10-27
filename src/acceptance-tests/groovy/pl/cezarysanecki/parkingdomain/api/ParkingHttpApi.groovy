package pl.cezarysanecki.parkingdomain.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

import java.time.LocalDateTime

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

/**
 * Sends HTTP requests to the application. Request bodies are plain objects serialized with the
 * application's ObjectMapper, so a test starts from a valid body and changes only what it needs
 * (null fields are not sent at all).
 */
class ParkingHttpApi {

  private final MockMvc mockMvc
  private final ObjectMapper objectMapper

  ParkingHttpApi(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc
    this.objectMapper = objectMapper
  }

  Response occupy(OccupyParkingSpotBody body) {
    return postJson("/parking/occupy", body)
  }

  Response occupyWithoutAccount(OccupyParkingSpotWithoutAccountBody body) {
    return postJson("/parking/occupy-without-account", body)
  }

  Response makeRequest(MakeRequestBody body) {
    return postJson("/requesting/request", body)
  }

  private Response postJson(String url, Object body) {
    def response = mockMvc.perform(post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(body)))
        .andReturn().response
    return new Response(
        status: response.status,
        contentType: response.contentType,
        problem: response.contentType == MediaType.APPLICATION_PROBLEM_JSON_VALUE
            ? objectMapper.readValue(response.contentAsString, Map)
            : null)
  }

  static class Response {
    int status
    String contentType
    Map problem // RFC 7807 body, only for application/problem+json
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(["metaClass"])
  static class OccupyParkingSpotBody {
    UUID occupantId
    UUID parkingSpotId
    String vehicleType
    Integer spotUnits // not part of the API anymore, only to simulate an old client
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(["metaClass"])
  static class OccupyParkingSpotWithoutAccountBody {
    String phoneNumber
    UUID parkingSpotId
    String vehicleType
    Integer spotUnits // not part of the API anymore, only to simulate an old client
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(["metaClass"])
  static class MakeRequestBody {
    UUID requesterId
    UUID parkingSpotId
    LocalDateTime from
    LocalDateTime to
    String vehicleType
    Integer spotUnits // not part of the API anymore, only to simulate an old client
  }

}
