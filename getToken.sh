#!/bin/bash

export VAULT_ADDR=https://vault.adeo.no
VAULT_SECRET_NAME=''
ENVIRONMENT_NAME=''

echo '**** Get token from Azure ****'
echo
read -p 'Vault secret name (appname): ' VAULT_SECRET_NAME
read -p 'Environment (dev | prod): ' ENVIRONMENT_NAME
echo

# Gcloud authorized and switch to namespace okonomi
# Ensure user is authenicated, and run login if not.x
gcloud auth print-identity-token &> /dev/null
if [ $? -gt 0 ]; then
    gcloud auth login
fi
kubectl config use-context $ENVIRONMENT_NAME-gcp
kubectl config set-context --current --namespace=okonomi

# Get secret from GCP
AZURE_APP_CLIENT_ID=$(kubectl exec -it $(kubectl get pods | grep sokos-ktor-template | cut -f1 -d' ') -c sokos-ktor-template -- env | grep -E "AZURE_APP_CLIENT_ID" | cut -d "=" -f 2 | tr -d '\r')
AZURE_OPENID_CONFIG_TOKEN_ENDPOINT=$(kubectl exec -it $(kubectl get pods | grep sokos-ktor-template | cut -f1 -d' ') -c sokos-ktor-template -- env | grep -E "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT" | cut -d "=" -f 2 | tr -d '\r')

# Get secret from Vault
[[ "$(vault token lookup -format=json | jq '.data.display_name' -r; exit ${PIPESTATUS[0]})" =~ "nav.no" ]] &>/dev/null || vault login -method=oidc -no-print
VAULT_CLIENT_ID=$(vault kv get -field=client_id azuread/"$ENVIRONMENT_NAME"/creds/"$ENVIRONMENT_NAME"-fss_okonomi_"$VAULT_SECRET_NAME")
VAULT_CLIENT_SECRET=$(vault kv get -field=client_secret azuread/"$ENVIRONMENT_NAME"/creds/"$ENVIRONMENT_NAME"-fss_okonomi_"$VAULT_SECRET_NAME")

echo
echo "(sokos-ktor-template) AZURE_APP_CLIENT_ID                  : $AZURE_APP_CLIENT_ID"
echo "($VAULT_SECRET_NAME) VAULT_CLIENT_ID                       : $VAULT_CLIENT_ID"
echo "($VAULT_SECRET_NAME) VAULT_CLIENT_SECRET                   : $VAULT_CLIENT_SECRET"
echo "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT    : $AZURE_OPENID_CONFIG_TOKEN_ENDPOINT"
echo

token=$(curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d "grant_type=client_credentials&client_id=$VAULT_CLIENT_ID&scope=api://$AZURE_APP_CLIENT_ID/.default&client_secret=$VAULT_CLIENT_SECRET" "$AZURE_OPENID_CONFIG_TOKEN_ENDPOINT" | jq -r .access_token)
echo
echo "Access token: $token"