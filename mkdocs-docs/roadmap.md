# Roadmap

## Done

- `Required`, `Email`, `MustBeMoreThan`, `MustBeLessThan`, `MustBeInRange`, `MustBeEqualTo`, `Custom`, `Optional` validation types
- `Flow.Down`, `Flow.Up`, `Flow.Splash` validation ordering
- `ErrorSafeValue` with `modified` dirty tracking
- `errorSafe()` convenience state builder
- `Form` composable with `LocalFormValidator` composition local
- Snackbar integration via `SnackBarProperties`
- Android, JVM (desktop), and iOS targets
- Maven Central publishing via NMCP

## Planned

- Cross-field validation rules (a field that depends on another field's current value without re-creating the validator)
- Compose Multiplatform Web target
- Accessibility: surface errors to semantics / TalkBack automatically
- Compose preview-friendly `LocalFormValidator` stub
