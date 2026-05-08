Sentence Builder — `ui/` Folder Notes
====================================

This project contains two UI packages:

1) `com.group37.sentencebuilder.ui` (this folder)
   - Purpose: Reference / prototype UI classes and earlier stubs.
   - Status: Kept intentionally for documentation and historical context.
   - Notes: Some classes in this package may mention "mock", "stub", or "placeholder".
            Those comments reflect that these files are not the active application UI.

2) `com.group37.sentencebuilder.ui_layer`
   - Purpose: The active JavaFX application UI used by the program at runtime.
   - Status: This is the implementation graders should evaluate for functionality.

Why keep `ui/` at all?
- It preserves earlier iterations and reference implementations (useful during development
  and for understanding design evolution) without impacting the runtime UI.

If you are trying to understand the running app:
- Start in `SentenceBuilderApp.java` and follow the FXML-loaded controllers under `ui_layer/`.

