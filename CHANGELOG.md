# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.6] - 2020-04-09
### Fixed
- QR Code generation now utilises `plateNumber` as id for vehicles (uses `identifierNumber` before).

## [1.1.5] - 2020-04-09
### Changed
- Updated APOR seed data in Lookup Table

## [1.1.4] - 2020-04-08
### Changed
- Updated new PDF two-page design (individual has two copies, vehicle uses full page).

## [1.1.3] - 2020-04-08
### Added
- RBAC configurations:
```
APPROVER
- endpoint: /registry/access-passes
  verbs: ALL

CHECKPOINT
- endpoint: /batch/access-passes
  verbs: ALL
```

### Fixed
- Access Pass getting approved even though an error occurs. Enabled transactional management for updating access passes.

## [1.1.2] - 2020-04-08
### Added
- RBAC for `DELETE` /registry/access-passes/**
- Mobile number formatting

### Fixed
- DB cred security leak

## Changed
- default expiration date is now April 30, 2020
- Control code generation now performed on-the-fly, rather than queried from the database.

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
