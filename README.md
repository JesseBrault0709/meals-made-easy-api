# Meals Made Easy API

## Getting Started
First, create a `.env` file with the following environment variables:

- `MINIO_ROOT_PASSWORD=<put something here>`
- `MYSQL_ROOT_PASSWORD=<put something here>`
- `MYSQL_PASSWORD=<put something here>`

Then run `docker compose up -d --build`. The `--build` option can be omitted if you
have already built the `api` from `Dockerfile`. Once Docker Compose has finished
starting everything, navigate to `http://localhost:8080/greeting`.
You should see a simple "Hello, World!" greeting.

**N.b.: the current configuration of the app is to use the `dev` Spring-Boot profile, 
which seeds the app with some simple recipes whose sources are located in the
`dev-data` directory.**