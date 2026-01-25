# Mechanical Repair Station

Status legend: todo -> implemented -> done. Ask for testing before moving from implemented to done.

## Ownership

- Owner: This Codex instance maintains the Mechanical Repair Station mod and handles all related tasks.
- MCP mod is a separate tool owned by another Codex instance; use it for testing only.
- Build note: after each build, copy the latest jar to `D:\b\Mine\GIT_Work\minecraft\mod_builds` (overwrite).

## Features

| Feature | Status | Notes |
| --- | --- | --- |
| Rotation buffer up to 1000, accepts shaft from behind | implemented | Uses kinetic speed to fill rotation buffer. |
| FE buffer up to 1,000,000, draws from adjacent blocks | implemented | Pulls FE from any neighboring block with FE capability. |
| Mana buffer up to 100,000, draws from pools within 3 blocks | implemented | Uses Botania pool reflection helper; needs in-game verification. |
| 10-slot item storage (5x2 grid) | implemented | Slot 0 is target item; slots 1-10 are materials. |
| Repair items with primary material, ~3 items per full repair, consume 100 FE per durability or 1 rotation if no FE | implemented | Free repair applies to non-enchanted leather/stone/wood items. |
| Repair enchanted items with 10 mana per durability | implemented | Requires mana buffer. |
| Upgrade button: consumes yyitems:will_of_durability and materials (3 + existing upgrade levels); adds 10% base max durability | implemented | Uses ItemStack mixin to add bonus max durability. |
| Sounds on repair (anvil) and upgrade (enchant) | implemented | Plays sound server-side. |
| Basic items free repair (non-enchanted leather/stone/wood); upgrades still require materials | implemented | Applies only to repair. |
| Death durability loss | todo | Config added but damage on death not applying; revisit with pre-death or drop handling. |
