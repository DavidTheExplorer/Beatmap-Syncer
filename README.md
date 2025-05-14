# Beatmap Syncer
This program minimzes the pain of keeping your osu! beatmaps synchronized across multiple computers.

## Usage
Currently available through CLI, the parameters are:
- *"-gameFolder="* <:> The path of your osu! folder(if unspecified, the default installation path is used).

## Workflow
Upon first launch, it keeps track of that moment as the beginning point.\
You don't have to do anything.

From that point onwards, it would scan for beatmap folders that were created or were modified during the period that had passed, and copy them into: **osu! folder/Beatmap Syncer/time of sync**\
Once the syncing is done, all you need to do is to copy the sync folder into a USB and Voilà!

**Automatic Server Syncing** is planned but won't come in the near future due to the complexity of the implementation.


## Important
* **Currently** the program  only works for Windows.
* **Don't** delete last sync folder! Its creation date marks the beginning date for the next scanning.
*  There is no support for scanning deleted beatmaps. Although implementation ideas exist, I haven't found a reasonable one.
