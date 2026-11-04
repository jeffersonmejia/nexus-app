variable "app_name" { type = string }
variable "server_name" { type = string }
variable "resource_group_name" { type = string }
variable "location" { type = string }
variable "subnet_id" { type = string }
variable "private_dns_zone_id" { type = string }
variable "administrator_username" { type = string }
variable "administrator_password" {
  type      = string
  sensitive = true
}
