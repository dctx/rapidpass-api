# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.3] - 2020-04-07
### Fixed
- JWT token hard coded to expire in 30 seconds. Set it to 1 day.

## [1.0.2] - 2020-04-06

### Added
- String trimming CSV bulk upload data for Bulk Registration for RapidPass.

### Changed
- Updated new PDF two-page design (individual has two copies, vehicle uses full page).

## [Released]

## [1.0.1] - 2020-04-06

### Added
- Requesting for a rapid pass now shows the `origin` related address fields. 
- Normalization of fields (plate number, mobile number, identifier number) before they are validated, for creating new rapid pass.

### Changed
- Access pass `remarks` column is now increased from 150 to 250 characters. 