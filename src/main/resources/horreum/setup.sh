#!/usr/bin/bash

# see how to create an API key here https://horreum.hyperfoil.io/docs/tasks/api-keys/
API_KEY=${API_KEY:-"CHANGE_ME"}

CWD="$(dirname "$0")"

echo "==== Creating schema ===="
echo ""
curl 'http://localhost:8080/api/schema/import/' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/schema.json

echo "==== Creating tests ===="
echo ""
curl 'http://localhost:8080/api/test/import/' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/test.json
curl 'http://localhost:8080/api/test/import/' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/test_prs.json