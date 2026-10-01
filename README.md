# AgriTech Recommendation API

Standalone Spring Boot API that recommends open supplier group orders to farmers. It uses the shared AgriTech PostgreSQL data model and does not modify the existing APIs.

## How recommendations work

The first version is deliberately explainable and rule-based:

- Recommend an open group order when its product matches a farmer's recorded expense items.
- Recommend an open group order when other farmers have already joined it.
- Reuse an existing matching recommendation so repeated requests do not create duplicates.

This is not a trained machine-learning model or generative-AI integration.

## Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/recommendations/generate` | Generate recommendations for a farmer |
| `GET` | `/api/recommendations/farmer/{farmerId}` | List a farmer's saved recommendations |
| `GET` | `/api/recommendations/{id}` | Retrieve one recommendation |
| `POST` | `/api/chat` | Send a prompt to the configured Ollama model |
| `GET` | `/actuator/health` | Service health check |

Generate request:

```json
{
  "farmerId": "FARMER-UUID"
}
```

## Run locally

Requirements: Java 17 and PostgreSQL 16.

```bash
./mvnw spring-boot:run
```

Configuration is supplied with environment variables:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/agritech
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your-password
```

For an isolated development environment:

```bash
./mvnw package
docker compose up --build
```

The included schema creates the tables needed by this API. Farmer, expense, supplier-product, and group-order data would normally be populated by the other AgriTech services.

Docker Compose also starts Ollama and pulls the lightweight `qwen2.5:0.5b` model. Test it with:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Explain AI simply"}'
```

Set `AI_MODEL` to use a different model. When Spring Boot runs outside Docker, set `AI_BASE_URL=http://localhost:11434`.

## Production deployment

`docker-compose.production.yml` runs only PostgreSQL and this API. Ollama stays
on a separate server and is configured through `AI_BASE_URL`.

The deployment workflow publishes the API image to GHCR and deploys it over
SSH on every push to `main`. Configure these GitHub Actions secrets:

- `VPS_HOST` - API server public IP
- `VPS_USER` - SSH user, normally `ubuntu`
- `VPS_SSH_KEY` - complete private SSH key
- `POSTGRES_PASSWORD` - strong database password
- `AI_BASE_URL` - URL of the separate Ollama server, including port `11434`

## Test

```bash
./mvnw verify
```
