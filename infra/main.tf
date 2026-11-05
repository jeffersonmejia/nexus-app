terraform {
  required_version = ">= 1.8.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
  }

  backend "azurerm" {}
}

provider "azurerm" {
  features {}
  subscription_id = var.subscription_id
}

variable "subscription_id" {
  description = "Azure subscription ID."
  type        = string
}

variable "resource_group_name" {
  description = "Pre-created resource group for application resources."
  type        = string
  default     = "nexus-app-rg"
}

variable "app_name" {
  description = "Base name for Container Apps resources."
  type        = string
  default     = "nexus-ai"
}

variable "acr_name" {
  description = "Globally unique ACR name (lowercase letters and digits, 5-50 characters)."
  type        = string
}

variable "app_location" {
  description = "Container Apps region. East US is blocked by subscription policy; East US 2 is permitted."
  type        = string
  default     = "eastus2"
}

variable "database_location" {
  description = "PostgreSQL region."
  type        = string
  default     = "brazilsouth"
}

variable "database_server_name" {
  description = "Globally unique name for the PostgreSQL Flexible Server."
  type        = string
  default     = "nexusai20261104jpdb"
}

variable "container_image" {
  description = "Image deployed to Container Apps."
  type        = string
  default     = "mcr.microsoft.com/azuredocs/containerapps-helloworld:latest"
}

variable "postgres_admin_username" {
  description = "PostgreSQL administrator username."
  type        = string
  default     = "nexusadmin"
}

variable "postgres_admin_password" {
  description = "PostgreSQL administrator password."
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Signing secret for application JWTs."
  type        = string
  sensitive   = true
}

data "azurerm_resource_group" "app" {
  name = var.resource_group_name
}

module "networking" {
  source              = "./modules/networking"
  app_name            = var.app_name
  resource_group_name = data.azurerm_resource_group.app.name
  app_location        = var.app_location
  database_location   = var.database_location
}

module "database" {
  source                 = "./modules/database"
  app_name               = var.app_name
  server_name            = var.database_server_name
  resource_group_name    = data.azurerm_resource_group.app.name
  location               = var.database_location
  subnet_id              = module.networking.database_subnet_id
  private_dns_zone_id    = module.networking.postgres_private_dns_zone_id
  administrator_username = var.postgres_admin_username
  administrator_password = var.postgres_admin_password
  depends_on             = [module.networking]
}

module "container_app" {
  source              = "./modules/container_app"
  app_name            = var.app_name
  resource_group_name = data.azurerm_resource_group.app.name
  location            = var.app_location
  subnet_id           = module.networking.container_apps_subnet_id
  acr_name            = var.acr_name
  container_image     = var.container_image
  database_fqdn       = module.database.fqdn
  database_username   = var.postgres_admin_username
  database_password   = var.postgres_admin_password
  jwt_secret          = var.jwt_secret
}

output "container_app_url" {
  description = "Public HTTPS URL for the application."
  value       = module.container_app.url
}

output "acr_login_server" {
  value = module.container_app.acr_login_server
}

output "container_app_name" {
  value = module.container_app.name
}

output "postgres_server_fqdn" {
  description = "Private PostgreSQL server hostname."
  value       = module.database.fqdn
}
