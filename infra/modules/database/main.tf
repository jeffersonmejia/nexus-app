resource "azurerm_postgresql_flexible_server" "app" {
  name                          = "${var.app_name}-db"
  resource_group_name           = var.resource_group_name
  location                      = var.location
  version                       = "16"
  delegated_subnet_id           = var.subnet_id
  private_dns_zone_id           = var.private_dns_zone_id
  public_network_access_enabled = false
  administrator_login           = var.administrator_username
  administrator_password       = var.administrator_password
  zone                          = "1"
  storage_mb                    = 32768
  sku_name                      = "B_Standard_B1ms"
  backup_retention_days         = 7

}

resource "azurerm_postgresql_flexible_server_database" "app" {
  name      = "nexus"
  server_id = azurerm_postgresql_flexible_server.app.id
  collation = "en_US.utf8"
  charset   = "UTF8"
}
