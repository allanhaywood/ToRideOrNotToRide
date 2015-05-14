In order to keep the forecast api key private, it is not included with the files on github (see .gitignore).
An api key can be obtained for free, by creating an account and selecting register here: https://developer.forecast.io/
In order for this app to work, a new values resource file called privateValues.xml will need to be provided in the res/values directory.
In it, you will need to add the following, replacing "key goes here" with your actual key.
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="forecast_apikey">key goes here</string>
</resources>