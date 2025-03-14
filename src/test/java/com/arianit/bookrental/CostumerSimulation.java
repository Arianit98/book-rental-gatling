package com.arianit.bookrental;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;

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
                    http("Get all costumers")
                            .get("/api/v1/costumers"),
                    pause(5),
                    http("Create costumer")
                            .post("/api/v1/costumers")
                            .body(RawFileBody("create_costumer.json"))
                            .check(header("Location").saveAs("locationHeader")),
                    pause(5))
            .exec(session -> {
                String location = session.getString("locationHeader");
                assert location != null;
                String costumerId = location.substring(location.lastIndexOf('/') + 1);
                return session.set("costumerId", Integer.parseInt(costumerId));
            })
            .exec(
                    http("Get costumer by id")
                            .get("/api/v1/costumers/#{costumerId}"),
                    pause(5),
                    http("Update costumer")
                            .put("/api/v1/costumers")
                            .body(StringBody(session -> {
                                int costumerId = session.getInt("costumerId");
                                return String.format("{\"id\": %d, \"name\": \"Arianit\", \"email\": \"arianit@gmail.com\", \"phone\": \"049197833\", \"address\": \"my address\", \"age\": 20}", costumerId);
                            })),
                    pause(8),
                    http("Delete costumer")
                            .delete("/api/v1/costumers/#{costumerId}")
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
