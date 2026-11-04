resource "azurerm_virtual_network" "app" {
  name                = "${var.app_name}-app-vnet"
  address_space       = ["10.20.0.0/16"]
  location            = var.app_location
  resource_group_name = var.resource_group_name
}

resource "azurerm_subnet" "container_apps" {
  name                 = "container-apps"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.app.name
  address_prefixes     = ["10.20.0.0/23"]

  delegation {
    name = "container-apps"
    service_delegation {
      name    = "Microsoft.App/environments"
      actions = ["Microsoft.Network/virtualNetworks/subnets/join/action"]
    }
  }
}

resource "azurerm_virtual_network" "database" {
  name                = "${var.app_name}-db-vnet"
  address_space       = ["10.30.0.0/16"]
  location            = var.database_location
  resource_group_name = var.resource_group_name
}

resource "azurerm_subnet" "postgres" {
  name                 = "postgres"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.database.name
  address_prefixes     = ["10.30.0.0/28"]

  delegation {
    name = "postgres-flexible-server"
    service_delegation {
      name    = "Microsoft.DBforPostgreSQL/flexibleServers"
      actions = ["Microsoft.Network/virtualNetworks/subnets/join/action"]
    }
  }
}

resource "azurerm_virtual_network_peering" "app_to_database" {
  name                      = "app-to-database"
  resource_group_name       = var.resource_group_name
  virtual_network_name      = azurerm_virtual_network.app.name
  remote_virtual_network_id = azurerm_virtual_network.database.id
}

resource "azurerm_virtual_network_peering" "database_to_app" {
  name                      = "database-to-app"
  resource_group_name       = var.resource_group_name
  virtual_network_name      = azurerm_virtual_network.database.name
  remote_virtual_network_id = azurerm_virtual_network.app.id
}

resource "azurerm_private_dns_zone" "postgres" {
  name                = "${var.app_name}.postgres.database.azure.com"
  resource_group_name = var.resource_group_name
}

resource "azurerm_private_dns_zone_virtual_network_link" "app" {
  name                  = "${var.app_name}-app-dns-link"
  private_dns_zone_name = azurerm_private_dns_zone.postgres.name
  virtual_network_id    = azurerm_virtual_network.app.id
  resource_group_name   = var.resource_group_name
}

resource "azurerm_private_dns_zone_virtual_network_link" "database" {
  name                  = "${var.app_name}-db-dns-link"
  private_dns_zone_name = azurerm_private_dns_zone.postgres.name
  virtual_network_id    = azurerm_virtual_network.database.id
  resource_group_name   = var.resource_group_name
}
