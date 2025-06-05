#!/bin/bash

# Ensure user is authenicated, and run login if not.
if ! gcloud auth print-identity-token &>/dev/null; then
    gcloud auth login
fi

kubectl config use-context dev-fss
kubectl config set-context --current --namespace=okonomi


 get_secret() {
     local secret_name=$1
     local key=$2
     kubectl get secret "$secret_name" -o "jsonpath={.data.$key}" | base64 --decode
 }

 get_secretName(){
   local prefix=$1
   kubectl get secrets --no-headers -o custom-columns=":metadata.name" | grep "^$prefix-" | head -n1
 }


AZURE_APP_CLIENT_ID="azure-app-client-id"
AZURE_APP_WELL_KNOWN_URL="azure-app-well-known-url"
POSTGRES_PASSWORD="pg-password"
POSTGRES_USERNAME="pg-username"
rm -f defaults.conf
{
    echo "AZURE_APP_CLIENT_ID=$AZURE_APP_CLIENT_ID"
    echo "AZURE_APP_WELL_KNOWN_URL=$AZURE_APP_WELL_KNOWN_URL"
    echo "POSTGRES_USERNAME=$POSTGRES_USERNAME"
    echo "POSTGRES_PASSWORD=$POSTGRES_PASSWORD"

} > defaults.conf

echo "defaults.conf created successfully."