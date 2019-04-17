# DEPRECIATION NOTICE
Please note, we have not used this plugin for many years now and, as such, it is likely that it doesn't work with modern ACR units or Cordova. We will keep the repo online for inspiration but we are unable to provide any support.

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

### Usage

#### ACR.onReady

This method will be invoke when Reader ready.

__Example__

```javascript
  ACR.onReady = function (reader) {
     alert("ready " + reader);
  }
```

#### ACR.addTagIdListener

Registers an event listener for Reader

```javascript
  ACR.addTagIdListener(success,failure);
```

__Parameters__

 - `success`: on detect a chip successful;
 - `failure`: on detect a chip failure;

__Example__

```javascript
  ACR.addTagIdListener(
      function(result){
        alert("UID: " + JSON.stringify(result));
      },
      function(result){
        alert("UID Failure: " + JSON.stringify(result));
      }
  );
```

#### ACR.readData

Read data from chip

```javascript
  ACR.readData(block,success,failure);
```

__Parameters__

 - `block`: which block you want to read.
 - `success`: successful callback;
 - `failure`: failure callback;

__Example__

```javascript
  ACR.readData(4,
      function(result){
        alert("Data: " + JSON.stringify(result));
      },
      function(result){
        alert("Data Failure: " + JSON.stringify(result));
      }
  );
```
#### ACR.writeData

write data to chip, maximum 16 character in each block

```javascript
  ACR.writeData(block,data,success,failure);
```

__Parameters__

 - `block`: which block you want to write.
 - `data`:  the data will be write to chip.
 - `success`: successful callback;
 - `failure`: failure callback;

__Example__

```javascript
  ACR.writeData(4,
      "test",
      function(result){
        alert("Write Data: " + JSON.stringify(result));
      },
      function(result){
        alert("Write Data Failure: " + JSON.stringify(result));
      }
  );
```
