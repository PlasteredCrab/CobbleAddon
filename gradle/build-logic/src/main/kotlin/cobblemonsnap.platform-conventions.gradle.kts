plugins {
    id("cobblemonsnap.base-conventions")
}

loom {
    val clientConfig = runConfigs.getByName("client")
    clientConfig.runDir = "runClient"
    clientConfig.programArg("--username=AshKetchum")
    //This is AshKetchum's UUID so you get an Ash Ketchum skin
    clientConfig.programArg("--uuid=93e4e551-589a-41cb-ab2d-435266c8e035")
    val serverConfig = runConfigs.getByName("server")
    serverConfig.runDir = "runServer"
}