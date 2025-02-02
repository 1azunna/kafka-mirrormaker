# Kafka Mirrormaker Helm Chart

This chart deploys kafka mirrormaker with the custom replication plugin

# Requirements

- Helm Secrets or Helm Sops

# Setting Up Authentication Credentials and Keystore Passwords

Use `kafka.secretConfig` to setup authentication

# Installation Example

If you want to replicate the `mytopic_*` topic across kafka clusters:

```bash
helm secrets upgrade --install mytopic-mm2 . --values secrets.yaml --namespace kafka --create-namespace
```