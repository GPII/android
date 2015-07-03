#!/bin/bash

java -jar ./anode/sdk/java/tools/stubgen.jar --verbose --out ./intents/src --classpath ./app/bin/classes net.gpii.AndroidIntentHandler
java -jar ./anode/sdk/java/tools/stubgen.jar --verbose --out ./a11yservices/src --classpath ./app/bin/classes net.gpii.AndroidA11ySettings
java -jar ./anode/sdk/java/tools/stubgen.jar --verbose --out ./androidSettings/src --classpath ./app/bin/classes net.gpii.AndroidFontSettings
java -jar ./anode/sdk/java/tools/stubgen.jar --verbose --out ./androidSettings/src --classpath ./app/bin/classes net.gpii.AndroidSettings
java -jar ./anode/sdk/java/tools/stubgen.jar --verbose --out ./audioManager/src --classpath ./app/bin/classes net.gpii.AndroidAudioManager
java -jar ./anode/sdk/java/tools/stubgen.jar --verbose --out ./persistentconfig/src --classpath ./app/bin/classes net.gpii.AndroidPersistentConfiguration
