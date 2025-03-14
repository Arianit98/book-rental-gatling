package com.arianit.bookrental;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;

public class ReservationSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8082")
            .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
            .acceptHeader("*/*")
            .header("Cache-Control", "no-cache")
            .header("Content-Type", "application/json")
            .acceptEncodingHeader("gzip, deflate, br");

    private String uri1 = "localhost";

    private ScenarioBuilder scn = scenario("ReservationSimulation")
            .exec(
                    http("Get all books")
                            .get("http://" + uri1 + ":8081/api/v1/books"),
                    pause(3),
                    http("Get all costumers")
                            .get("http://" + uri1 + ":8080/api/v1/costumers"),
                    pause(6),
                    http("Create new costumer")
                            .post("http://" + uri1 + ":8080/api/v1/costumers")
                            .body(RawFileBody("create_costumer.json"))
                            .check(header("Location").saveAs("locationHeaderCostumer")),
                    pause(4),
                    http("Create new book")
                            .post("http://" + uri1 + ":8081/api/v1/books")
                            .body(RawFileBody("create_book.json"))
                            .check(header("Location").saveAs("locationHeaderBook")),
                    pause(4)
            )
            .exec(session -> {
                String locationCostumer = session.getString("locationHeaderCostumer");
                assert locationCostumer != null;
                String costumerId = locationCostumer.substring(locationCostumer.lastIndexOf('/') + 1);
                String locationBook = session.getString("locationHeaderBook");
                assert locationBook != null;
                String bookId = locationBook.substring(locationBook.lastIndexOf('/') + 1);
                return session.set("costumerId", Integer.parseInt(costumerId)).set("bookId", Integer.parseInt(bookId));
            })
            .exec(
                    http("Get all reservations")
                            .get("/api/v1/reservations"),
                    pause(4),
                    http("Create reservation")
                            .post("/api/v1/reservations")
                            .body(StringBody(session -> {
                                int costumerId = session.getInt("costumerId");
                                int bookId = session.getInt("bookId");
                                return String.format("{\"costumerId\": %d, \"bookId\": %d, \"createdDate\": \"10.02.2024\", \"durationInDays\": 4}", costumerId, bookId);
                            })).check(header("Location").saveAs("locationHeaderReservation")),
                    pause(17))
            .exec(session -> {
                String locationReservation = session.getString("locationHeaderReservation");
                assert locationReservation != null;
                String reservationId = locationReservation.substring(locationReservation.lastIndexOf('/') + 1);
                return session.set("reservationId", Integer.parseInt(reservationId));
            })
            .exec(
                    http("Update reservation")
                            .put("/api/v1/reservations")
                            .body(StringBody(session -> {
                                int reservationId = session.getInt("reservationId");
                                int costumerId = session.getInt("costumerId");
                                int bookId = session.getInt("bookId");
                                return String.format("{\"id\": %d,\"costumerId\": %d, \"bookId\": %d, \"createdDate\": \"10.02.2024\", \"durationInDays\": 4}", reservationId, costumerId, bookId);
                            })),
                    pause(8),
                    http("Get reservation")
                            .get("/api/v1/reservations/#{reservationId}"),
                    pause(6),
                    http("Delete reservation")
                            .delete("/api/v1/reservations/#{reservationId}")
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
