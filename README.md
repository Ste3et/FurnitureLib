# FurnitureLib

The FurnitureLib is a packet based Entity spawning system, to bring new Creations (ArmorStand) builds to Spigot

## Getting Started

FurnitureLib Spawn fake Entitys on the Server and manage the functions from it

### Commands

```
/furniture
/furniture list (plugin/type/world) (side) | list furnitures for the spacific types
/furniture give <systemID> (player) (amount) | give an player a Furniture Model
/furniture debug | With an rightclick on a furniture tells you some Informations over it
/furniture manage | With an rightclick you can change some Protection Stuff add Friends to the Model to Interact with it
/furniture purge <time> | Remove Old furnitures from Owners who is "X-Days" not online
/furniture recipe <systemID> | Shows you the Crafting Recipe of an Furniture Model if it present
/furniture remove <type/player/world/plugin/ID/Distance/lookat/all> | Remove a Furniture with the spacific Option
/furniture toggle (player) | Enable/Disable packet sending to the Player
/furniture download <id> (name) | Download a Furniture from the Furniture Website https://dicecraft.de/furniture/models.php?page=0&name=
/furniture delete <systemID> | delete a Downloadet Model from the Game 
```

### Permissions

```
furniture.list | /furniture list permissions
  - furniture.list.plugin
  - furniture.list.type
  - furniture.list.world
furniture.give | /furniture give permissions
  - furniture.give.player | give another player a furniture
furniture.debug | /furniture debug permissions
furniture.manage | /furniture manage
  - furniture.manage.other | access for all furnitures not only the owns
furniture.purge | /furniture purge permission
furniture.recipe | /furniture recipe permission
  - furniture.recipe.edit | Edit a Furniture Recipe
  - furniture.recipe.remove | Remove a Furniture Recipe
furniture.remove | /furniture remove permission
  - furniture.remove.type
  - furniture.remove.player
  - furniture.remove.world
  - furniture.remove.plugin
  - furniture.remove.distance
  - furniture.remove.lookat
  - furniture.remove.all
furniture.toggle | /furniture toggle Permission
  - furniture.toggle.other | give permission to toggle another players packet sending
furniture.download | /furniture download Permission
furniture.delete | /furniture delete Permission
furniture.craft.<systemID> | This is the Permission to craft a Furniture the system id is like (chair,table,....)
furniture.place.<systemID> | This is the Permission to place a Furniture the system id is like (chair,table,....)
furniture.place.all | This is the Permission to ignore the place Permission check
  - furniture.place.all.<plugin> | This is the Permission to ignore the place Permission check for the plugin like furniture.place.all.FurnitureMaker
  - furniture.place.all.<kit> | This is the Permission to ignore the place Permission check look at https://dicecraft.de/furniture/config.php
furniture.limit.<kit> | You can find the players.yml in the limit Folder of the plugin and you can limit the Player Models with it
furniture.globallimit.<amount> | You can give each player a globallimit permission to place <amount> furniture exemple: 
                                 furniture.globallimit.25 in the default settings a maximal permission of 
                                 furniture.globallimit.150 is set you can increase this in the config.yml
furniture.bypass.limit | This is for admins or what you whant to give it to remove the limit of the Player Models
furniture.bypass.breakSpam | This is for admins or what you whant to give it to remove the timer between break 2 furniture
furniture.bypass.placeSpam | This is for admins or what you whant to give it to remove the timer between place 2 furniture
```

### Supported Plugins

* [LightAPI](https://www.spigotmc.org/resources/lightapi.4510/)
* [ProtectionLib](https://dicecraft.de/furniture/download.php?plugin=ProtectionLib&build=DevBuild&version=Spigot%201.13.x%20-%201.14.x)

### Add Your Own Models (Recommand)

```
Step 1: 
  Design a Model with the FurnitureMaker https://www.spigotmc.org/resources/furnituremaker.20667/ copy the *.dModel
  file to your Ressource folder of your Java Project
Step 2:
  new Project("Trunk", this, getResource("Models/Trunk.dModel")).setEditorProject(false);
      "Trunk" = ProjectName
      this = JavaPlugin instance
      getResource("Models/Trunk.dModel") = local *.dModel file
      setEditorProject(false) = Protect the Model for edit
Step 3:
  register your Project to all placed Furnitures
  	public void loadModels(){
		//Hook the class to the project
	for(ObjectID id : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(id==null) continue;
			if(id.getProjectOBJ() == null) continue;
			if(id.getSQLAction().equals(SQLAction.REMOVE)) continue;
			switch (id.getProjectOBJ().getName()) {
			case "Trunk": id.setFunctionObject(new Trunk(id));break;
			default:break;
			}
		}
	}
Step 4 (Register Project to Furniture after Spawn it):
	@EventHandler
	public void onFurnitureLateSpawn(FurnitureLateSpawnEvent event){
		//Hook the Furniture to the class then it will be placed
		if(event.getProject()==null) return;
		if(event.getProject().getName()==null) return;
		if(event.getID().getSQLAction().equals(SQLAction.REMOVE)) return;
		switch (event.getProject().getName()) {
			case "Trunk": event.getID().setFunctionObject(new Trunk(event.getID()));break;
		default:break;
		}
	}  
```

### Maven Support

```
  <repositories>
      <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
      </repository>
  </repositories>
```

```
  <dependency>
      <groupId>com.gitlab.Ste3et_C0st</groupId>
      <artifactId>FurnitureLib</artifactId>
      <version>v2.0.8</version>
  </dependency>
```

Under Construction :/

### Licensing

MIT License

Copyright (c) 2019 Ste3et_C0st

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.