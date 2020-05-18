
# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.0.1] - 2020-05-17
### Changed
- Hot fix for retrieving the principal data, to retrieve the apor types.
- [#475](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/475) `2.0.1.alpha.1` - When editing access passes,
    change the name to uppercase. 

### Fixed
- `2.0.1.alpha.1` Removed `/` from filename when retrieving newest checkpoint app URL filename.

## [2.0.0] - 2020-05-17
### Added
- [#458](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/458) Use Keycloak to enforce Permissions

## [1.8.3] - 2020-05-16
### Added
- [#463](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/463) CRUD operations for Scanner Devices
    - `GET registry/scanner-devices` - retrieve devices with optional filter
    - `POST registry/scanner-devices` - register new device
    - `GET registry/scanner-device/{unique_id}` - retrieve device matching id
    - `PUT registry/scanner-device/{unique_id}` - update device matching id
    - `DELETE registry/scanner-device/{unique_id}` - delete device matching id
- [#454](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/455) `1.8.3.alpha.1` Added new AporLookup model, with updated pomtable 
    definition and seed data.
- [#396](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/396) `1.8.3.alpha.3` - Added check version and download endpoint for Checkpoint app.
- [#449](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/449) `1.8.3.alpha.4` Restricts bulk upload by their APOR type.
    
### Changed
- [#455](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/455) `1.8.3.alpha.1` Batch upload now relies on `apor_lookup` table 
    for APOR type checking, rather than the old `lookup_table` table.

### Fixed
- [#461](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/461) `1.8.3.alpha.2` Fixed duplicate rapid passes caused by concurrency issues.


## [1.8.2] - 2020-05-08
### Changed
- [#453](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/453) `1.8.2.alpha.1` `registry/access-passes` now limited by keycloak `access_token` `aportypes` attribute

### Added
- new columns `email_sent`, `mobile_sent` on `notifier_log` table
- [#460](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/460) Destination city is now a required field.
- [#459](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/459) Overwrite destination city to `Multi City` for specific APOR types.
- [#441](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/441) `1.8.2.alpha.2` - Added revocation endpoint for checkpoint app.

## [1.8.1] - 2020-05-08
### Changed
- Temporarily? turned off role checking while keycloak is being finalized
- [#448](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/448) `1.8.1.alpha.1` Added search by email.
- [#405](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/405) `1.8.1.alpha.2` - `issuedBy` field when requesting for a RapidPass.

## [1.8.0] - 2020-05-04
### Added
- [#364](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/364) Keycloak implementation.

## [1.7.7] - 2020-05-05
### Added
- [#430](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/430) optional data from the csv files will be trimmed
    based on their database column max restrictions.
    
### Changed
- [#446](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/446) Increased email length from 50 to 256 characters.
- [#438](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/438) Sort the results of the access passes by 
    `valid_to` in descending order.
- [#444](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/444) Declined passes will not be inserted into the DB
    anymore.

## [1.7.6] - 2020-05-05
### Fixed
- [#445](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/445) Changed the requirements for the registry batch CSV import.
    
## [Released]
    
## [1.7.5] - 2020-05-04
### Added
- [#440](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/440) Added new registrars to be used by new users
    of the dashboard.

### Changed
- [#415](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/415) Updated registrar 37 (DTI-BOI) to use the correct
    short code.

## [1.7.4] - 2020-05-03
### Added
- [#429](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/429) Added update access pass endpoint, for support
    team or for approvers. 

## [1.7.3] - 2020-05-03
### Fixed
- [#435](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/435) Fixed issue of primitives being used in entity classes.

## [1.7.2] - 2020-05-01
### Changed
- [#431](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/431) Changed validation error message for invalid
mobile number input.

## [1.7.1] - 2020-05-01
### Added
- [#426](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/426) Added new endpoint to check the control code of
    the RapidPass.

## [1.7.0] - 2020-05-01
### Added
- [#403](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/403) Added Resend Text & Email Endpoint
- [#425](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/425) Added `notified` property when querying a RapidPass.

## [1.6.4] - 2020-04-28
### Fixed
- [#420](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/420) Increase supported length for id type and suffix.

## [1.6.3] - 2020-04-28
### Fixed
- [#419](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/419) Allows bulk upload to handle rows with empty emails.

## [1.6.2] - 2020-04-28
### Changed
- [#410](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/410) Added issuer when bulk upload/approval is performed.

## [1.6.1] - 2020-04-28
### Changed
- [#418](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/418) Updated APOR codes to include SO and DR.
- [#409](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/409) Increased issued_by from `varchar(20)` to `varchar(40)`. 

## [1.6.0] - 2020-04-28
### Fixed
- [#417](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/417) Fixed de-dup potential when performing bulk uploads, when
    an existing access pass is pending registered previously.

## [1.5.20] - 2020-04-27
### Fixed
- [#416](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/416) Fixed CSV bulk upload for required rows with 
missing data

## [1.5.20] - 2020-04-27
### Changed
- [#414](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/414) Pass type is always INDIVIDUAL when performing bulk upload.

## [1.5.19] - 2020-04-26
### Changed
- [#408](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/408) Updated PDF design to highlight the name of the person.

## [1.5.18] - 2020-04-26
### Changed
- [#406](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/406) Handle new lines inside of cells in the CSV file.

## [1.5.17] - 2020-04-26
### Changed
- [#404](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/404) Changed warning text on PDF regarding illegally tampering with the rapid pass.

## [1.5.16] - 2020-04-26
### Added
- [#402](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/402) Added an error message for missing columns in 
CSV batch upload

## [1.5.15] - 2020-04-25
- [#401](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/401) Expiration Date is now a system parameter, defaults to May 15, 2020

## [1.5.14] - 2020-04-24
### BugFix
- [#399](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/399) Removed hardcoded keys and cycle test keys.
- [#360](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/360) Fixed sending of email for declined transactions.

## [1.5.13] - 2020-04-23
### HotFix
- [#394](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/394) Enabled checkpoint auth endpoint.

## [1.5.12] - 2020-04-23
- [#392](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/392) Implemented endpoint toggling for easy configuration.

## [1.5.11] - 2020-04-22
- Implemented CSRF cookie implementation with dynamic domain

## [1.5.10] - 2020-04-22
- Reenabled CSRF for dev env with cookie path set to `/`

## [1.5.9] - 2020-04-22
- Created registrar data and mapping of the APOR to the registrars. 

## [1.5.8] - 2020-04-21
- disable muna csrf due to cookie disappearing act    

## [1.5.7] - 2020-04-21
- set csrf cookie path      

## [1.5.6] - 2020-04-21
- implemented `Access-Control-Allow-Credentials: true`

## [1.5.5] - 2020-04-21
- [#377](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/377) - Disable `GET /registry/scanner-devices` endpoint.
- implemented Spring Security CSRF          

## [1.5.4] - 2020-04-20
- [#370](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/370) - Handle incorrect format for CSV caused by Excel.

## [1.5.3] - 2020-04-20
### Fixed
- [#353](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/353) - API now returns a human readable error when
    an enum value could not be parsed (it used to throw a 500 error, informing user to see the application/project
    manager).
    
## [1.5.2] - 2020-04-20
### Changed
- [#384](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/384) Added security headers.

## [1.5.1] - 2020-04-20
### Changed
- [#382](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/382) Updated APOR seed data in Lookup Table.

### Added
- [#285](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/285) - Added an endpoint that 
retrieves the current access pass status

## [1.5.0] - 2020-04-19
### Fixed
- [#322](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/332) - Implemented registrar user account 
locking for several failed login attempts

## [1.4.8] - 2020-04-19
### Added
- [#381](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/381) - Added change password endpoint, for 
    approver dashboard users.

## [1.4.7] - 2020-04-18
### Fixed
- [#354](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/354) - Search `GET /registry/access-passes?search=query`
    is now case insensitive for both name and company.

## [1.4.6] - 2020-04-18
### Fixed
- [#242](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/242) - Suspended access passes will no longer take 
    priority when querying `GET /registry/access-passes/{referenceId}`.

## [1.4.5] - 2020-04-16
### Changed
- Closed `GET /checkpoint/access-passes/**` since currently being reworked for checkpoint.

## [1.4.4] - 2020-04-16
### Added
- Included IATF as ID Type (Individual) from the look up table.

## [1.4.3] - 2020-04-16
### Changed
- closed `GET /batch/access-passes` since currently being reworked for checkpoint.

## [1.4.2] - 2020-04-16
### Fixed
- [#368](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/368) - fixed regression issue to make sure `search`
    property works when performing `GET /registry/access-passes`.


## [1.4.1] - 2020-04-16
### Added
- [#371](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/371) - Added in the PDF and email that tampering with
    the PDF is illegal.

### Fixed
- [#358](https://gitlab.com/dctx/rapidpass/rapidpass-api/-/issues/358) Fixed missing email in `RapidPass` model.
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
### Added
- Included IATF as ID Type (Individual) from the look up table.

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

## [1.0.1] - 2020-04-06

### Added
- Requesting for a rapid pass now shows the `origin` related address fields. 
- Normalization of fields (plate number, mobile number, identifier number) before they are validated, for creating new rapid pass.

### Changed
- Access pass `remarks` column is now increased from 150 to 250 characters. 
