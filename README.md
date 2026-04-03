# BookShelf Manager

REST API for a small library or team bookshelf: track **books**, **members**, and **loans** with **H2** persistence, **validation**, **search/filter**, **overdue reporting**, and a simple **API key** gate for write operations.

## Features

- **Layered architecture**: REST controllers → services → JPA repositories → entities (clean separation for interviews and maintenance).
- **Catalog management**: add books with ISBN uniqueness and copy counts; list and fetch by id.
- **Search and filters**: query by title/author substring; optional `borrowableOnly` to list titles that still have free copies.
- **Loans**: checkout (reduces `availableCopies`), return (restores a copy), list active loans, list overdue active loans (due date before today).
- **Members**: register with unique email (case-insensitive).
- **Validation**: Jakarta Bean Validation on JSON request bodies (HTTP 400 with field details on failure).
- **Error handling**: consistent JSON errors for not-found (404) and business rules (400).
- **Simulated authentication**: mutating requests under `/api/*` require header `X-API-Key` matching `bookshelf.security.api-key` (GET stays open for easy browsing).

## Tech stack

- Java **21** (LTS)
- **Spring Boot 3.4** (Web, Data JPA, Validation)
- **H2** in-memory database (optional console at `/h2-console`)
- **Maven**

## How to run

Prerequisites: **JDK 21** and **Maven** on your PATH.

```bash
cd bookshelf-manager
mvn spring-boot:run
```

Application listens on **http://localhost:8080**.

To disable API key checks locally, set in `application.properties`:

```properties
bookshelf.security.enabled=false
```

Default key when enabled: `demo-secret` (see `application.properties`).

## Example usage (curl)

Replace `X-API-Key` if you changed `bookshelf.security.api-key`.

**List all books**

```bash
curl -s http://localhost:8080/api/books
```

**Books you can borrow right now**

```bash
curl -s "http://localhost:8080/api/books?borrowableOnly=true"
```

**Search**

```bash
curl -s "http://localhost:8080/api/books?q=clean"
```

**Active loans and overdue**

```bash
curl -s http://localhost:8080/api/loans/active
curl -s http://localhost:8080/api/loans/overdue
```

**Register a member** (requires API key)

```bash
curl -s -X POST http://localhost:8080/api/members ^
  -H "Content-Type: application/json" ^
  -H "X-API-Key: demo-secret" ^
  -d "{\"name\":\"Sam Patel\",\"email\":\"sam.patel@example.com\"}"
```

**Add a book**

```bash
curl -s -X POST http://localhost:8080/api/books ^
  -H "Content-Type: application/json" ^
  -H "X-API-Key: demo-secret" ^
  -d "{\"title\":\"Refactoring\",\"author\":\"Martin Fowler\",\"isbn\":\"978-0201485677\",\"totalCopies\":1}"
```

**Checkout** (use ids from your `/api/books` and `/api/members` responses)

```bash
curl -s -X POST http://localhost:8080/api/loans/checkout ^
  -H "Content-Type: application/json" ^
  -H "X-API-Key: demo-secret" ^
  -d "{\"bookId\":1,\"memberId\":1,\"loanDays\":14}"
```

**Return**

```bash
curl -s -X POST http://localhost:8080/api/loans/1/return ^
  -H "X-API-Key: demo-secret"
```

On Windows PowerShell, use single quotes for JSON or escape quotes differently; the README uses `^` line continuation for **cmd.exe**.

PowerShell example:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/members `
  -Headers @{ "X-API-Key" = "demo-secret" } `
  -ContentType "application/json" `
  -Body '{"name":"Sam Patel","email":"sam.patel@example.com"}'
```

## Sample data

On startup, `data.sql` loads:

- Three books (one fully on loan: *Design Patterns* has `availableCopies = 0`).
- Two members.
- One active loan (overdue relative to **today** if your system date is after 2026-03-15), useful for trying `/api/loans/overdue`.

## H2 console (optional)

1. Open http://localhost:8080/h2-console  
2. JDBC URL: `jdbc:h2:mem:bookshelf`  
3. User: `sa`, password: *(empty)*

## Project layout (high level)

- `controller` — HTTP mapping and input DTOs
- `service` — rules (inventory, overdue, uniqueness)
- `repository` — database queries
- `model` — JPA entities
- `dto` — request/response records for stable JSON
- `exception` — global error mapping
- `config` — clock bean, API key filter, security properties

## License

Demo / educational use.
