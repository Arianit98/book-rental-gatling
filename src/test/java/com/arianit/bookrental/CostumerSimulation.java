package com.arianit.bookrental;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CostumerSimulation extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:8080")
    .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("application/json, text/plain")
          .header("cache-control", "no-cache")
          .header("content-type", "application/json")
    .acceptEncodingHeader("gzip, deflate, br");


  private ScenarioBuilder scn = scenario("CostumerSimulation")
    .exec(
      http("Get all costumers:GET_http://localhost:8080/api/v1/costumers")
        .get("/api/v1/costumers"),
      pause(5),
      http("Get costumer with id 2:GET_http://localhost:8080/api/v1/costumers/2")
        .get("/api/v1/costumers/2"),
      pause(5),
      http("Update costumer with id 3:PUT_http://localhost:8080/api/v1/costumers/3")
        .put("/api/v1/costumers/3")
        .body(RawFileBody("0002_request.json")),
      pause(8),
      http("Create costumer:POST_http://localhost:8080/api/v1/costumers")
        .post("/api/v1/costumers")
        .body(RawFileBody("0003_request.json")),
      pause(5),
      http("Delete costumer:DELETE_http://localhost:8080/api/v1/costumers/1")
        .delete("/api/v1/costumers/1")
    );

  {
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
