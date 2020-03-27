{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "swaggerui.name" -}}
{{- default (printf "%s-swaggerui" .Chart.Name) .Values.swaggerUI.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "swaggerui.fullname" -}}
{{- if .Values.swaggerUI.fullnameOverride -}}
{{- .Values.swaggerUI.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default (printf "%s-swaggerui" .Chart.Name) .Values.swaggerUI.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "swaggerui.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}
