output "url" {
  value = "https://${azurerm_container_app.app.ingress[0].fqdn}"
}

output "acr_login_server" {
  value = azurerm_container_registry.app.login_server
}

output "name" {
  value = azurerm_container_app.app.name
}
