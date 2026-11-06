# Validation record

Authoring host: Windows, November 6, 2026. Requirements: Java 17+; Windows, macOS, Linux with a graphical desktop.

The README verification command is `java -cp build Adventure --self-test`. The companion GitHub Actions workflow describes reproducible build/test checks. Merely including that workflow does not mean it ran successfully. Host-specific verification results are recorded before publication.

Manual acceptance steps are in README.md. GUI behavior, hardware sensors, permissions, audio output, and OS isolation need verification on their target platform; compilation alone cannot establish these properties.

## Authoring result

Target toolchain/platform unavailable on the Windows authoring host. Source and build configuration were reviewed, and a target-platform GitHub Actions build/check workflow is provided. No successful native build or runtime result is claimed yet.

## Observed GitHub Actions result

Passed on November 6, 2026: [build/check run](https://github.com/netcodermike/spaced-repetition-adventure/actions/runs/42196157256).

Verified source commit: `229299974734bebac3ccd2c40d25c44ec47d077b`.

Java compilation, scheduler checks, persistence round trip, and malformed-deck rejection.

This record may be committed after the linked source-validation run; the linked SHA identifies exactly what was checked. Native GUI/hardware acceptance remains manual as described in README.md.
