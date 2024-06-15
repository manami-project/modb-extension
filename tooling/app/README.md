# modb-extension app

This application creates and updates the data that you can find in the `/data` folder of this repository.

## How to run

1. Setup config by creating a config file `tooling/app/src/main/resources/config.toml` with content:
```toml
[modb.extension.config]
dataDirectory=""
```
Set `dataDirectory` to the absolute path of json files on your machine.
2. Run application via `tooling/app/src/main/kotlin/io/github/manamiproject/modb/extension/Main.kt`