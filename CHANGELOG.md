# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [1.1.2-SNAPTSHOT] - 2020-04-08
### Added
- RBAC for `DELETE` /registry/access-passes/**
- Mobile number formatting

### Fixed
- DB cred security leak

## Changed
- default expiration date is now April 30, 2020

## [1.1.1] - 2020-04-07
### Changed
- GET requests now use query parameters (on the url) rather than using JSON Body Request.

## [1.1.0] - 2020-04-07
### Added
- RBAC configuration
- String trimming CSV bulk upload data for Bulk Registration for RapidPass.
- Search functionality for access passes.

### Fixed
- JWT token hard coded to expire in 30 seconds. Set it to 1 day.
- Security filter bugs.


## [1.0.2] - 2020-04-06

### Added
- String trimming CSV bulk upload data for Bulk Registration for RapidPass.

## [Released]

## [1.0.1] - 2020-04-06

### Added
- Requesting for a rapid pass now shows the `origin` related address fields. 
- Normalization of fields (plate number, mobile number, identifier number) before they are validated, for creating new rapid pass.

### Changed
- Access pass `remarks` column is now increased from 150 to 250 characters. 
