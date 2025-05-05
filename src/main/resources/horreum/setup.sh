#!/usr/bin/bash

CWD="$(dirname "$0")"

API="http://localhost:8080/api"

KEYCLOAK_URL=$( curl -k -s $API/config/keycloak | jq -r '.url')
TOKEN=$(curl -s -X POST "$KEYCLOAK_URL"/realms/horreum/protocol/openid-connect/token \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=horreum.bootstrap&password=secret&grant_type=password&client_id=horreum-ui' \
    | jq -r .access_token)

echo "==== Getting API key ===="
API_KEY=$(curl -s -X POST $API'/user/apikey' -H "Authorization: Bearer $TOKEN" -H 'content-type: application/json' -d '{"name": "test-key", "type": "USER"}')

echo "You API key is: $API_KEY"

echo "==== Creating schema ===="
echo ""
curl $API'/schema/import/' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/schema.json

echo "==== Creating tests ===="
echo ""
curl $API'/test/import/' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/test.json

echo "==== Uploading run ===="
echo ""
curl $API'/run/data?test=GithubIntegrationExample&start=$.info.start&stop=$.info.stop&owner=dev-team&access=PUBLIC' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/baseline_1.json
curl $API'/run/data?test=GithubIntegrationExample&start=$.info.start&stop=$.info.stop&owner=dev-team&access=PUBLIC' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/baseline_2.json
curl $API'/run/data?test=GithubIntegrationExample&start=$.info.start&stop=$.info.stop&owner=dev-team&access=PUBLIC' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/run_2.json
curl $API'/run/data?test=GithubIntegrationExample&start=$.info.start&stop=$.info.stop&owner=dev-team&access=PUBLIC' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/run_3.json
curl $API'/run/data?test=GithubIntegrationExample&start=$.info.start&stop=$.info.stop&owner=dev-team&access=PUBLIC' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/run_4.json
curl $API'/run/data?test=GithubIntegrationExample&start=$.info.start&stop=$.info.stop&owner=dev-team&access=PUBLIC' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/run_5.json
#curl $API'/run/data?test=GithubIntegrationExample&start=$.info.start&stop=$.info.stop&owner=dev-team&access=PUBLIC' -X POST -H 'content-type: application/json' -H "X-Horreum-API-Key: $API_KEY" -d @"$CWD"/run_6.json
