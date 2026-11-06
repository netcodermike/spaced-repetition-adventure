# Recall Quest — Spaced Repetition Adventure

Study flashcards in a desktop adventure where successful recall earns XP, unlocks equipment, and advances through locations while a scheduler controls when cards return.

**Language:** Java 17+  
**Platform:** Windows, macOS, Linux with a graphical desktop  
**Status:** First-release learning project. See implemented scope and limitations below; this is not a production-completeness claim.

## Features

- Swing desktop UI with reveal, remembered, and retry controls.
- Tab-separated custom decks and five sample computing cards.
- Review intervals of 1 day, 3 days, then ease-adjusted growth.
- Incorrect answers return the card to today's queue and reduce ease.
- Persistent XP, four locations, and equipment milestones.

## Requirements and quick start

Clone the repository, enter its folder, and install the language/platform tools listed above. Commands run from the repository root. No credentials are required except for Backup Vault's explicitly generated secrets.

```sh
git clone https://github.com/netcodermike/spaced-repetition-adventure.git
cd spaced-repetition-adventure
javac -d build src/Adventure.java
java -cp build Adventure
# Optional custom deck and save file:
java -cp build Adventure my-deck.tsv my-progress.properties
```

## Verification

```sh
java -cp build Adventure --self-test
```

Compile and run the self-test. Reveal a card and mark it remembered; confirm 10 XP. Mark another incorrect; it should remain due today. Restart and confirm XP and due dates persist. Finish all due cards and confirm the cleared message. Use a temporary save path when experimenting with scheduling.

See [validation notes](docs/VALIDATION.md) for what was actually executed during authoring. A committed CI workflow is a future check until its run is observed successful.

## Architecture and data

`src/Adventure.java` includes the scheduler, deck parser, Properties persistence, and Swing UI. `deck.tsv` has one `front<TAB>back` pair per line; `#` lines are comments. UTF-8 is used. A card's stable persistence key is URL-safe Base64 of its front and back text. Editing the card creates a new scheduling identity. Local calendar dates determine due cards. Remembered cards award 10 XP; boots unlock at 20, a compass at 50, and a staff at 100. Locations advance every 50 XP and cap at the Sky Library. The save is replaced via a temporary file beside `progress.properties`.

## Limitations and next steps

This is an SM-2-inspired binary scheduler rather than a full implementation of SM-2/FSRS. No deck editor, audio, cloud sync, battle simulation, or daily XP cap. Progress is plaintext. Repeated correct self-grades are trusted. Local clock changes affect scheduling. Duplicate identical cards share a save key. Only one app instance should write a progress file at a time. Writes use file replacement, without a portable guarantee of crash-atomicity across filesystems.

## Troubleshooting

Use a JDK, not just a JRE, for `javac`. A headless session cannot display Swing; `--self-test` still works. Deck lines must contain a real tab, not the characters backslash-t. Run from the project directory or supply deck/save paths. Invalid progress files fail explicitly; restore a known-good backup or use a new save path. The XP/location bar updates after each saved review.

## Development

Read the source entry point described above, make a focused change, run the verification command, and exercise the relevant manual scenario. [Contributing](CONTRIBUTING.md) explains bug reports and pull requests; [security notes](SECURITY.md) describe the trust assumptions. Runtime/generated files are ignored. No telemetry or paid API dependencies are included.

## License

[MIT](LICENSE), copyright 2026 netcodermike.
