variable "app_name" { type = string }
variable "resource_group_name" { type = string }
variable "location" { type = string }
variable "subnet_id" { type = string }
variable "acr_name" { type = string }
variable "container_image" { type = string }
variable "database_fqdn" { type = string }
variable "database_username" { type = string }
variable "database_password" {
  type      = string
  sensitive = true
}
