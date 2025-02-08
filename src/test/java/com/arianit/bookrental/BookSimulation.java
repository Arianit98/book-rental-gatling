package com.arianit.bookrental;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class BookSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8081")
            .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
            .acceptHeader("application/json, text/plain")
            .header("cache-control", "no-cache")
            .header("content-type", "application/json")
            .acceptEncodingHeader("gzip, deflate, br");


    private ScenarioBuilder scn = scenario("BookSimulation")
            .exec(
                    http("Get all books")
                            .get("/api/v1/books"),
                    pause(5),
                    http("Get book with id 2")
                            .get("/api/v1/books/2"),
                    pause(5),
                    http("Update book with id 3")
                            .put("/api/v1/books/3")
                            .body(RawFileBody("update_book.json")),
                    pause(8),
                    http("Create book")
                            .post("/api/v1/books")
                            .body(RawFileBody("create_book.json")),
                    pause(5),
                    http("Delete book")
                            .delete("/api/v1/books/1")
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
