Mob Locator

Mob Locator is a lightweight, client-side utility for Minecraft 1.21.1 that adds a "sonar-style" radar to your HUD. It visualizes the position of nearby entities by rendering pixel-perfect icons directly on your XP bar.

🚀 Features

- Proximity Tracking: Icons change size based on distance, ranging from a 3x3 pixel dot for distant mobs to a 7x7 square for nearby threats.
- Vertical Awareness: Indicators include small arrows to show if a mob is more than 3 blocks above or below your current position.
- Intuitive Color Coding:
  - Hostile (Red): Includes standard monsters and neutral mobs that have become aggressive (e.g., angry Endermen or Wolves).
  - Passive (White): Friendly creatures like cows, sheep, and pigs.
  - Invisible (Gray): Optional tracking for invisible entities.
- Variable Detection Range: Different mobs have unique detection ranges based on "hearing" logic—ranging from 8 blocks for bats to 192 blocks for the Ender Dragon.
- Performance Focused: A purely client-side mod with minimal impact on frame rates.

🛠️ Configuration

If you have Mod Menu and Cloth Config installed, you can access the configuration screen in-game.

| Setting	| Description	| Default |
| - | - | - |
| Show Invisible Mobs |	Toggles tracking for invisible entities. | false |
| Hostile Mob Color |	Hex color for aggressive entities. | 0xFF0000 |
| Passive Mob Color |	Hex color for peaceful entities. | 0xFFFFFF |
| Invisible Mob Color |	Hex color for invisible entities when enabled. | 0x808080 |

NOTE: Config files are saved at .minecraft/config/moblocator.json.

📦 Requirements

- Minecraft: 1.21.10
- Fabric Loader: >=0.17.0
- Java: >=21
- Dependencies:
  - Fabric API
  - Cloth Config API
  - Mod Menu (Optional, for in-game settings)
