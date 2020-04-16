# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.4.1] - 2020-04-16
### Fixed
- [#370](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/370) - Handle XLSX default or missing data, after
    the approver saves the Excel sheet as a CSV.

## [1.4.0] - 2020-04-12
### Removed
- [#122](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/122) - Removed `last_used` property from the
    `AccessPass`. No longer returned by the API when performing `GET /registry/access-passes`.

## [1.3.1-SNAPSHOT]
### Added
- [#172](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/172) - Can now bulk register approvers. See 
    `src/main/resources/approver-bulk-registration.csv` for template CSV.

## [1.3.0-SNAPSHOT]
### Changed
- Bulk upload validation has been optimized to not query database if basic validation fails.
- Bulk upload no longer rejects records if duplicate requests.
- Bulk upload can now send messages to a kafka topic called 'requests' instead of directly updating the RapidPass database.
- introduced 'bulk-upload.process' parameter to switch between kafka-based bulk upload or internal processing.
- added index on access_pass to optimize queries. 
- [#345](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/345) fixed `get /registry/access-passes` to allow multiple apor types in the query parameters
- [#350](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/350) - added new endpoint for syncing checkpoint devices 'get /batch/access-pass-events'

## [1.2.3] - 2020-04-13
### Fixed
- `/registry/access-passes` was already fixed, but `/registry/access-passes/` was not

## [1.2.1] - 2020-04-11
### Fixed
- `GET /batch/access-passes` with hard coded `lastSyncOn` logic.
- [#351](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/351) - Fixed missing dependencies on `develop`, 
  causing build failures.

### Changed
- Prevented unit tests from triggering PDF generation. PDFs can only be manually inspected to see
  whether the content and layouts are correct.

## [1.1.9] - 2020-04-10
### Fixed
- RBAC on `GET /registry/access-passes`
- Updated SMS and EMAIL spiel.

## [1.1.8] - 2020-04-09
### Fixed
- Fixed PDF missing a control code. 

## [1.1.7] - 2020-04-09
### Changed
- Updated new PDF single-page design (individual has two copies which is in 1/4 of A4, vehicle uses half page).

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
