# Book Rental Gatling

A performance testing project for a Book Rental API using [Gatling](https://gatling.io/). This project simulates various
user interactions with the API, including books and costumers (customers): creating, updating, deleting, and retrieving
books, reservations, and costumer data.

## Features

- Simulates book, reservation, and costumer operations
- Generates detailed HTML reports for each simulation run
- Configurable scenarios and test data

## Project Structure

```
pom.xml                # Maven build file
README.md              # Project documentation
src/
  test/
    java/              # Gatling simulation scripts (Scala/Java)
      com/             # Book, reservation, and costumer simulation classes
    resources/         # Test data and configuration files
      create_book.json
      create_costumer.json
      gatling.conf
      logback-test.xml
target/
  gatling/             # Output reports and logs from simulation runs
    booksimulation-*/  # Book simulation reports
    reservationsimulation-*/ # Reservation simulation reports
    costumersimulation-*/    # Costumer simulation reports (if present)
```

## Prerequisites

- Java 8 or higher
- Maven

## Setup & Usage

1. **Clone the repository:**
   ```sh
   git clone <repo-url>
   cd book-rental-gatling
   ```
2. **Run all Gatling simulations:**
   ```sh
    mvn gatling:test
   ```
   This will execute all available simulations (books, reservations, costumers).

3. **View reports:**
   Open the generated HTML report in `target/gatling/<simulation-folder>/index.html`.
    - Book simulation: `target/gatling/booksimulation-*/index.html`
    - Reservation simulation: `target/gatling/reservationsimulation-*/index.html`
    - Costumer simulation: `target/gatling/costumersimulation-*/index.html` (if present)

## Simulation Details

- **Book Simulation:** Tests API endpoints for creating, updating, deleting, and retrieving books.
- **Reservation Simulation:** Tests reservation creation, update, and retrieval.
- **Costumer Simulation:** Tests costumer (customer) creation and management endpoints.

## Customization

- Edit simulation scripts in `src/test/java/com/`
- Modify test data in `src/test/resources/`
- Update configuration in `gatling.conf`

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

MIT

## Contact

For questions or support, please contact the repository owner.
