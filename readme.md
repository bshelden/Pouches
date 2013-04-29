Pouches
=======

A minecraft mod that provides 'pouches', items you can carry around with you with the inventory space of a single chest.

Requirements
------------

 * [Minecraft Forge](http://files.minecraftforge.net/) - At least version 7.7.1.662 as that is what I build against.
 * Scala 2.10 (Included with minecraft forge)

Building
--------

To build, the following must be available on the classpath:

From `mcp/lib`

 * argo-3.2-src.jar
 * asm-all-4.1.jar
 * bcprov-debug-jdk15on-148.jar
 * guava-14.0-rc3.jar
 * scala-library.jar (if not present, grab this from a working minecraft forge client)

From `mcp/jars/bin`

 * jinput.jar
 * lwjgl.jar
 * lwjgl_util.jar
 * minecraft.jar

Building for release
--------------------

Forge versions targeting MC 1.5.1 do not yet have support for scala.  Some manual patching is required.

OvermindDL1 and LexManos have created a patch for MCP [here](http://overminddl1.com/minecraft/mcp_update_scala3.zip) that addresses this.
Note that the commands.py included may not work.  What's required is for checkscala() in runtime.py to be modified.  See
[here](https://github.com/MinecraftForge/FML/blob/00f00b17bf0da262e6fe3e327ca2deedf7146305/commands.patch#L66) for the working function.

1. Ensure that scala is on your path
2. Install forge
3. Extract the patch from OvermindDL1 and copy `runtime/bin/retroguard.jar` over the retroguard.jar in your `forge/mcp/runtime/bin`
4. Edit `forge/mcp/runtime/commands.py` so that checkscala() matches that linked above.
5. Copy the sources from `src/main/scala` to `forge/mcp/src/minecraft`
6. Run recompile.sh (in `forge/mcp`)
7. Run reobfuscate.sh (in `forge/mcp`)
8. Copy the folder `name` from `forge/mcp/reobf/minecraft` to an empty directory
9. Copy the resources from `src/main/resources` to the same directory
10. Create a zip from the *contents* of this directory (so name and mods are in the root of the zip)

You should now have a build you can load into a forge minecraft client or server.
