### create a new plugin
```bash
plugman create --name ACR-NFC-Reader-PhoneGap-Plugin --plugin_id com.frankgreen --plugin_version 0.0.1
```

### add a platform
```bash
plugman platform add --platform_name android
```


### install to project
```bash
git clone git@github.com:MorningCoffeeDev/ACR-NFC-Reader-PhoneGap-Plugin.git
cordova plugin add ../ACR-NFC-Reader-PhoneGap-Plugin/
```
