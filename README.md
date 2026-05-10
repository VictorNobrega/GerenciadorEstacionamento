# Gerenciador de Estacionamento

Backend em Java 21, Gradle e Spring Boot 3.5 para gerenciar vagas, eventos de entrada/estacionamento/saida e faturamento por setor.

## Tecnologias

- Java 21
- Spring Boot 3.5
- Gradle
- MySQL
- Swagger/OpenAPI com springdoc
- Docker e Docker Compose

## Como executar com Docker

```bash
docker compose up --build
```

Servicos expostos:

- API: `http://localhost:3003`
- Swagger: `http://localhost:3003/swagger-ui.html`
- Simulador: `http://localhost:3000`
- MySQL: `localhost:3306`

Ao iniciar, a aplicacao busca a configuracao da garagem em `GET /garage` no simulador e grava setores/vagas no MySQL.

## Como executar localmente

Suba um MySQL local e o simulador:

```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

Configure as variaveis se necessario:

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/parking_manager?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export SPRING_DATASOURCE_USERNAME=parking
export SPRING_DATASOURCE_PASSWORD=parking
export GARAGE_SIMULATOR_URL=http://localhost:3000
```

Execute:

```bash
./gradlew bootRun
```

## API

### Webhook

`POST /webhook`

ENTRY:

```bash
curl -X POST http://localhost:3003/webhook \
  -H 'Content-Type: application/json' \
  -d '{
    "license_plate": "ZUL0001",
    "entry_time": "2025-01-01T12:00:00.000Z",
    "event_type": "ENTRY"
  }'
```

PARKED:

```bash
curl -X POST http://localhost:3003/webhook \
  -H 'Content-Type: application/json' \
  -d '{
    "license_plate": "ZUL0001",
    "lat": -23.561684,
    "lng": -46.655981,
    "event_type": "PARKED"
  }'
```

EXIT:

```bash
curl -X POST http://localhost:3003/webhook \
  -H 'Content-Type: application/json' \
  -d '{
    "license_plate": "ZUL0001",
    "exit_time": "2025-01-01T13:01:00.000Z",
    "event_type": "EXIT"
  }'
```

### Faturamento

`GET /revenue?date=YYYY-MM-DD&sector=A`

```bash
curl 'http://localhost:3003/revenue?date=2025-01-01&sector=A'
```

Resposta:

```json
{
  "amount": 0.00,
  "currency": "BRL",
  "timestamp": "2025-01-01T12:00:00Z"
}
```

## Regras

- Ate 30 minutos nao ha cobranca.
- A partir de 31 minutos, cobra por hora cheia arredondada para cima, incluindo a primeira hora.
- O preco dinamico e congelado quando o veiculo estaciona em uma vaga.
- A vaga e ocupada no evento `PARKED`, pois esse evento informa `lat` e `lng`.
- A vaga e liberada no evento `EXIT`.

## Testes

```bash
./gradlew test
```
