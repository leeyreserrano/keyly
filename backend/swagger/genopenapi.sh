#!/bin/bash

# Intenta fer un curl silencios a la direcció
if curl --output /dev/null --silent --head --fail -k https://localhost:8081/api/v3/api-docs; then
    # Elimina l'antic
    rm -f openapi.json

    # Decarrega el nou
    curl -f -k https://localhost:8081/api/v3/api-docs -o ~/keyly/backend/swagger/openapi.json

    echo "Fitxer descarregat."
else
    echo "El servei no respon."
fi