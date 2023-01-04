# Beatmap Syncer
This program aims to minimze the pain of keeping your osu! beatmaps synchronized across multiple computers.

## How the program works
When you run it <ins>for the first time</ins>, it just defines the initial tracking time to that moment.

If you run it for example after 10 days, it grabs all beatmap folders that had any file modifications during that period, and copies them into a folder at: **osu! installation/Beatmap Syncer/time of sync**\
Then all you need to do is to copy paste the maps into another osu! songs folder and Voilà!

## Caveats
1. The program  only works for Windows.
2. There is no support for detecting removed beatmaps, I'm still thinking about how to implement that.
