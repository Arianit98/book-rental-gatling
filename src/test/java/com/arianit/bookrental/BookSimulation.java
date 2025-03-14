package com.arianit.bookrental;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.header;
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
                    http("Create book")
                            .post("/api/v1/books")
                            .body(RawFileBody("create_book.json"))
                            .check(header("Location").saveAs("locationHeader")),
                    pause(5)
            )
            .exec(session -> {
                String location = session.getString("locationHeader");
                assert location != null;
                String bookId = location.substring(location.lastIndexOf('/') + 1);
                return session.set("bookId", Integer.parseInt(bookId));
            })
            .exec(
                    http("Get book by id")
                            .get("/api/v1/books/#{bookId}"),
                    pause(5),
                    http("Update book by id")
                            .put("/api/v1/books")
                            .body(StringBody(session -> {
                                int bookId = session.getInt("bookId");
                                return String.format("{\"id\": %d, \"title\": \"Libri Titull\", \"author\": \"test author\", \"year\": 2011, \"stockNr\": 10, \"reservedNr\": 1}", bookId);
                            })),
                    pause(8),
                    http("Delete book")
                            .delete("/api/v1/books/#{bookId}")
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
