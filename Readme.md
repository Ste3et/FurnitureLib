## FurnitureLib
> [!NOTE]
> These readme is WIP.
---
A packed-based Minecraft Entity spawn Libary to decorate your server without lags.
The FurnitureLib Spawn models from Entitys to decorate your server with vanilla Models.
**Support**
- [discord](https://discord.gg/7vmyXz3), [spigot](https://www.spigotmc.org/resources/furniturelibary-protectionlib.9368/)

## Setup
---
You need ProtocolLib inside your plugins folder for the furnitureLib you can find it [here](https://www.spigotmc.org/resources/protocollib.1997/)
Download FurnitureLib from one of these websites:
- [spigot](https://www.spigotmc.org/resources/furniturelibary-protectionlib.9368/), [hanger](https://hangar.papermc.io/Ste3et_C0st/FurnitureLibary), [modrinth](https://modrinth.com/plugin/furniturelib)

Place it inside your **/plugins** folder and startup your server.
Please pay attention to download the right versions for your Server Version!
##### Repository [FurnitureLib]
**Maven**
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.Ste3et</groupId>
	<artifactId>FurnitureLib</artifactId>
    <version>3.1.4</version>
</dependency>
```
**Gradle**
```kotlin
repositories {
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly("com.github.Ste3et:FurnitureLib:3.1.4")
}
```