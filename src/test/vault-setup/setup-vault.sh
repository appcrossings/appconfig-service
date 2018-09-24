#/bin/bash

export VAULT_ADDR='http://127.0.0.1:8200'

./vault auth enable userpass

./vault write auth/userpass/users/test password=password policies=admins

./vault policy write admins ./admin.hcl
