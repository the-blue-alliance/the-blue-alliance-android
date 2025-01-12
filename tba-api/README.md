# tba-api
This module is intended to replace `:libTba`. `:libTba` is a generated set of TBA API models
using [a fork of swagger-codegen](https://github.com/the-blue-alliance/swagger-codegen) to support
features such as generating interfaces instead of concrete classes. That fork has not been updated
in some time, and is no longer compatible with the latest swagger specs.

This module aims to use [openapi-generator](https://github.com/OpenAPITools/openapi-generator) as-is
to generate concrete classes. This should more easily facilitate updates to the plugin and swagger
specs in the future, decreasing maintenance overhead.

## Updating generated models & API interfaces
To update the generated API models and Retrofit API interfaces, run `./gradlew tba-api:openApiGenerate`.