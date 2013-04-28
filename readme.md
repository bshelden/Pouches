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

As of Forge 1.5.1-7.7.2.678, forge/mcp can handle scala compilation.  If you are running from before this, you may need to patch your MCP:
OvermindDL1 and LexManos have created a patch for MCP [here](http://overminddl1.com/minecraft/mcp_update_scala3.zip) that addresses this.

1. Ensure that scala is on your path
2. Install forge
3. Extract the patch from OvermindDL1 over `forge/mcp` ensuring that files are overwritten
4. Copy the sources from `src/main/scala` to `forge/mcp/src/minecraft`
5. Run recompile.sh (in `forge/mcp`)
6. Run reobfuscate.sh (in `forge/mcp`)
7. Copy the folder `name` from `forge/mcp/reobf/minecraft` to an empty directory
8. Copy the resources from `src/main/resources` to the same directory
9. Create a zip from the *contents* of this directory (so name and mods are in the root of the zip)

You should now have a build you can load into a forge minecraft client or server.
