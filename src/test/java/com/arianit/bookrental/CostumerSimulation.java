package com.arianit.bookrental;

import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CostumerSimulation extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:8080")
    .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("PostmanRuntime/7.43.0");
  
  private Map<CharSequence, String> headers_0 = Map.ofEntries(
    Map.entry("Cache-Control", "no-cache"),
    Map.entry("Postman-Token", "37376bec-c3db-45a2-856e-0adc86cc64bc")
  );
  
  private Map<CharSequence, String> headers_1 = Map.ofEntries(
    Map.entry("Cache-Control", "no-cache"),
    Map.entry("Postman-Token", "fa775475-3e06-46bd-9b13-cb80e74492b7")
  );
  
  private Map<CharSequence, String> headers_2 = Map.ofEntries(
    Map.entry("Cache-Control", "no-cache"),
    Map.entry("Content-Type", "application/json"),
    Map.entry("Postman-Token", "129f70c3-e207-48e4-a3e0-12ccd608c3cb")
  );
  
  private Map<CharSequence, String> headers_3 = Map.ofEntries(
    Map.entry("Cache-Control", "no-cache"),
    Map.entry("Content-Type", "application/json"),
    Map.entry("Postman-Token", "9e644307-31f4-4b79-baa7-55069fa5d511")
  );
  
  private Map<CharSequence, String> headers_4 = Map.ofEntries(
    Map.entry("Cache-Control", "no-cache"),
    Map.entry("Postman-Token", "416dcf7c-9f40-4853-b2f4-18c2c0b620c6")
  );


  private ScenarioBuilder scn = scenario("CostumerSimulation")
    .exec(
      http("Get all costumers:GET_http://localhost:8080/api/v1/costumers")
        .get("/api/v1/costumers")
        .headers(headers_0),
      pause(5),
      http("Get costumer with id 2:GET_http://localhost:8080/api/v1/costumers/2")
        .get("/api/v1/costumers/2")
        .headers(headers_1),
      pause(5),
      http("Update costumer with id 3:PUT_http://localhost:8080/api/v1/costumers/3")
        .put("/api/v1/costumers/3")
        .headers(headers_2)
        .body(RawFileBody("0002_request.json")),
      pause(8),
      http("Create costumer:POST_http://localhost:8080/api/v1/costumers")
        .post("/api/v1/costumers")
        .headers(headers_3)
        .body(RawFileBody("0003_request.json")),
      pause(5),
      http("Delete costumer:DELETE_http://localhost:8080/api/v1/costumers/1")
        .delete("/api/v1/costumers/1")
        .headers(headers_4)
    );

  {
//	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
    setUp(
            scn.injectOpen(
                    nothingFor(4), // 1
                    atOnceUsers(10), // 2
                    rampUsers(10).during(5), // 3
                    constantUsersPerSec(20).during(15), // 4
                    constantUsersPerSec(20).during(15).randomized(), // 5
                    rampUsersPerSec(10).to(20).during(10), // 6
                    rampUsersPerSec(10).to(20).during(10).randomized(), // 7
                    stressPeakUsers(1000).during(20) // 8
            ).protocols(httpProtocol)
    );
  }
}
