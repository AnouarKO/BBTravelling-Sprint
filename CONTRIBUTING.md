# Contributing Guide

## Branching Strategy

- `main`: stable delivery branch (release-ready snapshots).
- `develop`: integration branch for the current sprint.
- `feature/<short-name>`: short-lived feature branches (UI, docs, navigation, domain, etc.).
- `fix/<short-name>`: bug fixes.

## Workflow

1. Create a branch from `develop`.
2. Keep commits focused and small.
3. Rebase with latest `develop` before opening a PR.
4. Open PR into `develop`.
5. Merge to `main` only for sprint release candidates.

## Commit Convention

Use imperative messages:

- `feat: add trip detail itinerary tab`
- `fix: handle missing trip in detail screen`
- `docs: update sprint final report`
- `refactor: move model package to domain`

## Code Quality Rules

- Keep UI state local and explicit.
- Prefer immutable models.
- Keep mock data in `data/`.
- Keep route names centralized in `navigation/Routes.kt`.
- Add previews for main composables.
- Keep pending logic marked with `@TODO`.
