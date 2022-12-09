# ADAMSmartFarming

## Features:

* Reads multiple sensor values from multiple microcontroller boards at once
* Charts sensor readings dynamically using MPAndroidChart
* Authentication through AWS cognito
* Displays crop suggestions powered by flask sever hosting ML algorithm
 * Top 3 are our suggested crops to plant
* Provides sensor warnings based on the MQTT values
* Stores the prediction data locally on the app using an SQLite Database
* Custom Icons created through Adobe XD

## Requires sensors to read the following:
* Soil NPK
* Soil PH
* Soil Moisture
* Water Turbidity
* Water PH
* Atmospheric Temperature
* Atmospheric Humidity

-- Corresponding MQTT tags can be seen in SerializableMqTTTypes.kt file

## Additional Android Libraries Used in Project 

* MPAndroidChart
  * For graphing the sensor readings
* OkHttp
  * Used for making requests to the flask server
* Material UI for the UI
* Kotlinx-Serialization
  * Serializing the JSON strings into data class objects
* AWS SDK for connecting the AWS IOT Core and receiving MQTT messages

## Demonstration 

<p align="center">
  <img src="/Images/menu.jpg" width="350" title="The App Menu">
  <img src="/Images/Graphing.jpg" width="350" title="Graphing Sensor Data">
</p>


<p align="center">
  <img src="/Images/crop_predictions.jpg" width="350" title="The App Menu">
  <img src="/Images/crop-prediction-history.jpg" width="350" title="Graphing Sensor Data">
</p>
