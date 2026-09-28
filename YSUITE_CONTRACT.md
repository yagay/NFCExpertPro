# YSuite Integration Contract

This project must remain independently buildable while exposing the same reusable `feature` source to YSuite.

Before refactoring, preserve these rules:

- Keep standalone `app` and reusable `feature` responsibilities separate.
- Do not copy feature source into YSuite or maintain a long-lived adapter mirror.
- In YSuite, reuse shared Root/LSPosed/log/diagnostics infrastructure wherever practical; standalone remains first-class.
- Keep architecture simple and make the module easy to add/remove.
- NFC stability and payment compatibility take priority over aggressive Hooking.

## YNFC requirements

- Preserve NFC/HCE access-card workflows and one-tap switching between configured cards.
- Door-card behavior must not break payment HCE routing.
- Keep compatibility with the actual device NFC stack instead of assuming one vendor implementation.
- NFC service crashes/failures must be logged before retry/reconfiguration.

Canonical cross-project requirements live in `yagay/YSuite/docs/PRODUCT_REQUIREMENTS.md` and `MODULE_REQUIREMENTS.md`.
